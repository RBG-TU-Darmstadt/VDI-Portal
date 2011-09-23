package vdi.commons.node.interfaces;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import vdi.commons.node.objects.NodeCreateVMRequest;
import vdi.commons.node.objects.NodeCreateVMResponse;
import vdi.commons.node.objects.NodeUpdateVMRequest;
import vdi.commons.node.objects.NodeUpdateVMResponse;
import vdi.commons.node.objects.NodeVM;

/**
 * The {@link NodeVMService} interface.
 * 
 * Used to control an VirtualBox instance.
 */
public interface NodeVMService {

	/**
	 * Creates a VirtualMachine.
	 * 
	 * @param request
	 *            the {@link NodeCreateVMRequest} object.
	 * @return a {@link NodeCreateVMResponse}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	NodeCreateVMResponse createVirtualMachine(NodeCreateVMRequest request);

	/**
	 * Removes a VirtualMachine by its machineID.
	 * 
	 * @param machineId
	 *            the machineId of the VM to delete.
	 */
	@DELETE
	@Path("/{machineId: [a-zA-Z0-9-_]+}")
	void removeVirtualMachine(@PathParam("machineId") String machineId, @QueryParam("deleteHdd") boolean deleteHdd);

	/**
	 * Get information of a VirtualMachine.
	 * 
	 * @param machineId
	 *            the machineId of the VM.
	 * @return a {@link NodeVM} object containing the information.
	 */
	@GET
	@Path("/{machineId: [a-zA-Z0-9-_]+}")
	@Produces(MediaType.APPLICATION_JSON)
	NodeVM getVirtualMachine(@PathParam("machineId") String machineId);

	/**
	 * Get all informations for all VirtualMachines.
	 * 
	 * @return a Map with machineId and {@link NodeVM} object containing the
	 *         information.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	HashMap<String, NodeVM> getVMs();

	/**
	 * Update an existing VirtualMachine.
	 * 
	 * @param machineId
	 *            the machineId of the VM to update.
	 * @param request
	 *            a {@link NodeUpdateVMRequest}
	 * @return a {@link NodeUpdateVMResponse}
	 */
	@PUT
	@Path("/{machineId: [a-zA-Z0-9-_]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	NodeUpdateVMResponse updateVirtualMachine(
			@PathParam("machineId") String machineId,
			NodeUpdateVMRequest request);

	/**
	 * Get a screenshot a VM.
	 * 
	 * @param machineId
	 *            the machineId to get the screenshot from
	 * @param width
	 *            the image width
	 * @param height
	 *            the image height
	 * @return the image as byte array
	 */
	@GET
	@Path("/{machineId: [a-zA-Z0-9-_]+}/screenshot")
	@Produces("image/png")
	byte[] getMachineScreenshot(@PathParam("machineId") String machineId,
			@QueryParam("width") int width, @QueryParam("height") int height);

	/**
	 * Get all possible VMTypes supported by the NodeController.
	 * 
	 * @return a Map<Family_ID, Map<OS_Type_ID, OS_Type_Description>>
	 */
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	HashMap<String, HashMap<String, String>> getVMTypes();

}
