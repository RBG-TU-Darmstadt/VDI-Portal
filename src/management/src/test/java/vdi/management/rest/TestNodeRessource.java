package vdi.management.rest;

import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.Test;

import vdi.commons.node.interfaces.NodeRegistrationService;
import vdi.commons.node.objects.NodeGetResourcesResponse;
import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;

public class TestNodeRessource {
	private static Logger LOGGER = Logger.getLogger(TestNodeRessource.class.getName());

	@Test
	public void registerNode() {
		NodeRegistrationService nodeRegistration = ProxyFactory.create(NodeRegistrationService.class,
				"http://localhost:8080/ManagementServer" + "/node/");

		// REGISTER:

		// create request
		NodeRegisterRequest registerRequest = new NodeRegisterRequest();

		NodeGetResourcesResponse ressources = new NodeGetResourcesResponse();
		ressources.cpuLoad = 0;
		ressources.memorySize = 1024;
		ressources.freeMemorySize = 512;
		ressources.diskSpace = 4096;
		ressources.freeDiskSpace = 1024;

		registerRequest.resources = ressources;
		registerRequest.address = "http://localhost:8080/Test/NodeController";

		NodeRegisterResponse response = null;
		try {
			LOGGER.info("Trying to register NodeController with address: " + registerRequest.address);
			response = nodeRegistration.register(registerRequest);
		} catch (ClientResponseFailure f) {
			LOGGER.warning(f.getMessage());
			LOGGER.info(ExceptionUtils.getFullStackTrace(f));
			throw new AssertionFailedError(f.getResponse().toString());
		}

		Assert.assertNotNull("Couldn't register at ManagementServer, response is null!", response);
		Assert.assertNotNull("Couldn't register at ManagementServer, response.nodeId is null!", response.nodeId);

		LOGGER.info("Registrations was successful, nodeID is: " + response.nodeId);

		LOGGER.info("Sending unregistration request with NodeID (" + response.nodeId + ") to ManagemetServer!");

		// unregister
		try {
			nodeRegistration.unregister(response.nodeId);
		} catch (ClientResponseFailure f) {
			LOGGER.warning(f.getMessage());
			LOGGER.info(ExceptionUtils.getFullStackTrace(f));
			throw new AssertionFailedError(f.getResponse().toString());
		}
	}

	@Test
	public void wrongAdressRegistration() {
		NodeRegistrationService nodeRegistration = ProxyFactory.create(NodeRegistrationService.class,
				"http://localhost:8080/ManagementServer" + "/node/");

		// REGISTER:

		// create request
		NodeRegisterRequest registerRequest = new NodeRegisterRequest();

		NodeGetResourcesResponse ressources = new NodeGetResourcesResponse();
		ressources.cpuLoad = 0;
		ressources.memorySize = 1024;
		ressources.freeMemorySize = 512;
		ressources.diskSpace = 4096;
		ressources.freeDiskSpace = 1024;

		registerRequest.resources = ressources;
		registerRequest.address = "THIS_IS_OBVIOSLY_WRONG! :-{}";

		NodeRegisterResponse response = null;
		try {
			LOGGER.info("Trying to register NodeController with address: " + registerRequest.address);
			response = nodeRegistration.register(registerRequest);
		} catch (ClientResponseFailure f) {
			LOGGER.finest(ExceptionUtils.getFullStackTrace(f));
			LOGGER.info(f.getResponse().getResponseStatus() + ":\n"
					+ ((ClientResponse<?>) f.getResponse()).getEntity(String.class));

			Assert.assertTrue("BAD_REQUEST expected, but was " + f.getResponse().getResponseStatus(), f.getResponse()
					.getResponseStatus() == Status.BAD_REQUEST);

			LOGGER.info("Registration failed, as expected.");
			return;
		}

		if (response != null && response.nodeId != null) {
			LOGGER.warning("Registerd at ManagementServer! But it should have failed!");
			LOGGER.info("Registrations was successful, nodeID is: " + response.nodeId);
			LOGGER.info("Sending unregistration request with NodeID (" + response.nodeId + ") to ManagemetServer!");
			try {
				nodeRegistration.unregister(response.nodeId);
			} catch (ClientResponseFailure f) {
			}
			throw new AssertionFailedError("Registerd at ManagementServer! But it should have failed!");
		}
	}
}
