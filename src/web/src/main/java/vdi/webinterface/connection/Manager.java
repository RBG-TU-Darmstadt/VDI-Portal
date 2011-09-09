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
import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.web.rest.interfaces.ManagementImageService;
import vdi.commons.web.rest.interfaces.ManagementVMService;
import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementTag;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;

@RemoteProxy
public class Manager {

	/*
	 * TODO: Use tudUserUniqueID from SSO
	 */
	private static String userId = "123456";

	protected List<ServerContext> serverContextList = null;

	ManagementVMService mangementVMService;
	ManagementImageService mangementImageService;

	public Manager() {
		mangementVMService = ProxyFactory.create(ManagementVMService.class, "http://localhost:8080/ManagementServer/vm/");
		mangementImageService = ProxyFactory.create(ManagementImageService.class, "http://localhost:8080/ManagementServer/images/");
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
	public String createVM(String name, String type, String description,
			Long memory, Long harddisk, Long vram, boolean acceleration2d,
			boolean acceleration3d) {
		ManagementCreateVMRequest request = new ManagementCreateVMRequest();
		request.name = name;
		request.osTypeId = type;
		request.description = description;
		request.memorySize = memory;
		request.hddSize = harddisk;
		request.vramSize = vram;
		request.accelerate2d = acceleration2d;
		request.accelerate3d = acceleration3d;

		ManagementCreateVMResponse response = mangementVMService.createVirtualMachine(userId, request);

		JSONObject json = new JSONObject();

		json.put("success", true);
		json.put("id", response.id);

		return json.toString();
	}

	@RemoteMethod
	public String getVMs() {
		// second Parameter null is an optional Tag
		ArrayList<ManagementVM> response = mangementVMService.getVMs(userId, null);

		JSONObject json = new JSONObject();

		json.put("success", true);

		JSONArray vms = new JSONArray();
		for(ManagementVM managementVM : response) {
			JSONObject vm = new JSONObject();
			vm.put("id", managementVM.id);
			vm.put("name", managementVM.name);
			vm.put("description", managementVM.description);
			vm.put("memory", managementVM.memorySize);
			vm.put("harddisk", managementVM.hddSize);

			JSONArray tags = new JSONArray();
			for(ManagementTag managementTag : managementVM.tags) {
				JSONObject tag = new JSONObject();
				tag.put("identifier", managementTag.identifier);
				tag.put("name", managementTag.name);

				tags.add(tag);
			}
			vm.put("tags", tags);

			long lastActive = 0;
			if(managementVM.lastActive != null) {
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

		mangementVMService.updateVirtualMachine(userId, id, request);

		JSONObject json = new JSONObject();

		json.put("success", true);

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

}
