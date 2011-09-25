package vdi.management.storage.DAO;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import vdi.management.storage.HibernateUtil;
import vdi.management.storage.entities.Node;

/**
 * The DAO class for {@link Node} Entity.
 */
public final class NodeDAO {
	private static final Logger LOGGER = Logger.getLogger(NodeDAO.class.getName());

	/**
	 * Private constructor for static class.
	 */
	private NodeDAO() {
	}

	/**
	 * @param nodeId
	 *            a nodeId
	 * @return boolean true if Node with param nodeId exists
	 */
	public static boolean exists(String nodeId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Long occ = (Long) session.createQuery("select count(*) from Node as n where n.nodeId = ?")
				.setString(0, nodeId).uniqueResult();

		session.getTransaction().commit();

		return (occ > 0);
	}

	/**
	 * @param nodeId
	 *            the nodeId
	 * @return Node with specified nodeId, otherwise null
	 */
	public static Node get(String nodeId) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			Node n = (Node) session.createQuery("from Node where nodeId=?").setString(0, nodeId).uniqueResult();

			session.getTransaction().commit();

			return n;
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
		}
		return null;
	}

	/**
	 * Get a list of all Nodes in the database.
	 * 
	 * @return a list with all {@link vdi.commons.node.interfaces.NodeVMService}
	 *         or null, if an hibernate exception occured
	 */
	public static List<Node> getNodes() {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			@SuppressWarnings("unchecked")
			List<Node> list = session.createCriteria(Node.class).list();

			session.getTransaction().commit();

			return list;
		} catch (HibernateException e) {
			LOGGER.warning(e.getMessage());
			LOGGER.fine(ExceptionUtils.getFullStackTrace(e));
		}
		return null;
	}

}
