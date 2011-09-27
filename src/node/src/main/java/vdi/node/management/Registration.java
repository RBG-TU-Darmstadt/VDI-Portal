package vdi.node.management;

import java.util.TimerTask;
import java.util.logging.Logger;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.Configuration;
import vdi.commons.common.RESTEasyClientExecutor;
import vdi.commons.node.interfaces.NodeRegistrationService;
import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;
import vdi.node.rest.Resources;

/**
 * This class registers this NodeController at the ManagementServer.
 */
public class Registration extends TimerTask {

	private static String nodeId = null;
	private static final Logger LOGGER = Logger.getLogger(Registration.class.getName());

	@Override
	public void run() {
		NodeRegistrationService nodeRegistration = ProxyFactory.create(NodeRegistrationService.class,
				Configuration.getProperty("managementserver.uri") + "/node/", RESTEasyClientExecutor.get());

		// create request
		NodeRegisterRequest registerRequest = new NodeRegisterRequest();
		registerRequest.address = Configuration.getProperty("node.internal_address");

		registerRequest.resources = Resources.getResourcesObject();

		NodeRegisterResponse response = null;
		try {
			LOGGER.fine("Trying to register NodeController with address: " + registerRequest.address);
			response = nodeRegistration.register(registerRequest);
		} catch (ClientResponseFailure e) {
			LOGGER.warning("Registering at ManagementServer failed: " + e.getMessage());
			// TODO: Retry at SERVICE UNAVAILABLE response code.
			throw new Error("Couldn't register at ManagementServer.");
		}

		if (response == null) {
			LOGGER.severe("Couldn't register at ManagementServer, response is null!");
			throw new Error("Couldn't register at ManagementServer, response is null!");
		}

		Registration.nodeId = response.nodeId;
		LOGGER.info("Registrations was successful, nodeID is: " + nodeId);
	}

	/**
	 * Call this method to unregister this NodeController from the
	 * ManagementServer.
	 */
	public static void unregister() {
		if (nodeId != null) {
			NodeRegistrationService nodeRegistration = ProxyFactory.create(NodeRegistrationService.class,
					Configuration.getProperty("managementserver.uri") + "/node/", RESTEasyClientExecutor.get());

			LOGGER.fine("Sending unregistration request with NodeID (" + nodeId + ") to ManagemetServer!");
			// unregister
			nodeRegistration.unregister(nodeId);
		}
	}

}
