package vdi.node;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.Configuration;
import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.commons.node.interfaces.NodeResourceService;
import vdi.commons.node.interfaces.NodeVMService;
import vdi.commons.node.objects.NodeCreateVMRequest;
import vdi.commons.node.objects.NodeCreateVMResponse;
import vdi.commons.node.objects.NodeGetResourcesResponse;
import vdi.commons.node.objects.NodeUpdateVMRequest;
import vdi.commons.node.objects.NodeUpdateVMResponse;

public class StressTest {
	public static String baseURI = null;
	public static String vhdFileName = "vhd"; // + threadNum + ".vdi"

	public static int userCount = 5;
	public static String csvToken = ",";

	private static long testDuration = 10 * 60 * 1000;

	// between
	private static int waitBetweenUsers = 500;
	private static int waitBeforeCreate = 10 * 1000;
	private static int waitBeforeStart = 60 * 1000;
	private static int waitBeforeStop = 5 * 60 * 1000;
	private static int waitBeforeRemove = 2 * 60 * 1000;

	private static int performanceCheckInterval = 60 * 1000;

	private static int usersRunning = 0;
	private static int vmsCreated = 0;
	private static int vmsRunning = 0;

	public static synchronized void userStoped() {
		usersRunning -= 1;
	}

	public static synchronized int getUsersRunning() {
		return usersRunning;
	}

	public static void vmCreated() {
		vmsCreated += 1;
	}

	public static void vmRemoved() {
		vmsCreated -= 1;
	}

	public static void vmStarted() {
		vmsRunning += 1;
	}

	public static void vmStopped() {
		vmsRunning -= 1;
	}

	public static synchronized int getVmsRunning() {
		return vmsRunning;
	}

	public static synchronized int getVmsCreated() {
		return vmsCreated;
	}

	private static DateFormat dateFormat = DateFormat.getDateTimeInstance();

	public static String formatTime(Date time) {
		String r = dateFormat.format(time);
		r += "." + time.getTime() % 1000;

		return r;
	}

	public static String getHMS(long time) {
		String r = new String();
		long h = time / (60 * 60 * 1000);
		long m = (time % (60 * 60 * 1000)) / (60 * 1000);
		double s = (time % (60 * 1000)) / 1000.0;
		if (h > 0)
			r = h + " h " + m + " min " + s + " sec";
		else if (m > 0)
			r = m + " min " + s + " sec";
		else
			r = s + " sec";
		return r;
	}

	public static Date testStartTime;

