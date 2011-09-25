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
	private Long ramSize;
	private Double cpuLoad;
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
	 * @return the ram size
	 */
	@Column(name = "RAM_SIZE")
	public Long getRamSize() {
		return ramSize;
	}

	/**
	 * @param ramSize
	 *            the freeRam to set
	 */
	public void setRamSize(Long ramSize) {
		this.ramSize = ramSize;
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
