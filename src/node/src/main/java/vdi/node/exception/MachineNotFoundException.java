package vdi.node.exception;

/**
 * To be thrown if a machine could not be found.
 */
public class MachineNotFoundException extends Exception {

	private static final long serialVersionUID = 1387527811975460937L;

	private String machineId;

	/**
	 * Constructor.
	 * 
	 * @param machineId
	 *            the machines id
	 */
	public MachineNotFoundException(String machineId) {
		super();
		this.machineId = machineId;
	}

	public String getMachineId() {
		return machineId;
	}

}
