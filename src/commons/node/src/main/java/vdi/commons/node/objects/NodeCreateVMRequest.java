package vdi.commons.node.objects;

/**
 * Contains informations for creating a VM.
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
	 * HDD size in MB.
	 */
	public long hddSize;

	/**
	 * VRAM size in MB.
	 */
	public long vramSize;

	/**
	 * 2D hardware-acceleration.
	 */
	public boolean accelerate2d;

	/**
	 * 3D hardware-acceleration.
	 */
	public boolean accelerate3d;

	/**
	 * path to an existing virtual hard disk image.
	 */
	public String hddFile;

}
