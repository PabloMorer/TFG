package Logger;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import Gui.GuiModel;

public class AppLog {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppLog.class);
	
	public AppLog() {
		
	}
	
	public Logger getLogger() {
		return this.LOGGER;
	}
}
