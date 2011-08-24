package vdi.management.storage;

import java.util.Date;
import java.util.List;

import vdi.management.storage.DAO.UserDAO;
import vdi.management.storage.DAO.VirtualMachineDAO;
import vdi.management.storage.entities.User;
import vdi.management.storage.entities.VirtualMachine;

public class HibernateTesting {
	private static User testUser;
	
	public static void main(String[] args) {
		testUser();
		testVirtualMachines();
	}

	private static void testUser() {
		testUser = new User("loginName", "surname", "lastname", "tuid",
				"emailadresse@dfdf.de");

		if (!UserDAO.exists("tuid")) {
			UserDAO.create(testUser);
			System.out.println("user created");
		}

		if (UserDAO.exists("tuid")) {
			User dbUser = UserDAO.get("tuid");
			System.out.println(dbUser.toString());
		}
	}
	
	private static void testVirtualMachines() {
		VirtualMachine vm = new VirtualMachine("machine1", "Ubuntu", new Date(), "TestMachine", testUser);

		if (!VirtualMachineDAO.exists("machine1")) {
			VirtualMachineDAO.create(vm);
		}

		if (VirtualMachineDAO.exists("machine1")) {
			VirtualMachine dbVm = VirtualMachineDAO.get("machine1");
			System.out.println(dbVm.getMachineId() + " - " + dbVm.getMachineName());
		}
		
		// get a list of all virtualMachines
		List<VirtualMachine> list = VirtualMachineDAO.getAllMachines();
		System.out.println("Getting all " + list.size() + " VirtualMachines:");
		for(VirtualMachine entry : list) {
			System.out.println(entry.toString());
		}
	}
}
