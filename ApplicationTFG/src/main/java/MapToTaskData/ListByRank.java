/**
 * 
 */
package MapToTaskData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import Logger.AppLog;



/**
 * Clase de pruebas.
 * @author bryanrvvargas
 *
 */
public class ListByRank {
	
	private static String PATH_MAP = "/Users/bryanrvvargas/Desktop/pruebas";
	private static String NAME_JSON = "virtual_trace.json";

	

	public static void main(String[] args) throws FileNotFoundException {
		
		long timeStart, timeEnd;
		timeStart = System.currentTimeMillis();
		BuildTaskInformation taskInfo = new BuildTaskInformation("/Users/pablomorer/Desktop/josons/virtual_trace8Proc.json", new AppLog());
		timeEnd = System.currentTimeMillis();
		
		//System.out.println("TIME:" + taskInfo.getTaskListByRank());
		
		
		
	}

	private static ArrayList<Map<String, Object>> readFile(String fileName) throws FileNotFoundException {
		
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(fileName));
		ArrayList<Map<String,Object>> data = gson.fromJson(reader, Object.class);
		
		
		return data;
	}




	
}
