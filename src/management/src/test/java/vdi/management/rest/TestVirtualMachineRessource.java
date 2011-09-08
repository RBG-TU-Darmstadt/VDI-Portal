package vdi.management.rest;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

import junit.framework.AssertionFailedError;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;

/**
 * Tests for {@link vdi.management.rest.VirtualMachineRessource}.
 */
public class TestVirtualMachineRessource {
	private static Logger LOGGER = Logger.getLogger(TestVirtualMachineRessource.class.getName());

	private VirtualMachineRessource vmr;
	private ManagementCreateVMRequest createVMRequest;
	private String userID = "0x7EEEEEEE";

	boolean init = false;

	private ManagementVM getVMByName(String name) {
		List<ManagementVM> machines = vmr.getVMs(userID, null);
		if (machines != null) {
			for (ManagementVM machine : machines) {
				if (machine.name.equals(name)) {
					return machine;
				}
			}
		}
		return null;
	}

	/**
	 * Initializing {@link VirtualMachineRessource} and
	 * {@link ManagementCreateVMRequest} used by tests.
	 */
	@Before
	public void setUp() {
		vmr = new VirtualMachineRessource();

		HashMap<String, HashMap<String, String>> vmTypes = vmr.getVMTypes();
		Assert.assertNotNull(vmTypes);
		Assert.assertFalse(vmTypes.isEmpty());
		Assert.assertFalse(vmTypes.values().iterator().next().keySet().isEmpty());
		String osTypeID = vmTypes.values().iterator().next().keySet().iterator().next();

		createVMRequest = new ManagementCreateVMRequest();
		createVMRequest.name = "Integration_Test_VM";
		createVMRequest.osTypeId = osTypeID;
		createVMRequest.description = "testing create delete vm";
		createVMRequest.memorySize = 128L;
		createVMRequest.hddSize = 512L;
		createVMRequest.vramSize = 32L;
		createVMRequest.accelerate2d = false;
		createVMRequest.accelerate3d = false;

		ManagementVM vm = getVMByName(createVMRequest.name);
		if (vm != null) {
			System.out.println("Warning: found ''" + createVMRequest.name + "'. Trying to delete.");
			if (vm.status != VirtualMachineStatus.STOPPED) {
				ManagementUpdateVMRequest request = new ManagementUpdateVMRequest();
				request.status = VirtualMachineStatus.STOPPED;
				vmr.updateVirtualMachine(userID, vm.machineId, request);
			}
			vmr.removeVirtualMachine(userID, vm.machineId);
		}
	}

	/**
	 * in case of a test failure, the test VM may still be present.<br>
	 * Try to delete test VM
	 */
	@After
	public void cleanup() {
		try {
			deleteVM(createVMRequest.name);
		} catch (Throwable t) {
		}
	}

	/**
	 * Tests for create and delete Methods:<br>
	 * 1. creation of a VM<br>
	 * 2. test if creation of the same VM fails<br>
	 * 3. delete VM
	 */
	@Test
	public void testCreateDeleteVM() {
		Assert.assertNotNull(vmr.getVMs(userID, null));

		int machineCount = vmr.getVMs(userID, null).size();

		try {
			ManagementCreateVMResponse response;
			String machineID = null;
			try {
				response = vmr.createVirtualMachine(userID, createVMRequest);
				machineID = response.machineId;
			} catch (ClientResponseFailure e) {
				LOGGER.warning(e.getStackTrace().toString());
				@SuppressWarnings("unchecked")
				ClientResponse<String> errorResponse = e.getResponse();
				if (errorResponse != null) {
					LOGGER.info("Response Status Code = " + errorResponse.getResponseStatus());
					String msg = errorResponse.getEntity();
					if(msg != null)
						LOGGER.info("Entity Returned:\n"+msg);
				}
				// something went wrong. TODO: find out what...
			}

			if (machineID == null) {
				LOGGER.warning("Creation of '" + createVMRequest.name + "' failed.");
				// Perhaps vm already existed trying to delete:
				deleteVM(createVMRequest.name);
			}
			Assume.assumeNotNull(machineID);

			ManagementVM testMachine = null;

			List<ManagementVM> machines = vmr.getVMs(userID, null);
			for (ManagementVM iMachine : machines) {
				if (iMachine.machineId.equals(machineID)) {
					testMachine = iMachine;
					break;
				}
			}
			Assert.assertNotNull("created vm not found", testMachine);
			Assert.assertTrue("machine count not increased after createVirtualMachine()",
					machines.size() == machineCount + 1);

			try {
				response = vmr.createVirtualMachine(userID, createVMRequest);
				Assert.assertNull("vm '" + createVMRequest.name + "' mustn't be created twice", response.machineId);
			} catch (ClientResponseFailure e) {
				Status status = Status.fromStatusCode(e.getResponse().getStatus());
				Assert.assertTrue("Expected Status BAD_REQUEST, but " + status, status == Status.BAD_REQUEST);
			}

			// Deleting test machine:
			vmr.removeVirtualMachine(userID, testMachine.machineId);

			boolean machineDeleted = true;
			machines = vmr.getVMs(userID, null);
			Assert.assertTrue("vm count not decreased after removeVirtualMachine()", machines.size() == machineCount);
			for (ManagementVM iMachine : machines) {
				if (iMachine.machineId == machineID) {
					machineDeleted = false;
					break;
				}
			}
			Assert.assertTrue(machineDeleted);

		} catch (Exception e) {
			e.printStackTrace();
			AssertionFailedError err = new AssertionFailedError("Unexpected: " + e.getMessage());
			err.initCause(e);
			throw err;
		}
	}

	private void deleteVM(String name) {
		ManagementVM testMachine = null;
		List<ManagementVM> machines = vmr.getVMs(userID, null);
		int machineCount = machines.size();
		for (ManagementVM iMachine : machines) {
			if (iMachine.name == name) {
				testMachine = iMachine;
				break;
			}
		}
		Assert.assertNotNull("VM '" + name + "' not found", testMachine);

		vmr.removeVirtualMachine(userID, testMachine.machineId);

		boolean machineDeleted = true;
		machines = vmr.getVMs(userID, null);
		Assert.assertTrue("getVMs().size dir not decrease after removeVirtualMachine()",
				machines.size() == machineCount - 1);
		for (ManagementVM iMachine : machines) {
			if (iMachine.machineId == testMachine.machineId) {
				machineDeleted = false;
				break;
			}
		}
		Assert.assertTrue("VM was not deleted", machineDeleted);
	}
}
