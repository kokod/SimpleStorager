package eu.nikoladichev.pts.ss.server.model;

// Generated Mar 29, 2013 1:40:49 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author Nikola Dichev This class describes the ORM of Bucket class and
 *         ssdb.bucket table
 * 
 */
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@SuppressWarnings("serial")
public class User implements java.io.Serializable {

	private String id;
	private String username;
	private String password;
	private Set<Bucket> buckets = new HashSet<Bucket>(0);

	public User() {
	}

	public User(String id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public User(String id, String username, String password, Set<File> files,
			Set<Bucket> buckets) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.buckets = buckets;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "username", unique = true, nullable = false, length = 64)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 64)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	public Set<Bucket> getBuckets() {
		return this.buckets;
	}

	public void setBuckets(Set<Bucket> buckets) {
		this.buckets = buckets;
	}

}
