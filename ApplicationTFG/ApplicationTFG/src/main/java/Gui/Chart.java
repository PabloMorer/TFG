package Gui;


import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import Controller.Controller;
import Model.TraceToMap.BuildTaskInformation;

/**
 * Clase encargada de dibujar un Grafico interpretando los datos obtenidos a partir de una traza MPI
 * @author bryanrvvargas
 *
 */
public class Chart extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener{
	
	private int HEIGHT = 307; // Altura
	private int WIDTH = 760;
	private int BORDER_LEFT = 30;
	private int BORDER_DOWN = 20;
	private int BORDER_UP = 10;
	private int WIDTH_BETWEEN_TIMESTAMPS = 30;
	private int LONG_POINT = 2;

	/**
	 * Class with colors used in application
	 */
	private ColorsApp colorsApp;
	
	/**
	 * Relation between colors and functions
	 */
	private HashMap<String,Color> colors;
	
	/**
	 * Relation between  functions and colors readed by Robot
	 */
	private HashMap<Color,String> functionColors;
	
	/**
	 * Real time representing a pixel
	 */
	private double timeInPixel;
	
	/**
	 * Ranks
	 */
	private int ranks;
	
	/**
	 * TimeStamp when start the first task
	 */
	private double timeInitial;
	
	/**
	 * TimeStamp end of last task
	 */
	private double timeEnd;
	
	/**
	 * Controller
	 */
	private Controller controller;
	
	/**
	 * Class with all information of tasks
	 */
	BuildTaskInformation taskInfo;
	
	/**
	 * Decimal format 
	 */
	private DecimalFormat df ;
	
	/**
	 * Long of x axis
	 */
	private int longX_Axis;
	
	/**
	 * Long of Y_Axis
	 */
	private int longY_Axis;
	
	/**
	 * Height of ranks 
	 */
	private int heightOfRanks;
	
	/**
	 * 
	 */
	private int myChart;
	
	/**
	 * Boolean StapByStep
	 */
	private boolean stepByStep;
	
	/**
	 * 
	 */
	private int indexCurrentTask;
	
	/**
	 * 
	 */
	private static Logger LOG;
	
	/**
	 * Maximum number of panels
	 */
	private int  MAX_NUM_PANELS = 4;
	
	/**
	 * Constructor
	 */
	public Chart(int myChart,Logger log) {
		this.LOG = log;
		this.colorsApp = new ColorsApp();
		this.myChart = myChart;
		this.stepByStep = false;
	}

	/**
	 * 
	 * @param taskInfo
	 * @param myChart
	 * @param stepByStep
	 * @param log
	 * @param maxPanels
	 */
	public Chart(BuildTaskInformation taskInfo,int myChart, boolean stepByStep, Logger log, int maxPanels) {
		this.LOG = log;
		this.myChart = myChart;
		this.colorsApp = new ColorsApp();
		this.initFunctionsNameColors();
		this.initcolors();
		this.stepByStep = stepByStep;
		
		this.indexCurrentTask = 1;
		

		this.ranks = taskInfo.getRanks();
		this.timeInitial = taskInfo.getTimeIni();
		this.timeEnd = taskInfo.getTimeEnd();
		this.taskInfo = taskInfo;
		this.df= new DecimalFormat("###.########");
		if(maxPanels > 0) {
			this.longX_Axis = this.WIDTH*maxPanels;
			this.timeInPixel =  ((timeEnd-timeInitial)/longX_Axis);
		}
		else {
			this.timeInPixel = taskInfo.getMinTimeTask(); // A min task will have a representation of one pixel
			this.longX_Axis = (int) ((timeEnd-timeInitial)/timeInPixel); //calculate long of x axis 
			LOG.info("TimeInPixel: " + timeInPixel);
			while(this.longX_Axis < this.WIDTH) {
				this.timeInPixel =this.timeInPixel/2 ; // Recalculamos 
				this.longX_Axis = (int) ((timeEnd-timeInitial)/timeInPixel); //calculate long of x axis 
				//LOG.info("Recalculamos timeInPixel: " + timeInPixel);
			}
		}
		
		this.heightOfRanks = (HEIGHT- BORDER_DOWN - BORDER_UP) / (ranks * 2);
		
		LOG.info(this.toString());
		initComponent();
	}
	
