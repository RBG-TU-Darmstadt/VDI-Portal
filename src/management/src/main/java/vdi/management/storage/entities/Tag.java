package vdi.management.storage.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.ForeignKey;

import vdi.commons.common.Util;

/**
 * Tag Entity.
 */
@Entity
public class Tag {

	private Set<VirtualMachine> virtualMachines;
	private Long id;
	private String name;
	private String slug;

	/**
	 * The default constructor.
	 */
	public Tag() {
	}

	/**
	 * @param name
	 *            the name if the Tag
	 */
	public Tag(String name) {
		this.name = name;
		this.slug = Util.generateSlug(name);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TAG_ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "TAG")
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
		this.slug = Util.generateSlug(name);
	}

	@Column(name = "SLUG")
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "VirtualMachineTags", joinColumns = @JoinColumn(name = "TAG_ID"), inverseJoinColumns = @JoinColumn(name = "ID"))
	@ForeignKey(name = "FK_Tag_VirtualMachine", inverseName = "FK_VirtualMachine_Tag")
	public Set<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}

	public void setVirtualMachines(Set<VirtualMachine> vms) {
		this.virtualMachines = vms;
	}

}
