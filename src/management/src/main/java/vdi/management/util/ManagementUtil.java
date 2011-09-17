package vdi.management.util;

/**
 *
 */
public final class ManagementUtil {

	/**
	 * 
	 */
	private ManagementUtil() { }

	/**
	 * Calculates a random number within a given interval.
	 * 
	 * @param min
	 *            the lower boundary of the interval
	 * @param max
	 *            the upper boundary of the interval
	 * @return a random number between min and max
	 */
	public static long randomNumber(int min, int max) {
		return Math.round((Math.random() * (max - min)) + min);
	}

}
