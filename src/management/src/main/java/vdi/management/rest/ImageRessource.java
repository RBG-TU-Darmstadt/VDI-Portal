package vdi.management.rest;

import java.util.List;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.node.interfaces.NodeImageService;
import vdi.commons.web.rest.interfaces.ManagementImageService;

/**
 * This class exports the ManagementImageService Interface for the WebInterface,
 * in order to receive images for the VirtualMachines on the NodeController.
 */
@Path("/images")
public class ImageRessource implements ManagementImageService {

	private NodeImageService nodeImageService;

	/**
	 * The Constructor connects to the NodeController.
	 */
	public ImageRessource() {
		nodeImageService = ProxyFactory.create(NodeImageService.class,
				"http://localhost:8080/NodeController/images/");
	}

	@Override
	public List<String> getImages() {
		return nodeImageService.getImages();
	}

}
