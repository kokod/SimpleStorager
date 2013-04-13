package eu.nikoladichev.pts.ss.server.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import eu.nikoladichev.pts.ss.server.model.File;

/**
 * Home object for domain model class File.
 * 
 * @see eu.nikoladichev.pts.ss.server.model.File
 * @author Nikola Dichev
 */
@Stateless
public class FileDAO {

	private static final Logger log = Logger.getLogger(FileDAO.class);

	/**
	 * The EntityManager is taken from the EE Container
	 */
	@PersistenceContext(unitName = "SimpleStorager")
	private EntityManager entityManager;

	/**
	 * Inserts or updates a File in the database
	 * 
	 * @param transientInstance
	 */
	public void persist(File transientInstance) {
		log.debug("persisting File instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	/**
	 * Deletes a file from the database
	 * 
	 * @param persistentInstance
	 */
	public void remove(File persistentInstance) {
		log.debug("removing File instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	/**
	 * Merge the state of the given entity into the current persistence context.
	 * 
	 * @param detachedInstance
	 * @return
	 */
	public File merge(File detachedInstance) {
		log.debug("merging File instance");
		try {
			File result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/**
	 * Finds a File object from it's given UUID
	 * 
	 * @param id
	 * @return
	 */
	public File findById(String id) {
		log.debug("getting File instance with id: " + id);
		try {
			File instance = entityManager.find(File.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * Finds a List of File objects from the database which are related to a
	 * given bucket
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<File> findByBucketId(String bucketId) {
		try {
			List<File> instance = (List<File>) entityManager.createQuery(
					"SELECT file " + "FROM File file "
							+ "WHERE file.bucket.id = '" + bucketId + "'")
					.getResultList();
			log.debug("get successful");
			return instance;
		} catch (NoResultException nre) {
			return null;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
