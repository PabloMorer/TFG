package Controller;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;

import Gui.AppGui;
import Gui.MenuPanel;
import Logger.AppLog;

public class Controller {

	private File mpiFile;
	private File[] mpiLibFiles;
	private AppLog LOGGER;
	private AppGui GUI;
	
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
	




}
