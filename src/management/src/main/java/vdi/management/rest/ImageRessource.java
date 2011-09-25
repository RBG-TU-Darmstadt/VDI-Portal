package vdi.management.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.node.interfaces.NodeImageService;
import vdi.commons.web.rest.interfaces.ManagementImageService;
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;

/**
 * This class exports the {@link ManagementImageService} Interface for the
 * WebInterface, in order to receive images for the VirtualMachines on the
 * NodeController.
 */
@Path("/images")
public class ImageRessource implements ManagementImageService {

	@Override
	public List<String> getImages() {
		Set<String> images = new HashSet<String>();
		List<Node> nodes = NodeDAO.getNodes();
		for (Node n : nodes) {
			NodeImageService nodeImageService = ProxyFactory.create(NodeImageService.class, n.getUri() + "/images/");
			images.addAll(nodeImageService.getImages());
		}

		return new ArrayList<String>(images);
	}

}
