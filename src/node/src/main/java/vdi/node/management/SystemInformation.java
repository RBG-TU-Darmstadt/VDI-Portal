package vdi.node.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.MBeanServerConnection;

import vdi.commons.common.Configuration;

/**
 * Class for retrieving system information.
 */
public final class SystemInformation {

	private static final Logger LOGGER = Logger.getLogger(SystemInformation.class.getName());

	private static final Integer PORT = 5000;

	/**
	 * Memory page size used by 'vm_stat'.
	 */
	private static final int PAGE_SIZE = 4096;

	private static Set<Integer> ports = new HashSet<Integer>();

	/**
	 * @return a free port
	 */
	public static synchronized Integer getPort() {
		Integer port = PORT;
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
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			return getFreeMemoryFromVmStat();
		} else {
			return VirtualMachine.getHostInformation().getMemoryAvailable();
		}
	}

	private static long getFreeMemoryFromVmStat() {
		try {
			Process proc = Runtime.getRuntime().exec("vm_stat");
			InputStream inStr = proc.getInputStream();
			BufferedReader buff = new BufferedReader(new InputStreamReader(inStr));

			String str;
			Long freePages = 0L;
			while ((str = buff.readLine()) != null) {
				Pattern pattern = Pattern.compile("(Pages free|Pages inactive|Pages speculative): +(\\d+).");
				Matcher matcher = pattern.matcher(str);

				while (matcher.find()) {
					freePages += Integer.valueOf(matcher.group(2));
				}
			}

			return freePages * PAGE_SIZE / 1024 / 1024;
		} catch (IOException e) {
			return -1;
		}
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
		return file.getTotalSpace() / 1024 / 1024;
	}

	/**
	 * @return the free disk space in MB
	 */
	public static long getFreeDiskSpace() {
		File file = new File("/");
		return file.getFreeSpace() / 1024 / 1024;
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

	/**
	 * Utility class must not be instantiated.
	 */
	private SystemInformation() { }
}
