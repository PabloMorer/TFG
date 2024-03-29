package Application.Model.CustomTrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import Logger.AppLog;


public class ConvertTrace {

	
	/**
	 * Name of the generated file
	 */
	private String fileName;
	
	/**
	 * Trace file
	 */
	private File traceFile;
	
	/**
	 * Boolean true if the trace has converted
	 */
	private boolean converted;

	private AppLog LOG;
	
	
	public ConvertTrace(AppLog log) {
		this.LOG = log;
	}
	/**
	 * Constructor, initialize @fileName and @traceFile with trace info
	 * @param traceFile Trace source
	 * @param pathDest Path destination
	 */
	public ConvertTrace(File traceFile, String pathDest, AppLog log) {
		// TODO Auto-generated constructor stub
		this.LOG = log;
		this.fileName = traceFile.getName().replaceAll(".txt", "Converted.txt") ;
		this.traceFile = traceFile;
		generateTrace(pathDest);
	}

	/**
	 * Convert trace reading @traceFile
	 */
	private void generateTrace(String path) {
		// TODO Auto-generated method stub
		BufferedReader reader = openFile(traceFile);
		if(reader != null) {
			String traceConverted = convertTrace(reader);
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeTrace(traceConverted, path,this.fileName);
			
		}
	}
	
	/**
	 * Write @traceConverted in a File in @PATH with name @fileName
	 * @param traceConverted String with new trace information
	 */
	private void writeTrace(String traceConverted, String path, String nameNewTrace) {
		// TODO Auto-generated method stub
		 FileWriter fileWriter;
		try {
			if(!path.endsWith(File.separator)) path += File.separator;
			fileWriter = new FileWriter(path + nameNewTrace);
			 BufferedWriter buffer = new BufferedWriter(fileWriter);
			 buffer.write(traceConverted);
			 buffer.close();
			 this.converted = true;
			 
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read the old trace and delete IniTimes of ranks and generate new trace file with timeStamp modified
	 * Se modifica las marcas temporales de cada linea teniendo en cuanta la primera tarea de cada rank
	 * @param reader File opened, ready for be readed
	 * @return a String with new trace info
	 */
	private String convertTrace(BufferedReader reader) {
		
		String trace = "";
		HashMap<Integer,Double> iniTimes = new HashMap<Integer,Double>();
		Double minTimeStamp = 0.0;
		try {
			String line = reader.readLine() ;
			String[] infoLine = line.split(" "); // [0]->time, [1]->task, [2]->id, [3]->Rank, [4]->timeStamp
			minTimeStamp = Double.valueOf(infoLine[4]);
			
			//read N lines with IniTime info of each rank
			while(infoLine[1].equals("Ini_time ") || infoLine[1] == "Ini_time " ) {
				
				iniTimes.put(Integer.parseInt(infoLine[3]),Double.valueOf(infoLine[4]));
				
				if(Double.valueOf(infoLine[4]) < minTimeStamp) { // save de min timeStamp
					minTimeStamp = Double.valueOf(infoLine[4]);
				}
				
				line = reader.readLine() ;
				infoLine = line.split(" ");
			}
			
			DecimalFormat df = new DecimalFormat("###.########");
			df.setMinimumIntegerDigits(5);
			df.setMinimumFractionDigits(6);
			LOG.info("INIT TIMES: " + iniTimes);
			LOG.info("MIN TIME: " + df.format(minTimeStamp));
			
			for(Entry<Integer,Double> timeStamp : iniTimes.entrySet()) { //recover all IniTimes and put in map, which key is the rank
				iniTimes.put(timeStamp.getKey(),timeStamp.getValue().doubleValue()-minTimeStamp.doubleValue());
			}
			
			LOG.info("INIT TIMES: " + iniTimes);
			
			
			PriorityQueue<TaskInformation> taskList = new PriorityQueue<TaskInformation>(); // ordered list by timeStamp
			
			while(line!= null) {
				
				//LOG.info("LINE: " + line);
				String newTimeStamp = df.format(Double.parseDouble(infoLine[0]) + iniTimes.get(Integer.parseInt(infoLine[3]))); 
				
				String newLine = newTimeStamp ;
				
				for(int i = 1; i < infoLine.length; i++) {
					newLine += " "+ infoLine[i].replaceAll("[^a-zA-Z0-9_.-_/-]", "");
				}
				
				newLine = newLine.replace(",", ".");
				newLine = newLine.replace("-1", "99999999");
				newLine += "\n";
				LOG.info("Line:" + line + ",New Line: " + newLine);
				
				try {
					taskList.add(new TaskInformation( (Double) df.parse(newTimeStamp),newLine));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				line = reader.readLine() ;
				if(line != null) {infoLine = line.split(" ");}
			}
			
			//generate string with converted information
			for(TaskInformation task : taskList) {
				trace += task.getLine() ;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return trace;
	}

	/**
	 * Custom @file trace and save in @destPath
	 * @param file source Trace file
	 * @param destPath destination path
	 */
	public String customSIMCANTrace(File file, String destPath) {
		
		BufferedReader reader = openFile(file);
		
		if(reader != null) {
			String traceConverted = convertSIMCANTrace(reader);
			try {
				FileWriter fileWriter;
				if(!destPath.endsWith(File.separator)) destPath += File.separator;
				fileWriter = new FileWriter(destPath + file.getName());
				BufferedWriter buffer = new BufferedWriter(fileWriter);
				buffer.write(traceConverted);
				buffer.close();
				reader.close();
			} catch (IOException e) {
				LOG.error(e);
				return "KO";
			}
			
		}
		return "OK";
	}
	
	private String convertSIMCANTrace(BufferedReader reader) {
		// TODO Auto-generated method stub
		String newTrace = "";
		DecimalFormat df = new DecimalFormat("###.########");
		df.setMinimumIntegerDigits(5);
		df.setMinimumFractionDigits(6);
		
		String line;
		try {
			line = reader.readLine();
			String[] infoLine = line.split(" ");
			
			while(line!= null) {
				
				//LOG.info("LINE: " + line);
				String newTimeStamp = df.format(Double.parseDouble(infoLine[0])); 
				
				String newLine = newTimeStamp ;
				
				for(int i = 1; i < infoLine.length; i++) {
					newLine += " "+ infoLine[i].replaceAll("[^a-zA-Z0-9_.-]", "");
				}
				
				newLine = newLine.replace(",", ".");
				newLine += "\n";
				
				newTrace += newLine;
				
				LOG.info("Line:" + line + ",New Line: " + newLine);
				
				line = reader.readLine() ;
				if(line != null) infoLine = line.split(" ");
			}
			
		} catch (IOException e) {
			LOG.error(e);
		}
		
		return newTrace;
	}

	/**
	 * Open a File @traceFile and return bufferReader ready for be readed
	 * @param traceFile
	 * @return BufferedReader opened
	 */
	private BufferedReader openFile(File traceFile) {
		
		FileReader fileReader= null;
		BufferedReader reader = null;
		try {
			fileReader = new FileReader(traceFile);
			reader = new BufferedReader(fileReader);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return reader;
	}

	/**
	 * @return @fileName
	 */
	public String getNameTrace() {
		return fileName;
	}

}
