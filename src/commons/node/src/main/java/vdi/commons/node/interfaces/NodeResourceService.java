package vdi.commons.node.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vdi.commons.node.objects.NodeGetResourcesResponse;

/**
 * The {@link NodeResourceService} Interface.
 * 
 * Used to get resource information from a node.
 */
public interface NodeResourceService {

	/**
	 * @return a {@link NodeGetResourcesResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	NodeGetResourcesResponse getResources();

}
