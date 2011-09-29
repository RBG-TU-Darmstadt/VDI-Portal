package vdi.commons.web.rest.interfaces;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vdi.commons.web.rest.objects.ManagementTag;

/**
 * REST Interface providing a list tags in use.
 */
public interface ManagementTagService {

	/**
	 * Query a list of {@link ManagementTag tags} from ManagementServer.
	 * 
	 * @return list of {@link ManagementTag} used by VMs.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<ManagementTag> getTags();

}
