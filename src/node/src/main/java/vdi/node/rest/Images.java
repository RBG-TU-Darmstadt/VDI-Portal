package vdi.node.rest;

import java.util.List;

import javax.ws.rs.Path;

import vdi.commons.node.interfaces.NodeImageService;
import vdi.node.management.ImageController;

/**
 * Resource for ISO-images.
 */
@Path("/images")
public class Images implements NodeImageService {

	@Override
	public List<String> getImages() {
		return ImageController.getInstance().getAvailableImages();
	}

}
