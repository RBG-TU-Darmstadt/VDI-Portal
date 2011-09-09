package vdi.commons.web.rest.objects;

import java.util.ArrayList;
import java.util.Date;

import vdi.commons.common.objects.VirtualMachineStatus;

public class ManagementVM {

	public Long id;

	public String name;

	public String osTypeId;

	public String description;

	public Long memorySize;

	public Long hddSize;

	public ArrayList<ManagementTag> tags;

	public VirtualMachineStatus status;

	public String rdpUrl;

	public Date lastActive;

	public String image;

}
