package GetProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Clase encargada de leer fichero de properties
 * @author bryanrvvargas
 *
 */
public class GetProperties { 

	Properties properties;
	
	public GetProperties() {
		properties = new Properties();
		try {
			File propertieFile = new File("/home/pablomorer/Desktop/TFGPablo/Server/conf.properties");
			InputStream input = new FileInputStream(propertieFile);
			properties.load(input);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getProperty(String nameProperty) {
		return properties.getProperty(nameProperty);
	}
}
