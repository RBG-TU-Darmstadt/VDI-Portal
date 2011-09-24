package vdi.management.rest;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;

import junit.framework.AssertionFailedError;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.web.rest.interfaces.ManagementVMService;
import vdi.commons.web.rest.objects.ManagementCreateVMRequest;
import vdi.commons.web.rest.objects.ManagementCreateVMResponse;
import vdi.commons.web.rest.objects.ManagementUpdateVMRequest;
import vdi.commons.web.rest.objects.ManagementVM;

/**
 * Tests for {@link vdi.management.rest.VirtualMachineRessource}.
 */
public class TestVirtualMachineRessource {
	private static Logger LOGGER = Logger.getLogger(TestVirtualMachineRessource.class.getName());

	private ManagementVMService vmr;
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
		vmr = ProxyFactory.create(ManagementVMService.class, "http://localhost:8080/ManagementServer" + "/vm/");

		HashMap<String, HashMap<String, String>> vmTypes = vmr.getVMTypes();
		Assert.assertNotNull("vmTypes must not be null.", vmTypes);
		Assert.assertFalse("vmTypes must not be empty.", vmTypes.isEmpty());
		Assert.assertFalse("vmTypes values must be keySets.", vmTypes.values().iterator().next().keySet().isEmpty());
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
				vmr.updateVirtualMachine(userID, vm.id, request);
			}
			vmr.removeVirtualMachine(userID, vm.id);
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
			Long vmId = null;
			try {
				response = vmr.createVirtualMachine(userID, createVMRequest);
				vmId = response.id;
			} catch (WebApplicationException e) {
				LOGGER.warning(e.getMessage());
				LOGGER.info(e.getStackTrace().toString());
				Assert.assertTrue("VM creation failed.",false);
			} catch (ClientResponseFailure e) {
				LOGGER.warning(e.getStackTrace().toString());
				@SuppressWarnings("unchecked")
				ClientResponse<String> errorResponse = e.getResponse();
				if (errorResponse != null) {
					LOGGER.info("Response Status Code = " + errorResponse.getResponseStatus());
					String msg = errorResponse.getEntity();
					if (msg != null)
						LOGGER.info("Entity Returned:\n" + msg);
				}
				// something went wrong. TODO: find out what...
			}

			if (vmId == null) {
				LOGGER.warning("Creation of '" + createVMRequest.name + "' failed.");
				// Perhaps vm already existed trying to delete:
				deleteVM(createVMRequest.name);
			}
			Assume.assumeNotNull(vmId);

			ManagementVM testMachine = null;

			List<ManagementVM> machines = vmr.getVMs(userID, null);
			for (ManagementVM iMachine : machines) {
				if (iMachine.id.equals(vmId)) {
					testMachine = iMachine;
					break;
				}
			}
			Assert.assertNotNull("created vm not found", testMachine);
			Assert.assertTrue("machine count not increased after createVirtualMachine()",
					machines.size() == machineCount + 1);

			// Deleting test machine:
			vmr.removeVirtualMachine(userID, testMachine.id);

			boolean machineDeleted = true;
			machines = vmr.getVMs(userID, null);
			Assert.assertTrue("vm count not decreased after removeVirtualMachine()", machines.size() == machineCount);
			for (ManagementVM iMachine : machines) {
				if (iMachine.id == vmId) {
					machineDeleted = false;
					break;
				}
			}
			Assert.assertTrue("VM was not deleted", machineDeleted);

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

		vmr.removeVirtualMachine(userID, testMachine.id);

		boolean machineDeleted = true;
		machines = vmr.getVMs(userID, null);
		Assert.assertTrue("getVMs().size dir not decrease after removeVirtualMachine()",
				machines.size() == machineCount - 1);
		for (ManagementVM iMachine : machines) {
			if (iMachine.id == testMachine.id) {
				machineDeleted = false;
				break;
			}
		}
		Assert.assertTrue("VM was not deleted", machineDeleted);
	}
}
