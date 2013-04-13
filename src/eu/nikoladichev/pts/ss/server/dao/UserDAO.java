package eu.nikoladichev.pts.ss.server.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import eu.nikoladichev.pts.ss.server.model.User;

/**
 * Home object for domain model class User.
 * 
 * @see eu.nikoladichev.pts.ss.server.model.User
 * @author Nikola Dichev
 */
@Stateless
public class UserDAO {

	private static final Logger log = Logger.getLogger(UserDAO.class);

	/**
	 * The EntityManager is taken from the EE Container
	 */
	@PersistenceContext(unitName = "SimpleStorager")
	private EntityManager entityManager;

	/**
	 * Inserts or updates a User in the database
	 * 
	 * @param transientInstance
	 */
	public void persist(User transientInstance) {
		log.debug("persisting User instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	/**
	 * Deletes a user from the database
	 * 
	 * @param persistentInstance
	 */
	public void remove(User persistentInstance) {
		log.debug("removing User instance");
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
	public User merge(User detachedInstance) {
		log.debug("merging User instance");
		try {
			User result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/**
	 * Finds a User object from it's given UUID
	 * 
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		log.debug("getting User instance with id: " + id);
		try {
			User instance = entityManager.find(User.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * Finds a User object from the database by a given username
	 * 
	 * @param username
	 * @return
	 */
	public User findByUsername(String username) {
		log.debug("getting User instance with id: " + username);
		try {
			User instance = (User) entityManager.createQuery(
					"SELECT user " + "FROM User user "
							+ "WHERE user.username = '" + username + "'")
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
