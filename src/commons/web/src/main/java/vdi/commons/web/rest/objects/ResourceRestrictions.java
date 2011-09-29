package vdi.commons.web.rest.objects;

/**
 * Contains information about restrictions for VM creation and update defined by
 * the ManagementServer.
 * 
 * @see vdi.commons.web.rest.interfaces.ManagementVMService#getResourceRestrictions()
 *      ManagementVMService#getResourceRestrictions()
 */
public class ResourceRestrictions {
	/**
	 * Minimal RAM in MB.
	 */
	public int minMemory;

	/**
	 * Maximal RAM in MB.
	 */
	public int maxMemory;

	/**
	 * Minimal HDD size in MB.
	 */
	public int minHdd;

	/**
	 * Maximal HDD size in MB.
	 */
	public int maxHdd;

	/**
	 * Minimal VRam in MB.
	 */
	public int minVRam;

	/**
	 * Maximal VRam in MB.
	 */
	public int maxVRam;

}