	/**
	 * 
	 * @param taskInfo
	 * @param myChart
	 * @param stepByStep
	 * @param timeEndCompare
	 * @param log
	 * @param maxPanels
	 */
	public Chart(BuildTaskInformation taskInfo, int myChart, boolean stepByStep, double timeEndCompare,Logger log, int maxPanels) {
		this.LOG = log;
		LOG.info("New Chart, compare time: " + timeEndCompare);
		this.myChart = myChart;
		this.colorsApp = new ColorsApp();
		this.initFunctionsNameColors();
		this.initcolors();
		this.stepByStep = stepByStep;
		
		this.indexCurrentTask = 1;
		
		this.ranks = taskInfo.getRanks();
		this.timeInitial = taskInfo.getTimeIni();
		if(timeEndCompare != 0)this.timeEnd = timeEndCompare;
		else this.timeEnd = taskInfo.getTimeEnd();
		this.taskInfo = taskInfo;
		this.df= new DecimalFormat("###.########");
		
		
		if(maxPanels > 0) {
			this.longX_Axis = this.WIDTH*maxPanels;
			this.timeInPixel =  ((timeEnd-timeInitial)/longX_Axis);
		}
		else {
			this.timeInPixel = taskInfo.getMinTimeTask(); // A min task will have a representation of one pixel
			this.longX_Axis = (int) ((timeEnd-timeInitial)/timeInPixel); //calculate long of x axis 
			LOG.info("TimeInPixel: " + timeInPixel);
			while(this.longX_Axis < this.WIDTH) {
				this.timeInPixel =this.timeInPixel/2 ; // Recalculamos 
				this.longX_Axis = (int) ((timeEnd-timeInitial)/timeInPixel); //calculate long of x axis 
				//LOG.info("Recalculamos timeInPixel: " + timeInPixel);
			}
		}
		
		this.heightOfRanks = (HEIGHT- BORDER_DOWN - BORDER_UP) / (ranks * 2);
		
		LOG.info(this.toString());
		initComponent();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(colorsApp.greyL3);
		super.setSize(new Dimension(WIDTH+BORDER_LEFT+10,HEIGHT));
		
	}
	
