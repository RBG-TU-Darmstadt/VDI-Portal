package vdi.management.util;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.node.interfaces.NodeResourceService;
import vdi.commons.node.interfaces.NodeVMService;
import vdi.commons.node.objects.NodeGetResourcesResponse;
import vdi.commons.node.objects.NodeVM;
import vdi.management.storage.Hibernate;
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.VirtualMachine;

/**
 * This class extends the TimerTask and polls the NodeController to check
 * current status of running VMs and for available resources.
 */
public class PollNodeController extends TimerTask {
	private static final Logger LOGGER = Logger.getLogger(PollNodeController.class.getName());

	@Override
	public void run() {
		pollStatusChanges();

		// poll available resources
		List<Node> nodes = NodeDAO.getNodes();
		if (nodes != null) {
			pollAvailableResources(nodes);
		}
	}

	/**
	 * Polls the given NodeController for available resources and updates the
	 * corresponding database entry.
	 * 
	 * @param nodes
	 *            the NodeController to poll
	 */
	private void pollAvailableResources(List<Node> nodes) {
		for (Node n : nodes) {
			NodeResourceService resource = ProxyFactory.create(
					NodeResourceService.class, n.getUri() + "/resources/");
			NodeGetResourcesResponse response = resource.getResources();

			// store resources
			n.setMemorySize(response.memorySize);
			n.setFreeMemorySize(response.freeMemorySize);
			n.setCpuLoad(response.cpuLoad);
			n.setDiskSpace(response.diskSpace);
			n.setFreeDiskSpace(response.freeDiskSpace);
			n.setCores(response.cores);

			if (!Hibernate.saveOrUpdateObject(n)) {
				LOGGER.warning("DB: update node resources failed.");
			}
		}
	}

	/**
	 * Polls every NodeController with running VMs about the state of this
	 * Machines and eventually updates the VM database entry.
	 */
	private void pollStatusChanges() {
		boolean changed = false;
		// get all running machines from the database
		List<VirtualMachine> runningVMs = VirtualMachineDAO
				.getMachinesByStatus(VirtualMachineStatus.STARTED);

		for (VirtualMachine vm : runningVMs) {
			NodeVMService nodeVMService = ProxyFactory.create(
					NodeVMService.class, vm.getNode().getUri() + "/vm/");
			NodeVM nodeVM = nodeVMService.getVirtualMachine(vm.getMachineId());
			// if VM is stopped on NodeController, update Database entry
			if (nodeVM.status == VirtualMachineStatus.STOPPED) {
				changed = true;
				vm.setStatus(VirtualMachineStatus.STOPPED);
				vm.setRdpUrl(null);
				if (!VirtualMachineDAO.update(vm)) {
					LOGGER.warning("DB: update vm status failed.");
				}
			}
		}

		if (changed) {
			// TODO: notify WebInterface about the change
		}

	}

}
