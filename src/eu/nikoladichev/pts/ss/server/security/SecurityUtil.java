package eu.nikoladichev.pts.ss.server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import eu.nikoladichev.pts.ss.server.dao.UserDAO;
import eu.nikoladichev.pts.ss.server.model.User;

/**
 * This classed is used to encrypt users password and to check if a user with
 * given credentials is valid or invalid.
 * 
 * @author Koko D
 * 
 */
public class SecurityUtil {

	/**
	 * This String is concatenated to the given password.
	 */
	//TODO put it in the server.properties file so the user can change it.
	private static final String SALT = "T3S7_TH3_FUcK!n_K3y-Bu7_!T's_SuCh_@-BUlL_Sh!7";

	Logger log = Logger.getLogger(SecurityUtil.class);

	/**
	 * Private constructor of the SecurityUtil class which doesn't allow the
	 * class to be addressed directly.
	 */
	private SecurityUtil() {
	}

	/**
	 * Static instance method.
	 */
	public static SecurityUtil getInstance() {
		return new SecurityUtil();
	}

	/**
	 * Calls methods to encode the given input with multiple encoding methods.
	 * The encoding formula is SHA256(MD5).
	 * 
	 * @param password
	 * @return
	 */
	public String calculateHash(String password) {
		String encodedPassword = calculateSHA256(calculateMD5(password));
		log.debug("SHA256(MD5()) of " + password + " is " + encodedPassword);
		return encodedPassword;
	}

	/**
	 * Encodes the given input with SHA-256 encryption method. For more security
	 * we concatenate the input with the given SALT string.
	 * 
	 * @param password
	 * @return
	 */
	private String calculateSHA256(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException nsae) {
			log.error("No SHA-256 algorithm found");
			throw new RuntimeException();
		}
		digest.update(SALT.getBytes());
		digest.update(password.getBytes());
		byte[] SHADigest = digest.digest();
		return getHex(SHADigest);
	}

	/**
	 * Encodes the given input with MD5 encryption method.
	 * 
	 * @param password
	 * @return
	 */
	private String calculateMD5(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			log.error("No MD5 algorithm found");
			throw new RuntimeException();
		}
		digest.update(password.getBytes());
		byte[] MD5Digest = digest.digest();
		return getHex(MD5Digest);
	}

	/**
	 * Universal method to get the Hex by a given byte array generated with the
	 * encryption methods implemented above.
	 * 
	 * @param byteData
	 * @return
	 */
	private String getHex(byte[] byteData) {
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Checks if the given User is existing in the database by a given username
	 * and password.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean checkSecuredSession(String username, String password) {
		UserDAO userDao;
		try {
			InitialContext ctx = new InitialContext();
			userDao = (UserDAO) ctx.lookup("java:app/SServer/UserDAO");
			User user = (User) userDao.findByUsername(username);
			if (user != null && user.getPassword().equals(password)) {
				return true;
			}
		} catch (NamingException ne) {
			log.error("NamingException: " + ne);
			return false;
		}
		return false;
	}
}
