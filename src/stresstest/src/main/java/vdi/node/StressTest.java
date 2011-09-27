package vdi.node;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	public static String baseURI = "http://xf06-vm4.rbg.informatik.tu-darmstadt.de:8080/NodeController";
	public static String vhdBasePath = "";
	public static String vhdFileName = "vhd"; // + threadNum + ".vhd"

	public static int userCount = 10;
	private static String csvToken = ";";

	private static long testDuration = 10 * 60 * 1000;

	private static int waitBeforeCreate = 10 * 1000;
	private static int waitBeforeStart = 60 * 1000;
	private static int waitBeforeStop = 5 * 60 * 1000;
	private static int waitBeforeRemove = 2 * 60 * 1000;

	private static int performanceCheckInterval = 60 * 1000;

	private static int usersRunning;

	public static synchronized void userStoped() {
		usersRunning -= 1;
	}

	public static synchronized int getUsersRunning() {
		return usersRunning;
	}

	private static DateFormat dateFormat = DateFormat.getDateTimeInstance();

	public static String formatTime(Date time) {
		String r = dateFormat.format(time);
		r += "." + time.getTime() % 1000;

		return r;
	}

	public static synchronized void logUserAction(int userNum, String action, Date start, Date stop, String status) {
		long duration = stop.getTime() - start.getTime();
		String tupel = userNum + csvToken + action + csvToken + formatTime(start) + csvToken + formatTime(stop)
				+ csvToken + duration + csvToken + status + "\n";
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
		// Load configuration
		try {
			InputStream is = new FileInputStream("configuration.properties");
			Configuration.loadProperties(is);
			is.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		// create userActions.csv
		try {
			FileWriter fw = new FileWriter("userActions.csv", false);
			fw.write("user" + csvToken + "action" + csvToken + "start" + csvToken + "stop" + csvToken + "duration"
					+ csvToken + "status" + "\n");
			System.out.println("user" + csvToken + "action" + csvToken + "start" + csvToken + "stop" + csvToken
					+ "duration" + csvToken + "status");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// create userActions.csv
		try {
			FileWriter fw = new FileWriter("performance.csv", false);
			fw.write("time" + csvToken + "cpuLoad" + csvToken + "freeMemory" + csvToken + "freeHDD" + "\n");
			System.out.println("time" + csvToken + "testPercent" + csvToken + "cpuLoad" + csvToken + "freeMemory"
					+ csvToken + "freeHDD");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		usersRunning = userCount;

		ArrayList<TestVMThread> users = new ArrayList<TestVMThread>();
		for (int i = 0; i < userCount; ++i) {
			TestVMThread thread = new TestVMThread(i, waitBeforeCreate, waitBeforeStart, waitBeforeStop,
					waitBeforeRemove);
			users.add(thread);
			thread.start();
		}

		NodeResourceService nodeRes = ProxyFactory.create(NodeResourceService.class, StressTest.baseURI
				+ "/resources/");

		Date testStartTime = new Date();
		Date curTime;
		long duration;
		do {
			curTime = new Date();
			duration = curTime.getTime() - testStartTime.getTime();

			NodeGetResourcesResponse resources = nodeRes.getResources();

			try {
				FileWriter fw = new FileWriter("performance.csv", true);
				fw.write(formatTime(curTime) + csvToken + (duration * 100 / testDuration) + csvToken
						+ resources.cpuLoad + csvToken + resources.freeMemorySize + csvToken
						+ resources.freeDiskSpace + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(formatTime(curTime) + csvToken + (duration * 100 / testDuration) + csvToken
					+ resources.cpuLoad + csvToken + resources.freeMemorySize + csvToken + resources.freeDiskSpace);

			if (getUsersRunning() <= 0) {
				System.err.println("all users failed");
				break;
			}

			try {
				Thread.sleep(performanceCheckInterval);
			} catch (InterruptedException e) {
			}
		} while ((curTime.getTime() - testStartTime.getTime()) < testDuration);

		for (TestVMThread testVMThread : users) {
			testVMThread.endThread();
		}

		// wait for all Threads to finish:
		while (getUsersRunning() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void setUp() {
	}

};

class TestVMThread extends Thread {
	private int threadNum;
	private String machineID;
	private boolean _running = true;

	private int waitBeforeCreate;
	private int waitBeforeStart;
	private int waitBeforeStop;
	private int waitBeforeRemove;

	private NodeVMService vmService;

	private Date threadStartTime;

	public TestVMThread(int threadNum, int waitBeforeCreate, int waitBeforeStart, int waitBeforeStop,
			int waitBeforeRemove) {
		this.threadNum = threadNum;
		this.waitBeforeCreate = waitBeforeCreate;
		this.waitBeforeRemove = waitBeforeRemove;
		this.waitBeforeStart = waitBeforeStart;
		this.waitBeforeStop = waitBeforeStop;

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

		NodeCreateVMRequest vm = new NodeCreateVMRequest();

		vm.name = "stresstest_vm_" + threadNum;
		vm.osTypeId = "DOS";
		vm.description = "";
		vm.hddFile = StressTest.vhdBasePath + "/" + StressTest.vhdFileName + threadNum + ".vhd";
		vm.hddSize = 512;
		vm.memorySize = 32;
		vm.vramSize = 8;
		vm.accelerate2d = true;
		vm.accelerate3d = false;

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
		return true;
	}

	private boolean removeVM(boolean deleteVHD) {
		startUserAction("removeVM");

		try {
			vmService.removeVirtualMachine(machineID, deleteVHD);
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
					removeVM(true);
					break;
				}
			}

			try {
				sleep(waitBeforeRemove);
			} catch (InterruptedException e) {
			}

			if (!removeVM(true)) {
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
