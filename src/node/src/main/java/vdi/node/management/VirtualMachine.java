package vdi.node.management;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.mozilla.interfaces.MediumVariant;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.AuthType;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IHost;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IVRDEServer;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import vdi.commons.common.Configuration;
import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.node.exception.DuplicateMachineNameException;
import vdi.node.exception.MachineNotFoundException;

/**
 * Class for interaction with the VirtualBox API.
 */
public class VirtualMachine {

	private static VirtualBoxManager manager;
	private static IVirtualBox virtualBox;

	private static final Logger LOGGER = Logger.getLogger(VirtualMachine.class.getName());

	private IMachine machine = null;

	/**
	 * Establishes a connection to the VirtualBox instance
	 */
	static {
		if (System.getProperty("vbox.home") == null) {
			System.setProperty("vbox.home", Configuration.getProperty("vbox.home"));
		}

		manager = VirtualBoxManager.createInstance(null);
		virtualBox = manager.getVBox();

		LOGGER.info("Connected to VirtualBox " + virtualBox.getVersion());
	}

	/**
	 * @return VirtualBox Host Object
	 */
	public static synchronized IHost getHostInformation() {
		return virtualBox.getHost();
	}

	/**
	 * Returns a list of all virtual machines managed by this virtual box
	 * instance.
	 * 
	 * @return a list of machines
	 */
	public static synchronized List<IMachine> getAllMachines() {
		return virtualBox.getMachines();
	}

	/**
	 * Returns a map of guest OS types.
	 * 
	 * @return Map<Family_ID, Map<OS_Type_ID, OS_Type_Description>>
	 */
	public static synchronized HashMap<String, HashMap<String, String>> getGuestOsTypes() {
		HashMap<String, HashMap<String, String>> osTypes = new HashMap<String, HashMap<String, String>>();

		for (IGuestOSType type : virtualBox.getGuestOSTypes()) {
			if (!osTypes.containsKey(type.getFamilyId())) {
				osTypes.put(type.getFamilyId(), new HashMap<String, String>());
			}
			osTypes.get(type.getFamilyId()).put(type.getId(), type.getDescription());
		}

		return osTypes;
	}

	/**
	 * Searches for a virtual machine by ID.
	 * 
	 * @param machineId
	 *            the machine's ID
	 * @throws MachineNotFoundException
	 *             thrown if no machine with this ID could be found
	 */
	public VirtualMachine(String machineId) throws MachineNotFoundException {
		try {
			machine = virtualBox.findMachine(machineId);
		} catch (VBoxException e) {
			LOGGER.fine(e.getMessage());
			LOGGER.finest(ExceptionUtils.getFullStackTrace(e));
		}

		if (machine == null) {
			LOGGER.warning("Could not find virtual machine with ID " + machineId);
			throw new MachineNotFoundException(machineId);
		}
	}

	/**
	 * Creates a new virtual machine.
	 * 
	 * @param name
	 *            the name of the machine
	 * @param osTypeId
	 *            the OS-Type identifier (not the family id!), @see
	 *            VMController#getGuestOsTypes()
	 * @param description
	 *            a description for the machine
	 * @param memorySize
	 *            the RAM size in MB
	 * @param hddSize
	 *            the HDD size in MB
	 * @param accelerate2d
	 *            2D-acceleration
	 * @param accelerate3d
	 *            3D-acceleration
	 * @param vramSize
	 *            Video-RAM size in MB
	 * @throws DuplicateMachineNameException
	 *             Indicates, that a machine with the given name already exists.
	 */
	public VirtualMachine(String name, String osTypeId, String description, Long memorySize, Long hddSize,
			boolean accelerate2d, boolean accelerate3d, long vramSize) throws DuplicateMachineNameException {
		createVirtualMachine(name, osTypeId, description, memorySize, accelerate2d, accelerate3d, vramSize);

		if (Configuration.getProperty("node.vdifolder") != null && ! Configuration.getProperty("node.vdifolder").isEmpty()) {
			createHdd(hddSize, Configuration.getProperty("node.vdifolder") + "/" + name + ".vdi");
		} else {
			createHdd(hddSize, getPath() + "hdd0.vdi");
		}
		
	}

