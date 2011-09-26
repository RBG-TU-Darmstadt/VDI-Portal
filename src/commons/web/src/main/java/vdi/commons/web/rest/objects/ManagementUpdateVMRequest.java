package vdi.commons.web.rest.objects;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * This request object contains attributes describing the changed values of an
 * existing VM.
 * Common attributes are inherited from {@link ManagementVMRequest}
 */
public class ManagementUpdateVMRequest extends ManagementVMRequest {

	public VirtualMachineStatus status;

	public String image;

}