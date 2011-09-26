package vdi.commons.web.rest.objects;

import java.util.List;

/**
 * Common attributes for creating and updating a VM.
 */
public class ManagementVMRequest {

	public String name;

	public String description;

	public Long memorySize;

	public Long vramSize;

	public Boolean accelerate2d;

	public Boolean accelerate3d;

	public List<String> tags;

}
