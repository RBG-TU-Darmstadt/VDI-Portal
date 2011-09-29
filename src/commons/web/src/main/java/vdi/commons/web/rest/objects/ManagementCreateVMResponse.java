package vdi.commons.web.rest.objects;

/**
 * Response from
 * {@link vdi.commons.web.rest.interfaces.ManagementVMService#createVirtualMachine(String, ManagementCreateVMRequest)
 * ManagementVMService#createVirtualMachine()}.
 */
public class ManagementCreateVMResponse {

	/**
	 * Identifies the VM at the ManagementServer.
	 */
	public Long id;

}
