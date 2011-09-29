package vdi.commons.web.rest.objects;

/**
 * This request object contains attributes for creating a VM. Common attributes
 * are inherited from {@link ManagementVMRequest}
 */
public class ManagementCreateVMRequest extends ManagementVMRequest {

	/**
	 * The VBox TypeId of the OS used on this VM.
	 */
	public String osTypeId;

	/**
	 * size of HDD to be created for this VM.
	 */
	public Long hddSize;

}
