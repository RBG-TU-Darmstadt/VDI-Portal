package vdi.management.rest;

import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.spi.NoLogWebApplicationException;

import vdi.commons.node.interfaces.NodeRegistrationService;
import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;
import vdi.management.storage.Hibernate;
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;

/**
 * This class implements the {@link NodeRegistrationService} Interface to enable
 * NodeController to register with the ManagementServer.
 */
@Path("/node")
public class NodesRessource implements NodeRegistrationService {
	private static final Logger LOGGER = Logger.getLogger(NodesRessource.class.getName());

	@Override
	public NodeRegisterResponse register(NodeRegisterRequest request) {
		LOGGER.info("recived register request from '" + request.address + "'");

		// create and store database object
		Node node = new Node();
		node.setNodeId(UUID.randomUUID().toString());
		try {
			node.setUri(request.address);
		} catch (URISyntaxException e) {
			throw new NoLogWebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage()).build());
		}

		// store Nodes resources
		node.setMemorySize(request.resources.memorySize);
		node.setFreeMemorySize(request.resources.freeMemorySize);
		node.setCpuLoad(request.resources.cpuLoad);
		node.setDiskSpace(request.resources.diskSpace);
		node.setFreeDiskSpace(request.resources.freeDiskSpace);
		node.setCores(request.resources.cores);

		// save
		if (!Hibernate.saveOrUpdateObject(node)) {
			throw new NoLogWebApplicationException(Response.status(Status.SERVICE_UNAVAILABLE)
					.type(MediaType.TEXT_PLAIN).entity("DB: node update failed").build());
		}

		// send corresponding response
		NodeRegisterResponse response = new NodeRegisterResponse();
		response.nodeId = node.getNodeId();

		LOGGER.info("registered node '" + request.address + "' as '" + node.getNodeId() + "'");

		return response;
	}

	@Override
	public void unregister(String nodeId) {
		LOGGER.fine("recived unregister request for '" + nodeId + "'");
		Node n = NodeDAO.get(nodeId);
		if (n != null) {
			// delete node entry from database
			if (Hibernate.deleteObject(n)) {
				LOGGER.info("node '" + n.getNodeId() + "' unregistered.");
			} else {
				LOGGER.warning("unregistering node '" + n.getNodeId() + "' failed.");
			}
		} else {
			LOGGER.info("unregister ignored. Node '" + nodeId + "' not registered.");
		}
	}

}