	/**
	 * Creates a new virtual machine.
	 * 
	 * @param name
	 *            the name of the machine
	 * @param osTypeId
	 *            the OS-Type identifier (not the family id!), @see
	 *            VMController#getGuestOsTypes()
	 * @param description
	 *            a description for the machine
	 * @param memorySize
	 *            the RAM size in MB
	 * @param accelerate2d
	 *            2D-acceleration
	 * @param accelerate3d
	 *            3D-acceleration
	 * @param vramSize
	 *            Video-RAM size in MB
	 * @param hddFile
	 *            Path and Filename for the VDI-Image
	 * @throws DuplicateMachineNameException
	 *             Indicates, that a machine with the given name already exists.
	 */
	public VirtualMachine(String name, String osTypeId, String description, Long memorySize, boolean accelerate2d,
			boolean accelerate3d, long vramSize, String hddFile) throws DuplicateMachineNameException {
		createVirtualMachine(name, osTypeId, description, memorySize, accelerate2d, accelerate3d, vramSize);

		attachHdd(hddFile);
	}

	/**
	 * Creates a new virtual machine.
	 * 
	 * @param name
	 *            the name of the machine
	 * @param osTypeId
	 *            the OS-Type identifier (not the family id!), @see
	 *            VMController#getGuestOsTypes()
	 * @param description
	 *            a description for the machine
	 * @param memorySize
	 *            the RAM size in MB
	 * @param accelerate2d
	 *            2D-acceleration
	 * @param accelerate3d
	 *            3D-acceleration
	 * @param vramSize
	 *            Video-RAM size in MB
	 * @throws DuplicateMachineNameException
	 *             Indicates, that a machine with the given name already exists.
	 */
	private void createVirtualMachine(String name, String osTypeId, String description, Long memorySize,
			boolean accelerate2d, boolean accelerate3d, long vramSize) throws DuplicateMachineNameException {
		IMachine newMachine = null;
		try {
			newMachine = virtualBox.createMachine(null, name, osTypeId, null, false);
		} catch (VBoxException e) {
			if (e.getMessage().contains("Guest OS type")) {
				throw new InvalidParameterException("Invalid OS type '" + osTypeId + "'.");
			} else if (e.getMessage().contains("Argument aName is empty or NULL")) {
				throw new InvalidParameterException("Machine name cannot be empty or NULL.");
			} else if (e.getMessage().contains("already exists")) {
				throw new DuplicateMachineNameException(name);
			} else {
				throw e;
			}
		}

		newMachine.setDescription(description);
		newMachine.setMemorySize(memorySize);

		// Storage controllers
		newMachine.addStorageController("ide", StorageBus.IDE);

		// hardware acceleration
		newMachine.setAccelerate2DVideoEnabled(accelerate2d);
		newMachine.setAccelerate3DEnabled(accelerate3d);
		newMachine.setVRAMSize(vramSize);

		newMachine.saveSettings();
		virtualBox.registerMachine(newMachine);

		machine = newMachine;

		ISession session = manager.getSessionObject();
		newMachine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();

		// Attach CD/DVD drive
		mutable.attachDevice("ide", 1, 0, DeviceType.DVD, null);

		// Enable RDP
		if (!mutable.getVRDEServer().getEnabled()) {
			IVRDEServer rde = mutable.getVRDEServer();
			rde.setEnabled(true);
			rde.setAllowMultiConnection(false);
			rde.setAuthType(AuthType.Null);
			rde.setAuthTimeout(5000L);
		}

		mutable.saveSettings();
		session.unlockMachine();

		LOGGER.info("Created virtual machine with ID " + newMachine.getId());
	}

	private void attachHdd(String pathAndFilename) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();

		// Attach HDD
		IMedium hdd = virtualBox.openMedium(pathAndFilename, DeviceType.HardDisk, AccessMode.ReadWrite, true);
		mutable.attachDevice("ide", 0, 0, DeviceType.HardDisk, hdd);

