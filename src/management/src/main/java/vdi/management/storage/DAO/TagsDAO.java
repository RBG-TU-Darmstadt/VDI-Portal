package vdi.management.storage.DAO;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import vdi.management.storage.Hibernate;
import vdi.management.storage.HibernateUtil;
import vdi.management.storage.entities.Tag;

/**
 * The DAO for Tags Entity.
 */
public final class TagsDAO {

	private static final Logger LOGGER = Logger.getLogger(NodeDAO.class.getName());

	/**
	 * Private constructor for static class.
	 */
	private TagsDAO() {
	}

	/**
	 * Checks if the Tag identified by its name exists.
	 * 
	 * @param tag
	 *            the name of the tag
	 * @return true if exists, otherwise false
	 */
	public static boolean exists(String tag) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Long occ = (Long) session.createQuery("select count(*) from Tag as t where t.name = ?").setString(0, tag)
				.uniqueResult();

		session.getTransaction().commit();

		return (occ > 0);
	}

	/**
	 * Creates a Tag entity and stores it.
	 * 
	 * @param t
	 *            the Tag to store
	 * @return true at success
	 */
	public static boolean create(Tag t) {
		return Hibernate.saveObject(t);
	}

	/**
	 * Updates a database entry for a Tag.
	 * 
	 * @param t
	 *            the tag to update
	 * @return true at success
	 */
	public static boolean update(Tag t) {
		return Hibernate.saveOrUpdateObject(t);
	}

	/**
	 * Get a tag by its name. If this tag does not exist, it will be created.
	 * 
	 * @param tag
	 *            the name of the tag as String
	 * @return the Tag corresponding Tag
	 */
	public static Tag get(String tag) {
		if (!TagsDAO.exists(tag)) {
			return new Tag(tag);
		} else {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();

			Tag t = (Tag) session.createQuery("from Tag where name=?")
					.setString(0, tag).uniqueResult();

			session.getTransaction().commit();

			return t;
		}
	}

	/**
	 * Select a Tag from Database by its slug.
	 * 
	 * @param tagSlug
	 *            the slug
	 * @return the Tag object with specified slug.
	 */
	public static Tag getBySlug(String tagSlug) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Tag t = (Tag) session.createQuery("from Tag where slug=?").setString(0, tagSlug).uniqueResult();

		session.getTransaction().commit();

		return t;
	}

	/**
	 * Receives all tags from the database. 
	 * 
	 * @return a list with all existing tags
	 */
	public static List<Tag> getAllTags() {
		try {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();

			@SuppressWarnings("unchecked")
			List<Tag> list = session.createQuery("from Tag").list();

			session.getTransaction().commit();

			return list;
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
		}

		return null;
	}

}
