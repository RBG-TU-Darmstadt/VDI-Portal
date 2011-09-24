package vdi.management.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.DAO.TagsDAO;
import vdi.management.storage.DAO.UserDAO;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.Tag;
import vdi.management.storage.entities.User;
import vdi.management.storage.entities.VirtualMachine;
import vdi.management.util.ManagementUtil;

/**
 * Exports the {@link vdi.commons.web.rest.interfaces.ManagementVMService
 * ManagementVMService} Interface for the WebInterface.
 */
@Path("/vm")
public class VirtualMachineRessource implements ManagementVMService {

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
	public ManagementCreateVMResponse createVirtualMachine(String userId, ManagementCreateVMRequest webRequest) {
		// Store VM to database
		VirtualMachine vm = new VirtualMachine();

		vm.setMachineName(webRequest.name);
		vm.setCreationDate(new Date());
		vm.setOsType(webRequest.osTypeId);
		vm.setDescription(webRequest.description);
		vm.setHddSize(webRequest.hddSize);
		vm.setMemorySize(webRequest.memorySize);
		vm.setVram(webRequest.vramSize);
		vm.setAccelerate2d(webRequest.accelerate2d);
		vm.setAccelerate3d(webRequest.accelerate3d);
		User vmUser = UserDAO.get(userId);

		if (vmUser == null) {
			throw new WebApplicationException(Response.status(Status.SERVICE_UNAVAILABLE)
					.entity("DB: user request failed.").build());
		}

		vm.setUser(vmUser);
		vm.setStatus(VirtualMachineStatus.STOPPED);

		if (!Hibernate.saveObject(vm)) {
			throw new WebApplicationException(Response.status(Status.SERVICE_UNAVAILABLE)
					.entity("DB: vm insert failed.").build());
		}

		// send response to WebInterface
		ManagementCreateVMResponse webResponse = new ManagementCreateVMResponse();
		webResponse.id = vm.getId();

		return webResponse;
	}

	@Override
	public void removeVirtualMachine(String userId, Long vmId) {
		VirtualMachine vm = VirtualMachineDAO.get(vmId);

		// Delete VM from NodeController
		if (vm.getNode() == null) {
			createVMOnNode(vm);
		}
		NodeVMService service = selectNodeService(vm.getNode());
		service.removeVirtualMachine(vm.getMachineId(), true);

		// Delete VM from the database
		Hibernate.deleteObject(vm);
	}

	@Override
	public HashMap<String, HashMap<String, String>> getVMTypes() {
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();

		for (Node n : NodeDAO.getNodes()) {
			result.putAll(selectNodeService(n).getVMTypes());
		}
		return result;
	}

	@Override
	public ArrayList<ManagementVM> getVMs(String userId, String tagSlug) {
		ArrayList<ManagementVM> result = new ArrayList<ManagementVM>();
		List<VirtualMachine> vms;

		// check if queryParam for tag is not omitted
		if (tagSlug != null) {
			vms = VirtualMachineDAO.getByTag(UserDAO.get(userId), TagsDAO.getBySlug(tagSlug));
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
	public void updateVirtualMachine(String userId, Long id, ManagementUpdateVMRequest webRequest) {

		// get VM from database
		VirtualMachine vm = VirtualMachineDAO.get(id);

		// create request for NodeController
		NodeUpdateVMRequest nodeRequest = new NodeUpdateVMRequest();

		// Only save if part of the request / changed
		if (webRequest.status != null && webRequest.status != vm.getStatus()) {
			nodeRequest.status = webRequest.status;
			if (webRequest.status == VirtualMachineStatus.STARTED) {
				if (vm.getStatus() == VirtualMachineStatus.STOPPED && vm.getNode() == null) {
					// create virtual machine on NodeController
					createVMOnNode(vm);
				}

				// start VM
				nodeRequest.image = vm.getImage();
				NodeUpdateVMResponse updateResponse = selectNodeService(vm.getNode()).updateVirtualMachine(
						vm.getMachineId(), nodeRequest);

				// store RDP address
				vm.setRdpUrl(updateResponse.rdpUrl);
			} else if (webRequest.status == VirtualMachineStatus.STOPPED) {
				// stop virtual machine
				nodeRequest.image = null;
				selectNodeService(vm.getNode()).updateVirtualMachine(vm.getMachineId(), nodeRequest);

				// FIXME
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// if this empty block will remain after fixing above, there
					// needs to be an useful comment
				}

				// delete virtual machine from NodeController
				selectNodeService(vm.getNode()).removeVirtualMachine(vm.getMachineId(), false);

				vm.setRdpUrl(null);
				vm.setMachineId(null);
				vm.setMachineId(null);
				vm.setNode(null);
			} else {
				// pause virtual machine
				selectNodeService(vm.getNode()).updateVirtualMachine(vm.getMachineId(), nodeRequest);
			}

			vm.setStatus(webRequest.status);
			vm.setLastActive(new Date());

		}
		if (webRequest.image != null) {
			vm.setImage(webRequest.image);
		}
		if (webRequest.machineName != null) {
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

		VirtualMachineDAO.update(vm);
	}

	@Override
	public byte[] getMachineScreenshot(String userId, String id, int width, int height) {
		// TODO: find a better solution than this
		VirtualMachine vm = VirtualMachineDAO.get(Long.parseLong(id));
		String machineId = vm.getMachineId();
		return selectNodeService(vm.getNode()).getMachineScreenshot(machineId, width, height);
	}

	/**
	 * Chooses a NodeController.
	 * 
	 * @return the chosen {@link Node}
	 */
	public Node chooseNode() {
		List<Node> nodes = NodeDAO.getNodes();

		if (nodes.size() == 0) {
			return null;
		}

		// chose random node
		int random = (int) ManagementUtil.randomNumber(1, nodes.size());
		return nodes.get(random - 1);
	}

	/**
	 * Helper method to get the {@link NodeVMService} for a given {@link Node}.
	 * 
	 * @param node
	 *            the {@link Node}
	 * @return the {@link NodeVMService} for the given Node
	 */
	private NodeVMService selectNodeService(Node node) {
		return ProxyFactory.create(NodeVMService.class, node.getUri() + "/vm/");
	}

	/**
	 * Creates a virtual machine on a NodeController.
	 * 
	 * @param vm
	 *            the virtual machine to create
	 */
	private void createVMOnNode(VirtualMachine vm) {
		NodeCreateVMRequest nodeCreateRequest = new NodeCreateVMRequest();
		nodeCreateRequest.name = vm.getMachineName();
		nodeCreateRequest.osTypeId = vm.getOsType();
		nodeCreateRequest.description = vm.getDescription();
		nodeCreateRequest.memorySize = vm.getMemorySize();
		if (vm.getHddPath() == null) {
			nodeCreateRequest.hddSize = vm.getHddSize();
		} else {
			// hdd is already created
			nodeCreateRequest.hddSize = 0;
			nodeCreateRequest.hddFile = vm.getHddPath();
		}
		nodeCreateRequest.vramSize = vm.getVram();
		nodeCreateRequest.accelerate2d = vm.isAccelerate2d();
		nodeCreateRequest.accelerate3d = vm.isAccelerate3d();

		// choose NodeController
		vm.setNode(chooseNode());

		// Create machine on node controller
		NodeCreateVMResponse nodeResponse = selectNodeService(vm.getNode()).createVirtualMachine(nodeCreateRequest);

		// save the machine id and hdd path
		vm.setMachineId(nodeResponse.machineId);
		vm.setHddPath(nodeResponse.hddFile);
	}
}
