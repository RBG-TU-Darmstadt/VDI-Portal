package vdi.webinterface.connection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class InitializeContext implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		// Register RESTEasy client provider factory
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {}

}
