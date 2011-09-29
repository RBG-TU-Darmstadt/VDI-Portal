package vdi.commons.web.rest.interfaces;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST interface providing a list of available ISO images.
 */
public interface ManagementImageService {

	/**
	 * Query a list of images from ManagementServer.
	 * 
	 * @return list of available images.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<String> getImages();

}
