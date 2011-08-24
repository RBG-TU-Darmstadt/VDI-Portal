package vdi.node.management;

import java.security.InvalidParameterException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualbox_4_1.IMachine;

import vdi.node.exception.DuplicateMachineNameException;
import vdi.node.exception.MachineNotFoundException;
import vdi.commons.common.Configuration;
import vdi.node.management.VirtualMachine;

public class TestVirtualMachine {
	boolean init = false;
	String machine_name = "Unit_Test_VM";

	public void skipOnInitFailure() {
		Assume.assumeTrue(init);
	}

	public VirtualMachine getVMByName(String name) {
		List<IMachine> machines = VirtualMachine.getAllMachines();
		if (machines != null) {
			for (IMachine machine : machines) {
				if (machine.getName().equals(name)) {
					try {
						return new VirtualMachine(machine.getId());
					} catch (MachineNotFoundException e) {
						throw (AssertionError) new AssertionError("VirtualMachine did not find an existing machine!")
								.initCause(e);
					}
				}
			}
		}
		return null;
	}

	@BeforeClass
	public static void setupAll() {
		Configuration.setProperty("vbox.home", "");
	}

	@AfterClass
	public static void cleanupAll() {
		VirtualMachine.cleanup();
	}

	@Before
	public void setup() {
		// Suche Test VM:
		try {
			VirtualMachine.getAllMachines();
			init = true;

			VirtualMachine vm = getVMByName(machine_name);
			if (vm != null) {
				System.out.println("Warning: found '" + machine_name + "'. Trying to delete...");
				vm.delete();
				Assert.assertNull("Deletion of '" + machine_name + "' failed!", getVMByName(machine_name));
				System.out.println("'" + machine_name + "' deleted.");
			}
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
	}

	@After
	public void cleanup() {
		// trying to delete machine if something went wrong
		try {
			getVMByName(machine_name).delete();
		} catch (Throwable e) {
		}
	}

	@Test
	public void init() {
		Assert.assertTrue("Initialization failed", init);
	}

	@Test(expected = InvalidParameterException.class)
	public void createMachineWithInvalidOsType() throws InvalidParameterException, DuplicateMachineNameException {
		skipOnInitFailure();

		@SuppressWarnings("unused")
		VirtualMachine machine = new VirtualMachine("test", "foobar", "", 512L, 3L, false, false, 32L);
	}

	@Test(expected = InvalidParameterException.class)
	public void createMachineWithNoName() throws InvalidParameterException, DuplicateMachineNameException {
		skipOnInitFailure();

		@SuppressWarnings("unused")
		VirtualMachine machine = new VirtualMachine("", "Linux26", "", 512L, 3L, false, false, 32L);
	}

	@Test
	public void createDeleteVm() {
		skipOnInitFailure();
		createVM(machine_name);
		deleteVm(machine_name);
	}

	private void createVM(String name) {
		Assert.assertNotNull(VirtualMachine.getGuestOsTypes());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().isEmpty());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().isEmpty());
		String osTypeID = VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().iterator().next();

		Assert.assertNotNull(VirtualMachine.getAllMachines());
		int machineCount = VirtualMachine.getAllMachines().size();

		try {
			VirtualMachine vm = new VirtualMachine(name, osTypeID, "testing create delete vm", 128L, 512L, false,
					false, 32L);

			Assert.assertTrue("getMachines().size() not increased by one after createMachine()", VirtualMachine
					.getAllMachines().size() == machineCount + 1);

			try {
				VirtualMachine vmTest = new VirtualMachine(vm.getId());
				Assert.assertEquals("Created VM is not equal found vm", vm.getId(), vmTest.getId());
			} catch (MachineNotFoundException e) {
				throw (AssertionFailedError) new AssertionFailedError("created vm was not found").initCause(e);
			}

			try {
				new VirtualMachine(name, osTypeID, "testing create delete vm", 128L, 512L, false, false, 32L);
				Assert.assertTrue("No exception with creation of same machine name", true);
			} catch (DuplicateMachineNameException e) {
			}
		} catch (Exception e) {
			throw (AssertionFailedError) new AssertionFailedError("Cought unexpected exception:" + e.getMessage())
					.initCause(e);
		}
	}

	private void deleteVm(String name) {
		VirtualMachine vm = getVMByName(name);

		Assert.assertNotNull("Machine '" + name + "' not found", vm);

		vm.delete();

		vm = getVMByName(name);
		Assert.assertNull("Machine '" + name + "' found after delete()", vm);
	}
}
