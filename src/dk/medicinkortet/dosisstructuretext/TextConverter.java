package dk.medicinkortet.dosisstructuretext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TextConverter {

	protected static boolean useJavaImplementation = true;
	
	static {
		Properties properties = new Properties();
		InputStream propStream = TextConverter.class.getClassLoader().getResourceAsStream("project.properties");
		if(propStream == null) {
			try {
				propStream = new java.io.FileInputStream("project.properties");
			}
			catch(FileNotFoundException e) {
				// Ignore, use the default
			}
		}
	
		if(propStream != null) {
			try {
				properties.load(propStream);
			} catch (IOException e) {
				// Ignore, use the default
			}
			finally {
				try {
					propStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		useJavaImplementation = properties.getProperty("implementation") != null ? properties.getProperty("implementation").compareToIgnoreCase("java") == 0 : true;
	}
	
}
