package vdi.commons.node.objects;

public class NodeRegisterRequest {

	// The nodes address
	public String address;

	// The nodes current resource information
	public NodeGetResourcesResponse resources;

	@Override
	public String toString() {
		return "{address=" + address + ", resources=" + resources + "}";
	}
	
}
