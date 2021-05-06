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

/**
*
* @author bryanrvvargas
*/
public class AppGui {
	 MenuPanel mpanel;
	 JButton loadMPI;
	 Logger LOGGER;
	 Controller controller;
	 JFileChooser fileChooser;
	


   public AppGui(Controller controller) {
	    this.controller = controller;
	    JFrame frame = new JFrame();
	    
	    mpanel =  new MenuPanel(controller);
	   


		frame.add(mpanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("TFG APP");
		frame.pack();
		frame.setVisible(true);
   }
   
   

	
	

              
                        

   
}
	

