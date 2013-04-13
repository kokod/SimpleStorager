package eu.nikoladichev.pts.ss.server.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Nikola Dichev This class describes the ORM of Bucket class and
 *         ssdb.bucket table
 * 
 */
@Entity
@Table(name = "bucket")
@SuppressWarnings("serial")
public class Bucket implements java.io.Serializable {

	private String id;
	private User user;
	private String name;
	private Set<File> files = new HashSet<File>(0);

	public Bucket() {
	}

	public Bucket(String id, User user, String name) {
		this.id = id;
		this.user = user;
		this.name = name;
	}

	public Bucket(String id, User user, String name, Set<File> files) {
		this.id = id;
		this.user = user;
		this.name = name;
		this.files = files;
	}

	/**
	 * Bucket ID - countains UUID
	 * 
	 * @return
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * EAGER Mapping of User class gives us the User instance which is related
	 * to the given Bucket object instance.
	 * 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_user_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bucket")
	public Set<File> getFiles() {
		return this.files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}
}
