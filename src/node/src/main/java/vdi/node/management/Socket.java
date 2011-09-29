package vdi.node.management;

/**
 * Representing a socket.
 */
public class Socket {

	/**
	 * IP or DNS.
	 */
	public String ip;

	/**
	 * String containing the port number.
	 */
	public String port;

	/**
	 * Constructor.
	 * 
	 * @param ip
	 *            the IP-address
	 * @param port
	 *            the port
	 */
	public Socket(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

}
