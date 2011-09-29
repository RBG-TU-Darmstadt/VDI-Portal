package vdi.commons.web.rest.objects;

import java.util.List;

/**
 * Common attributes for creating and updating a VM.
 */
public class ManagementVMRequest {

	/**
	 * Name of the VM as displayed to the user.
	 */
	public String name;

	/**
	 * Description of the VM.
	 */
	public String description;

	/**
	 * RAM size in MB.
	 */
	public Long memorySize;

	/**
	 * VRam size in MB.
	 */
	public Long vramSize;

	/**
	 * Enable 2D acceleration.
	 */
	public Boolean accelerate2d;

	/**
	 * Enable 3D acceleration.
	 */
	public Boolean accelerate3d;

	/**
	 * List of tags assigned to this VM.
	 */
	public List<String> tags;

}
