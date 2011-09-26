package vdi.commons.web.rest.objects;

import java.util.List;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * This request object contains attributes describing the changed values of an
 * existing VM.
 */
public class ManagementUpdateVMRequest {

	public VirtualMachineStatus status;

	public String image;

	public String machineName;

	public String description;

	public Long memorySize;

	public Long vramSize;

	public Boolean accelerate2d;

	public Boolean accelerate3d;
	
	public List<String> tags;

}