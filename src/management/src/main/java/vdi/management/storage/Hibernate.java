package vdi.management.storage;

import org.hibernate.Session;

/**
 * Helper class for Hibernate.
 */
public final class Hibernate {

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
	 */
	public static void saveObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			session.save(o);
			session.getTransaction().commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Saves or updates an object in database.
	 *
	 * @param o
	 *            the object
	 */
	public static void saveOrUpdateObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			session.merge(o);
			session.getTransaction().commit();
		} catch (Exception e) {
			// TODO: handle Exception
			e.printStackTrace();
		}
	}

	/**
	 * Deletes an object from the database.
	 *
	 * @param o
	 *            the object to delete
	 */
	public static void deleteObject(Object o) {
		try {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			session.delete(o);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
