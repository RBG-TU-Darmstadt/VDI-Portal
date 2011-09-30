package vdi.management.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.NoLogWebApplicationException;

import vdi.commons.common.Configuration;
import vdi.commons.common.HttpStatus;
import vdi.commons.common.RESTEasyClientExecutor;
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
import vdi.commons.web.rest.objects.ManagementVMRequest;
import vdi.commons.web.rest.objects.ResourceRestrictions;
import vdi.management.storage.Hibernate;
import vdi.management.storage.DAO.NodeDAO;
import vdi.management.storage.DAO.TagsDAO;
import vdi.management.storage.DAO.UserDAO;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.Tag;
import vdi.management.storage.entities.User;
import vdi.management.storage.entities.VirtualMachine;
import vdi.management.util.BoundsException;
import vdi.management.util.Scheduling;

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
		// Check against restriction
		try {
			checkBounds(webRequest);

			if (webRequest.hddSize < loadRestrictions().minHdd) {
				throw new BoundsException("HDD size must be higher than " + loadRestrictions().minHdd + "MB");
			} else if (webRequest.hddSize > loadRestrictions().maxHdd) {
				throw new BoundsException("HDD size must be lower than " + loadRestrictions().maxHdd + "MB");
			}
		} catch (BoundsException e) {
			throw new NoLogWebApplicationException(Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage()).build());
		}

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
		if (webRequest.tags != null) {
			for (String t : webRequest.tags) {
				Tag tag = TagsDAO.get(t);
				vm.getTags().add(tag);
			}
		}

		User vmUser = UserDAO.get(userId);

		if (vmUser == null) {
			throw new NoLogWebApplicationException(Response.status(Status.SERVICE_UNAVAILABLE)
					.type(MediaType.TEXT_PLAIN).entity("DB: user request failed.").build());
		}

		vm.setUser(vmUser);
		vm.setStatus(VirtualMachineStatus.STOPPED);

		if (!Hibernate.saveObject(vm)) {
			throw new NoLogWebApplicationException(Response.status(Status.SERVICE_UNAVAILABLE)
					.type(MediaType.TEXT_PLAIN).entity("DB: vm insert failed.").build());
		}

		// send response to WebInterface
		ManagementCreateVMResponse webResponse = new ManagementCreateVMResponse();
		webResponse.id = vm.getId();

		return webResponse;
	}

	@Override
	public void removeVirtualMachine(String userId, Long vmId) {
		VirtualMachine vm = VirtualMachineDAO.get(vmId);

		// Remove machine if deployed
		if (vm.getNode() != null) {
			NodeVMService service = selectNodeService(vm.getNode());
			service.removeVirtualMachine(vm.getMachineId());
		}

		// Remove disk image
		selectNodeService(Scheduling.selectRandomNode()).removeDisk(vm.getHddFile());

		// Delete VM from the database
		Hibernate.deleteObject(vm);
	}

	@Override
	public HashMap<String, HashMap<String, String>> getVMTypes() {
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();

		result = selectNodeService(Scheduling.selectRandomNode()).getVMTypes();

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
		// Check against restriction
		try {
			checkBounds(webRequest);
		} catch (BoundsException e) {
			throw new NoLogWebApplicationException(Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage()).build());
		}

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
					// The problem is, that a vm is not immediately unlocked after
					// the vm is stopped.
				}

				// delete virtual machine from NodeController
				selectNodeService(vm.getNode()).removeVirtualMachine(vm.getMachineId());

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
			if (vm.getStatus() == VirtualMachineStatus.STARTED
					|| vm.getStatus() == VirtualMachineStatus.PAUSED) {
				NodeUpdateVMRequest request = new NodeUpdateVMRequest();
				request.image = webRequest.image;

				selectNodeService(vm.getNode())
						.updateVirtualMachine(vm.getMachineId(), request);
			}
		}
		if (webRequest.name != null) {
			vm.setMachineName(webRequest.name);
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
		if (webRequest.tags != null) {
			vm.getTags().clear();
			for (String t : webRequest.tags) {
				Tag tag = TagsDAO.get(t);
				vm.getTags().add(tag);
			}
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
	 * Helper method to get the {@link NodeVMService} for a given {@link Node}.
	 * 
	 * @param node
	 *            the {@link Node}
	 * @return the {@link NodeVMService} for the given Node
	 */
	private NodeVMService selectNodeService(Node node) {
		return ProxyFactory.create(NodeVMService.class, node.getUri() + "/vm/", RESTEasyClientExecutor.get());
	}

	/**
	 * Creates a virtual machine on a NodeController.
	 * 
	 * @param vm
	 *            the virtual machine to create
	 */
	private void createVMOnNode(VirtualMachine vm) {
		NodeCreateVMRequest nodeCreateRequest = new NodeCreateVMRequest();
		nodeCreateRequest.name = "vdi_" + vm.getId();
		nodeCreateRequest.osTypeId = vm.getOsType();
		nodeCreateRequest.description = "'" + vm.getMachineName() + "' of user: " + vm.getUser().getTuid();
		nodeCreateRequest.memorySize = vm.getMemorySize();
		if (vm.getHddFile() == null) {
			nodeCreateRequest.hddSize = vm.getHddSize();
		} else {
			// hdd is already created
			nodeCreateRequest.hddSize = 0;
			nodeCreateRequest.hddFile = vm.getHddFile();
		}
		nodeCreateRequest.vramSize = vm.getVram();
		nodeCreateRequest.accelerate2d = vm.isAccelerate2d();
		nodeCreateRequest.accelerate3d = vm.isAccelerate3d();

		// choose NodeController
		List<Node> nodes = NodeDAO.getNodes();
		vm.setNode(Scheduling.selectSuitableNode(nodes, vm));

		if (vm.getNode() == null) {
			// HTTP Status Code: 507 Insufficient Storage
			throw new NoLogWebApplicationException(Response.status(HttpStatus.INSUFFICIENT_STORAGE).build());
		}

		// Create machine on node controller
		NodeCreateVMResponse nodeResponse = selectNodeService(vm.getNode()).createVirtualMachine(nodeCreateRequest);

		// save the machine id and hdd path
		vm.setMachineId(nodeResponse.machineId);
		vm.setHddFile(nodeResponse.hddFile);
	}

	@Override
	public ResourceRestrictions getResourceRestrictions() {
		return loadRestrictions();
	}

	/**
	 * Load the Restrictions from configuration file.
	 * 
	 * @return {@link ResourceRestrictions} with all restrictions
	 */
	private static ResourceRestrictions loadRestrictions() {
		ResourceRestrictions restrictions = new ResourceRestrictions();

		restrictions.minMemory = Integer.parseInt(Configuration
				.getProperty("vm.min_memory"));
		restrictions.maxMemory = Integer.parseInt(Configuration
				.getProperty("vm.max_memory"));
		restrictions.minHdd = Integer.parseInt(Configuration
				.getProperty("vm.min_hdd"));
		restrictions.maxHdd = Integer.parseInt(Configuration
				.getProperty("vm.max_hdd"));
		restrictions.minVRam = Integer.parseInt(Configuration
				.getProperty("vm.min_vram"));
		restrictions.maxVRam = Integer.parseInt(Configuration
				.getProperty("vm.max_vram"));

		return restrictions;
	}

	/**
	 * Checks whether the request complies with the restrictions.
	 * 
	 * @param webRequest
	 *            the request to check bounds
	 * @throws BoundsException
	 *             on invalid request parameters
	 */
	private static void checkBounds(ManagementVMRequest webRequest)
			throws BoundsException {
		if (webRequest.memorySize != null) {
			if (webRequest.memorySize < loadRestrictions().minMemory) {
				throw new BoundsException("Memory size must be higher than "
						+ loadRestrictions().minMemory + "MB");
			} else if (webRequest.memorySize > loadRestrictions().maxMemory) {
				throw new BoundsException("Memory size must be lower than "
						+ loadRestrictions().maxMemory + "MB");
			}
		}
		if (webRequest.vramSize != null) {
			if (webRequest.vramSize < loadRestrictions().minVRam) {
				throw new BoundsException("VRam size must be higher than "
						+ loadRestrictions().minVRam + "MB");
			} else if (webRequest.vramSize > loadRestrictions().maxVRam) {
				throw new BoundsException("VRam size must be lower than "
						+ loadRestrictions().maxVRam + "MB");
			}
		}
	}

}
