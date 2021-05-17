package Gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;


import Controller.Controller;
import Logger.AppLog;

/**
*
* @author bryanrvvargas
*/
public class AppGui {
	 MenuPanel mpanel;
	 JButton loadMPI;
	 AppLog LOGGER;
	 Controller controller;
	 JFileChooser fileChooser;
	// ChartPanel chartpanel;
	// public ChartPanel chart1;
	// public ChartPanel chart2;


   public AppGui(Controller controller) {
	    this.controller = controller;
	    JFrame frame = new JFrame();
	    

	    mpanel =  new MenuPanel(controller);
		//chart1 = new ChartPanel(1,this.LOGGER);
	//	chart2 = new ChartPanel(2,this.LOGGER);


		frame.add(mpanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("TFG APP");
		frame.pack();
		frame.setVisible(true);
   }



public void setPopUpMessage(String message) {
	JOptionPane.showMessageDialog(null, message);
	
}
   
   

	
	

              
                        

   
}
	

