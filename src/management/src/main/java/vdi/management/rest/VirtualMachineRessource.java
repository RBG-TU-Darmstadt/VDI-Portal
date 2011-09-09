package vdi.management.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.node.interfaces.NodeVMService;
import vdi.commons.node.objects.NodeCreateVMRequest;
import vdi.commons.node.objects.NodeCreateVMResponse;
import vdi.commons.node.objects.NodeUpdateVMRequest;
import vdi.commons.node.objects.NodeUpdateVMResponse;
import vdi.commons.web.rest.interfaces.ManagementVMService;
import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementTag;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;
import vdi.management.storage.Hibernate;
import vdi.management.storage.DAO.TagsDAO;
import vdi.management.storage.DAO.UserDAO;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.Tag;
import vdi.management.storage.entities.User;
import vdi.management.storage.entities.VirtualMachine;

/**
 * Exports the {@link ManagementVMSercive} Interface for the WebInterface.
 */
@Path("/vm")
public class VirtualMachineRessource implements ManagementVMService {

	private NodeVMService nodeVMService;

	/**
	 * The constructor connects to the NodeController.
	 */
	public VirtualMachineRessource() {
		nodeVMService = ProxyFactory.create(NodeVMService.class,
				"http://localhost:8080/NodeController/vm/");
	}

	@Override
	public ManagementVM getVM(String userId, Long id) {
		VirtualMachine vm = VirtualMachineDAO.get(id);

		ManagementVM resultVM = new ManagementVM();
		resultVM.id = vm.getId();
		resultVM.name = vm.getMachineName();
		resultVM.description = vm.getDescription();
		resultVM.osTypeId = vm.getOsType();
		resultVM.memorySize = vm.getMemorySize();
		resultVM.hddSize = vm.getHddSize();

		resultVM.tags = new ArrayList<ManagementTag>();
		for (Tag tag : vm.getTags()) {
			ManagementTag mgmtTag = new ManagementTag();
			mgmtTag.name = tag.getName();
			mgmtTag.identifier = tag.getSlug();
			resultVM.tags.add(mgmtTag);
		}

		resultVM.lastActive = vm.getLastActive();
		resultVM.status = vm.getStatus();
		resultVM.rdpUrl = vm.getRdpUrl();
		resultVM.image = vm.getImage();

		return resultVM;
	}

	@Override
	public ManagementCreateVMResponse createVirtualMachine(String userId,
			ManagementCreateVMRequest webRequest) {
		NodeCreateVMRequest nodeRequest = new NodeCreateVMRequest();
		nodeRequest.name = webRequest.name;
		nodeRequest.osTypeId = webRequest.osTypeId;
		nodeRequest.description = webRequest.description;
		nodeRequest.memorySize = webRequest.memorySize;
		nodeRequest.hddSize = webRequest.hddSize;
		nodeRequest.vramSize = webRequest.vramSize;
		nodeRequest.accelerate2d = webRequest.accelerate2d;
		nodeRequest.accelerate3d = webRequest.accelerate3d;

		// Create machine on node controller
		NodeCreateVMResponse nodeResponse = nodeVMService
				.createVirtualMachine(nodeRequest);
		
		VirtualMachine vm = new VirtualMachine();
		// Safe successfully created VM to database
		if (nodeResponse.machineId != null) {
			vm.setMachineName(webRequest.name);
			vm.setMachineId(nodeResponse.machineId);
			vm.setCreationDate(new Date());
			vm.setOsType(webRequest.osTypeId);
			vm.setDescription(webRequest.description);
			vm.setHddSize(webRequest.hddSize);
			vm.setMemorySize(webRequest.memorySize);
			vm.setVram(webRequest.vramSize);
			vm.setAccelerate2d(webRequest.accelerate2d);
			vm.setAccelerate3d(webRequest.accelerate3d);
			User vmUser = UserDAO.get(userId);
			vm.setUser(vmUser);
			vm.setStatus(VirtualMachineStatus.STOPPED);

			Hibernate.saveObject(vm);
		}

		// send response to WebInterface
		ManagementCreateVMResponse webResponse = new ManagementCreateVMResponse();
		webResponse.id = vm.getId();

		return webResponse;
	}

