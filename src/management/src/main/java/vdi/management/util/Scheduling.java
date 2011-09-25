package vdi.management.util;

import java.util.Collections;
import java.util.List;

import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;

/**
 * Bundles scheduling functionality in one place
 */
public class Scheduling {

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
