package vdi.management.util;

/**
 * This exception is thrown when a request doesn't comply with the specified
 * bound a VM.
 */
public class BoundsException extends Exception {

	private static final long serialVersionUID = 897790458389285403L;

	/**
	 * @param arg0
	 *            an error message
	 */
	public BoundsException(String arg0) {
		super(arg0);
	}

}
