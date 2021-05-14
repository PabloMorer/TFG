package Gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileSystemView;

import com.sun.org.slf4j.internal.Logger;

import Controller.Controller;
import Logger.AppLog;

/**
 * 
 * @author pablomorer
 *
 */
public class MenuPanel extends JPanel {
	
	JButton loadMPI;
	JButton loadMPIlib;
	JButton sendMPI;

	JFileChooser fileChooser;
	Choice ch;
	Logger LOGGER;
	Controller controller;
	private static Object[] optionsChartWidth  = {"1","2","3","4","5","10","Automatic"};
	
	int numLibs = 0;

	public MenuPanel(Controller controller) {
		this.controller = controller;
		controller.setNumLibFiles(numLibs);
		
        setMinimumSize(new Dimension(800, 114));
        setPreferredSize(new Dimension(800, 114));
        setSize(new Dimension(800, 114));

		loadMPI = new JButton("Load MPI file");
		loadMPI.setEnabled(true);
		loadMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadButtonActionPerformed(evt);
            }
        });
		
		loadMPIlib = new JButton("Load libs");
		loadMPIlib.setEnabled(false);
		loadMPIlib.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadLibButtonActionPerformed(evt);
            }
        });
		
		
		sendMPI = new JButton("Send MPI");
		sendMPI.setEnabled(false);
		
		sendMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	sendMPIActionPerformed(evt);
            }
        });
		
		this.add(loadMPI);
		//selectNumber("Select Number of processors");
		this.add(loadMPIlib);
		this.add(sendMPI);
	}
	


	private void enabledButton(JButton button) {
    	String style = button.getFont().getFontName();
    	button.setFont(new Font(style,0,11));
    	button.setEnabled(true);
    	
	}

    
    private File chooseFile(String title) {
    	fileChooser = new JFileChooser();
    	int ret = fileChooser.showOpenDialog(getParent());
    	
    	//int option = fileChooser.showOpenDialog(null);    	
    	try {   		
    		if(ret == fileChooser.APPROVE_OPTION) {
    			File file = fileChooser.getSelectedFile();
    			JOptionPane.showMessageDialog(getParent(), file);
    			
    			return file;
    		}
    		else {
    			LOGGER.error("ERROR MenuPanel chooseFile(): No file has been chosen");
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    
    private File[] chooseFileLib(String title) {
		fileChooser = new JFileChooser();
    	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	if(!fileChooser.isMultiSelectionEnabled()) {
    		fileChooser.setMultiSelectionEnabled(true);
    	}
    	//int option = fileChooser.showOpenDialog(null);   
    	int ret = fileChooser.showOpenDialog(getParent());
    	try {   		
    		if(ret == fileChooser.APPROVE_OPTION) {
    			File[] file = fileChooser.getSelectedFiles();
    			for(int i = 0; i < file.length; i++) {
    				JOptionPane.showMessageDialog(getParent(), file[i]);
    			}
    			
    			
    			return file;
    		}
    		else {
    			LOGGER.error("ERROR MenuPanel chooseFile(): No file has been chosen");
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
		
	}
    



  
   private void loadLibButtonActionPerformed(ActionEvent evt) {
    	File[] files = chooseFileLib("Choose your libraries");
    	
    	numLibs = 0;
    	while(numLibs < files.length && controller.isH(files[numLibs])) {
    		numLibs++;
    	}
    	if(numLibs == files.length) {
    		controller.setMPILibFile(files);
    		controller.setNumLibFiles(numLibs);
    		
    	}else {
    		JOptionPane.showMessageDialog(null, "You need to choose files with extension .h");
    	}	
    } 
     
	
	private void loadButtonActionPerformed(ActionEvent evt) {
    	this.loadMPI.setText("Loaded MPI File");
    	File file = chooseFile("Choose your program");
    	if(file != null) {
    		if(controller.isC(file)) {	
    	   		controller.setMPIFile(file);
    	   		enabledButton(loadMPIlib);
    	   		enabledButton(sendMPI);
    		}else {
    			JOptionPane.showMessageDialog(null, "You need to choose a file with extension .c");
    		}
 
    		
    	}
    	else {
    		JOptionPane.showMessageDialog(null, "You need to choose a file");
    		LOGGER.error("ERROR MenuPanel jButtonChooseMpiFileActionPerformed() : No file has been chosen");
    	}
		
	}

	
	private void sendMPIActionPerformed(ActionEvent evt) {
    	//int maxWorkers = -1;
    	//String numWorkers =  (String) JOptionPane.showInputDialog(null, "Choose number of workers" , "Maximum Width", JOptionPane.QUESTION_MESSAGE, null, optionsChartWidth, optionsChartWidth[0]);
    	String numRanks = JOptionPane.showInputDialog("Elija el número de Workers a ejecutar", null);
		//if(!widthChoose.equals("Automatic") )maxWidth = Integer.parseInt(widthChoose);
    	

   		this.controller.setNProc(numRanks);  			
		controller.initSocket();
	}
	



}
