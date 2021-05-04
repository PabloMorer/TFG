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
import javax.swing.JTextArea;
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

	public MenuPanel(Controller controller) {
		this.controller = controller;
		
        setMinimumSize(new Dimension(800, 114));
        setPreferredSize(new Dimension(800, 114));
        setSize(new Dimension(800, 114));

		loadMPI = new JButton("Load MPI");
		loadMPI.setEnabled(true);
		loadMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadButtonActionPerformed(evt);
            }
        });
		
		loadMPIlib = new JButton("Load MPI lib");
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
		selectNumber("Select Number of processors");
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
    
	private void selectNumber(String title) {
		
		ch = new Choice();
		ch.addItem("1");
		ch.addItem("2");
		ch.addItem("4");
		ch.addItem("8");
		ch.addItem("16");
		ch.addItem("32");
		ch.addItem("64");
		
		this.add(ch);
		
		controller.setNProc(ch.getSelectedIndex() + 1);
	}

  
   private void loadLibButtonActionPerformed(ActionEvent evt) {
    	File[] files = chooseFileLib("Choose your libraries");
    	
    	int i = 0;
    	while(i < files.length && controller.isH(files[i])) {
    		i++;
    	}
    	if(i == files.length) {
    		controller.setMPILibFile(files);
    		
    	}else {
    		JOptionPane.showMessageDialog(null, "You need to choose files with extension .h");
    	}	
    } 
     
	
	private void loadButtonActionPerformed(ActionEvent evt) {
    	this.loadMPI.setText("Load");
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
		controller.initSocket();
	}
	



}
