package vdi.management.rest;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import vdi.management.util.PollMachineStatus;

/**
 * This class registers as Tomcat ServletListener in order to be able to execute
 * commands on Serlvet initialization.
 */
public class InitializeContext implements ServletContextListener {

	/**
	 * How often should the ManagementServer poll the NodeController for status
	 * changes.
	 */
	static final int SECONDS = 5;
	private Timer polling;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		// Register RESTEasy client provider factory
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

		// start polling for MachineStatus changes
		polling = new Timer();
		polling.scheduleAtFixedRate(new PollMachineStatus(), 0, SECONDS * 1000);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		// stop polling TimerTask
		polling.cancel();
	}

}
