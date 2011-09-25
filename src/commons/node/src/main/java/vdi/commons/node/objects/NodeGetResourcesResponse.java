package vdi.commons.node.objects;

public class NodeGetResourcesResponse {

	// RAM size in MB
	public long ramSize;

	// CPU load in %
	public double cpuLoad;

	// Free disk space in bytes
	public long freeDiskSpace;

	@Override
	public String toString() {
		return "{ramSize=" + ramSize + ", cpuLoad=" + cpuLoad + ", freeDiskSpace=" + freeDiskSpace + "}";
	}

}