	@Override
	public void removeVirtualMachine(String userId, Long id) {
		VirtualMachine vm = VirtualMachineDAO.get(id);

		// Delete VM from NodeController
		nodeVMService.removeVirtualMachine(vm.getMachineId());

		// Delete VM from the database
		Hibernate.deleteObject(vm);
	}

	@Override
	public HashMap<String, HashMap<String, String>> getVMTypes() {
		return nodeVMService.getVMTypes();
	}

	@Override
	public ArrayList<ManagementVM> getVMs(String userId, String tagSlug) {
		ArrayList<ManagementVM> result = new ArrayList<ManagementVM>();
		List<VirtualMachine> vms;

		// check if queryParam for tag is not omitted
		if (tagSlug != null) {
			vms = VirtualMachineDAO.getByTag(UserDAO.get(userId),
					TagsDAO.getBySlug(tagSlug));
		} else {
			vms = UserDAO.get(userId).getVirtualMachines();
		}

		for (VirtualMachine vm : vms) {
			ManagementVM resultVM = new ManagementVM();
			resultVM.id = vm.getId();
			resultVM.name = vm.getMachineName();
			resultVM.description = vm.getDescription();
			resultVM.osTypeId = vm.getOsType();
			resultVM.memorySize = vm.getMemorySize();
			resultVM.hddSize = vm.getHddSize();

			resultVM.tags = new ArrayList<ManagementTag>();
			for (Tag tag : vm.getTags()) {
				ManagementTag mgmtTag = new ManagementTag();
				mgmtTag.name = tag.getName();
				mgmtTag.identifier = tag.getSlug();
				resultVM.tags.add(mgmtTag);
			}

			resultVM.lastActive = vm.getLastActive();
			resultVM.status = vm.getStatus();
			resultVM.rdpUrl = vm.getRdpUrl();
			resultVM.image = vm.getImage();

			result.add(resultVM);
		}

		return result;
	}

	@Override
	public void updateVirtualMachine(String userId, Long id,
			ManagementUpdateVMRequest webRequest) {
		VirtualMachine vm = VirtualMachineDAO.get(id);

		NodeUpdateVMRequest nodeRequest = new NodeUpdateVMRequest();
		nodeRequest.status = webRequest.status;
		nodeRequest.image = webRequest.image;
		nodeRequest.memorySize = webRequest.memorySize;
		nodeRequest.vramSize = webRequest.vramSize;
		nodeRequest.accelerate2d = webRequest.accelerate2d;
		nodeRequest.accelerate3d = webRequest.accelerate3d;

		NodeUpdateVMResponse nodeResponse = nodeVMService.updateVirtualMachine(
				vm.getMachineId(), nodeRequest);

		// Update VirtualMachine in db
		if (webRequest.status != null) {
			vm.setStatus(webRequest.status);
		}
		if (webRequest.image != null) {
			vm.setImage(webRequest.image);
		}
		if (webRequest.machineName  != null) {
			vm.setMachineName(webRequest.machineName);
		}
		if (webRequest.description != null) {
			vm.setDescription(webRequest.description);
		}
		if (webRequest.memorySize != null) {
			vm.setMemorySize(webRequest.memorySize);
		}
		if (webRequest.vramSize != null) {
			vm.setVram(webRequest.vramSize);
		}
		if (webRequest.accelerate2d != null) {
			vm.setAccelerate2d(webRequest.accelerate2d);
		}
		if (webRequest.accelerate3d != null) {
			vm.setAccelerate3d(webRequest.accelerate3d);
		}

		vm.setLastActive(new Date());
		vm.setRdpUrl(nodeResponse.rdpUrl);
		VirtualMachineDAO.update(vm);
	}

	@Override
	public byte[] getMachineScreenshot(String userId, String id,
			int width, int height) {
		// TODO: find a better solution than this
		String machineId = VirtualMachineDAO.get(Long.parseLong(id)).getMachineId();
		return nodeVMService.getMachineScreenshot(machineId, width, height);
	}

}
