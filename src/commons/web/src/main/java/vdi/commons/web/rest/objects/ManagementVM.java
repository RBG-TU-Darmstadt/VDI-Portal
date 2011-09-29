package vdi.commons.web.rest.objects;

import java.util.ArrayList;
import java.util.Date;

import vdi.commons.common.objects.VirtualMachineStatus;

/**
 * Contains all information about an VM stored in ManagementServer.
 * 
 * @see vdi.commons.web.rest.interfaces.ManagementVMService#getVM(String, Long) ManagementVMService.getVM()
 * @see vdi.commons.web.rest.interfaces.ManagementVMService#getVMs(String, String) ManagementVMService.getVMs()
 */
public class ManagementVM {

	/**
	 * Identifies the VM at ManagementSever.
	 */
	public Long id;

	/**
	 * Name of the VM as displayed to the user.
	 */
	public String name;

	/**
	 * OS Type Identifier used by VBox.
	 */
	public String osTypeId;

	/**
	 * Description of the VM.
	 */
	public String description;

	/**
	 * RAM in MB.
	 */
	public Long memorySize;

	/**
	 * HDD size in MB.
	 */
	public Long hddSize;

	/**
	 * List of tags assign to this VM.
	 */
	public ArrayList<ManagementTag> tags;

	/**
	 * Is this VM {@link VirtualMachineStatus#STARTED STARTED},
	 * {@link VirtualMachineStatus#STOPPED STOPPED} or
	 * {@link VirtualMachineStatus#PAUSED PAUSED}?
	 */
	public VirtualMachineStatus status;

	/**
	 * The URL to connect RDP-Client to, if VM is running.
	 */
	public String rdpUrl;

	/**
	 * Last time this VM was running.
	 */
	public Date lastActive;

	/**
	 * Image name, if mounted image exists.
	 */
	public String image;

}