	private void initComponent() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        repaint();
    }
	
	@Override
    public void paint(Graphics g) {
        super.paint(g);
        //LOG.info("REPAINT:" + this.toString());
        Graphics2D g2 = (Graphics2D) g;

        

        if (dragger) {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset);
            at.scale(zoomFactor, zoomFactor);
            g2.transform(at);
            //xDiff += xDiff;//

            /*if (released) {
            	LOG.info("ANTES RELEASED startPoint.x: " + startPoint.x + "xOffset : " + xOffset + "xDiff = " + xDiff);
                xOffset += xDiff;
                LOG.info("DESPUES RELEASED startPoint.x: " + startPoint.x + "xOffset : " + xOffset + "xDiff = " + xDiff);
                //yOffset += yDiff;
                dragger = false;
            }*/

        }

        //All drawings go here
       drawAxis(g2);
       if(!this.stepByStep) {
    	   drawAllTask(g2);
    	   //moveToTask(g2);
       }
       else {
    	   drawStepTask(g2);
    	   
       }
       
    }

	


	private void drawAxis(Graphics2D g) {
		// TODO Auto-generated method stub
		drawX(g);
		drawY(g);
	}

	private void drawY(Graphics2D g) {
		
		Graphics2D segment = (Graphics2D) g;
		segment.setColor(Color.BLACK);
		segment.setFont(new Font("Arial", 0, 10));
		segment.drawLine(BORDER_LEFT-2,BORDER_UP,BORDER_LEFT-2,HEIGHT-BORDER_DOWN);
		segment.drawLine(BORDER_LEFT-1,BORDER_UP,BORDER_LEFT-1,HEIGHT-BORDER_DOWN );
		
		segment.setColor(Color.WHITE);
		segment.setFont(new Font("Arial", 0, 9));
		segment.drawString("Ranks", 2, BORDER_UP);
		for(int i = 0; i < this.ranks; i++) {
			
			//Dibujamos marcas por cada rank
			segment.setFont(new Font("Arial", 0, 10));
			int y = HEIGHT - BORDER_DOWN - ((this.heightOfRanks*2) + (this.heightOfRanks*2*i)); // calculamos punto y donde empezara a dibujarse
			
			segment.setColor(Color.WHITE);
			segment.drawLine(BORDER_LEFT-2,y,BORDER_LEFT-2,y+this.heightOfRanks);
			segment.drawLine(BORDER_LEFT-1,y,BORDER_LEFT-1,y+this.heightOfRanks);
			segment.drawString(String.valueOf((int)(i)),BORDER_LEFT-20, y+(int)(this.heightOfRanks*0.75));
			
			
			for(int j = 0; j <= this.heightOfRanks; j++) {
				segment.setColor(colorsApp.WAITING_COLOR_MPI);
				//segment.drawLine(BORDER_LEFT,y+j,BORDER_LEFT + this.longX_Axis,y+j);
				segment.drawLine(BORDER_LEFT,y+j,BORDER_LEFT + longX_Axis,y+j);
			}
		
		}
		
		
	}

	private void drawX(Graphics2D g) {
		// TODO Auto-generated method stub
		Graphics2D segment = (Graphics2D) g;
		segment.setColor(Color.BLACK);
		segment.drawLine(BORDER_LEFT,this.getHeight()-BORDER_DOWN-1,BORDER_LEFT + this.longX_Axis,this.getHeight()-BORDER_DOWN-1);
		segment.drawLine(BORDER_LEFT,this.getHeight()-BORDER_DOWN,BORDER_LEFT + this.longX_Axis,this.getHeight()-BORDER_DOWN);
		
		
		segment.setColor(Color.WHITE);
		segment.drawLine(BORDER_LEFT,this.getHeight()-BORDER_DOWN-1,BORDER_LEFT+LONG_POINT,this.getHeight()-BORDER_DOWN-1);
		segment.drawLine(BORDER_LEFT,this.getHeight()-BORDER_DOWN,BORDER_LEFT+LONG_POINT,this.getHeight()-BORDER_DOWN);
		segment.drawString("0.0", BORDER_LEFT-10 , this.getHeight() - (int)(BORDER_DOWN/3));
		
		
		segment.setFont(new Font("Arial", 0, 5));
		int Xstamp = BORDER_LEFT + WIDTH_BETWEEN_TIMESTAMPS , i = 1;
		
		while(Xstamp < this.longX_Axis+(this.BORDER_LEFT+2)) {
			double time = i * this.timeInPixel * WIDTH_BETWEEN_TIMESTAMPS;
			
			segment.drawLine(Xstamp,this.getHeight()-BORDER_DOWN-1,Xstamp+LONG_POINT,this.getHeight()-BORDER_DOWN-1);
			segment.drawLine(Xstamp,this.getHeight()-BORDER_DOWN,Xstamp+LONG_POINT,this.getHeight()-BORDER_DOWN);
			segment.drawString(df.format(time), Xstamp-10 , this.getHeight() - (int)(BORDER_DOWN/2));
			
			i++;
			Xstamp+= WIDTH_BETWEEN_TIMESTAMPS ;
		}
	}

	private void drawAllTask(Graphics2D g) {
		if(this.taskInfo!= null) {
			drawMPIFunctions(g);
			drawESFunctions(g);
			drawMPIComunications(g);
		}
	}
	
	private void drawTask(HashMap<String, Object> function, Graphics2D g) {
		
		double timeIni = Double.parseDouble(function.get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
		double timeEnd = Double.parseDouble(function.get("xEnd").toString().replaceAll(",", ".")) - this.timeInitial;
		int myRank = Integer.parseInt(function.get("Rank").toString());
		String nameFunction = function.get("Task").toString();
		
		//convertimos marcas de tiempo 
		int xIni = (int) Math.round(timeIni / this.timeInPixel)+ BORDER_LEFT;
		int xEnd = (int) Math.round(timeEnd / this.timeInPixel)+ BORDER_LEFT;
		int y = HEIGHT - BORDER_DOWN - ((this.heightOfRanks*2) + (this.heightOfRanks*2*myRank)); // calculamos punto y donde empezara a dibujarse
		
		//LOG.info("Receive timeIni:" + timeIni + " -> "+ xIni + " timeEnd: " + timeEnd + " -> " + xEnd);
		
		Graphics2D segment = (Graphics2D) g;
		
		for(int j = 0; j <= this.heightOfRanks; j++) {
			segment.setColor(this.colors.get(nameFunction));
			segment.drawLine(xIni,y+j,xEnd,y+j);
		}
		
		
		//LOG.info("COLOR FOR TASK : " + nameFunction + " C: " + this.colors.get(nameFunction) );
		/*if(nameFunction.contains("MPI")) { // si es MPI se dibujara la tarea en la parte baja del rank
			y += (int)(this.heightOfRanks/2);
			for(int j = 0; j <= (int)(this.heightOfRanks/2); j++) {
				segment.setColor(this.colors.get(nameFunction));
				segment.drawLine(xIni,y+j,xEnd,y+j);
			}
		}
		else {
			if(nameFunction.toLowerCase().contains("wri") || nameFunction.toLowerCase().contains("rea")) {//write or read
				y += (int)(this.heightOfRanks/4);
				for(int j = 0; j <= (int)(this.heightOfRanks/4); j++) {
					segment.setColor(this.colors.get(nameFunction));
					segment.drawLine(xIni,y+j,xEnd,y+j);
				}
			}
			else {//creat,open,close
				for(int j = 0; j <= (int)(this.heightOfRanks/4); j++) {
					segment.setColor(this.colors.get(nameFunction));
					segment.drawLine(xIni,y+j,xEnd,y+j);
				}
			}
			
			
		}*/
		//Lineas de tiempo
		//segment.setColor(Color.black);
		//segment.drawLine(xIni,y,xIni,this.HEIGHT - this.BORDER_DOWN);
		//segment.drawLine(xEnd,y,xEnd,this.HEIGHT - this.BORDER_DOWN);
		
	}
	
	private void drawComunication(HashMap<String, Object> source,HashMap<String, Object> dest, Graphics2D g) {
		String nameFunction = source.get("Task").toString();
		
		if( source.get("xIni") != null && source.get("xEnd")!= null && source.get("Rank")!= null && 
			dest.get("xIni")   != null && dest.get("xEnd")  != null && dest.get("Rank")  != null) {
			
			double timeIniSource = Double.parseDouble(source.get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
			double timeEndSource = Double.parseDouble(source.get("xEnd").toString().replaceAll(",", ".")) - this.timeInitial;
			int rankSource = Integer.parseInt(source.get("Rank").toString());
			
			double timeIniDest = Double.parseDouble(dest.get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
			double timeEndDest = Double.parseDouble(dest.get("xEnd").toString().replaceAll(",", ".")) - this.timeInitial;
			int rankDest = Integer.parseInt(dest.get("Rank").toString());
			
			int xIni = (int) Math.round(timeIniSource / this.timeInPixel)+ BORDER_LEFT;
			int xEnd = (int) Math.round(timeEndDest / this.timeInPixel)+ BORDER_LEFT;
			int yIni = HEIGHT - BORDER_DOWN - ((this.heightOfRanks*2) + (this.heightOfRanks*2*rankSource)); // calculamos punto y donde empezara a dibujarse
			int yEnd = HEIGHT - BORDER_DOWN - ((this.heightOfRanks*2) + (this.heightOfRanks*2*rankDest));
			
			Graphics2D segment = (Graphics2D) g;
			segment.setColor(this.colors.get(nameFunction));
			segment.drawLine(xIni, yIni , xEnd, yEnd+this.heightOfRanks);
		}
		
		
	}
	
	private void drawMPIFunctions(Graphics2D g) {
		
		for(Map<String, Object> task : this.taskInfo.getListTaskAndComunications()) {
			if(task.containsKey("Function")) {
				Map function = (Map) task.get("Function");
				if(function.get("Task").toString().contains("MPI")) {
					drawTask((HashMap<String, Object>) task.get("Function"), g);
				}
			}
		}
	}
	
	
	private void drawMPIComunications(Graphics2D g) {
		
		for(Map<String, Object> task : this.taskInfo.getListTaskAndComunications()) {
			if(!task.containsKey("Function")) { // Only MPI CALLS
				drawComunication((HashMap<String, Object>)task.get("Source"),(HashMap<String, Object>)task.get("Dest"),g);
			}
		}
	}
	
	private void drawESFunctions(Graphics2D g) {
		
		for(Map<String, Object> task : this.taskInfo.getListTaskAndComunications()) {
			if(task.containsKey("Function")) {
				Map function = (Map) task.get("Function");
				if(!function.get("Task").toString().contains("MPI")) { // Solo llamadas de E/S
					drawTask((HashMap<String, Object>) task.get("Function"), g);
				}
			}
		}
	}

	private void drawStepTask(Graphics2D g2) {
		// TODO Auto-generated method stub
		System.out.println("STEP TASK: " + this.indexCurrentTask);
		double timeIni = 0;
		if(this.indexCurrentTask >= 0) {
			for(int i = 0 ; i < this.indexCurrentTask; i++) {
				//LOG.info(taskInfo.getOrderListTask().get(i).toString());
				Map<String,Object> task = taskInfo.getOrderListTask().get(i).getTask() ;
				
				if(task.containsKey("Function")) {
					drawTask((HashMap<String, Object>) task.get("Function"), (Graphics2D) g2);
					timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Function")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				}
				else {
					drawComunication((HashMap<String, Object>)task.get("Source"),(HashMap<String, Object>)task.get("Dest"),(Graphics2D) g2);
					timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Source")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				}
			}
			
			
		}
		
		int xIni = (int) Math.round(timeIni / this.timeInPixel)+ BORDER_LEFT;
		xDiff = -xIni + WIDTH_BETWEEN_TIMESTAMPS ;
		dragger = true;
		
	}
	

	@SuppressWarnings("unchecked")
	private void moveToTask(Graphics2D g2) {
		// TODO Auto-generated method stub
		if(taskInfo != null) {
			Map<String,Object> task = taskInfo.getOrderListTask().get(this.indexCurrentTask).getTask() ;
			double timeIni;
			if(task.containsKey("Function")) {
				
				timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Function")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				String rank = ((HashMap<String, Object>) task.get("Function")).get("Rank").toString();
				this.controller.setTaskInChartMenuPanel(((HashMap<String, Object>) task.get("Function")).get("Task").toString() + " [" + rank + "]", myChart);
			}
			else {
				timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Source")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				String nameTask = ((HashMap<String, Object>) task.get("Source")).get("Task").toString();
				nameTask = nameTask.split("_",3)[0]+"_"+ nameTask.split("_",3)[1];
				String source = ((HashMap<String, Object>) task.get("Source")).get("Rank").toString();
				String dest = ((HashMap<String, Object>) task.get("Dest")).get("Rank").toString();
				this.controller.setTaskInChartMenuPanel(nameTask + " [" + source + "]->[" + dest + "]", myChart);
			}
			
			int xIni = (int) Math.round(timeIni / this.timeInPixel)+ BORDER_LEFT;
			xDiff = -xIni + WIDTH_BETWEEN_TIMESTAMPS ;
			//LOG.info("X CALCULADO: " + xIni + " xdiff:" + xDiff);
			dragger = true;
			repaint();
		}
	}
	
	public void drawNextTask() {
		if(this.stepByStep) {
			if(this.indexCurrentTask < taskInfo.getOrderListTask().size()-1)this.indexCurrentTask++;
			Map<String,Object> task = taskInfo.getOrderListTask().get(this.indexCurrentTask-1).getTask() ;
			LOG.info("INDEX: " + this.indexCurrentTask + ",TASK: " + task);
			double timeIni;
			if(task.containsKey("Function")) {
				
				timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Function")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				String rank = ((HashMap<String, Object>) task.get("Function")).get("Rank").toString();
				this.controller.setTaskInChartMenuPanel(((HashMap<String, Object>) task.get("Function")).get("Task").toString() + " [" + rank + "] " + df.format(timeIni), myChart);
			}
			else {
				timeIni = Double.parseDouble(((HashMap<String, Object>) task.get("Source")).get("xIni").toString().replaceAll(",", ".")) - this.timeInitial;
				String nameTask = ((HashMap<String, Object>) task.get("Source")).get("Task").toString();
				nameTask = nameTask.split("_",3)[0]+"_"+ nameTask.split("_",3)[1];
				String source = ((HashMap<String, Object>) task.get("Source")).get("Rank").toString();
				String dest = ((HashMap<String, Object>) task.get("Dest")).get("Rank").toString();
				this.controller.setTaskInChartMenuPanel(nameTask + " [" + source + "]->[" + dest + "] " + df.format(timeIni), myChart);
			}
			repaint();
		}
		else {
			if(this.indexCurrentTask < taskInfo.getOrderListTask().size()-1)this.indexCurrentTask++;
			moveToTask((Graphics2D) this.getGraphics());
		}
	}
	
	public void drawPreviusTask() {
		//if(this.indexCurrentTask == taskInfo.getOrderListTask().size() -1) this.stepByStep = true;
		if(this.stepByStep) {
			System.out.println("INDEX--: " + this.indexCurrentTask);
			if(this.indexCurrentTask > 0)this.indexCurrentTask--;
			repaint();
		}
		else {
			if(this.indexCurrentTask > 0)this.indexCurrentTask--;
			moveToTask((Graphics2D) this.getGraphics());
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		JFrame window = new JFrame("Chart");
		BuildTaskInformation taskInfo = new BuildTaskInformation("/Users/pablomorer/Desktop/TFG/TraceLib/Ejecuciones/Broadcast/4Workers/virtual_trace.json", LOG);
		Chart myChart = new Chart(taskInfo,1,false, LOG ,1);
		
		window.add(myChart);
		myChart.setVisible(true);
		window.setSize(myChart.WIDTH+20, myChart.HEIGHT+40);
		window.setVisible(true);
		
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		Thread.sleep(3000);
		
		
	}
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	private Color colorLine(String task) {
		if(this.colors.containsKey(task))
			return this.colors.get(task);
		else return Color.BLACK;
	}
	
	
	/**
	 * Move the graphic foreward
	 * @param moveX pixels moved in x
	 * @param moveY pixels moved in y
	 */
	public void moveForewardGraphic(int moveX, int moveY) {
		
		//Point curPoint = e.getLocationOnScreen();
        xDiff -= moveX;
        yDiff -= moveY;
        dragger = true;
        repaint();
	}

	/**
	 * Move the graphic back
	 * @param moveX pixels moved in x
	 * @param moveY pixels moved in y
	 */
	public void moveBackGraphic(int moveX, int moveY) {
		
		//Point curPoint = e.getLocationOnScreen();
        xDiff += moveX;
        yDiff += moveY;
        dragger = true;
        repaint();
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		Point curPoint = e.getLocationOnScreen();
		//LOG.info("ANTES DRAGGER curPoint.x: " + curPoint.x + "startPoint.x: " + startPoint.x + "xOffset : " + xOffset + "xDiff = " + xDiff);
        xDiff = xDiff + ((curPoint.x - startPoint.x) / 16);
        //LOG.info("DESPUES DRAGGER curPoint.x: " + curPoint.x + "startPoint.x: " + startPoint.x + "xOffset : " + xOffset + "xDiff = " + xDiff);
        //yDiff = curPoint.y - startPoint.y;

        dragger = true;
        repaint();
		
	}
	
	

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		Point curPoint = e.getLocationOnScreen();
		try {
			Robot r = new Robot(); 
			Color color = r.getPixelColor(curPoint.x, curPoint.y);
			if(this.functionColors.get(color) != null) {
				double myX = MouseInfo.getPointerInfo().getLocation().getX() - xDiff - getLocationOnScreen().getX() - BORDER_LEFT;
				this.controller.setTaskInChartMenuPanel(this.functionColors.get(color) + df.format((myX * this.timeInPixel)) , this.myChart);
			}
			else {
				//LOG.info("COLOR NOT FOUND: " + color);
				this.controller.setTaskInChartMenuPanel("No task",this.myChart);
			}
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		//released = false;
	    startPoint = MouseInfo.getPointerInfo().getLocation();
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		//released = true;
        //repaint();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
				/*zoomer = true;

		        //Zoom in
		        if (e.getWheelRotation() < 0) {
		            zoomFactor *= 1.1;
		            repaint();
		        }
		        //Zoom out
		        if (e.getWheelRotation() > 0) {
		            zoomFactor /= 1.1;
		            repaint();
		        }*/
	}
	
	public void setController(Controller controller) {
		LOG.info("SET CONTROLLER CHART");
		this.controller = controller;
	}
	
	private void initcolors() {
		
		this.colors = new HashMap<String,Color>();
		this.colors.put("Creat", colorsApp.CREAT_COLOR);
		this.colors.put("Open", colorsApp.OPEN_COLOR);
		this.colors.put("fOpen", colorsApp.FOPEN_COLOR);
		this.colors.put("Close", colorsApp.CLOSE_COLOR);
		this.colors.put("fClose", colorsApp.FCLOSE_COLOR);
		this.colors.put("Write", colorsApp.WRITE_COLOR);
		this.colors.put("fWrite", colorsApp.FWRITE_COLOR);
		this.colors.put("pWrite", colorsApp.PWRITE_COLOR);
		this.colors.put("Read", colorsApp.READ_COLOR);
		this.colors.put("fRead", colorsApp.FREAD_COLOR);
		this.colors.put("pRead", colorsApp.PREAD_COLOR);
		this.colors.put("MPI_Send", colorsApp.MPI_SEND_COLOR);
		this.colors.put("MPI_Send_Com", colorsApp.MPI_SEND_COM_COLOR);
		this.colors.put("MPI_Recv", colorsApp.MPI_RECV_COLOR);
		this.colors.put("MPI_Recv_Com", colorsApp.MPI_RECV_COM_COLOR);
		this.colors.put("MPI_Scatter", colorsApp.MPI_SCATTER_COLOR);
		this.colors.put("MPI_Scatter_Com",colorsApp.MPI_SCATTER_COM_COLOR);
		this.colors.put("MPI_Gather", colorsApp.MPI_GATHER_COLOR);
		this.colors.put("MPI_Gather_Com", colorsApp.MPI_GATHER_COM_COLOR);
		this.colors.put("MPI_Broadcast", colorsApp.MPI_BCAST_COLOR);
		this.colors.put("MPI_Bcast_Com", colorsApp.MPI_BCAST_COM_COLOR);
		
	}
	
	private void initFunctionsNameColors() {
		
		this.functionColors = new HashMap<Color,String>();
		
		functionColors.put(colorsApp.OTHER_CREAT_COLOR, "creat ");
		functionColors.put(colorsApp.OTHER_OPEN_COLOR, "open ");
		functionColors.put(colorsApp.OTHER_FOPEN_COLOR, "fopen ");
		functionColors.put(colorsApp.OTHER_CLOSE_COLOR, "close ");
		functionColors.put(colorsApp.OTHER_FCLOSE_COLOR, "fclose ");
		functionColors.put(colorsApp.OTHER_WRITE_COLOR, "write ");
		functionColors.put(colorsApp.OTHER_FWRITE_COLOR, "fwrite ");
		functionColors.put(colorsApp.OTHER_PWRITE_COLOR, "pwrite ");
		functionColors.put(colorsApp.OTHER_READ_COLOR, "read ");
		functionColors.put(colorsApp.OTHER_FREAD_COLOR, "fread ");
		functionColors.put(colorsApp.OTHER_PREAD_COLOR, "pread ");
		functionColors.put(colorsApp.OTHER_MPI_SEND_COLOR, "MPI_Send ");
		functionColors.put(colorsApp.OTHER_MPI_SEND_COM_COLOR, "MPI_Send ");
		functionColors.put(colorsApp.OTHER_MPI_RECV_COLOR, "MPI_Recv ");
		functionColors.put(colorsApp.OTHER_MPI_RECV_COM_COLOR, "MPI_Recv ");
		functionColors.put(colorsApp.OTHER_MPI_GATHER_COLOR, "MPI_Gather ");
		functionColors.put(colorsApp.OTHER_MPI_GATHER_COM_COLOR, "MPI_Gather ");
		functionColors.put(colorsApp.OTHER_MPI_SCATTER_COLOR, "MPI_Scatter ");
		functionColors.put(colorsApp.OTHER_MPI_SCATTER_COM_COLOR, "MPI_Scatter ");
		functionColors.put(colorsApp.OTHER_MPI_BCAST_COLOR, "MPI_Broadcast ");
		functionColors.put(colorsApp.OTHER_MPI_BCAST_COM_COLOR, "MPI_Broadcast ");
		functionColors.put(colorsApp.OTHER_WAITING_COLOR_ES, "CPU ");
		functionColors.put(colorsApp.OTHER_WAITING_COLOR_MPI, "CPU ");
	}




	/**
	 * Variables necesarias para zoom
	 */
	private double zoomFactor = 1;
	private double prevZoomFactor = 1;
	private boolean zoomer;
	private boolean dragger;
	private boolean released;
	private double xOffset = 0;
	private double yOffset = 0;
	private int xDiff;
	private int yDiff;
	private Point startPoint;
	/**
	 * Variables necesarias para zoom
	 */
	
	
	@Override
	public String toString() {
		return "ChartNew [ timeInPixel=" + timeInPixel + ", ranks="
				+ ranks + ", timeInitial=" + timeInitial + ", timeEnd=" + timeEnd + ", longX_Axis=" + longX_Axis
				+ ", longY_Axis=" + longY_Axis + "]";
	}
}
