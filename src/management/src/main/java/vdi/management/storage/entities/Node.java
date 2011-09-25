package vdi.management.storage.entities;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Node Entity stores all informations about registered NodeControllers.
 */
@Entity
public class Node {

	private Long id;
	private String nodeId;
	private URI uri;
	private Long memorySize;
	private Long freeMemorySize;
	private Double cpuLoad;
	private Long diskSpace;
	private Long freeDiskSpace;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NODE_ID", nullable = false)
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String machineID) {
		this.nodeId = machineID;
	}

	/**
	 * @return the uri as String or null if uri is not set
	 */
	@Column(name = "URI")
	public String getUri() {
		String result = null;
		if (uri != null) {
			result = uri.toString();
		}
		return result;
	}

	/**
	 * @param uri
	 *            the uri as String
	 * @throws URISyntaxException
	 *             on error parsing the uri
	 */
	public void setUri(String uri) throws URISyntaxException {
		this.uri = new URI(uri);
	}

	/**
	 * @return the memory size
	 */
	@Column(name = "MEMORY_SIZE")
	public Long getMemorySize() {
		return memorySize;
	}

	/**
	 * @param memorySize
	 *            the memory size to set
	 */
	public void setMemorySize(Long memorySize) {
		this.memorySize = memorySize;
	}

	/**
	 * @return the free memory size
	 */
	@Column(name = "FREE_MEMORY_SIZE")
	public Long getFreeMemorySize() {
		return freeMemorySize;
	}

	/**
	 * @param freeMemorySize the free memory size to set
	 */
	public void setFreeMemorySize(Long freeMemorySize) {
		this.freeMemorySize = freeMemorySize;
	}

	/**
	 * @return the cpuLoad
	 */
	@Column(name = "CPU_LOAD")
	public Double getCpuLoad() {
		return cpuLoad;
	}

	/**
	 * @param cpuLoad
	 *            the cpuLoad to set
	 */
	public void setCpuLoad(Double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	/**
	 * @return the diskSpace
	 */
	@Column(name = "DISKSPACE")
	public Long getDiskSpace() {
		return diskSpace;
	}

	/**
	 * @param diskSpace the diskSpace to set
	 */
	public void setDiskSpace(Long diskSpace) {
		this.diskSpace = diskSpace;
	}

	/**
	 * @return the freeDiskSpace
	 */
	@Column(name = "DISKSPACE_FREE")
	public Long getFreeDiskSpace() {
		return freeDiskSpace;
	}

	/**
	 * @param freeDiskSpace
	 *            the freeDiskSpace to set
	 */
	public void setFreeDiskSpace(Long freeDiskSpace) {
		this.freeDiskSpace = freeDiskSpace;
	}

}
