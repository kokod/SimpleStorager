package eu.nikoladichev.pts.ss.server.model;

// Generated Mar 29, 2013 1:40:49 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Nikola Dichev This class describes the ORM of File class and
 *         ssdb.file table
 * 
 */
@Entity
@Table(name = "file")
@SuppressWarnings("serial")
public class File implements java.io.Serializable {

	private String id;
	private Bucket bucket;
	private String name;

	public File() {
	}

	public File(String id, Bucket bucket, String name) {
		this.id = id;
		this.bucket = bucket;
		this.name = name;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * EAGER Mapping of Bucket class gives us the User instance which is related
	 * to the given File object instance.
	 * 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_bucket_id", nullable = false)
	public Bucket getBucket() {
		return this.bucket;
	}

	public void setBucket(Bucket user) {
		this.bucket = user;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
