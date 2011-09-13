package vdi.management.storage.DAO;

import java.util.List;

import org.hibernate.Session;

import vdi.management.storage.HibernateUtil;
import vdi.management.storage.entities.Node;

/**
 * The DAO class for Node Entity.
 */
public final class NodeDAO {

	/**
	 * Private constructor for static class.
	 */
	private NodeDAO() { }

	/**
	 * @param nodeId
	 *            a nodeId
	 * @return boolean true if Node with param nodeId exists
	 */
	public static boolean exists(String nodeId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Long occ = (Long) session
				.createQuery(
						"select count(*) from Node as n where n.nodeId = ?")
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
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Node n = (Node) session.createQuery("from Node where nodeId=?")
				.setString(0, nodeId).uniqueResult();

		session.getTransaction().commit();

		return n;
	}

	/**
	 * Get a list of all Nodes in the database.
	 *
	 * @return a list with all {@link NodeVMService}
	 */
	public static List<Node> getNodes() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<Node> list = session
				.createCriteria(Node.class).list();

		session.getTransaction().commit();

		return list;
	}

}
