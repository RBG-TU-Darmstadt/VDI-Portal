package vdi.commons.node.objects;

/**
 * Response used by
 * {@link vdi.commons.node.interfaces.NodeRegistrationService#register(NodeRegisterRequest)
 * NodeRegistrationService.register(NodeRegisterRequest)}.
 */
public class NodeRegisterResponse {

	/**
	 * The NodeController's ID. <br />
	 * Needed to unregister NodeController.
	 * 
	 * @see vdi.commons.node.interfaces.NodeRegistrationService#unregister(String)
	 *      NodeRegistrationService.unregister(String nodeId)
	 */
	public String nodeId;

}
