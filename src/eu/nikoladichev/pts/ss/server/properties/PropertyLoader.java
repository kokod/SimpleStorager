package eu.nikoladichev.pts.ss.server.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to load custom properties - usually a directory where all
 * files are stored
 * 
 * @author Koko D
 * 
 */
public class PropertyLoader {

	private static PropertyLoader propertyLoader = new PropertyLoader();

	/**
	 * Private constructor of the PropertyLoader class which doesn't allow the
	 * class to be addressed directly.
	 */
	private PropertyLoader() {
	}

	/**
	 * Static instance method.
	 */
	public static PropertyLoader getInstance() {
		return propertyLoader;
	}

	/**
	 * Loads properties from server.properties file located in the same package
	 * as the class.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Properties loadServerProperties() throws IOException {
		Properties properties = new Properties();
		InputStream propertyFile = this.getClass().getResourceAsStream(
				"server.properties");
		if (propertyFile == null) {
			throw new FileNotFoundException("property file '" + propertyFile
					+ "' not found in the classpath");
		}

		properties.load(propertyFile);
		return properties;
	}
}
