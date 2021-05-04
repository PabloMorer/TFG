package Gui;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
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
public class AppGui extends JFrame {

	ColorsApp myColor;

	Controller controller;
	                    
	private JPanel chartPanel1;
	private JPanel chartPanel2;
	private JPanel menuPanel;
	
	public MenuPanel menu;
	public ChartPanel chart1;
	public ChartPanel chart2;
	private Logger LOGGER;
	// End of variables declaration  
   /**
    * Creates new form AppView
    */
   /*public AppView() {
	   myColor = new ColorsApp();
	   menuPanel = new JPanel();
       chartPanel1 = new JPanel();
       chartPanel2 = new JPanel();
       menu = new MenuPanel();
	   chart1 = new ChartPanel(1);
	   chart2 = new ChartPanel(2);
       initComponents();
   }*/

   public AppGui(Controller controller) {
	  
	   this.LOGGER = controller.getLogger().getLogger(); //Logger
	   myColor = new ColorsApp(); //class to select color
	   menuPanel = new JPanel(); //upper menu
       chartPanel1 = new JPanel(); //top chart panel
       chartPanel2 = new JPanel(); //bot chart panel
	   menu = new MenuPanel(controller); //select menu
	//   chart1 = new ChartPanel(1,this.LOGGER); //chart that will be in first chart panel
	//   chart2 = new ChartPanel(2,this.LOGGER); //chart that will be in second chart panel
       initComponents(); //init components
   }
   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
   private void initComponents() {

       setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
       setBackground(myColor.greyL1);
       //this.setUndecorated(false);
       this.setResizable(false);
       this.setSize(800, 820);
       //this.setLocation(200, 0);
       //this.setSize(800, 800);
       
       chartPanel1.setSize(800,340);
       chartPanel2.setSize(800,340);
       
       GroupLayout menuPanelLayout = new GroupLayout(menuPanel);
       menuPanel.setLayout(menuPanelLayout);
       menuPanel.setBackground(myColor.greyL1);
       menuPanelLayout.setHorizontalGroup(
           menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 800, Short.MAX_VALUE)
           .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(menuPanelLayout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
       );
       menuPanelLayout.setVerticalGroup(
           menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 114, Short.MAX_VALUE)
           .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(menuPanelLayout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
       );

       GroupLayout chartPanel1Layout = new GroupLayout(chartPanel1);
       chartPanel1.setLayout(chartPanel1Layout);
       chartPanel1.setBackground(myColor.greyL1);
       chartPanel1Layout.setHorizontalGroup(
           chartPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 800, Short.MAX_VALUE)
           .addGroup(chartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(chartPanel1Layout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(chart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
       );
       chartPanel1Layout.setVerticalGroup(
           chartPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 343, Short.MAX_VALUE)
           .addGroup(chartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(chartPanel1Layout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(chart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
           
       );

       GroupLayout chartPanel2Layout = new GroupLayout(chartPanel2);
       chartPanel2.setLayout(chartPanel2Layout);
       chartPanel2.setBackground(myColor.greyL1);
       chartPanel2Layout.setHorizontalGroup(
           chartPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 800, Short.MAX_VALUE)
           .addGroup(chartPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(chartPanel2Layout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(chart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
       );
       chartPanel2Layout.setVerticalGroup(
           chartPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGap(0, 343, Short.MAX_VALUE)
           .addGroup(chartPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addGroup(chartPanel2Layout.createSequentialGroup()
                       .addGap(0, 0, Short.MAX_VALUE)
                       .addComponent(chart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addGap(0, 0, Short.MAX_VALUE)))
       );

       GroupLayout layout = new GroupLayout(getContentPane());
      
       getContentPane().setLayout(layout);
       layout.setHorizontalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addComponent(menuPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
           .addComponent(chartPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
           .addComponent(chartPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
       );
       layout.setVerticalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
               .addGap(0, 0, Short.MAX_VALUE)
               .addComponent(menuPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               .addGap(0, 0, 0)
               .addComponent(chartPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               .addGap(0, 0, 0)
               .addComponent(chartPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
       );
       
       //revalidate();
       //repaint();
       //pack();
   }// </editor-fold>                        
                        

   
}
	
