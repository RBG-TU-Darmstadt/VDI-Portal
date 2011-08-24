package vdi.commons.node.objects;

public class NodeGetResourcesResponse {

	public long ramSize;
	
	public double cpuLoad;
	
	public long freeDiskSpace;

	@Override
	public String toString() {
		return "{ramSize=" + ramSize + ", cpuLoad=" + cpuLoad
				+ ", freeDiskSpace=" + freeDiskSpace + "}";
	}

}
