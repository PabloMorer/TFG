package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
	
	private AppLog log;
	private static  org.apache.log4j.Logger LOGGER;
	private JFileChooser fileChooser;
	private JFileChooser fileChooserLibs;
	
	private JPanel jPanelLevel1;
	private JPanel jPanelLevel2;
	private JPanel jPanelLevel3;
	
	private JButton loadMPI;
	private JButton loadLibMPI;
	private JButton sendMPI;
	private JMenuBar menu;
	
	private Controller controller;
	
	ColorsApp colors;
	
	private JScrollPane jScrollPaneSelectFile1;
	private JTextArea jTextAreaSelectFile1;
	private JScrollPane jScrollPaneTask1;
	private JTextArea jTextAreaTask1;
	
	public MenuPanel(Controller controller) {

	    this.setSize(800, 820);
	    this.controller = controller;
	    this.log = controller.getLogger();
	    this.LOGGER = log.getLogger();
	    initComps();
	    //creates Menu
	    
		//creates the GUI


	}
	
	private void initComps() {
		
		colors = new ColorsApp();
		jPanelLevel1 = new JPanel();
		jPanelLevel2 = new JPanel();
		jPanelLevel3 = new JPanel();
		
        setMinimumSize(new Dimension(800, 114));
        setPreferredSize(new Dimension(800, 114));
        setSize(new Dimension(800, 114));
        setBackground(colors.greyL1);
        jPanelLevel1.setBackground(colors.greyL1);
        jPanelLevel2.setBackground(colors.greyL1);
        jPanelLevel3.setBackground(colors.greyL1);
		
				
		fileChooser = new JFileChooser();
		fileChooserLibs = new JFileChooser();
		
		
		loadMPI = new JButton();
		loadMPI.setEnabled(true);
		loadMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadButtonActionPerformed(evt);
            }
        });
		
		loadLibMPI = new JButton();
		loadLibMPI.setEnabled(false);
		loadLibMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadButtonActionPerformed(evt);
            }
        });
		
		sendMPI = new JButton();
		sendMPI.setEnabled(false);
		sendMPI.setText("Load MPI file...");
		sendMPI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	loadLibButtonActionPerformed(evt);
            }
        });
		
		jScrollPaneSelectFile1 = new JScrollPane();
		jTextAreaSelectFile1 = new JTextArea();
		jScrollPaneTask1 = new JScrollPane();
		jTextAreaTask1 = new JTextArea();
		
		jScrollPaneSelectFile1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneSelectFile1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneSelectFile1.setBorder(BorderFactory.createLineBorder(colors.greyL1));
        jTextAreaSelectFile1.setColumns(20);
        jTextAreaSelectFile1.setRows(1);
        jTextAreaSelectFile1.setTabSize(1);
        jTextAreaSelectFile1.setEditable(false);
        jTextAreaSelectFile1.setText("Select MPI File");
        jTextAreaSelectFile1.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextAreaSelectFile1.setMargin(new Insets(5, 0, 0, 0));
        jTextAreaSelectFile1.setMaximumSize(new Dimension(150, 26));
        jTextAreaSelectFile1.setMinimumSize(new Dimension(150, 26));
        jTextAreaSelectFile1.setSize(new Dimension(150, 26));
        jTextAreaSelectFile1.setBackground(colors.greyL1);
        jScrollPaneSelectFile1.setViewportView(jTextAreaSelectFile1);

        jScrollPaneTask1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneTask1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextAreaTask1.setColumns(20);
        jTextAreaTask1.setRows(1);
        jTextAreaTask1.setTabSize(1);
        jTextAreaTask1.setEditable(false);
        jTextAreaTask1.setText("                      MPI Execution");
        jTextAreaTask1.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextAreaTask1.setBackground(colors.greyL1);
        jTextAreaTask1.setMargin(new Insets(5, 0, 0, 0));
        jTextAreaTask1.setMaximumSize(new Dimension(150, 26));
        jTextAreaTask1.setMinimumSize(new Dimension(150, 26));
        jTextAreaTask1.setPreferredSize(new Dimension(240, 26));
        jTextAreaTask1.setSize(new Dimension(150, 26));
        jScrollPaneTask1.setViewportView(jTextAreaTask1);
        jScrollPaneTask1.setBorder(BorderFactory.createLineBorder(colors.greyL1));

        GroupLayout jPanelLevel1Layout = new GroupLayout(jPanelLevel1);
        jPanelLevel1.setLayout(jPanelLevel1Layout);
        jPanelLevel1Layout.setHorizontalGroup(
            jPanelLevel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLevel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneSelectFile1, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(loadMPI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPaneTask1, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadLibMPI, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendMPI, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

		
		
		
	}

	private void enabledButton(JButton button) {
    	String style = button.getFont().getFontName();
    	button.setFont(new Font(style,0,11));
    	button.setEnabled(true);
    	
	}
    private File chooseFile(String title) {
    	
    	
    	fileChooser.setDialogTitle(title);
    	int option = fileChooser.showOpenDialog(null);
    	
    	try {
    		
    		if(option == fileChooser.APPROVE_OPTION) {
    			File file = fileChooser.getSelectedFile();
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
    
    private File[] chooseFileLib() {
    	//File[] files = new File[10];
    	this.loadLibMPI.setText("Load Lib");
    	int option = fileChooserLibs.showOpenDialog(null);
    	
    	try {
    		if(option == fileChooser.APPROVE_OPTION) {
    			File[] files = fileChooser.getSelectedFiles();
    			return files;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return null;
    }
     
    private void loadLibButtonActionPerformed(ActionEvent evt) {
    	File[] files = chooseFileLib();
    	
    	int i = 0;
    	while(i < files.length && controller.isH(files[i])) {
    		i++;
    	}
    	if(i == files.length - 1) {
    		controller.setMPILibFile(files);
    		enabledButton(sendMPI);
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
    	   		enabledButton(loadLibMPI);
    		}else {
    			JOptionPane.showMessageDialog(null, "You need to choose a file with extension .c");
    		}
 
    		
    	}
    	else {
    		JOptionPane.showMessageDialog(null, "You need to choose a file");
    		LOGGER.error("ERROR MenuPanel jButtonChooseMpiFileActionPerformed() : No file has been chosen");
    	}
		
	}



}
