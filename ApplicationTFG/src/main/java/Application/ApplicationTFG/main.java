package Application.ApplicationTFG;

import Controller.Controller;
import Gui.GuiModel;
import Logger.AppLog;

/**
 * 
 * @author pablomorer
 *
 */
public class main {
	
	private static Controller controller;
	private static AppLog LOG;
	private static GuiModel Gui;
	/**
	 * Main application
	 * @param args
	 */
	public static void main(String[] args) {
		
		LOG = new AppLog();
		controller = new Controller(LOG);
		controller.initGui();
		
	}
}
