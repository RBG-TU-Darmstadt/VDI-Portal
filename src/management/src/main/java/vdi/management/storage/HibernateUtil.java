package vdi.management.storage;

import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Helper class for hibernate providing a configured {@link SessionFactory}.
 */
public final class HibernateUtil {

	/**
	 * Private constructor.
	 */
	private HibernateUtil() {
	}

	private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());

	private static SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();

		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			LOGGER.warning("Exception in HibernateUtil caught: " + ex.toString());

			throw new Error("Could not create Hibernate session factory.", ex);
		}
	}

	/**
	 * @return the current sessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
