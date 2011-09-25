package vdi.node.exception;

/**
 * To be thrown if the name of a machine is not unique.
 */
public class DuplicateMachineNameException extends Exception {

	private static final long serialVersionUID = 8288326730602468230L;

	private String machineName;

	/**
	 * Constructor.
	 * 
	 * @param machineName
	 *            the machines names
	 */
	public DuplicateMachineNameException(String machineName) {
		super("Machine with name '" + machineName + "' already exists.");

		this.machineName = machineName;
	}

	public String getMachineName() {
		return this.machineName;
	}

}
