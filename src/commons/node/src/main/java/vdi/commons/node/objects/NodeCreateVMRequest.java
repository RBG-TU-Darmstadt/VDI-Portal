package vdi.commons.node.objects;

/**
 * Contains informations for creating a VM. <br />
 * Used by
 * {@link vdi.commons.node.interfaces.NodeVMService#createVirtualMachine(NodeCreateVMRequest)
 * NodeVMService.createVirtualMachine(NodeCreateVMRequest)}.
 */
public class NodeCreateVMRequest {

	/**
	 * The machines name.
	 */
	public String name;

	/**
	 * The machines OS-Type-ID.
	 */
	public String osTypeId;

	/**
	 * The machines description.
	 */
	public String description;

	/**
	 * RAM size in MB.
	 */
	public long memorySize;

	/**
	 * HDD size in MB. Ignored if {@link NodeCreateVMRequest#hddFile hddFile} is set.
	 */
	public long hddSize;

	/**
	 * VRAM size in MB.
	 */
	public long vramSize;

	/**
	 * Enable 2D hardware-acceleration.
	 */
	public boolean accelerate2d;

	/**
	 * Enable 3D hardware-acceleration.
	 */
	public boolean accelerate3d;

	/**
	 * Path to an existing virtual hard disk image.
	 */
	public String hddFile;

}
