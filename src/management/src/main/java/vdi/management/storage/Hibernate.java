package vdi.management.storage;

import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Helper class for Hibernate.
 */
public final class Hibernate {

	private static final Logger LOGGER = Logger.getLogger(Hibernate.class.getName());

	/**
	 * Private Constructor.
	 */
	private Hibernate() {
	}

	/**
	 * Saves an object.
	 * 
	 * @param o
	 *            the object to save
	 * @return true, if successful.
	 */
	public static boolean saveObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.save(o);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
			return false;
		}

		return true;
	}

	/**
	 * Saves or updates an object in database.
	 * 
	 * @param o
	 *            the object
	 * @return true, if successful.
	 */
	public static boolean saveOrUpdateObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.merge(o);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
			return false;
		}
		return true;
	}

	/**
	 * Deletes an object from the database.
	 * 
	 * @param o
	 *            the object to delete
	 * @return true, if successful.
	 */
	public static boolean deleteObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.delete(o);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
			return false;
		}
		return true;
	}
}
