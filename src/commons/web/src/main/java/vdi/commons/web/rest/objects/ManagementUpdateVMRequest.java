package vdi.commons.web.rest.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * This request object contains attributes describing the changed values of an
 * existing VM. Common attributes are inherited from {@link ManagementVMRequest}
 */
public class ManagementUpdateVMRequest extends ManagementVMRequest {

	/**
	 * Set to start, stop or pause the VM.
	 */
	public VirtualMachineStatus status;

	/**
	 * Set to image name to mount or empty string to unmount an image.
	 */
	public String image;

}
