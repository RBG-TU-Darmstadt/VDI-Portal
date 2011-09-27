package vdi.node.rest;

import javax.ws.rs.Path;

import vdi.commons.node.interfaces.NodeResourceService;
import vdi.commons.node.objects.NodeGetResourcesResponse;
import vdi.node.management.SystemInformation;

/**
 * Resource for machine resources.
 */
@Path("/resources")
public class Resources implements NodeResourceService {

	@Override
	public NodeGetResourcesResponse getResources() {
		return getResourcesObject();
	}

	/**
	 * @return resource object
	 */
	public static NodeGetResourcesResponse getResourcesObject() {
		NodeGetResourcesResponse response = new NodeGetResourcesResponse();

		response.cpuLoad = SystemInformation.getCpuLoad();
		response.memorySize = SystemInformation.getRamSize();
		response.freeMemorySize = SystemInformation.getFreeRamSize();
		response.diskSpace = SystemInformation.getDiskSpace();
		response.freeDiskSpace = SystemInformation.getFreeDiskSpace();
		response.cores = SystemInformation.getCores();

		return response;
	}

}
