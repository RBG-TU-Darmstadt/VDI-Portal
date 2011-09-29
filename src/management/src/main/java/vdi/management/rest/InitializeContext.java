package vdi.management.rest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import vdi.commons.common.Configuration;
import vdi.management.storage.HibernateUtil;
import vdi.management.util.PollNodeController;

/**
 * This class registers as Tomcat ServletListener in order to be able to execute
 * commands on Serlvet initialization.
 */
public class InitializeContext implements ServletContextListener {

	private Timer polling;

	private static final Logger LOGGER = Logger.getLogger(InitializeContext.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		// Load configuration
		InputStream is = contextEvent.getServletContext().getResourceAsStream("/WEB-INF/configuration.properties");

		if (is != null) {
			Configuration.loadProperties(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new Error("Missing configuration file '/WEB-INF/configuration.properties'");
		}

		// Register RESTEasy client provider factory
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

		// start polling for MachineStatus changes
		polling = new Timer();
		polling.scheduleAtFixedRate(new PollNodeController(), 0,
				Integer.parseInt(Configuration.getProperty("polling.interval")));
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		// stop polling TimerTask
		polling.cancel();

		// closing Hibernate SessionFactory:
		HibernateUtil.getSessionFactory().close();

		// unregisters JDBC driver
		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {
			Driver d = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(d);
			} catch (SQLException e) {
				LOGGER.warning("Error deregistering driver '"
						+ d.getClass().getName() + "'!");
			}
		}
	}
}
