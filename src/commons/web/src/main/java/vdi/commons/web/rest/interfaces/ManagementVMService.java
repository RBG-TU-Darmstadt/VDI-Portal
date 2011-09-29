package vdi.commons.web.rest.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;
import vdi.commons.web.rest.objects.ResourceRestrictions;

/**
 * REST interface for managing Virtual Machines.
 */
public interface ManagementVMService {

	/**
	 * Create a VM on the ManagementServer.
	 * 
	 * @param userId
	 *            specifying the user, this VM belongs to.
	 * @param request
	 *            containing information about the VM.
	 * @return response containing the id of the created VM.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	ManagementCreateVMResponse createVirtualMachine(@HeaderParam("User") String userId,
			ManagementCreateVMRequest request);

	/**
	 * Query informations about a specific VM.
	 * 
	 * @param userId
	 *            ID of the user making this request.
	 * @param id
	 *            ID of the VM to be retrieved.
	 * @return Response containing informations about the queried VM.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [a-zA-Z0-9-_]+}")
	ManagementVM getVM(@HeaderParam("User") String userId, @PathParam("id") Long id);

	/**
	 * Permanently removes a VM and all of it's data.
	 * 
	 * @param userId
	 *            ID of the user making this request.
	 * @param id
	 *            ID of the VM to be removed.
	 */
	@DELETE
	@Path("/{id: [a-zA-Z0-9-_]+}")
	void removeVirtualMachine(@HeaderParam("User") String userId, @PathParam("id") Long id);

	/**
	 * Retrieve a list of VMs belonging to a user. VMs can be filtered for
	 * having specific tags.
	 * 
	 * @param userId
	 *            ID of the user making this request.
	 * @param tag
	 *            only VMs having tag specified will be retrieved.
	 * @return the list of VMs belonging to the user and having tag set.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	ArrayList<ManagementVM> getVMs(@HeaderParam("User") String userId, @QueryParam("tag") String tag);

	/**
	 * Update an existing VM. Used to start, stop or pause a VM.
	 * 
	 * @param userId
	 *            ID of the user making this request.
	 * @param id
	 *            ID of the VM to be updated.
	 * @param request
	 *            Providing informations about updated to be performed.
	 */
	@PUT
	@Path("/{id: [a-zA-Z0-9-_]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void updateVirtualMachine(@HeaderParam("User") String userId, @PathParam("id") Long id,
			ManagementUpdateVMRequest request);

	/**
	 * Retrieve latest screenshot of a VM.
	 * 
	 * @param userId
	 *            ID of the user making this request.
	 * @param id
	 *            ID of the VM to retrieve a screenshot from.
	 * @param width
	 *            Screenshot width
	 * @param height
	 *            Screenshot height
	 * @return Byte-Array containing image data.
	 */
	@GET
	@Path("/{id: [a-zA-Z0-9-_]+}/screenshot")
	@Produces("image/png")
	byte[] getMachineScreenshot(@HeaderParam("User") String userId, @PathParam("id") String id,
			@QueryParam("width") int width, @QueryParam("height") int height);

	/**
	 * Retrieve Map of available VM OS Types.
	 * 
	 * @return OS-Category -> { OS-ID -> OS Name}
	 */
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	HashMap<String, HashMap<String, String>> getVMTypes();

	/**
	 * Get VM resource restrictions defined by the ManagementServer.
	 * 
	 * @return Response containing the restrictions.
	 */
	@GET
	@Path("/resources")
	@Produces(MediaType.APPLICATION_JSON)
	ResourceRestrictions getResourceRestrictions();

}
