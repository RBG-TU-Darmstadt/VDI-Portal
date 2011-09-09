package vdi.commons.node.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

public class NodeUpdateVMRequest {

	// The new VM status
	public VirtualMachineStatus status;

	// The image to be mounted
	public String image;

	// The new RAM size in MB
	public Long memorySize;

	// The new VRAM size in MB
	public Long vramSize;

	// The new 2D-acceleration value
	public Boolean accelerate2d;

	// The new 3D-acceleration value
	public Boolean accelerate3d;

}
