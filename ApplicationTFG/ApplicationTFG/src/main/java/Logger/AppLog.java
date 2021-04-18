package Logger;


import org.apache.log4j.Logger;



public class AppLog {
	private static final Logger LOGGER = Logger.getLogger(AppLog.class);
	
	public AppLog() {
		
	}
	
	public Logger getLogger() {
		return this.LOGGER;
	}
}
