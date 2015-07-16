package Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
 *
 * @author Archie Gunasekara
 * @date 2015.06.02
 * 
 */

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