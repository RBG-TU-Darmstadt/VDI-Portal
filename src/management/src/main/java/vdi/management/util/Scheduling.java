package vdi.management.util;

import java.util.Collections;
import java.util.List;

import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;

public class Scheduling {

	public static Node selectNode() {
		// Simply select random node
		List<Node> nodes = NodeDAO.getNodes();
		Collections.shuffle(nodes);

		return nodes.get(0);
	}

}
