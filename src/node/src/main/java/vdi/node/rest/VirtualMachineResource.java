package vdi.node.rest;

import java.security.InvalidParameterException;
import java.util.HashMap;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.virtualbox_4_1.IMachine;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.node.interfaces.NodeVMService;
import vdi.commons.node.objects.NodeCreateVMRequest;
import vdi.commons.node.objects.NodeCreateVMResponse;
import vdi.commons.node.objects.NodeUpdateVMRequest;
import vdi.commons.node.objects.NodeUpdateVMResponse;
import vdi.commons.node.objects.NodeVM;
import vdi.node.exception.DuplicateMachineNameException;
import vdi.node.exception.MachineNotFoundException;
import vdi.node.management.ImageController;
import vdi.node.management.Socket;
import vdi.node.management.VirtualMachine;

/**
 * Resource for virtual machines.
 */
@Path("/vm")
public class VirtualMachineResource implements NodeVMService {

	@Override
	public NodeCreateVMResponse createVirtualMachine(NodeCreateVMRequest request) {
		VirtualMachine machine;
		try {
			if (request.hddSize == 0) {
				machine = new VirtualMachine(request.name, request.osTypeId, request.description, request.memorySize,
						request.accelerate2d, request.accelerate3d, request.vramSize, request.hddFile);
			} else {
				machine = new VirtualMachine(request.name, request.osTypeId, request.description, request.memorySize,
						request.hddSize, request.accelerate2d, request.accelerate3d, request.vramSize);
			}
		} catch (InvalidParameterException e) {
			throw new NoLogWebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage()).build());
		} catch (DuplicateMachineNameException e) {
			throw new NoLogWebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
					.entity(e.getMessage()).build());
		}

		NodeCreateVMResponse response = new NodeCreateVMResponse();
		response.machineId = machine.getId();
		response.hddFile = machine.getHarddiskMedium().getLocation();

		return response;
	}

	@Override
	public HashMap<String, NodeVM> getVMs() {
		HashMap<String, NodeVM> response = new HashMap<String, NodeVM>();

		for (IMachine machine : VirtualMachine.getAllMachines()) {
			NodeVM responseVM = new NodeVM();
			responseVM.name = machine.getName();
			responseVM.osTypeId = machine.getOSTypeId();

			responseVM.status = VirtualMachine.getState(machine);

			response.put(machine.getId(), responseVM);
		}

		return response;
	}

	@Override
	public NodeVM getVirtualMachine(String machineId) {
		VirtualMachine machine;
		try {
			machine = new VirtualMachine(machineId);
		} catch (MachineNotFoundException e) {
			throw new NoLogWebApplicationException(Status.NOT_FOUND);
		}

		NodeVM responseVM = new NodeVM();
		responseVM.name = machine.getName();
		responseVM.osTypeId = machine.getOSTypeId();
		responseVM.status = machine.getState();

		return responseVM;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getVMTypes() {
		return VirtualMachine.getGuestOsTypes();
	}

	@Override
	public NodeUpdateVMResponse updateVirtualMachine(String machineId, NodeUpdateVMRequest request) {
		VirtualMachine machine;
		try {
			machine = new VirtualMachine(machineId);
		} catch (MachineNotFoundException e) {
			throw new NoLogWebApplicationException(Status.NOT_FOUND);
		}

		NodeUpdateVMResponse response = new NodeUpdateVMResponse();

		// Virtual Machine state handling
		if (request.status != null) {
			if (request.status == VirtualMachineStatus.STARTED) {
				Socket socket = null;

				if (machine.getState() == VirtualMachineStatus.STOPPED) {
					socket = machine.launch();
				} else if (machine.getState() == VirtualMachineStatus.PAUSED) {
					socket = machine.resume();
				}

				if (socket != null) {
					response.rdpUrl = socket.ip + ":" + socket.port;
				}
			} else if (request.status == VirtualMachineStatus.PAUSED) {
				if (machine.getState() == VirtualMachineStatus.STARTED) {
					machine.pause();
				}
			} else if (request.status == VirtualMachineStatus.STOPPED) {
				if (machine.getState() == VirtualMachineStatus.STARTED) {
					machine.stop();
				}
			}
		}

		// image handling
		if (request.image != null) {
			String currentImage = machine.getMountedMediumLocation();

			if (request.image == "") {
				if (currentImage != null) {
					machine.unmountIso();
				}
			} else if (request.image != ImageController.getInstance().getNameFromPath(currentImage)) {
				if (currentImage != null) {
					machine.unmountIso();
				}

				String image = ImageController.getInstance().getPathForName(request.image);

				machine.mountIso(image);
			}
		}

		if (request.memorySize != null) {
			machine.setMemorySize(request.memorySize);
		}

		if (request.vramSize != null) {
			machine.setVramSize(request.vramSize);
		}

		if (request.accelerate2d != null) {
			machine.setAccelerate2d(request.accelerate2d);
		}

		if (request.accelerate3d != null) {
			machine.setAccelerate3d(request.accelerate3d);
		}

		return response;
	}

	@Override
	public void removeVirtualMachine(String machineId, boolean deleteHdd) {
		VirtualMachine machine;
		try {
			machine = new VirtualMachine(machineId);
		} catch (MachineNotFoundException e) {
			throw new NoLogWebApplicationException(Status.NOT_FOUND);
		}

		machine.delete(deleteHdd);
	}

	@Override
	public byte[] getMachineScreenshot(String machineId, int width, int height) {
		VirtualMachine machine;
		try {
			machine = new VirtualMachine(machineId);
		} catch (MachineNotFoundException e) {
			throw new NoLogWebApplicationException(Status.NOT_FOUND);
		}

		return machine.getThumbnail(width, height);
	}

}
