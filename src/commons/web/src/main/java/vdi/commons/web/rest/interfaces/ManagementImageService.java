package vdi.commons.web.rest.interfaces;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface ManagementImageService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getImages();

}
