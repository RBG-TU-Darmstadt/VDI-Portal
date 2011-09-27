package vdi.node.management;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;

import vdi.commons.common.Configuration;

/**
 * Class for retrieving system information.
 */
public class SystemInformation {

	private static final Logger LOGGER = Logger.getLogger(SystemInformation.class.getName());

	private static Set<Integer> ports = new HashSet<Integer>();

	/**
	 * @return a free port
	 */
	public static synchronized Integer getPort() {
		Integer port = 5000;
		while (ports.contains(port)) {
			port++;
		}
		ports.add(port);
		return port;
	}

	/**
	 * @return the ram size in MB
	 */
	public static Long getRamSize() {
		return VirtualMachine.getHostInformation().getMemorySize();
	}

	/**
	 * @return the free ram size in MB
	 */
	public static Long getFreeRamSize() {
		return VirtualMachine.getHostInformation().getMemoryAvailable();
	}

	/**
	 * Returns the system load average for the last minute.
	 * 
	 * @return the CPU load average; negative value if not available
	 */
	public static double getCpuLoad() {
		MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

		OperatingSystemMXBean osMBean;
		try {
			osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
					OperatingSystemMXBean.class);
		} catch (IOException e) {
			LOGGER.warning("Couldn't get CPU load information object.");
			return -1;
		}

		return osMBean.getSystemLoadAverage();
	}

	/**
	 * @return the free disk space in MB
	 */
	public static long getDiskSpace() {
		File file = new File("/");
		return file.getTotalSpace() / 1024;
	}

	/**
	 * @return the free disk space in MB
	 */
	public static long getFreeDiskSpace() {
		File file = new File("/");
		return file.getFreeSpace() / 1024;
	}

	public static String getNodeEndpoint() {
		return Configuration.getProperty("node.rdp_endpoint");
	}

	/**
	 * @return the number of processor cores
	 */
	public static int getCores() {
		return Runtime.getRuntime().availableProcessors();
	}
}
