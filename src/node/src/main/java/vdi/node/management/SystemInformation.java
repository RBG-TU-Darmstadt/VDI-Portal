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
		return port;
	}

	public static Long getRamSize() {
		return VirtualMachine.getHostInformation().getMemorySize();
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
	 * @return the free disk space in bytes
	 */
	public static long getFreeDiskSpace() {
		File file = new File("/");
		return file.getFreeSpace();
	}

	public static String getNodeEndpoint() {
		return Configuration.getProperty("node.rdp_endpoint");
	}
}
