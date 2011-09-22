package vdi.commons.node.objects;

public class NodeCreateVMRequest {

	// The machines name
	public String name;

	// The machines OS-Type-ID
	public String osTypeId;

	// The machines description
	public String description;

	// RAM size in MB
	public long memorySize;

	// HDD size in GB
	public long hddSize;
	
	// VRAM size in MB
	public long vramSize;
	
	// 2D hardware-acceleration
	public boolean accelerate2d;
	
	// 3D hardware-acceleration
	public boolean accelerate3d;

	// path to an existing virtual harddisk image
	public String hddPathAndFilename;

}
