package eu.nikoladichev.pts.ss.server.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import eu.nikoladichev.pts.ss.server.model.Bucket;

/**
 * Home object for domain model class Bucket.
 * 
 * @see eu.nikoladichev.pts.ss.server.model.Bucket
 * @author Nikola Dichev
 */
@Stateless
public class BucketDAO {

	private static final Logger log = Logger.getLogger(BucketDAO.class);

	/**
	 * The EntityManager is taken from the EE Container
	 */
	@PersistenceContext(unitName = "SimpleStorager")
	private EntityManager entityManager;

	/**
	 * Inserts or updates a Bucket in the database
	 * 
	 * @param transientInstance
	 */
	public void persist(Bucket transientInstance) {
		log.debug("persisting Bucket instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	/**
	 * Deletes a bucket from the database
	 * 
	 * @param persistentInstance
	 */
	public void remove(Bucket persistentInstance) {
		log.debug("removing Bucket instance");
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
	public Bucket merge(Bucket detachedInstance) {
		log.debug("merging Bucket instance");
		try {
			Bucket result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/**
	 * Finds a Bucket object from it's given UUID
	 * 
	 * @param id
	 * @return
	 */
	public Bucket findById(String id) {
		log.debug("getting Bucket instance with id: " + id);
		try {
			Bucket instance = entityManager.find(Bucket.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * Finds a List of Bucket objects from the database which are related to a
	 * given user
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bucket> findByUsername(String username) {
		log.debug("getting User instance with id: " + username);
		try {
			List<Bucket> instance = (List<Bucket>) entityManager
					.createQuery(
							"SELECT bucket " + "FROM Bucket bucket "
									+ "WHERE bucket.user.username = '"
									+ username + "'").getResultList();
			log.debug("get successful");
			return instance;
		} catch (NoResultException nre) {
			return null;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * Finds a single Bucket object from the database by a given username and a
	 * bucket name
	 * 
	 * @param username
	 * @param bucketName
	 * @return
	 */
	public Bucket findByName(String username, String bucketName) {
		log.debug("getting User instance with id: " + username);
		try {
			Bucket instance = (Bucket) entityManager.createQuery(
					"SELECT bucket " + "FROM Bucket bucket "
							+ "WHERE bucket.user.username = '" + username + "'"
							+ "and bucket.name = '" + bucketName + "'")
					.getSingleResult();
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
