package vdi.management.rest;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import vdi.management.storage.HibernateUtil;
import vdi.management.util.PollNodeController;

/**
 * This class registers as Tomcat ServletListener in order to be able to execute
 * commands on Serlvet initialization.
 */
public class InitializeContext implements ServletContextListener {

	/**
	 * Interval in ms for ManagementServer polling the NodeControllers for
	 * status changes.
	 * 
	 * TODO: move to configuration property.
	 */
	static final int POLL_INTERVAL = 5000;
	private Timer polling;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		// Register RESTEasy client provider factory
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

		// start polling for MachineStatus changes
		polling = new Timer();
		polling.scheduleAtFixedRate(new PollNodeController(), 0, POLL_INTERVAL);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		// stop polling TimerTask
		polling.cancel();

		// closing Hibernate SessionFactory:
		HibernateUtil.getSessionFactory().close();
	}

}
