package vdi.commons.node.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * Request used to update an existing VM.
 */
public class NodeUpdateVMRequest {

	/**
	 * Set to start, stop or pause the VM.
	 */
	public VirtualMachineStatus status;

	/**
	 * Set to image path, if an image should be mounted.
	 */
	public String image;

	/**
	 * Set to change RAM size (in MB).
	 */
	public Long memorySize;

	/**
	 * Set to change VRAM size (in MB).
	 */
	public Long vramSize;

	/**
	 * Set to enable or disable 2D acceleration.
	 */
	public Boolean accelerate2d;

	/**
	 * Set to enable or disable 3D acceleration.
	 */
	public Boolean accelerate3d;

}
