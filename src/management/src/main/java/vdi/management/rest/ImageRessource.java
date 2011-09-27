package vdi.management.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.RESTEasyClientExecutor;
import vdi.commons.node.interfaces.NodeImageService;
import vdi.commons.web.rest.interfaces.ManagementImageService;
import vdi.management.util.Scheduling;

/**
 * This class exports the {@link ManagementImageService} Interface for the
 * WebInterface, in order to receive images for the VirtualMachines on the
 * NodeController.
 */
@Path("/images")
public class ImageRessource implements ManagementImageService {

	@Override
	public List<String> getImages() {
		List<String> images = new ArrayList<String>();

		NodeImageService nodeImageService = ProxyFactory.create(NodeImageService.class,
				Scheduling.selectRandomNode().getUri() + "/images/", RESTEasyClientExecutor.get());
		images = nodeImageService.getImages();

		return images;
	}

}
