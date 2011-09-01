package vdi.management.rest;

import java.net.URISyntaxException;
import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import vdi.commons.node.interfaces.NodeRegistrationService;
import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;
import vdi.management.storage.Hibernate;
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;

/**
 * This class implements the {@link NodeRegistraion} Interface to enable NodeController
 * to register with the ManagementServer.
 */
@Path("/node")
public class NodesRessource implements NodeRegistrationService {

	@Override
	public NodeRegisterResponse register(NodeRegisterRequest request) {

		// create and store database object
		Node node = new Node();
		node.setNodeId(UUID.randomUUID().toString());
		try {
			node.setUri(request.address);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST).entity(e.getMessage()).build());
		}

		// store Nodes resources
		node.setCpuLoad(request.resources.cpuLoad);
		node.setFreeDiskSpace(request.resources.freeDiskSpace);
		node.setRamSize(request.resources.ramSize);

		// save
		Hibernate.saveOrUpdateObject(node);

		// send corresponding response
		NodeRegisterResponse response = new NodeRegisterResponse();
		response.nodeId = node.getNodeId();

		return response;
	}

	@Override
	public void unregister(String nodeId) {
		// delete node entry from database
		Hibernate.deleteObject(NodeDAO.get(nodeId));
	}

}
