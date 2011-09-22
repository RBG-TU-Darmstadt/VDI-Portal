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
import org.virtualbox_4_1.VBoxException;

import vdi.node.exception.DuplicateMachineNameException;
import vdi.node.exception.MachineNotFoundException;
import vdi.commons.common.Configuration;
import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.node.management.VirtualMachine;

public class TestVirtualMachine {
	boolean init = false;
	String vm_name = "Unit_Test_VM";
	VirtualMachine vm = null;

	private void skipOnInitFailure() {
		Assume.assumeTrue(init);
	}

	private VirtualMachine getVMByName(String name) {
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

	private String getOsTypeId() {
		Assert.assertNotNull(VirtualMachine.getGuestOsTypes());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().isEmpty());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().isEmpty());
		String osTypeId = VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().iterator().next();
		Assert.assertNotNull("could not get valid osTypeId", osTypeId);
		return osTypeId;
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

			VirtualMachine vm = getVMByName(vm_name);
			if (vm != null) {
				System.out.println("Warning: found '" + vm_name + "'. Trying to delete...");
				if (vm.getState() != VirtualMachineStatus.STOPPED) {
					vm.stop();
				}
				vm.delete();
				Assert.assertNull("Deletion of '" + vm_name + "' failed!", getVMByName(vm_name));
				System.out.println("'" + vm_name + "' deleted.");
			}
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
	}

	@After
	public void cleanup() {
		// trying to delete machine if something went wrong
		try {
			if (vm != null) {
				if (vm.getState() != VirtualMachineStatus.STOPPED) {
					System.out.println("Stoping test vm.");
					vm.stop();
					Thread.sleep(1000);
				}
				vm.delete();
				vm = null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			vm = getVMByName(vm_name);
			if (vm != null) {
				if (vm.getState() != VirtualMachineStatus.STOPPED)
					vm.stop();
				vm.delete();
				vm = null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
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

	@Test(expected = MachineNotFoundException.class)
	public void searchNonExistingMachine() throws MachineNotFoundException {
		skipOnInitFailure();

		vm = new VirtualMachine("NOT_EXISTING_MACHINE");
	}

	@Test
	public void createDeleteVm() {
		skipOnInitFailure();

		createVM(vm_name);
		deleteVm(vm_name);
	}

	private void createVM(String name) {
		Assert.assertNotNull(VirtualMachine.getGuestOsTypes());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().isEmpty());
		Assert.assertFalse(VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().isEmpty());
		String osTypeId = VirtualMachine.getGuestOsTypes().values().iterator().next().keySet().iterator().next();

		Assert.assertNotNull(VirtualMachine.getAllMachines());
		int machineCount = VirtualMachine.getAllMachines().size();

		try {
			vm = new VirtualMachine(name, osTypeId, "testing create delete vm", 128L, 512L, false, false, 32L);

			Assert.assertTrue("getMachines().size() not increased by one after createMachine()", VirtualMachine
					.getAllMachines().size() == machineCount + 1);

			try {
				VirtualMachine vmTest = new VirtualMachine(vm.getId());
				Assert.assertEquals("Created VM is not equal found vm", vm.getId(), vmTest.getId());
			} catch (MachineNotFoundException e) {
				throw (AssertionFailedError) new AssertionFailedError("created vm was not found").initCause(e);
			}

			try {
				VirtualMachine vm2 = new VirtualMachine(name, osTypeId, "testing create delete vm", 128L, 512L,
						false, false, 32L);
				vm2.delete();
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

	/**
	 * Creates a VM for further testing.
	 * 
	 * @param description
	 *            a short description helping to debug if deleting vm failed
	 * @return Virtual Machine to test VM class
	 */
	private void getTestVM(String description) {
		skipOnInitFailure();

		Assume.assumeTrue(vm == null);

		String osTypeId = null;
		try {
			osTypeId = getOsTypeId();
		} catch (Throwable t) {
			Assume.assumeNoException(t);
		}

		try {
			vm = new VirtualMachine(vm_name, osTypeId, description, 128L, 512L, false, false, 32L);
		} catch (DuplicateMachineNameException e) {
			Assume.assumeNoException(e);
		}

		Assume.assumeNotNull(vm);
	}

	private boolean deleteVM(VirtualMachine vm) {
		try {
			vm.delete();
		} catch (VBoxException e) {
			// catching
			// "Cannot unregister the machine 'Unit_Test_VM' while it is locked"
			// (0x80bb0007)
			return false;
		}
		return true;
	}

	@Test
	public void vmStartPauseResumeTest() {
		getTestVM("vm status test");

		VirtualMachineStatus vmStatus = vm.getState();
		Assert.assertEquals("Expected state 'STOPPED' but '" + vmStatus + "'", VirtualMachineStatus.STOPPED, vmStatus);

		// starting Machine:
		// TODO: ask, why it is called launch instead of start
		Socket s = vm.launch();
		Assert.assertNotNull("starting vm failed", s);

		vmStatus = vm.getState();
		Assert.assertEquals("Expected state 'STARTED' but '" + vmStatus + "'", VirtualMachineStatus.STARTED, vmStatus);

		// pause Machine:
		vm.pause();

		vmStatus = vm.getState();
		Assert.assertEquals("Expected state 'PAUSED' but '" + vmStatus + "'", VirtualMachineStatus.PAUSED, vmStatus);

		// resume Machine:
		s = vm.resume();
		Assert.assertNotNull("resuming paused vm failed", s);

		vmStatus = vm.getState();
		Assert.assertEquals("Expected state 'STARTED' but '" + vmStatus + "'", VirtualMachineStatus.STARTED, vmStatus);

		// stop Machine:
		vm.stop();
		vmStatus = vm.getState();
		Assert.assertEquals("Expected state 'STOPPED' but '" + vmStatus + "'", VirtualMachineStatus.STOPPED, vmStatus);

		// delete machine:
		// TODO: move waiting to delete() method?
		int i;
		int maxcount = 20;
		int millisecs = 500;
		for (i = 0; i < maxcount && !deleteVM(vm); i++) {
			try {
				Thread.sleep(millisecs);
			} catch (InterruptedException e) {
				i--;
			}
		}
		Assert.assertTrue("Could not delete VM after waiting " + maxcount * millisecs / 1000 + " seconds.",
				i < maxcount);

		vm = null;
	}
}
