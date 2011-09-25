package vdi.commons.web.rest.interfaces;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vdi.commons.web.rest.objects.ManagementTag;

public interface ManagementTagService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ManagementTag> getTags();

}
