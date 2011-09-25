package vdi.commons.node.interfaces;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vdi.commons.node.objects.NodeRegisterRequest;
import vdi.commons.node.objects.NodeRegisterResponse;

/**
 * The {@link NodeRegistrationService} Interface.
 * 
 * Used for register and unregister nodes from the management server.
 */
public interface NodeRegistrationService {

	/**
	 * @param request
	 *            a request object
	 * @return a {@link NodeRegisterResponse} containing the nodeId
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	NodeRegisterResponse register(NodeRegisterRequest request);

	/**
	 * @param nodeId
	 *            the nodeId to delete
	 */
	@DELETE
	@Path("/{nodeId: [a-zA-Z0-9-]+}")
	void unregister(@PathParam("nodeId") String nodeId);

}
