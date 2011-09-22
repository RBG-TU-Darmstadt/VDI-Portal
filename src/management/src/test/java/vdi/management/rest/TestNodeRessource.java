package vdi.management.rest;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.Test;

import vdi.commons.common.Configuration;
import vdi.commons.node.interfaces.NodeRegistrationService;
import vdi.commons.node.objects.NodeGetResourcesResponse;
import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;

public class TestNodeRessource {
	private static Logger LOGGER = Logger.getLogger(TestNodeRessource.class.getName());
	
	@Test
	public void registerNode()
	{
		NodeRegistrationService nodeRegistration = ProxyFactory.create(NodeRegistrationService.class,
				"http://localhost:8080/ManagementServer"+"/node/");
				//"http://xf06-vm4.rbg.informatik.tu-darmstadt.de:8080/ManagementServer/node/");
		
		// REGISTER:

		// create request
		NodeRegisterRequest registerRequest = new NodeRegisterRequest();
		registerRequest.address = Configuration
				.getProperty("node.internal_address");

		NodeGetResourcesResponse ressources = new NodeGetResourcesResponse();

		ressources.cpuLoad = 0;
		ressources.ramSize = 16;
		ressources.freeDiskSpace = 32;
		registerRequest.resources = ressources;
		registerRequest.address = "localhost";

		NodeRegisterResponse response = null;
		try {
			LOGGER.info("Trying to register NodeController with address: "
					+ registerRequest.address);
			response = nodeRegistration.register(registerRequest);
		} catch (WebApplicationException e) {
			LOGGER.warning("Registering at ManagementServer failed: "
					+ e.getMessage());
		} catch (ClientResponseFailure f)
		{
			f.printStackTrace();
			LOGGER.warning(f.getStackTrace().toString());
			Assert.assertTrue(f.getMessage(),false);
		}
		
		if (response == null) {
			throw new RuntimeException(
					"Couldn't register at ManagementServer, response is null!");
		}

		String nodeId = response.nodeId;
		LOGGER.info("Registrations was successful, nodeID is: " + nodeId);
		
		
		// UNREGISTER:
		
		LOGGER.info("Sending unregistration request with NodeID (" + nodeId
				+ ") to ManagemetServer!");
		// unregister
		nodeRegistration.unregister(nodeId);
	}
}
