package vdi.commons.node.objects;

public class NodeGetResourcesResponse {

	// RAM size in MB
	public long memorySize;

	// Available RAM size in MB
	public long freeMemorySize;

	// CPU load in %
	public double cpuLoad;

	// Disk space in MB
	public long diskSpace;

	// Free disk space in MB
	public long freeDiskSpace;

	@Override
	public String toString() {
		return "{memorySize=" + memorySize + ", freeMemorySize=" + freeMemorySize + ", cpuLoad="
				+ cpuLoad + ", diskSpace=" + diskSpace + ", freeDiskSpace=" + freeDiskSpace + "}";
	}

}
