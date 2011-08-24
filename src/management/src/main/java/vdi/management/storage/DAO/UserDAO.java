package vdi.management.storage.DAO;

import org.hibernate.Session;

import vdi.management.storage.Hibernate;
import vdi.management.storage.HibernateUtil;
import vdi.management.storage.entities.User;

/**
 * The DAO for User Entity.
 */
public final class UserDAO {

	/**
	 * Private constructor.
	 */
	private UserDAO() {
	}

	/**
	 * Checks if the user with a specified tuid exists.
	 *
	 * @param tuid
	 *            the tuid of the user
	 * @return true, if the user exists, otherwise false
	 */
	public static boolean exists(String tuid) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Long occ = (Long) session
				.createQuery("select count(*) from User as u where u.tuid = ?")
				.setString(0, tuid).uniqueResult();

		session.getTransaction().commit();

		return (occ > 0);
	}

	/**
	 * Stores the user in the database.
	 *
	 * @param u
	 *            the User to store
	 */
	public static void create(User u) {
		Hibernate.saveObject(u);
	}

	/**
	 * Upates a User entity.
	 *
	 * @param u
	 *            the user to update
	 */
	public static void update(User u) {
		Hibernate.saveOrUpdateObject(u);
	}

	/**
	 * Retrieves the user with given TUID. If user does not exist in the
	 * database, a new user with this tuid is created.
	 *
	 * @param tuid
	 *            the users tuid
	 * @return the user with specified tuid
	 */
	public static User get(String tuid) {
		User user;
		if (UserDAO.exists(tuid)) {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();

			user = (User) session.createQuery("from User where tuid=?")
					.setString(0, tuid).uniqueResult();

			session.getTransaction().commit();
		} else {
			user = new User();
			user.setTuid(tuid);
			UserDAO.create(user);
		}
		return user;
	}

}
