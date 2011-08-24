package vdi.commons.node.objects;

public class NodeRegisterRequest {

	public String address;

	public NodeGetResourcesResponse resources;

	@Override
	public String toString() {
		return "{address=" + address + ", resources=" + resources + "}";
	}
	
}
