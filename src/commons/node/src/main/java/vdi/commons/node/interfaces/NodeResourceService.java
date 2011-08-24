package vdi.commons.node.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vdi.commons.node.objects.NodeGetResourcesResponse;

/**
 * The {@link NodeResourceService} Interface.
 */
public interface NodeResourceService {

	/**
	 * @return a {@link NodeGetResourcesResponse}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	NodeGetResourcesResponse getResources();

}
