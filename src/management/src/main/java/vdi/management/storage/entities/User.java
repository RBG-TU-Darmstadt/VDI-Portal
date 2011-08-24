package vdi.management.storage.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * User Entity.
 */
@Entity
@Table(name = "USERS")
public class User {

	private Long id;
	private String loginName;
	private String firstName;
	private String lastName;
	private String tuid;
	private String email;

	private List<VirtualMachine> virtualMachines = new ArrayList<VirtualMachine>();

	/**
	 * The default constructor.
	 */
	public User() {
	}

	/**
	 * @param loginName
	 *            the loginName of the user
	 * @param firstName
	 *            the users first name
	 * @param lastName
	 *            the users last name
	 * @param tuid
	 *            the tuid of the user
	 * @param email
	 *            the users email address
	 */
	public User(String loginName, String firstName, String lastName,
			String tuid, String email) {
		this.loginName = loginName;
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.tuid = tuid;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	// {__AUTHUSERCONTEXT__=student, cn=simon_o, __AUTHTYPE__=TUID,
	// surname=Olberding, givenName=Simon, tudUserUniqueID=99374839}
	@Column(name = "USER_LOGIN")
	public String getLoginName() {
		return loginName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "USER_FIRSTNAME")
	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "USER_LASTNAME")
	public String getLastName() {
		return lastName;
	}

	public void setTuid(String tuid) {
		this.tuid = tuid;
	}

	@Column(name = "USER_TUID")
	public String getTuid() {
		return tuid;
	}

	public void setVirtualMachines(List<VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	public List<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}

	@Override
	public String toString() {
		return "login: " + loginName + "\t surname: " + getFirstName()
				+ "\t lastname: " + getLastName() + "\t tuid: " + tuid
				+ "\t email: " + email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "USER_EMAIL")
	public String getEmail() {
		return email;
	}
}
