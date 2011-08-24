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
		response.ramSize = SystemInformation.getRamSize();
		response.freeDiskSpace = SystemInformation.getFreeDiskSpace();

		return response;
	}

}
