package vdi.management.util;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.VirtualMachine;

/**
 * Bundles scheduling functionality in one place.
 */
public final class Scheduling {

	private static final Logger LOGGER = Logger.getLogger(Scheduling.class.getName());

	private static final double MIN_CPU_LOAD = 0.001;

	/**
	 * Private constructor.
	 */
	private Scheduling() {
	}

	/**
	 * Chooses a random NodeController.
	 * 
	 * @return the chosen {@link Node}
	 */
	public static Node selectRandomNode() {
		// Simply select random node
		List<Node> nodes = NodeDAO.getNodes();
		Collections.shuffle(nodes);

		return nodes.get(0);
	}

	/**
	 * Selects the best Node to deploy the given VM on.
	 * 
	 * <p>
	 * For every Node an index is calculated using the following algorithm: <br />
	 * {@code index = (freeRAM^2 / RAM) * (core / load)}
	 * </p>
	 * 
	 * <p>
	 * (freeRAM / RAM) calculates the free Memory percentage. Since we want to
	 * prefer Nodes with more free RAM but equal percentage, we multiply by
	 * freeRAM again to get a higher score. <br />
	 * As the load is relative to the number of processor cores available, we
	 * divide (load / core), but as a higher load means lower scores we use the
	 * multiplicative inverse (core / load).
	 * </p>
	 * 
	 * <p>
	 * The Node with the highest index is returned as the most suitable Node.
	 * </p>
	 * 
	 * @param nodes
	 *            the list of available Nodes
	 * @param vm
	 *            the VM to deploy
	 * @return the best suited Node, or null when no node has been found
	 */
	public static Node selectSuitableNode(List<Node> nodes, VirtualMachine vm) {
		if (nodes == null) {
			Scheduling.LOGGER.warning("No Nodes given to choose!");
			return null;
		}

		// sorted Map containing suitable Nodes (value) and their performance
		// index (key)
		TreeMap<Integer, Node> sortedNodes = new TreeMap<Integer, Node>();

		// first of all check available RAM and available HDD space
		for (Node n : nodes) {
			boolean enoughMemory = n.getFreeMemorySize() > vm.getMemorySize();
			boolean enoughDiskSpace = n.getFreeDiskSpace() > vm.getHddSize();

			if (enoughMemory && enoughDiskSpace) {
				// choose best Node from available resources
				// calculate performance index
				// index = (freeRAM^2 / availRAM) * (core / load)
				if (n.getCpuLoad() < MIN_CPU_LOAD) {
					n.setCpuLoad(MIN_CPU_LOAD);
				}
				Integer index = (int) ((n.getFreeMemorySize() * n.getFreeMemorySize() / n.getMemorySize())
						* (n.getCores() / n.getCpuLoad()));
				// add to TreeMap
				sortedNodes.put(index, n);
			}
		}

		Entry<Integer, Node> chosenNode = sortedNodes.pollLastEntry();
		if (chosenNode == null) {
			Scheduling.LOGGER.warning("No Node with enough resources has been found!");
			return null;
		}

		Scheduling.LOGGER.info("Chose Node '"
				+ chosenNode.getValue().getUri() + "' with index '"
				+ chosenNode.getKey() + "' for VM: '" + vm.getMachineName()
				+ "'");
		return chosenNode.getValue();
	}

}
