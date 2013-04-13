package eu.nikoladichev.pts.ss.server.services;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataParam;

import eu.nikoladichev.pts.ss.server.model.User;
import eu.nikoladichev.pts.ss.server.security.SecurityUtil;
import eu.nikoladichev.pts.ss.server.ui.HTMLUtil;

/**
 * This class is used for user authentication and registration.
 * 
 * @author Nikola Dichev
 * 
 */
@Path("/")
public class UserService extends BaseService {

	private static Logger log = Logger.getLogger(UserService.class
			.getSimpleName());

	/**
	 * This method persists a new user into the database.
	 * 
	 * @param username
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/register")
	public Response register(@FormDataParam("username") String username,
			@FormDataParam("password") String password,
			@FormDataParam("confirmPassword") String confirmPassword) {
		if (password == null || password.length() == 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Password must not be empty!").build();
		} else if (password.length() < 4) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Password must be at least 5 characters!").build();
		} else if (!password.equals(confirmPassword)) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Difference between passwords!").build();
		}

		User existingUser = (User) userDao.findByUsername(username);
		if (existingUser != null) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("User with username: \"" + username
							+ "\" already exists.").build();
		} else {
			User user = new User(UUID.randomUUID().toString(), username,
					SecurityUtil.getInstance().calculateHash(password));
			userDao.persist(user);
			log.info("Inserted user with username: " + username);
			return Response.status(Response.Status.CREATED)
					.entity("Inserting user with username: " + username)
					.build();
		}
	}

	/**
	 * This method authenticates the input user and creates a USER and PASS
	 * Cookies so the authenticated user can obtain a session and will be able
	 * to browse all resources available to him in the ./services/ URI path.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	@Path("/login")
	public Response login(@FormDataParam("username") String username,
			@FormDataParam("password") String password) {
		User user = (User) userDao.findByUsername(username);
		if (user == null) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Incorrect username!").build();
		} else if (password == null || password.length() == 0) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Password must not be empty!").build();
		} else if (user.getPassword().equals(
				SecurityUtil.getInstance().calculateHash(password))) {
			NewCookie userCookie = new NewCookie("USER", new String(
					Base64.encode(username)));
			NewCookie passCookie = new NewCookie("PASS", SecurityUtil
					.getInstance().calculateHash(password));
			return Response
					.ok("Welcome koko. <a href='./'>Click here to proceed.</a>")
					.cookie(userCookie).cookie(passCookie).build();
		} else
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Wrong password!").build();
	}

	/**
	 * Default method for this resource. When invoked it creates an HTML page
	 * displaying the user home page after authentication.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Response getHomePage(@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {
		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();
		return Response.ok(
				HTMLUtil.createUserHomePage(new String(Base64.decode(username
						.getValue())))).build();
	}
}
