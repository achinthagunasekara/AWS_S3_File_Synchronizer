/*
 * This application is deisnged to Synchronize files between a given
 * directory and AWS S3 Bucket
 *
 * @author Archie Gunasekara
 * @date 2015.05.12
 * 
 */
 
package Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {
	
	private final String file = "config.properties";
	private final Properties properties;
	private static ConfigFileReader instance;
	
	public static ConfigFileReader getConfigFileReaderInstance() throws IOException {
		
		if(instance == null) {
			
			instance = new ConfigFileReader();
		}
		
		return instance;
	}
	
	private ConfigFileReader() throws IOException {
		
		properties = new Properties();
		properties.load(new FileInputStream(file));
	}
	
	public String getPropertyFor(String s) {
		
		return properties.getProperty(s);
	}
}