		mutable.saveSettings();
		session.unlockMachine();
	}

	/**
	 * Creates and attachs a virtual harddisk.
	 * 
	 * @param size
	 *            the size in MB
	 * @param pathAndFilename
	 *            the path for the vdi-file + it's filename (e.g. "hdd0.vdi")
	 */
	private void createHdd(long size, String pathAndFilename) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();

		// Attach HDD
		IMedium hdd = virtualBox.createHardDisk("vdi", pathAndFilename);
		IProgress createHdd = hdd.createBaseStorage(size * 1024 * 1024, MediumVariant.Standard);
		createHdd.waitForCompletion(10000);
		mutable.attachDevice("ide", 0, 0, DeviceType.HardDisk, hdd);

		mutable.saveSettings();
		session.unlockMachine();
	}

	/**
	 * Clean up the connection to VirtualBox.
	 */
	public static void cleanup() {
		manager.cleanup();

		LOGGER.info("Cleaned up VirtualBox");
	}

	/**
	 * Deletes the virtual machine.
	 * 
	 * @param deleteHdd
	 *            true to delete vm's attached vhd.
	 */
	public synchronized void delete(boolean deleteHdd) {
		LOGGER.info("Delete virtual machine with ID " + this.getId());
		if (deleteHdd) {
			machine.delete(machine.unregister(CleanupMode.DetachAllReturnHardDisksOnly));
		} else {
			machine.delete(machine.unregister(CleanupMode.DetachAllReturnNone));
		}
	}

	/**
	 * Returns the virtual machine's ID.
	 * 
	 * @return the ID
	 */
	public synchronized String getId() {
		return machine.getId();
	}

	/**
	 * Returns the virtual machine's name.
	 * 
	 * @return the name
	 */
	public synchronized String getName() {
		return machine.getName();
	}

	/**
	 * Retrieve the currently mounted ISO image's name.
	 * 
	 * @return the ISO path or null, if no image is mounted
	 */
	public synchronized String getMountedMediumLocation() {
		IMedium medium = getMountedMedium();

		if (medium == null) {
			LOGGER.warning("No medium mounted -- could not return medium location.");
			return null;
		} else {
			return medium.getLocation();
		}
	}

	/**
	 * Retrieve the currently mounted ISO image.
	 * 
	 * @return the medium
	 */
	public synchronized IMedium getMountedMedium() {
		return machine.getMedium("ide", 1, 0);
	}

	/**
	 * Retrieve the virtual hard disk medium.
	 * 
	 * @return the vhd medium. No error handling.
	 */
	public synchronized IMedium getHarddiskMedium() {
		return machine.getMedium("ide", 0, 0);
	}

	/**
	 * Returns the virtual machine's OS type.
	 * 
	 * @return the OS type's ID
	 */
	public synchronized String getOSTypeId() {
		return machine.getOSTypeId();
	}

	/**
	 * Returns the virtual machine's path by taking the log-path's parent
	 * directory.
	 * 
	 * @return the virtual machine's path
	 */
	private synchronized String getPath() {
		return machine.getLogFolder().substring(0, machine.getLogFolder().lastIndexOf('/') + 1);
	}

	/**
	 * Returns the virtual machine's state.
	 * 
	 * @return the state
	 */
	public synchronized VirtualMachineStatus getState() {
		return getState(machine);
	}

	/**
	 * Returns the given virtual machine's state.
	 * 
	 * @param machine
	 *            the machine which state should be returned
	 * @return the state
	 */
	public static synchronized VirtualMachineStatus getState(IMachine machine) {
		switch (machine.getState()) {
		case Running:
			return VirtualMachineStatus.STARTED;
		case PoweredOff:
			return VirtualMachineStatus.STOPPED;
		case Paused:
			return VirtualMachineStatus.PAUSED;
		default:
			LOGGER.warning("Unmapped machine state: " + machine.getState());
			return null;
		}
	}

	/**
	 * Takes a screenshot from a running virtual machine.
	 * 
	 * @param width
	 *            the screenshot width
	 * @param height
	 *            the screenshot height
	 * 
	 * @return the screenshot
	 */
	public synchronized byte[] getThumbnail(int width, int height) {
		ISession session = manager.getSessionObject();
		byte[] screenshot = null;

		try {
			machine.lockMachine(session, LockType.Shared);
			screenshot = session.getConsole().getDisplay().takeScreenShotPNGToArray(0L, (long) width, (long) height);
		} catch (VBoxException e) {
			LOGGER.warning("Could not get a virtual machine thumbnail.");
			return null;
		} finally {
			session.unlockMachine();
		}

		return screenshot;
	}

	/**
	 * Launches the virtual machine and starts the RDE server.
	 * 
	 * @return null, if the launch failed, or an object with IP and port for the
	 *         RDE-connection
	 */
	public synchronized Socket launch() {
		ISession session1 = manager.getSessionObject();
		machine.lockMachine(session1, LockType.Write);
		IMachine mutable = session1.getMachine();

		IVRDEServer rde = mutable.getVRDEServer();
		rde.setVRDEProperty("TCP/Ports", SystemInformation.getPort().toString());

		mutable.saveSettings();
		session1.unlockMachine();

		ISession session2 = manager.getSessionObject();
		IProgress progress = machine.launchVMProcess(session2, "headless", "");

		progress.waitForCompletion(5000);
		session2.unlockMachine();

		if (progress.getResultCode() != 0) {
			LOGGER.severe("Couldn't launch VM " + machine.getName());

			return null;
		} else {
			String port = machine.getVRDEServer().getVRDEProperty("TCP/Ports");
			LOGGER.info("Started VM " + machine.getName() + " on Port " + port);

			return new Socket(SystemInformation.getNodeEndpoint(), port);
		}
	}

	/**
	 * Mount an ISO image as a DVD device.
	 * 
	 * @param isoPath
	 *            the ISO image's full path
	 */
	public synchronized void mountIso(String isoPath) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);
		IMachine mutable = session.getMachine();

		IMedium medium = virtualBox.openMedium(isoPath, DeviceType.DVD, AccessMode.ReadOnly, true);

		mutable.mountMedium("ide", 1, 0, medium, true);

		mutable.saveSettings();
		session.unlockMachine();
	}

	/**
	 * Pauses the running virtual machine.
	 */
	public synchronized void pause() {
		ISession session = manager.getSessionObject();

		machine.lockMachine(session, LockType.Shared);
		session.getConsole().pause();

		session.unlockMachine();
	}

	/**
	 * Resumes a paused virtual machine.
	 * 
	 * @return object with IP and port for the RDE-connection
	 */
	public synchronized Socket resume() {
		ISession session = manager.getSessionObject();

		machine.lockMachine(session, LockType.Shared);
		session.getConsole().resume();

		session.unlockMachine();

		String port = machine.getVRDEServer().getVRDEProperty("TCP/Ports");

		return new Socket(SystemInformation.getNodeEndpoint(), port);
	}

	/**
	 * Stops the VM.
	 */
	public synchronized void stop() {
		ISession session = manager.getSessionObject();

		machine.lockMachine(session, LockType.Shared);
		IProgress progress = session.getConsole().powerDown();
		progress.waitForCompletion(15000);

		if (progress.getResultCode() != 0) {
			LOGGER.warning("Machine shutdown timed out." + machine.getName());
		}

		session.unlockMachine();
	}

	/**
	 * Unmount an ISO image.
	 */
	public synchronized void unmountIso() {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);
		IMachine mutable = session.getMachine();

		mutable.mountMedium("ide", 1, 0, null, true);

		mutable.saveSettings();
		session.unlockMachine();
	}

	/**
	 * Updates the machines name.
	 * 
	 * @param name
	 *            the new name
	 */
	public synchronized void setName(String name) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setName(name);
		mutable.saveSettings();

		session.unlockMachine();
	}

	/**
	 * Updates the machines description.
	 * 
	 * @param description
	 *            the new description
	 */
	public synchronized void setDescription(String description) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setDescription(description);
		mutable.saveSettings();

		session.unlockMachine();
	}

	/**
	 * Updates the machines memory size.
	 * 
	 * @param memorySize
	 *            the new memory size
	 */
	public synchronized void setMemorySize(Long memorySize) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setMemorySize(memorySize);
		mutable.saveSettings();

		session.unlockMachine();
	}

	/**
	 * Updates the machines VRAM size.
	 * 
	 * @param vramSize
	 *            the new VRAM size
	 */
	public synchronized void setVramSize(Long vramSize) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setVRAMSize(vramSize);
		mutable.saveSettings();

		session.unlockMachine();
	}

	/**
	 * Update the machines 2D acceleration.
	 * 
	 * @param accelerate2d
	 *            true if the 2D acceleration should be enabled, false if not
	 */
	public synchronized void setAccelerate2d(boolean accelerate2d) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setAccelerate2DVideoEnabled(accelerate2d);
		mutable.saveSettings();

		session.unlockMachine();
	}

	/**
	 * Update the machines 3D acceleration.
	 * 
	 * @param accelerate3d
	 *            true if the 3D acceleration should be enabled, false if not
	 */
	public synchronized void setAccelerate3d(boolean accelerate3d) {
		ISession session = manager.getSessionObject();
		machine.lockMachine(session, LockType.Shared);

		IMachine mutable = session.getMachine();

		mutable.setAccelerate3DEnabled(accelerate3d);
		mutable.saveSettings();

		session.unlockMachine();
	}

}