	public static synchronized void logUserAction(int userNum, String action, Date start, Date stop, String status) {
		long duration = stop.getTime() - start.getTime();

		String tupel = (start.getTime() - testStartTime.getTime()) + csvToken + userNum + csvToken + action
				+ csvToken + formatTime(start) + csvToken + formatTime(stop) + csvToken + duration + csvToken
				+ status + "\n";

		System.out.print(tupel);

		try {
			FileWriter fw = new FileWriter("userActions.csv", true);
			fw.write(tupel);
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String configFile = "configuration.properties";
		if (args.length > 0)
			configFile = args[0];

		// Load configuration
		try {
			InputStream is = new FileInputStream(configFile);
			Configuration.loadProperties(is);
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Setting Values:
		String value;

		value = Configuration.getProperty("baseURI");
		if (value != null)
			baseURI = value;
		System.out.println("baseURI = " + baseURI);

		value = Configuration.getProperty("vm.vhdFileName");
		if (value != null)
			vhdFileName = value;
		System.out.println("vm.vhdFileName = " + vhdFileName);
		System.out.println("User 0 vhd file: " + vhdFileName + "0.vdi");

		value = Configuration.getProperty("wait.betweenUsers");
		if (value != null)
			waitBetweenUsers = Integer.parseInt(value);
		System.out.println("wait.betweenUsers = " + waitBetweenUsers + " (" + getHMS(waitBetweenUsers) + ")");

		value = Configuration.getProperty("wait.beforeCreate");
		if (value != null)
			waitBeforeCreate = Integer.parseInt(value);
		System.out.println("wait.beforeCreate = " + waitBeforeCreate + " (" + getHMS(waitBeforeCreate) + ")");

		value = Configuration.getProperty("wait.beforeStart");
		if (value != null)
			waitBeforeStart = Integer.parseInt(value);
		System.out.println("wait.beforeStart = " + waitBeforeStart + " (" + getHMS(waitBeforeStart) + ")");

		value = Configuration.getProperty("wait.beforeStop");
		if (value != null)
			waitBeforeStop = Integer.parseInt(value);
		System.out.println("wait.beforeStop = " + waitBeforeStop + " (" + getHMS(waitBeforeStop) + ")");

		value = Configuration.getProperty("wait.beforeRemove");
		if (value != null)
			waitBeforeRemove = Integer.parseInt(value);
		System.out.println("wait.beforeRemove = " + waitBeforeRemove + " (" + getHMS(waitBeforeRemove) + ")");

		value = Configuration.getProperty("performance.checkInterval");
		if (value != null)
			performanceCheckInterval = Integer.parseInt(value);
		System.out.println("performance.checkInterval = " + performanceCheckInterval + " ("
				+ getHMS(performanceCheckInterval) + ")");

		value = Configuration.getProperty("test.duration");
		if (value != null)
			testDuration = Long.parseLong(value);
		System.out.println("test.duration = " + testDuration + " (" + getHMS(testDuration) + ")");

		value = Configuration.getProperty("test.users");
		if (value != null)
			userCount = Integer.parseInt(value);
		System.out.println("test.users = " + userCount);

		NodeCreateVMRequest vm = new NodeCreateVMRequest();
		vm.name = "stresstest_vm_";
		vm.osTypeId = "DOS";
		vm.description = "";
		vm.hddFile = StressTest.vhdFileName;
		vm.memorySize = 32;
		vm.vramSize = 8;
		vm.accelerate2d = true;
		vm.accelerate3d = false;

		value = Configuration.getProperty("vm.name");
		if (value != null)
			vm.name = value;
		System.out.println("vm.name = " + vm.name);

		value = Configuration.getProperty("vm.osTypeId");
		if (value != null)
			vm.osTypeId = value;
		System.out.println("vm.osTypeId = " + vm.osTypeId);

		value = Configuration.getProperty("vm.description");
		if (value != null)
			vm.description = value;
		System.out.println("vm.description = " + vm.description);

		value = Configuration.getProperty("vm.memorySize");
		if (value != null)
			vm.memorySize = Long.parseLong(value);
		System.out.println("vm.memorySize = " + vm.memorySize);

		value = Configuration.getProperty("vm.accelerate2d");
		if (value != null)
			vm.accelerate2d = Boolean.parseBoolean(value);
		System.out.println("vm.accelerate2d = " + vm.accelerate2d);

		value = Configuration.getProperty("vm.accelerate3d");
		if (value != null)
			vm.accelerate3d = Boolean.parseBoolean(value);
		System.out.println("vm.accelerate3d = " + vm.accelerate3d);

		// create userActions.csv
		try {
			String header = "time" + csvToken + "user" + csvToken + "action" + csvToken + "start" + csvToken + "stop"
					+ csvToken + "duration" + csvToken + "status" + "\n";

			System.out.print("\n" + header);

			FileWriter fw = new FileWriter("userActions.csv", false);
			fw.write(header);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// create userActions.csv
		try {
			String header = "time" + csvToken + "date" + csvToken + "cpuLoad" + csvToken + "freeMemory" + csvToken
					+ "freeHDD" + csvToken + "created" + csvToken + "running" + "\n";

			System.out.print(header);

			FileWriter fw = new FileWriter("performance.csv", false);
			fw.write(header);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		testStartTime = new Date();

		usersRunning = userCount;

		// Start performance monitoring
		Timer timer = new Timer();
        timer.scheduleAtFixedRate(new PerformanceCheckThread(testStartTime), testStartTime, performanceCheckInterval);

		ArrayList<TestVMThread> users = new ArrayList<TestVMThread>();
		for (int i = 0; i < userCount; ++i) {
			TestVMThread thread = new TestVMThread(i, waitBeforeCreate, waitBeforeStart, waitBeforeStop,
					waitBeforeRemove, vm);
			users.add(thread);
			thread.start();
			try {
				Thread.sleep(waitBetweenUsers);
			} catch (InterruptedException e) {
			}
		}

		// Wait till test time has elapsed
		do {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}

			if (getUsersRunning() <= 0) {
				System.err.println("all users failed");
				break;
			}
		} while ((new Date().getTime() - testStartTime.getTime()) < testDuration);

		for (TestVMThread testVMThread : users) {
			testVMThread.endThread();
		}

		System.out.println("Waiting for user threads to finish.");

		// wait for all Threads to finish:
		while (getUsersRunning() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}

		// Stop performance monitoring
		try {
			Thread.sleep(performanceCheckInterval);
		} catch (InterruptedException e) {}

		timer.cancel();

		System.out.println("Done.");
	}

	public static void setUp() {
	}

};

class PerformanceCheckThread extends TimerTask {

	Date start;
	NodeResourceService nodeResourceService;

	public PerformanceCheckThread(Date start) {
		this.start = start;
		nodeResourceService = ProxyFactory.create(NodeResourceService.class,
				StressTest.baseURI + "/resources/");
	}

	@Override
	public void run() {
		Date curTime = new Date();
		long duration = curTime.getTime() - start.getTime();

		NodeGetResourcesResponse resources = nodeResourceService.getResources();
		logPerformanceData(curTime, duration, resources.cpuLoad, resources.freeMemorySize,
				resources.freeDiskSpace);
	}

	public static synchronized void logPerformanceData(Date curTime, long duration, double cpuLoad,
			long freeMemorySize, long freeDiskSpace) {
		String performanceStr = duration + StressTest.csvToken + StressTest.formatTime(curTime) + StressTest.csvToken + cpuLoad + StressTest.csvToken
				+ freeMemorySize + StressTest.csvToken + freeDiskSpace + StressTest.csvToken + StressTest.getVmsCreated() + StressTest.csvToken + StressTest.getVmsRunning()
				+ "\n";

		System.out.print(performanceStr);

		try {
			FileWriter fw = new FileWriter("performance.csv", true);
			fw.write(performanceStr);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class TestVMThread extends Thread {
	private int threadNum;
	private String machineID;
	private boolean _running = true;

	private int waitBeforeCreate;
	private int waitBeforeStart;
	private int waitBeforeStop;
	private int waitBeforeRemove;

	private NodeVMService vmService;
	private NodeCreateVMRequest vm;

	private Date threadStartTime;

	public TestVMThread(int threadNum, int waitBeforeCreate, int waitBeforeStart, int waitBeforeStop,
			int waitBeforeRemove, NodeCreateVMRequest vm) {
		this.threadNum = threadNum;
		this.waitBeforeCreate = waitBeforeCreate;
		this.waitBeforeRemove = waitBeforeRemove;
		this.waitBeforeStart = waitBeforeStart;
		this.waitBeforeStop = waitBeforeStop;

		this.vm = new NodeCreateVMRequest();

		this.vm.name = vm.name + threadNum;
		this.vm.hddFile = vm.hddFile + threadNum + ".vdi";

		this.vm.accelerate2d = vm.accelerate2d;
		this.vm.accelerate3d = vm.accelerate3d;
		this.vm.description = vm.description;
		this.vm.memorySize = vm.memorySize;
		this.vm.osTypeId = vm.osTypeId;
		this.vm.vramSize = vm.vramSize;

		vmService = ProxyFactory.create(NodeVMService.class, StressTest.baseURI + "/vm/");
	}

	public synchronized void endThread() {
		_running = false;
	}

	private synchronized boolean running() {
		return _running;
	}

	private String action = null;
	private Date actionStartTime;

	private void startUserAction(String newAction) {
		if (action != null) {
			throw new RuntimeException("endUserAction() must be called befor new startUserAction().");
		}
		actionStartTime = new Date();
		action = newAction;
	}

	private void endUserAction(String status) {
		if (action == null) {
			throw new RuntimeException("startUserAction() must be called befor endUserAction().");
		}
		StressTest.logUserAction(threadNum, action, actionStartTime, new Date(), status);
		action = null;
	}

	private boolean createVm() {
		startUserAction("createVM");

		try {
			NodeCreateVMResponse response = vmService.createVirtualMachine(vm);
			machineID = response.machineId;
		} catch (ClientResponseFailure f) {
			System.err.println(f.getResponse().getResponseStatus());
			System.err.println(((ClientResponse<?>) f.getResponse()).getEntity(String.class));
			endUserAction("failed");
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			endUserAction("failed");
			return false;
		}
		endUserAction("ok");
		StressTest.vmCreated();
		return true;
	}

	private boolean startVM() {
		startUserAction("startVM");

		NodeUpdateVMRequest vm = new NodeUpdateVMRequest();

		vm.status = VirtualMachineStatus.STARTED;

		try {
			NodeUpdateVMResponse response = vmService.updateVirtualMachine(machineID, vm);
			if (response.rdpUrl != null) {
				endUserAction("ok");
				StressTest.vmStarted();
				return true;
			}
		} catch (ClientResponseFailure f) {
			System.err.println(f.getResponse().getResponseStatus());
			System.err.println(((ClientResponse<?>) f.getResponse()).getEntity(String.class));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		endUserAction("failed");
		return false;
	}

	private boolean stopVM() {
		startUserAction("stopVM");

		NodeUpdateVMRequest vm = new NodeUpdateVMRequest();

		vm.status = VirtualMachineStatus.STOPPED;

		try {
			vmService.updateVirtualMachine(machineID, vm);
		} catch (ClientResponseFailure f) {
			System.err.println(f.getResponse().getResponseStatus());
			System.err.println(((ClientResponse<?>) f.getResponse()).getEntity(String.class));
			endUserAction("failed");
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			endUserAction("failed");
			return false;
		}
		endUserAction("ok");
		StressTest.vmStopped();
		return true;
	}

	private boolean removeVM() {
		startUserAction("removeVM");

		try {
			vmService.removeVirtualMachine(machineID);
		} catch (ClientResponseFailure f) {
			System.err.println(f.getResponse().getResponseStatus());
			System.err.println(((ClientResponse<?>) f.getResponse()).getEntity(String.class));
			endUserAction("failed");
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			endUserAction("failed");
			return false;
		}
		endUserAction("ok");
		StressTest.vmRemoved();
		return true;
	}

	@Override
	public void run() {
		threadStartTime = new Date();

		while (running()) {
			try {
				sleep(waitBeforeCreate);
			} catch (InterruptedException e) {
			}

			if (!createVm()) {
				break;
			}

			try {
				sleep(waitBeforeStart);
			} catch (InterruptedException e) {
			}

			if (startVM()) {
				try {
					sleep(waitBeforeStop);
				} catch (InterruptedException e) {
				}

				if (!stopVM()) {
					removeVM();
					break;
				}
			}

			try {
				sleep(waitBeforeRemove);
			} catch (InterruptedException e) {
			}

			if (!removeVM()) {
				break;
			}
		}
		String status;
		if (running())
			status = "failed";
		else
			status = "finished";

		StressTest.logUserAction(threadNum, "user", threadStartTime, new Date(), status);
		StressTest.userStoped();
	}
}
