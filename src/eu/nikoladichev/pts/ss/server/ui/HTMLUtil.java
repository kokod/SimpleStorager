package eu.nikoladichev.pts.ss.server.ui;

import java.util.List;

import eu.nikoladichev.pts.ss.server.model.Bucket;
import eu.nikoladichev.pts.ss.server.model.File;

/**
 * This is an Utility class which creates HTML pages which are requested from
 * the REST Service methods.
 * 
 * @author Koko D
 * 
 */
public class HTMLUtil {

	private static StringBuilder sb;

	/**
	 * This method creates the basic HTML body. It's used from all methods
	 * above.
	 * 
	 * @param title
	 * @param content
	 * @return
	 */
	private static String createHtml(String title, String content) {
		sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>");
		sb.append(title);
		sb.append("</title>");
		sb.append("<body>");
		sb.append(content);
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * This method creates the users HTML home page.
	 * 
	 * @param username
	 * @return
	 */
	public static String createUserHomePage(String username) {
		String title = username;
		sb = new StringBuilder();
		sb.append("<h1>");
		sb.append(username);
		sb.append("'s home page.");
		sb.append("</h1>");
		sb.append("<a href='./upload/'>Upload Files</a>" + "<br/>");
		sb.append("<a href='./bucket/list'>List Uploaded Files</a>");
		return HTMLUtil.createHtml(title, sb.toString());
	}

	/**
	 * This method creates an upload HTML form.
	 * 
	 * @param buckets
	 * @return
	 */
	public static String createUploadForm(List<String> buckets) {
		String title = "Upload form";
		sb = new StringBuilder();
		if (buckets != null && buckets.size() > 0) {
			sb.append("<form action='./send' method='post' enctype='multipart/form-data'>");
			sb.append("<br />Select Bucket: ");
			sb.append("<select name='bucket'>");
			for (String bucket : buckets) {
				sb.append("<option>");
				sb.append(bucket);
				sb.append("</option>");
			}
			sb.append("</select>");
			sb.append("<a href='../bucket/new'>Or create a new bucket</a>");
			sb.append("<p>");
			sb.append("Select a file : <input type='file' name='file' size='45' />");
			sb.append("</p>");
			sb.append("<input type='submit' value='Upload It' />");
			sb.append("</form>");
		} else {
			sb.append("<h3 align='center'>No buckets found</h3>");
			sb.append("<h3 align='center'><a href='../bucket/create'>Create a bucket</a></h3>");
		}
		return HTMLUtil.createHtml(title, sb.toString());
	}

	/**
	 * This method creates an Bucket creation HTML form.
	 * 
	 * @return
	 */
	public static String createBucketForm() {
		String title = "Create bucket form";
		sb = new StringBuilder();
		sb.append("<form action='./create' method='post' enctype='multipart/form-data'>");
		sb.append("Bucket name : <input type='text' name='bucket' size='40' />");
		sb.append("<input type='submit' value='Create bucket' />");
		sb.append("</form>");
		return HTMLUtil.createHtml(title, sb.toString());
	}

	/**
	 * This method creates a HTML page displaying all available buckets for the
	 * given user.
	 * 
	 * @param username
	 * @param buckets
	 * @return
	 */
	public static String listBuckets(String username, List<Bucket> buckets) {
		String title = "Listing buckets for user " + username + ".";
		sb = new StringBuilder();
		if (buckets != null && buckets.size() > 0) {
			sb.append("<h3>Select bucket:</h3>");
			sb.append("<ul>");
			for (Bucket bucket : buckets) {
				sb.append("<li><a href='../file/list/").append(bucket.getId())
						.append("'>");
				sb.append(bucket.getName());
				sb.append("</a></li>");
			}
			sb.append("</ul>");
		} else {
			sb.append("<h3 align='center'>No buckets found</h3>");
			sb.append("<h3 align='center'><a href='../bucket/new'>Create a bucket</a></h3>");
		}
		return HTMLUtil.createHtml(title, sb.toString());
	}

	/**
	 * This method displays files contained in the requested bucket, and their
	 * download links.
	 * 
	 * @param bucketName
	 * @param username
	 * @param files
	 * @return
	 */
	public static String listFilesInBucket(String bucketName, String username,
			List<File> files) {
		String title = "Listing files in bucket" + bucketName + " for user "
				+ username + ".";
		sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr>");
		sb.append("<th>File name</th>");
		sb.append("</tr>");
		for (File file : files) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(file.getName());
			sb.append("</td>");
			sb.append("<td>");
			sb.append("<a href='../download/").append(file.getId())
					.append("'>Download</a>");
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return HTMLUtil.createHtml(title, sb.toString());
	}
}
