package eu.nikoladichev.pts.ss.server.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataParam;

import eu.nikoladichev.pts.ss.server.model.Bucket;
import eu.nikoladichev.pts.ss.server.model.File;
import eu.nikoladichev.pts.ss.server.properties.PropertyLoader;
import eu.nikoladichev.pts.ss.server.ui.HTMLUtil;

/**
 * This class is providing logic for uploading files.
 * 
 * @author Nikola Dichev
 * 
 */
@Path("/upload")
public class UploadService extends BaseService {

	private static Logger log = Logger.getLogger(UploadService.class
			.getSimpleName());

	/**
	 * This is the default method for this resource. When invoked it creates
	 * HTML form for uploading files into a specific bucket.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Response getUploadPage(@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();

		List<Bucket> buckets = null;
		buckets = bucketDao.findByUsername(new String(Base64.decode(username
				.getValue())));
		List<String> bucketNames = new ArrayList<String>();
		for (Bucket bucket : buckets) {
			bucketNames.add(bucket.getName());
		}
		log.info("Number of buckets for user "
				+ new String(Base64.decode(username.getValue())) + " is "
				+ bucketNames.size());
		return Response.ok().entity(HTMLUtil.createUploadForm(bucketNames))
				.build();
	}

	/**
	 * When invoked this method persists the input file to a directory described
	 * in the server.properties file (MAIN_DIR) and also persists the File
	 * instance to the db. The path is created with the following formula:
	 * (MAIN_DIR)/(USER)/(BUCKET)/(FILE_NAME)
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @param bucket
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("/send")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("bucket") String bucket,
			@CookieParam("USER") Cookie username,
			@CookieParam("PASS") Cookie password) {

		if (!checkSecurity(username, password))
			return Response.serverError().status(Response.Status.BAD_REQUEST)
					.build();

		String output = null;
		try {
			String serverStorageRootDir = PropertyLoader.getInstance()
					.loadServerProperties().getProperty("upload_dir")
					.toString();

			String usernameDecrypted = new String(Base64.decode(username
					.getValue()));
			String currentPath = serverStorageRootDir + java.io.File.separator
					+ usernameDecrypted + java.io.File.separator + bucket
					+ java.io.File.separator;
			log.info("UPLOAD DIRECTORY IS: " + currentPath);
			java.io.File directory = new java.io.File(currentPath);
			if (!directory.exists()) {
				log.info(serverStorageRootDir
						+ " directory doesn't exist. Creating it now...");
				boolean result = directory.mkdirs();
				if (result)
					log.info("Directory " + currentPath
							+ " created successfully.");
				else {
					log.info("Directory " + currentPath
							+ " couldn't be created.");
					return Response
							.status(Status.INTERNAL_SERVER_ERROR)
							.entity("Service temporary unavailable. Please contact your system administrator")
							.build();
				}
			}
			String uploadedFileLocation = currentPath
					+ fileDetail.getFileName();
			writeToFile(uploadedInputStream, uploadedFileLocation);
			output = "File uploaded to : " + uploadedFileLocation;
			File file = new File(UUID.randomUUID().toString(),
					bucketDao.findByName(usernameDecrypted, bucket),
					fileDetail.getFileName());
			log.info("Inserting file with bucket name: "
					+ bucketDao.findByName(usernameDecrypted, bucket).getName());
			fileDao.persist(file);
			return Response
					.ok("File uploaded successfully. <a href='../'>Click here to continue.</a>")
					.build();
		} catch (IOException e) {
			log.warn("Couldn't find upload directory. Please check server.properties file!");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(output)
					.build();
		}
	}

	/**
	 * This method writes the file input file to the file system.
	 * 
	 * @param uploadedInputStream
	 * @param uploadedFileDestination
	 */
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileDestination) {
		try {
			OutputStream out = new FileOutputStream(new java.io.File(
					uploadedFileDestination));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			log.info("IOException: " + e.getMessage());
		}
	}
}
