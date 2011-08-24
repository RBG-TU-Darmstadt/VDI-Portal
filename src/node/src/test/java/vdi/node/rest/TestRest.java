package vdi.node.rest;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import vdi.commons.node.interfaces.NodeVMService;

public class TestRest {
	@Test
	public void TestRestVM() {
		try {
			NodeVMService nodeVMService = ProxyFactory.create(NodeVMService.class,
					"http://localhost:8080/NodeController/vm/");

			Assert.assertNotNull("No rest: http://localhost:8080/NodeController/vm", nodeVMService);
			Assert.assertFalse("NodeVMService: Empty VM Types entry", nodeVMService.getVMTypes().isEmpty());
		} catch (ClientResponseFailure e) {
			Assume.assumeNoException(e);
		}
	}
}
