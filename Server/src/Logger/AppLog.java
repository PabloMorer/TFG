package Logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;
/**
 * 
 * @author bryanrvvargas
 * Based in code: https://examples.javacodegeeks.com/core-java/util/logging/java-util-logging-example/
 * SIN FUNCIONAR
 * 
 */
public class AppLog {

	private Logger logger;
	
	private Handler consoleHandler;
	
	private Handler fileHandler;
	
	public AppLog() {
		this.logger = Logger.getLogger(AppLog.class.getName()); 
		
		consoleHandler = new ConsoleHandler();
		try {
			System.out.println("LOG LOG() initialiize file logger: "); 
			fileHandler  = new FileHandler("/home/pablomorer/Desktop/TFGPablo/Server/Logger.log");
			
			this.logger.addHandler(consoleHandler);
			this.logger.addHandler(fileHandler);
			
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);
			
			logger.config("Config done.");
			logger.removeHandler(consoleHandler);

			logger.log(Level.INFO, "LOGGER FINER");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	/**
	 * Register info @message in log file
	 * @param message
	 */
	public  void info(String message){
		//logger.log(Level.INFO,message);
		System.out.println(message);
	}
	
	/**
	 * Register error @message in log file
	 * @param message
	 */
	public  void error(String message){
		//logger.log(Level.SEVERE,message);
		System.out.println(message);
	}
	
	/**
	 * Register debug @message in log file
	 * @param message
	 */
	public  void debug(String message){
		//logger.log(Level.WARNING,message);
		System.out.println(message);
	}

	public void error(Exception e) {
		// TODO Auto-generated method stub
		logger.log(Level.SEVERE, "ERROR", e);
	}
}
