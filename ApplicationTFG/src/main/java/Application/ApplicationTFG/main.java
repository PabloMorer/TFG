package Application.ApplicationTFG;

import Controller.Controller;
import Gui.MenuPanel;
import Logger.AppLog;

/**
 * 
 * @author pablomorer
 *
 */
public class main {
	
	private static Controller controller;
	private static AppLog LOG;
	private static MenuPanel Gui;
	/**
	 * Main application
	 * @param args
	 */
	public static void main(String[] args) {
		
		LOG = new AppLog(); //Init Logger
		controller = new Controller(LOG); //INit Controller
		controller.initGui(); //Init Gui
		
	}
}
