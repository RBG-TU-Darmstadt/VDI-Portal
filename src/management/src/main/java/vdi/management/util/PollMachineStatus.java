package vdi.management.util;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.node.interfaces.NodeVMService;
import vdi.commons.node.objects.NodeVM;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.VirtualMachine;

/**
 * This class extends the TimerTask and Polls the NodeController to check
 * current status of running VMs.
 */
public class PollMachineStatus extends TimerTask {
	private static final Logger LOGGER = Logger.getLogger(PollMachineStatus.class.getName());

	private NodeVMService nodeVMService;

	/**
	 * The constructor connects to the NodeController.
	 */
	public PollMachineStatus() {
		nodeVMService = ProxyFactory.create(NodeVMService.class, "http://localhost:8080/NodeController/vm/");
	}

	@Override
	public void run() {
		boolean changed = false;
		// get all running machines from the database
		List<VirtualMachine> runningVMs = VirtualMachineDAO.getMachinesByStatus(VirtualMachineStatus.STARTED);

		for (VirtualMachine vm : runningVMs) {
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
			// TODO: notify Web1Interface about the change
		}

	}

}
