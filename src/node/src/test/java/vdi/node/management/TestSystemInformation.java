package vdi.node.management;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import vdi.commons.common.Configuration;

public class TestSystemInformation {
	@BeforeClass
	public static void setupClass() {
		Configuration.setProperty("vbox.home", "/");
	}

	@AfterClass
	public static void cleanupClass() {
		try {
			VirtualMachine.cleanup();
		} catch (Throwable t) {
		}
	}

	@Test
	public void testFreeDiskSpace() {
		long freeDiskSpace = SystemInformation.getFreeDiskSpace();
		System.out.println("Free disk space: " + freeDiskSpace + " Bytes ( "
				+ (freeDiskSpace * 100 / (1024 * 1024 * 1024)) / 100.0 + " GB )");
		Assert.assertTrue(freeDiskSpace > 0);
	}

	@Test
	public void testCPULoad() {
		double cpuLoad = SystemInformation.getCpuLoad();
		System.out.println("Avarage cpu load: " + cpuLoad);
		Assert.assertTrue("CPU load not available", cpuLoad >= 0);
	}

	@Test
	public void testRamSize() {
		try {
			long ramSize = SystemInformation.getRamSize();
			System.out.println("Ram size: " + ramSize + " MB");
			Assert.assertTrue(ramSize > 0);
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
			Assume.assumeNoException(e);
		}
	}
}
