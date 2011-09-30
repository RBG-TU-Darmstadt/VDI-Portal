package vdi.commons.node.objects;

/**
 * Response used by
 * {@link vdi.commons.node.interfaces.NodeVMService#createVirtualMachine
 * NodeVMService.createVirtualMachine}.
 */
public class NodeCreateVMResponse {

	/**
	 * The machines ID.
	 */
	public String machineId;

	/**
	 * Filename of the virtual hard disk file.
	 */
	public String hddFile;

}
