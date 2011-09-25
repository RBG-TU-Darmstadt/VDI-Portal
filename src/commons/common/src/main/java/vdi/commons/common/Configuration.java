package vdi.commons.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for all projects.
 */
public final class Configuration {

	private static Properties properties = new Properties();

	/**
	 * private constructor.
	 */
	private Configuration() {
	}

	/**
	 * Loads properties from InputStream and stores them in local properties
	 * variable.
	 * 
	 * @param stream
	 *            the inputStream of properties file
	 */
	public static void loadProperties(InputStream stream) {
		try {
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();

			throw new RuntimeException("Could not load properties file.");
		}
	}

	/**
	 * @param key
	 *            of the property
	 * @return the property of key
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Changes an existing property temporarily.
	 * 
	 * @param key
	 *            the key to change
	 * @param value
	 *            the new value
	 * @return the previous value of the specified key in this property list, or
	 *         null if it did not have one.
	 */
	public static Object setProperty(String key, String value) {
		return properties.setProperty(key, value);
	}
}
