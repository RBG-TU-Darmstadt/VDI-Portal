package vdi.management.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vdi.management.storage.entities.Node;
import vdi.management.storage.entities.VirtualMachine;

public class TestSchedule {

	/**
	 * Name: Testmachine 
	 * Memory: 256 MB 
	 * HDD: 5GB
	 */
	private VirtualMachine vm;
	private List<Node> nodes;

	@Before
	public void init() {
		this.vm = new VirtualMachine();
		this.vm.setMachineName("Testmachine");
		this.vm.setMemorySize(256L);
		this.vm.setHddSize(5120L);

		this.nodes = new ArrayList<Node>();
	}

	@Test
	public void noNodesGiven() {
		this.nodes = null;

		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertNull(result);
	}

	@Test
	public void oneStressedNode() {
		Node stressedNode = getStressedNode();
		nodes.add(stressedNode);

		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertEquals(result.getId(), stressedNode.getId());
	}
	
	@Test
	public void stressUnstressedNodes() {
		Node stressedNode = getStressedNode();
		Node normalNode = getNormalNode();
		
		nodes.add(stressedNode);
		nodes.add(normalNode);
		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertEquals(result.getId(), normalNode.getId());
	}
	
	@Test
	public void twoNormelLessMemory() {
		Node normalNode1 = getNormalNode();
		Node normalNode2 = getNormalNode();
		// decrease normalNode2 free RAM
		normalNode2.setFreeMemorySize(900L);
		
		nodes.add(normalNode1);
		nodes.add(normalNode2);
		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertEquals(result.getId(), normalNode1.getId());
	}
	
	@Test
	public void twoNormelLessLoad() {
		Node normalNode1 = getNormalNode();
		Node normalNode2 = getNormalNode();
		// increase normalNode2 load
		normalNode2.setCpuLoad(0.3);
		
		nodes.add(normalNode1);
		nodes.add(normalNode2);
		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertEquals(result.getId(), normalNode1.getId());
	}
	
	@Test
	public void notEnoughResources() {
		Node notEnoughRAM = getNormalNode();
		notEnoughRAM.setFreeMemorySize(200L);
		
		Node notEnoughHDD = getNormalNode();
		notEnoughHDD.setFreeDiskSpace(4096L);
		
		nodes.add(notEnoughHDD);
		nodes.add(notEnoughRAM);
		
		Node result = Scheduling.selectSuitableNode(nodes, vm);
		assertNull(result);
	}

	/**
	 * ID: 1
	 * Cores: 1 
	 * Load: 4,0 
	 * RAM: 1 GB 
	 * FreeRAM: 300 MB 
	 * HDD: 8 GB 
	 * free HDD: 6 GB
	 * 
	 * @return Node
	 */
	private Node getStressedNode() {
		Node stressedNode = new Node();
		stressedNode.setId(1L);
		stressedNode.setCores(1);
		stressedNode.setCpuLoad(4.0);
		stressedNode.setMemorySize(1024L);
		stressedNode.setFreeMemorySize(300L);
		stressedNode.setDiskSpace(8192L);
		stressedNode.setFreeDiskSpace(6144L);
		return stressedNode;
	}
	
	/**
	 * ID: 2
	 * Cores: 1 
	 * Load: 0,2 
	 * RAM: 2 GB 
	 * FreeRAM: 1 GB
	 * HDD: 8 GB 
	 * free HDD: 8 GB
	 * 
	 * @return Node
	 */
	private Node getNormalNode() {
		Node easyNode = new Node();
		easyNode.setId(2L);
		easyNode.setCores(1);
		easyNode.setCpuLoad(0.2);
		easyNode.setMemorySize(2048L);
		easyNode.setFreeMemorySize(1024L);
		easyNode.setDiskSpace(8192L);
		easyNode.setFreeDiskSpace(8192L);
		return easyNode;
	}

}
