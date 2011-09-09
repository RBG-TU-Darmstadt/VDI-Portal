package vdi.management.storage.DAO;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import vdi.commons.common.objects.VirtualMachineStatus;
import vdi.management.storage.Hibernate;
import vdi.management.storage.HibernateUtil;
import vdi.management.storage.entities.Tag;
import vdi.management.storage.entities.User;
import vdi.management.storage.entities.VirtualMachine;

/**
 * The DAO for VirtualMachines.
 */
public final class VirtualMachineDAO {

	/**
	 * Private constructor.
	 */
	private VirtualMachineDAO() {
	}

	/**
	 * Checks whether a VM exists.
	 * 
	 * @param machineID
	 *            the id of the VM
	 * @return true if VM exists, otherwise false
	 */
	public static boolean exists(String machineID) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Long occ = (Long) session
				.createQuery(
						"select count(*) from VirtualMachine as vm where vm.machineId = ?")
				.setString(0, machineID).uniqueResult();

		session.getTransaction().commit();

		return (occ > 0);
	}

	/**
	 * Stores a VM in the database.
	 * 
	 * @param vm
	 *            the VM to store.
	 */
	public static void create(VirtualMachine vm) {
		Hibernate.saveObject(vm);
	}

	/**
	 * Updates a VM.
	 * 
	 * @param vm
	 *            the VM to update
	 */
	public static void update(VirtualMachine vm) {
		Hibernate.saveOrUpdateObject(vm);
	}

	/**
	 * Gets a VM by its machineID.
	 * 
	 * @param machineID
	 *            the machineID as String.
	 * @return the VM Object, or null
	 */
	public static VirtualMachine get(String machineID) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		VirtualMachine vm = (VirtualMachine) session
				.createQuery("from VirtualMachine where MACHINE_ID=?")
				.setString(0, machineID).uniqueResult();

		session.getTransaction().commit();

		return vm;
	}

	/**
	 * Gets a VM by its id.
	 * 
	 * @param id
	 *            the unique id
	 * @return the VM Object, or null
	 */
	public static VirtualMachine get(Long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		VirtualMachine vm = (VirtualMachine) session
				.createQuery("from VirtualMachine where id=?")
				.setLong(0, id).uniqueResult();

		session.getTransaction().commit();

		return vm;
	}

	/**
	 * Get a list of all VMs in the database.
	 *
	 * @return a list with all VMs
	 */
	public static List<VirtualMachine> getAllMachines() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<VirtualMachine> list = session
				.createCriteria(VirtualMachine.class).list();

		session.getTransaction().commit();

		return list;
	}

	/**
	 * Get machines with a specified {@link VirtualMachineStatus}.
	 *
	 * @param status
	 *            the status
	 * @return a list with VMs and specified status
	 */
	public static List<VirtualMachine> getMachinesByStatus(
			VirtualMachineStatus status) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<VirtualMachine> vms = session
				.createQuery("from VirtualMachine where status = ?")
				.setParameter(0, status).list();

		session.getTransaction().commit();

		return vms;
	}

	/**
	 * Get all VirtualMachines from a User with a specified Tag.
	 *
	 * @param user
	 *            the user
	 * @param tag
	 *            the tag
	 * @return a list with VirtualMachines from User user with Tag tag.
	 */
	public static List<VirtualMachine> getByTag(User user, Tag tag) {
		List<VirtualMachine> result = new ArrayList<VirtualMachine>();

		for (VirtualMachine vm : tag.getVirtualMachines()) {
			if (vm.getUser().getId().equals(user.getId())) {
				result.add(vm);
			}
		}

		return result;
	}

}
