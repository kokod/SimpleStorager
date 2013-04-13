package eu.nikoladichev.pts.ss.server.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.sun.jersey.core.util.Base64;

import eu.nikoladichev.pts.ss.server.model.Bucket;
import eu.nikoladichev.pts.ss.server.model.File;
import eu.nikoladichev.pts.ss.server.model.User;
import eu.nikoladichev.pts.ss.server.properties.PropertyLoader;
import eu.nikoladichev.pts.ss.server.ui.HTMLUtil;

/**
 * This class provides logic to list and/or download files for a given User.
 * 
 * @author Koko D
 * 
 */
@Path("/file")
public class FileService extends BaseService {

	private static Logger log = Logger.getLogger(FileService.class
			.getSimpleName());

	/**
	 * When invoked this method provides HTML which will list all files in a
	 * given bucket.
	 * 
	 * @param bucketId
	 * @param username
	 * @param password
	 * @return
	 */
	@GET
	@Path("/list/{bucketId}")
	@Produces(MediaType.TEXT_HTML)
	public Response listFilesInBucket(@PathParam("bucketId") String bucketId,
			@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();

		String usernameDecrypted = new String(
				Base64.decode(username.getValue()));
		Bucket bucket = bucketDao.findById(bucketId);
		List<File> files = fileDao.findByBucketId(bucketId);
		log.info("Listing " + files.size() + " with bucketId - " + bucketId
				+ " for user " + new String(Base64.decode(username.getValue())));
		return Response
				.ok()
				.entity(HTMLUtil.listFilesInBucket(bucket.getName(),
						usernameDecrypted, files)).build();
	}

	/**
	 * When invoked this method provides logic for downloading the given file by
	 * it's UUID.
	 * 
	 * @param fileId
	 * @param username
	 * @param password
	 * @return
	 */
	// TODO Implement logic to close the FileInputStream
	@GET
	@Path("/download/{fileId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("fileId") String fileId,
			@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();
		try {
			File file = fileDao.findById(fileId);
			log.info("File" + file);
			Bucket bucket = file.getBucket();

			log.info("Bucket" + bucket);
			User user = bucket.getUser();
			log.info("User" + user);

			String serverStorageRootDir = PropertyLoader.getInstance()
					.loadServerProperties().getProperty("upload_dir")
					.toString();

			String currentPath = serverStorageRootDir + java.io.File.separator
					+ user.getUsername() + java.io.File.separator
					+ bucket.getName() + java.io.File.separator
					+ file.getName();
			log.info("Trying to download " + currentPath);
			return Response
					.ok()
					.entity(new FileInputStream(new java.io.File(currentPath)))
					.header("Content-Disposition",
							"attachment; filename=" + file.getName() + "")
					.build();
		} catch (IOException ioe) {
			log.error("File cannot be opened");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
