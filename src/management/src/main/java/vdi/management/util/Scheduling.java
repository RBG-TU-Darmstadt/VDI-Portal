package vdi.management.util;

import java.util.Collections;
import java.util.List;

import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.VirtualMachine;

/**
 * Bundles scheduling functionality in one place.
 */
public final class Scheduling {

	/**
	 * Private constructor.
	 */
	private Scheduling() {
	}

	/**
	 * Chooses a NodeController.
	 * 
	 * @return the chosen {@link Node}
	 */
	public static Node selectNode() {
		// Simply select random node
		List<Node> nodes = NodeDAO.getNodes();
		Collections.shuffle(nodes);

		return nodes.get(0);
	}

}
