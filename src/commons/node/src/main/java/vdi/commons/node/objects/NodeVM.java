package vdi.commons.node.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * Containing basic information about an VM.
 * 
 * @see vdi.commons.node.interfaces.NodeVMService#getVirtualMachine(String)
 *      NodeVMService.getVirtualMachine(String machineId)
 * @see vdi.commons.node.interfaces.NodeVMService#getVMs()
 *      NodeVMService.getVMs()
 */
public class NodeVM {

	/**
	 * The VM's name in VBox.
	 */
	public String name;

	/**
	 * The VM's OS-Type-ID.
	 */
	public String osTypeId;

	/**
	 * Current VM's status ({@link VirtualMachineStatus#STARTED STARTED},
	 * {@link VirtualMachineStatus#PAUSED PAUSED},
	 * {@link VirtualMachineStatus#STOPPED STOPPED}).
	 */
	public VirtualMachineStatus status;

}
