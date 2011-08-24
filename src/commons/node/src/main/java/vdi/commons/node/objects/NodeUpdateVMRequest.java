package vdi.commons.node.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

public class NodeUpdateVMRequest {

	public VirtualMachineStatus status;

	public String image;

	public String name;
	
	public String description;

	public Long memorySize;

	public Long vramSize;
	
	public Boolean accelerate2d;
	
	public Boolean accelerate3d;

}
