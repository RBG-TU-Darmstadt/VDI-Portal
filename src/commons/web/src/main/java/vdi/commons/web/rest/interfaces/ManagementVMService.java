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

public interface ManagementVMService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	ManagementCreateVMResponse createVirtualMachine(@HeaderParam("User") String userId,
			ManagementCreateVMRequest request);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [a-zA-Z0-9-_]+}")
	public ManagementVM getVM(@HeaderParam("User") String userId, @PathParam("id") Long id);

	@DELETE
	@Path("/{id: [a-zA-Z0-9-_]+}")
	public void removeVirtualMachine(@HeaderParam("User") String userId, @PathParam("id") Long id);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<ManagementVM> getVMs(@HeaderParam("User") String userId, @QueryParam("tag") String tag);

	@PUT
	@Path("/{id: [a-zA-Z0-9-_]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateVirtualMachine(@HeaderParam("User") String userId, @PathParam("id") Long id,
			ManagementUpdateVMRequest request);

	@GET
	@Path("/{id: [a-zA-Z0-9-_]+}/screenshot")
	@Produces("image/png")
	public byte[] getMachineScreenshot(@HeaderParam("User") String userId, @PathParam("id") String id,
			@QueryParam("width") int width, @QueryParam("height") int height);

	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, HashMap<String, String>> getVMTypes();

	@GET
	@Path("/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceRestrictions getResourceRestrictions();

}
