package vdi.commons.node.objects;

/**
 * Request used to register at a ManagementServer.
 * 
 * @see vdi.commons.node.interfaces.NodeRegistrationService#register(NodeRegisterRequest)
 *      NodeRegistrationService.register(NodeRegisterRequest)
 */
public class NodeRegisterRequest {

	/**
	 * NodeController's URI.
	 */
	public String address;

	/**
	 * Node's current resource information.
	 */
	public NodeGetResourcesResponse resources;

	@Override
	public String toString() {
		return "{address=" + address + ", resources=" + resources + "}";
	}

}
