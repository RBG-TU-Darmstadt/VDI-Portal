package vdi.commons.web.rest.objects;

import java.util.List;

public class ManagementCreateVMRequest {

	public String name;

	public String osTypeId;

	public String description;

	public Long memorySize;

	public Long hddSize;

	public Long vramSize;

	public boolean accelerate2d;

	public boolean accelerate3d;
	
	public List<String> tags;

}
