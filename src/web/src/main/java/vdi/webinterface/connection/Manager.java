package vdi.webinterface.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.Configuration;
import vdi.commons.common.RESTEasyClientExecutor;
import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.web.rest.interfaces.ManagementImageService;
import vdi.commons.web.rest.interfaces.ManagementTagService;
import vdi.commons.web.rest.interfaces.ManagementVMService;
import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementTag;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;
import vdi.commons.web.rest.objects.ResourceRestrictions;

@RemoteProxy
public class Manager {

	/*
	 * TODO: Use tudUserUniqueID from SSO
	 */
	private static String userId = "123456";

	protected List<ServerContext> serverContextList = null;

	ManagementVMService mangementVMService;
	ManagementImageService mangementImageService;
	ManagementTagService mangementTagService;

	public Manager() {
		mangementVMService = ProxyFactory.create(ManagementVMService.class,
				Configuration.getProperty("managementserver.uri") + "/vm/",
				RESTEasyClientExecutor.get());
		mangementImageService = ProxyFactory.create(ManagementImageService.class,
				Configuration.getProperty("managementserver.uri") + "/images/",
				RESTEasyClientExecutor.get());
		mangementTagService = ProxyFactory.create(ManagementTagService.class,
				Configuration.getProperty("managementserver.uri") + "/tags/",
				RESTEasyClientExecutor.get());
	}

	@RemoteMethod
	public void register() {
		ServerContext serverContext = null;

		try {
			// Get server context, if necessary
			serverContext = ServerContextFactory.get();

			// Register function under current session id
			this.serverContextList.add(serverContext);
		} catch (Exception e) {
		}
	}

	@RemoteMethod
	public String createVM(String name, String description, String type,
			String image, Long memory, Long harddisk, Long vram,
			boolean acceleration2d, boolean acceleration3d, List<String> tags) {
		ManagementCreateVMRequest createRequest = new ManagementCreateVMRequest();
		createRequest.name = name;
		createRequest.osTypeId = type;
		createRequest.description = description;
		createRequest.memorySize = memory;
		createRequest.hddSize = harddisk;
		createRequest.vramSize = vram;
		createRequest.accelerate2d = acceleration2d;
		createRequest.accelerate3d = acceleration3d;
		createRequest.tags = tags;

		// Create machine
		ManagementCreateVMResponse createResponse = mangementVMService.createVirtualMachine(userId, createRequest);

		if ( ! image.isEmpty()) {
			ManagementUpdateVMRequest mountRequest = new ManagementUpdateVMRequest();
			mountRequest.image = image;

			mangementVMService.updateVirtualMachine(userId, createResponse.id, mountRequest);
		}

		JSONObject json = new JSONObject();

		json.put("success", true);
		json.put("id", createResponse.id);

		return json.toString();
	}

	@RemoteMethod
	public String getVMs() {
		// second Parameter null is an optional Tag
		ArrayList<ManagementVM> response = mangementVMService.getVMs(userId, null);

		JSONObject json = new JSONObject();

		json.put("success", true);

		JSONArray vms = new JSONArray();
		for (ManagementVM managementVM : response) {
			JSONObject vm = new JSONObject();
			vm.put("id", managementVM.id);
			vm.put("name", managementVM.name);
			vm.put("description", managementVM.description);
			vm.put("memory", managementVM.memorySize);
			vm.put("harddisk", managementVM.hddSize);

			JSONArray tags = new JSONArray();
			for (ManagementTag managementTag : managementVM.tags) {
				JSONObject tag = new JSONObject();
				tag.put("identifier", managementTag.identifier);
				tag.put("name", managementTag.name);

				tags.add(tag);
			}
			vm.put("tags", tags);

			long lastActive = 0;
			if (managementVM.lastActive != null) {
				lastActive = managementVM.lastActive.getTime();
			}
			vm.put("last_active", lastActive);

			vm.put("status", managementVM.status);
			vm.put("rdp_url", managementVM.rdpUrl);
			vm.put("image", managementVM.image);

			vms.add(vm);
		}

		json.put("vms", vms);

		return json.toString();
	}

	@RemoteMethod
	public String startVM(Long id) {
		ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
		request.status = VirtualMachineStatus.STARTED;

		boolean success = true;
		try {
			mangementVMService.updateVirtualMachine(userId, id, request);
		} catch(ClientResponseFailure f) {
			success = false;
		}

		JSONObject json = new JSONObject();

		json.put("success", success);

		return json.toString();
	}

	@RemoteMethod
	public String pauseVM(Long id) {
		ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
		request.status = VirtualMachineStatus.PAUSED;

		mangementVMService.updateVirtualMachine(userId, id, request);

		JSONObject json = new JSONObject();

		json.put("success", true);

		return json.toString();
	}

	@RemoteMethod
	public String stopVM(Long id) {
		ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
		request.status = VirtualMachineStatus.STOPPED;

		mangementVMService.updateVirtualMachine(userId, id, request);

		JSONObject json = new JSONObject();

		json.put("success", true);

		return json.toString();
	}

	@RemoteMethod
	public String removeVM(Long id) {
		mangementVMService.removeVirtualMachine(userId, id);

		JSONObject json = new JSONObject();

		json.put("success", true);

		return json.toString();
	}

	@RemoteMethod
	public String getVMTypes() {
		HashMap<String, HashMap<String, String>> response = mangementVMService.getVMTypes();

		JSONObject json = new JSONObject();

		json.put("success", true);
		json.put("types", response);

		return json.toString();
	}

	@RemoteMethod
	public String getRestrictions() {
		ResourceRestrictions response = mangementVMService.getResourceRestrictions();

		JSONObject json = new JSONObject();
		json.put("success", true);

		JSONObject restrictions = new JSONObject();
		restrictions.put("minMemory", response.minMemory);
		restrictions.put("maxMemory", response.maxMemory);
		restrictions.put("minHdd", response.minHdd);
		restrictions.put("maxHdd", response.maxHdd);
		restrictions.put("minVRam", response.minVRam);
		restrictions.put("maxVRam", response.maxVRam);
		json.put("restrictions", restrictions);

		return json.toString();
	}

	@RemoteMethod
	public String getImages() {
		List<String> response = mangementImageService.getImages();

		JSONObject json = new JSONObject();

		json.put("success", true);
		json.put("images", response);

		return json.toString();
	}

	@RemoteMethod
	public String mountImage(Long id, String imageName) {
		ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
		request.image = imageName;

		mangementVMService.updateVirtualMachine(userId, id, request);

		JSONObject json = new JSONObject();

		json.put("success", true);

		return json.toString();
	}

	@RemoteMethod
	public String unmountImage(Long id) {
		ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
		request.image = "";

		mangementVMService.updateVirtualMachine(userId, id, request);

		JSONObject json = new JSONObject();

		json.put("success", true);

		return json.toString();
	}

	@RemoteMethod
	public String getTags() {
		List<ManagementTag> response = mangementTagService.getTags();

		JSONObject json = new JSONObject();

		json.put("success", true);

		JSONArray tags = new JSONArray();
		for(ManagementTag tag : response) {
			tags.add(tag.name);
		}

		json.put("tags", tags);

		return json.toString();
	}

}
