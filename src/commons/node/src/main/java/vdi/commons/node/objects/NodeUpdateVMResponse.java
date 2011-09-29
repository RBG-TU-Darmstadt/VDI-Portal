package vdi.commons.node.objects;

/**
 * Response used by
 * {@link vdi.commons.node.interfaces.NodeVMService#updateVirtualMachine(String, NodeUpdateVMRequest)
 * NodeVMService.updateVirtualMachine()}.
 */
public class NodeUpdateVMResponse {

	/**
	 * Set to RDP URL, if VM is running.
	 */
	public String rdpUrl;

}
