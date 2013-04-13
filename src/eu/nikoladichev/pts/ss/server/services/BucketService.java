package eu.nikoladichev.pts.ss.server.services;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataParam;

import eu.nikoladichev.pts.ss.server.model.Bucket;
import eu.nikoladichev.pts.ss.server.model.User;
import eu.nikoladichev.pts.ss.server.ui.HTMLUtil;

/**
 * This class provides methods for Bucket creation and read operations.
 * 
 * @author Nikola Dichev
 * 
 */
@Path("/bucket")
public class BucketService extends BaseService {

	private static Logger log = Logger.getLogger(BucketService.class
			.getSimpleName());

	/**
	 * When invoked this method produces HTML content displaying a Bucket
	 * creation form.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@GET
	@Path("/new")
	@Produces(MediaType.TEXT_HTML)
	public Response createBucketPage(@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();

		return Response.ok().entity(HTMLUtil.createBucketForm()).build();
	}

	/**
	 * When invoked this method persist a new Bucket in the database.
	 * 
	 * @param bucketName
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public Response createBucket(@FormDataParam("bucket") String bucketName,
			@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();
		String uuid = UUID.randomUUID().toString();
		User user = userDao.findByUsername(new String(Base64.decode(username
				.getValue())));
		Bucket bucket = new Bucket(uuid, user, bucketName);
		try {
			bucketDao.persist(bucket);
			log.info(user.getUsername() + " inserted new bucket with name "
					+ bucketName);
		} catch (Exception e) {
			log.error("Error inserting bucket " + bucketName + "for user "
					+ username);
		}
		return Response
				.ok("Bucket created successfully. <a href='../'>Click here to continue.</a>")
				.build();
	}

	/**
	 * When invoked this method displays a HTML page displaying all buckets for
	 * a given User.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@GET
	@Path("/list")
	@Produces(MediaType.TEXT_HTML)
	public Response listBuckets(@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {
		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();
		String usernameDecrypted = new String(
				Base64.decode(username.getValue()));
		List<Bucket> buckets = bucketDao.findByUsername(usernameDecrypted);
		return Response.ok()
				.entity(HTMLUtil.listBuckets(usernameDecrypted, buckets))
				.build();
	}
}
