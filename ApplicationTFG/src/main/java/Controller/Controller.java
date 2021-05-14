package Controller;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;
import org.jfree.util.Log;


import Gui.AppGui;
import Gui.MenuPanel;
import Logger.AppLog;
import Sockets.Client;

public class Controller {

	private File mpiFile;
	private File[] mpiLibFiles;
	private AppLog LOGGER;
	private AppGui GUI;
	private String nProc;
	private int numLibFiles;
	private String tracePath;
	/**
	 * Model App
	 */

	
	public Controller(AppLog LOG) {
		this.LOGGER = LOG;
	}
	
	public boolean isC(File file) {
		return "c".equals(FilenameUtils.getExtension(file.getName()));
	}
	public boolean isH(File file) {
		return "h".equals(FilenameUtils.getExtension(file.getName()));
	}
	public void initGui() {
		this.GUI = new AppGui(this);
	}
	public void setMPIFile(File file) {
		this.mpiFile = file;
	}
	public File getMPIFile() {
		return this.mpiFile;
	}
	
	public File[] getMPILibFile() {
		return this.mpiLibFiles;
	}
	
	public void setMPILibFile(File[] file) {
		this.mpiLibFiles = file;
	}
	
	public AppLog getLogger() {
		return this.LOGGER;
	}
	public void initSocket() {
		Client c = new Client(this);
	}

	public void setNProc(String nProc) {
		this.nProc = nProc;	
	}
	public String getNProc() {
		return this.nProc;
	}
	public String[] getNameLibs() {
		String[] names = new String[this.mpiLibFiles.length];
		
		for(int i = 0; i < this.mpiLibFiles.length; i++) {
			names[i] = this.mpiLibFiles[i].getName();
		}
		return names;
			
	}

	public void setNumLibFiles(int i) {
		this.numLibFiles = i;	
	}
	public int getNumLibFiles() {
		return this.numLibFiles;
	}



	public void setPopUpMessage(String message) {
		this.GUI.setPopUpMessage(message);
		
	}

	


	public String getTraceLib(){
		return this.tracePath;
	}
	public void setTraceLib(String trace) {
		this.tracePath = trace;
	}

	




}
