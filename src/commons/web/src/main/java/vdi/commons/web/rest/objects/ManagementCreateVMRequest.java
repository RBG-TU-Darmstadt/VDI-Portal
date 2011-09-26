package vdi.commons.web.rest.objects;

/**
 * This request object contains attributes for creating a VM.
 * Common attributes are inherited from {@link ManagementVMRequest}
 */
public class ManagementCreateVMRequest extends ManagementVMRequest {

	public String osTypeId;

	public Long hddSize;

}
