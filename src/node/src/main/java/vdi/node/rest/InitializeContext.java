package vdi.node.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import vdi.commons.common.Configuration;
import vdi.node.management.Registration;
import vdi.node.management.VirtualMachine;

/**
 * Initialization.
 */
public class InitializeContext implements ServletContextListener {
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

		// Start registration after delay
		int seconds = Integer.parseInt(Configuration.getProperty("node.registration_delay"));
		LOGGER.info("starting registration to '" + Configuration.getProperty("managementserver.uri") + "' in "
				+ seconds + " seconds.");
		Timer registration = new Timer();
		registration.schedule(new Registration(), seconds * 1000);
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		// Clean up VirtualBox
		try {
			VirtualMachine.cleanup();
		} catch (ExceptionInInitializerError e) {
		}

		// unregister this NodeController at ManagementServer
		Registration.unregister();
	}

}
