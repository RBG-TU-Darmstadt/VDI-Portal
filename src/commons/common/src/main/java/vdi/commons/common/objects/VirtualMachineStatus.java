package vdi.commons.common.objects;

/**
 * Possible VirtualMachine status.
 */
public enum VirtualMachineStatus {
	/**
	 * VM is running or should be started.
	 */
	STARTED,
	/**
	 * vm is stopped or should be stopped.
	 */
	STOPPED,
	/**
	 * vm is paused or should be paused.
	 */
	PAUSED
}
