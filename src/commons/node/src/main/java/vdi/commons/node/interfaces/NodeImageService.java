package vdi.commons.node.interfaces;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The {@link NodeImageService} interface.
 * 
 * Used for managing ISO-images.
 */
public interface NodeImageService {

	/**
	 * @return a list of filenames
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<String> getImages();

}
