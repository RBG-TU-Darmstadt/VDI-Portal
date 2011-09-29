package vdi.commons.node.objects;

/**
 * Response used by
 * {@link vdi.commons.node.interfaces.NodeResourceService#getResources()
 * NodeResourceService.getResources()}.
 */
public class NodeGetResourcesResponse {

	/**
	 * RAM size in MB.
	 */
	public long memorySize;

	/**
	 * Available RAM size in MB.
	 */
	public long freeMemorySize;

	/**
	 * CPU load in %.
	 */
	public double cpuLoad;

	/**
	 * Disk space in MB.
	 */
	public long diskSpace;

	/**
	 * Free disk space in MB.
	 */
	public long freeDiskSpace;

	/**
	 * Number of processor cores.
	 */
	public int cores;

	@Override
	public String toString() {
		return "{memorySize=" + memorySize + ", freeMemorySize=" + freeMemorySize + ", cpuLoad=" + cpuLoad
				+ ", diskSpace=" + diskSpace + ", freeDiskSpace=" + freeDiskSpace + "}";
	}

}
