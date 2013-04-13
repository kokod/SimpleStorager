package eu.nikoladichev.pts.ss.server.services;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Cookie;

import org.apache.log4j.Logger;

import com.sun.jersey.core.util.Base64;

import eu.nikoladichev.pts.ss.server.dao.BucketDAO;
import eu.nikoladichev.pts.ss.server.dao.FileDAO;
import eu.nikoladichev.pts.ss.server.dao.UserDAO;
import eu.nikoladichev.pts.ss.server.security.SecurityUtil;

/**
 * This class is used with all REST Service classes in the application. It
 * provides JNDI context lookups and security checks.
 * 
 * @author Nikola Dichev
 * 
 */
public class BaseService {

	private static Logger log = Logger.getLogger(BaseService.class
			.getSimpleName());

	/**
	 * The next properties are package scoped so they can be used directly with
	 * all REST classes that extends BaseService.
	 */
	UserDAO userDao;
	BucketDAO bucketDao;
	FileDAO fileDao;

	/**
	 * Default constructor. When object is created it creates JNDI lookups to
	 * connect all DAOs so the application can make DB operations.
	 */
	public BaseService() {
		try {
			InitialContext ctx = new InitialContext();
			userDao = (UserDAO) ctx.lookup("java:app/SServer/UserDAO");
			bucketDao = (BucketDAO) ctx.lookup("java:app/SServer/BucketDAO");
			fileDao = (FileDAO) ctx.lookup("java:app/SServer/FileDAO");
		} catch (NamingException ne) {
			log.error("NamingException: " + ne);
		}
	}

	/**
	 * This method is used for all classes which require security authorization.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	protected boolean checkSecurity(Cookie username, Cookie password) {
		boolean isSecured = false;

		if (username != null && password != null) {
			isSecured = SecurityUtil.getInstance().checkSecuredSession(
					new String(Base64.decode(username.getValue())),
					password.getValue());
		} else {
			log.warn("No cookie was created.");
			return false;
		}

		return isSecured;
	}
}
