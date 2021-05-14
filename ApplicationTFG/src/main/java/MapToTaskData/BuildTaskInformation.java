/**
 * 
 */
package MapToTaskData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import Logger.AppLog;


/**
 * @author bryanrvvargas
 *
 */
public class BuildTaskInformation {
	
	private static String KEY_TIME = "TimeStamp";
	private static String KEY_TASK_NAME = "TaskName";
	private static String KEY_MY_RANK = "MyRank";
	private static String KEY_OTHER_RANK = "OtherRank";
	private static String KEY_SIZE = "Size";
	private static String KEY_ID = "Id";
	private static String KEY_RANK_ = "RANK_";
	private static String SEND = "_send";
	private static String RECV = "_recv";
	private static String BCAST = "_bcas";
	private static String SCAT = "_scat";
	private static String GATH = "_gath";
	private static String INI = "Ini";
	private static String END = "End";
	
	private static List<String> taskNamesList =  Arrays.asList(SEND,RECV,BCAST,SCAT,GATH); 
	private static List<String> taskNamesCompleteList = Arrays.asList("MPI_Send_Com","MPI_Recv_Com","MPI_Bcast_Com","MPI_Scatter_Com","MPI_Gather_Com"); 
	
	private String pathfile;
	
	//private static String PATH_MAP = "/Users/bryanrvvargas/Desktop/TFG/workspace/virtual_trace.json";
	//private static String NAME_JSON = "virtual_trace.json";
	
	/**
	 * 
	 */
	private Map<String, ArrayList<Map<String, Object>>> taskListByRank;
	
	
	/**
	 * Listado de tareas 
	 */
	private ArrayList<Map<String, Object>> listTaskAndComunications;
	
	
	/**
	 * 
	 */
	private HashMap<String,String> functions;
	
	/**
	 * Numero de ranks 
	 */
	private int ranks; // numero de ranks
	
	/**
	 * Numero maximo de tareas en un rank
	 */
	private int maxTaskInRank; // mayor numero de tareas en un rank
	
	/**
	 * Tiempo minimo entre dos tareas
	 */
	private double minTimeTask;// duración de la tarea más corta
	/**
	 * Marca de tiempo de la ultima tarea
	 */
	private double timeEnd;
	/**
	 * Marca de tiempo de la primera tarea
	 */
	private double timeIni;
	
	/**
	 * Listado de tareas ordenadas
	 */
	private ArrayList<TaskData> orderListTask;
	private AppLog LOG;
	
	
	public BuildTaskInformation(String pathFile, AppLog log) throws FileNotFoundException  {
		ArrayList<Map<String, Object>> taskList;
		this.LOG = log;
		initializeFunctionsNames();
		this.maxTaskInRank = 0;
		this.pathfile = pathFile;
		taskList = readFile(pathFile);
		this.timeIni = Double.parseDouble(taskList.get(0).get(KEY_TIME).toString().replaceAll(",", "."));
		this.timeEnd = Double.parseDouble(taskList.get(taskList.size()-1).get(KEY_TIME).toString().replaceAll(",", "."));
		this.taskListByRank = generateTaskListByRank(taskList);
		LOG.info("\n\nTask list: " + this.taskListByRank);
		this.ranks = this.taskListByRank.size();
		this.minTimeTask = -1;
		this.listTaskAndComunications = getListOfComunications(generateTaskListAllRanks(taskList));
		LOG.info("\n\n Task list com: " + this.listTaskAndComunications);
		this.orderListTask = getOrderTaskList(listTaskAndComunications);
		System.out.println("\n\n FINAL ORDER LIST" + this.orderListTask);
		DecimalFormat df = new DecimalFormat("###.########");
		//LOG.info("\n\nMIN TIME TASK IS: " + df.format( this.minTimeTask ) + "\n\n");
			
		
		
	}
	
	
	


	/**
	 * Genera lista de tareas, ordenandolas a partir de la @listOfComunications
	 * @param listOfComunications Listado de tareas que pueden ser funciones o comunicaciones entre ranks
	 * @return
	 */
	private ArrayList<TaskData> getOrderTaskList(ArrayList<Map<String, Object>> listOfComunications) {
		// TODO Auto-generated method stub
		//System.out.println("LIST " + listOfComunications);
		ArrayList<TaskData> orderList = new ArrayList<TaskData>(listOfComunications.size());
		
		for(Map<String, Object> currentTask : listOfComunications) {
			TaskData task = new TaskData(currentTask);
			if(task.getTask()!= null)orderList.add(task);
		}
		
		orderList.sort(null);
		//System.out.println("OrderList" + orderList);
		
		return orderList;
	}





	/**
	 * Lee fichero
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	private ArrayList<Map<String, Object>> readFile(String fileName) throws FileNotFoundException {
		
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(fileName));
		ArrayList<Map<String,Object>> data = gson.fromJson(reader, Object.class);
		
		
		return data;
	}
	
	/** Metodo que convierte el listado de tareas en un map con clave Worker y el valor es el listado de tareas asociadas al Worker
	 * @param taskList
	 * @return Map where key is Rank and value is list of task executed by rank 
	 */
	private Map<String, ArrayList<Map<String, Object>>> generateTaskListByRank(ArrayList<Map<String, Object>> taskList) {
		
		Map<String,ArrayList<Map<String,Object>>> listsByRank = new TreeMap<String,ArrayList<Map<String,Object>>>();
		
		for(Map<String,Object> task : taskList){
		
			String keyNameRank = KEY_RANK_ + task.get(KEY_MY_RANK);
			if(listsByRank.containsKey(keyNameRank)){
				listsByRank.get(keyNameRank).add(task);	
				if(listsByRank.get(keyNameRank).size() > this.maxTaskInRank) this.maxTaskInRank = listsByRank.get(keyNameRank).size();
			}
			else{
				listsByRank.put(keyNameRank, new ArrayList<Map<String,Object>>());
				listsByRank.get(keyNameRank).add(task);
			}
			
		}
		return listsByRank;
	}
	
	
	/**Metodo que me devuelve Lista de tareas de comunicacion, send, recv.
	 * @param taskList
	 * @return Map con envio y recepcion de datos
	 */
	private Map<String,ArrayList<Map<String,Object>>> generateTaskListComunications(ArrayList<Map<String, Object>> taskList) {
		
		Map<String,ArrayList<Map<String,Object>>> listsByRank = new TreeMap<String,ArrayList<Map<String,Object>>>();
		
		for(Map<String,Object> task : taskList){
			if(task.get(KEY_TASK_NAME).toString().contains(SEND) || task.get(KEY_TASK_NAME).toString().contains(RECV)|| 
					task.get(KEY_TASK_NAME).toString().contains(BCAST)|| task.get(KEY_TASK_NAME).toString().contains(SCAT) || task.get(KEY_TASK_NAME).toString().contains(GATH)) {
				String keyNameRank = KEY_RANK_ + task.get(KEY_MY_RANK);
				if(listsByRank.containsKey(keyNameRank)){
					listsByRank.get(keyNameRank).add(task);	
				}
				else{
					listsByRank.put(keyNameRank, new ArrayList<Map<String,Object>>());
					listsByRank.get(keyNameRank).add(task);
				}
			}
		}
		
		return listsByRank;	
	}
	
	/**Metodo que me devuelve Lista de tareas de tareas de todos los ranks
	 * @param taskList
	 * @return Map con envio y recepcion de datos
	 */
	private Map<String,ArrayList<Map<String,Object>>> generateTaskListAllRanks(ArrayList<Map<String, Object>> taskList) {
		
		Map<String,ArrayList<Map<String,Object>>> listsByRank = new TreeMap<String,ArrayList<Map<String,Object>>>();
		
		for(Map<String,Object> task : taskList){
			
			String keyNameRank = KEY_RANK_ + task.get(KEY_MY_RANK);
			if(listsByRank.containsKey(keyNameRank)){
				listsByRank.get(keyNameRank).add(task);	
			}
			else{
				listsByRank.put(keyNameRank, new ArrayList<Map<String,Object>>());
				listsByRank.get(keyNameRank).add(task);
			}
			
		}
		
		return listsByRank;	
	}
	/**
	 * Metodo que extrae del  @comunicationsByRank todas las comunicaciones entre workers, dependiendo del tipo de tareas (send,recv,bcast)
	 * @param comunicationsByRank
	 * @return
	 */
	public ArrayList<Map<String, Object>>  getListOfComunications(Map<String,ArrayList<Map<String,Object>>> comunicationsByRank){
		ArrayList<Map<String,Object>> listComunications = new ArrayList<Map<String,Object>>();
		int rankActual = 0;
		for(Entry<String,ArrayList<Map<String,Object>>> rank : comunicationsByRank.entrySet()) { //recorremos todas las tareas de cada rank
			ArrayList<Map<String,Object>> taskListRank = rank.getValue(); //obtenemos la lista de tareas del rank actual
			//LOG.info("RECOVER LIST OF RANK: " + rank.getKey()); 
			int indexTask = 0;
			while( indexTask < taskListRank.size()) { // recorremos cada tarea
				Map<String,Object> task = taskListRank.get(indexTask);
				
				Map<String,Object> otherTask = null ;
				int indexOtherTask;
				boolean found = false;
				for( indexOtherTask = indexTask+1 ; indexOtherTask < taskListRank.size()  ; indexOtherTask++) { // buscamos la tarea con el mismo id
					if(taskListRank.get(indexOtherTask).get(KEY_ID).equals(task.get(KEY_ID)) ) { 
						otherTask = taskListRank.get(indexOtherTask); 
						break;   
					}
				}
				//LOG.info("TASK: " + task + " OtherTask: "+ otherTask);
				if(otherTask != null && (task.get(KEY_TASK_NAME).toString().contains("send") || task.get(KEY_TASK_NAME).toString().contains("recv")) ) {//tareas que se conecten con otro rank
					if(!task.get(KEY_OTHER_RANK).toString().equals("99999999")) { //Tareas que se conoce origen y destino
						generateComunicationInfo(task,indexTask,otherTask,indexOtherTask,taskListRank,comunicationsByRank,listComunications);
					}
					else { //Tareas que desconoce uno de los ranks
						generateAndSearchComunicationInfo(task,indexTask,otherTask,indexOtherTask,taskListRank,comunicationsByRank,listComunications);
					}	
				}
				else if(otherTask != null && (task.get(KEY_TASK_NAME).toString().contains("bcas") || task.get(KEY_TASK_NAME).toString().contains("scat") 
						|| task.get(KEY_TASK_NAME).toString().contains("gath"))) { // tareas de multiples comunicaciones
					generateMultipleComunicationInfo(task,indexTask,otherTask,indexOtherTask,taskListRank,comunicationsByRank,listComunications);
				}
				else if(otherTask != null){ // tareas de E/S
					generateTaskInfo(task,otherTask,listComunications);
					taskListRank.remove(indexOtherTask);
					taskListRank.remove(indexTask);
				}
				else {
					LOG.error("+++++++++++++++++++++++++++++++++ERROR task " + task + "doesnt have a other task");
				}
				
			}
			rankActual++;
		}
		
		return listComunications;
	}
	
	/**
	 * A partir de las tareas, es capaz de generar un Map con informacion de origen y destindo y nombre de tarea.
	 * @param task
	 * @param indexTask
	 * @param otherTask
	 * @param indexOtherTask
	 * @param taskListRank
	 * @param comunicationsByRank
	 * @param listComunications 
	 */
	private void generateMultipleComunicationInfo(Map<String, Object> task, int indexTask,
			Map<String, Object> otherTask, int indexOtherTask, ArrayList<Map<String, Object>> taskListRank,
			Map<String, ArrayList<Map<String, Object>>> comunicationsByRank, ArrayList<Map<String, Object>> listComunications) {
		
		int posIniRankSend = -1, posEndRankSend = -1;
		String taskName = task.get(KEY_TASK_NAME).toString().contains("Ini") ? task.get(KEY_TASK_NAME).toString(): otherTask.get(KEY_TASK_NAME).toString();
		
		//busco en rankOther la tarea taskname
		int i  , j;
		for(i = 0; i < comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).size();  i++) {
			if(comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i).get(KEY_TASK_NAME).toString().equals(taskName) &&
					comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i).get(KEY_SIZE).toString().equals(task.get(KEY_SIZE).toString())) break;
		}
		for(j = 0; j < comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).size();  j++) {
			
			if(comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(j).get(KEY_ID).equals(
					comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i).get(KEY_ID)) &&
					!comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(j).get(KEY_TASK_NAME).equals(taskName)) {
				break;
			}
		}
		
		for(int indexRecv = 0; indexRecv < this.ranks ; indexRecv++) {//genero comunicacion por cada rank
			
			int senderTaskIndex = 0, senderOtherIndex = 0;
			
			ArrayList<Map<String, Object>> taskListOtherRank = comunicationsByRank.get(KEY_RANK_+String.valueOf(indexRecv));
			
			while(senderTaskIndex < taskListOtherRank.size()) {
				if(taskListOtherRank.get(senderTaskIndex).get(KEY_TASK_NAME).equals(taskName))	{break;}
				senderTaskIndex++;
			}
			senderOtherIndex = 0;
			while(senderOtherIndex < taskListOtherRank.size()) {
				if(taskListOtherRank.get(senderTaskIndex).get(KEY_ID).equals(taskListOtherRank.get(senderOtherIndex).get(KEY_ID)) &&
						!taskListOtherRank.get(senderTaskIndex).get(KEY_TASK_NAME).equals(taskListOtherRank.get(senderOtherIndex).get(KEY_TASK_NAME)))	{break;}
				senderOtherIndex++;
			}
			
			if(task.get(KEY_TASK_NAME).toString().contains("gath")) { // el orden es inverso
				listComunications.add(
						getInfoComunication( taskListOtherRank.get(senderTaskIndex),
								taskListOtherRank.get(senderOtherIndex),
								comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i),
								comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(j)));
			}
			else {
				listComunications.add(
						getInfoComunication(comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i),
								comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(j),
								taskListOtherRank.get(senderTaskIndex),
								taskListOtherRank.get(senderOtherIndex)));
			}
			
			
			generateTaskInfo(
					comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(i),
					comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).get(j),
					listComunications);
			
			generateTaskInfo(
					taskListOtherRank.get(senderTaskIndex),
					taskListOtherRank.get(senderOtherIndex),
					listComunications);
			
			if(indexRecv != Integer.parseInt(task.get(KEY_OTHER_RANK).toString())) {
				if(senderTaskIndex < senderOtherIndex) {
					taskListOtherRank.remove(senderOtherIndex);
					taskListOtherRank.remove(senderTaskIndex);
				}
				else {
					taskListOtherRank.remove(senderTaskIndex);
					taskListOtherRank.remove(senderOtherIndex);
				}
				
			}
			else {
				posIniRankSend = senderTaskIndex;
				posEndRankSend = senderOtherIndex;
			}
			
		}
		if(posIniRankSend < posEndRankSend) {
			comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).remove(posEndRankSend);
			comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).remove(posIniRankSend);
		}
		else {
			comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).remove(posIniRankSend);
			comunicationsByRank.get(KEY_RANK_+task.get(KEY_OTHER_RANK).toString()).remove(posEndRankSend);
		}
		
	}

	/**
	 * Metodo que se llama cuando uno de los ranks asociados a una tarea, emisor o receptor, se desconocen
	 * @param task
	 * @param indexTask
	 * @param otherTask
	 * @param indexOtherTask
	 * @param taskListRank
	 * @param comunicationsByRank
	 * @param listComunications 
	 */
	private void generateAndSearchComunicationInfo(Map<String, Object> task, int indexTask,
			Map<String, Object> otherTask, int indexOtherTask, ArrayList<Map<String, Object>> taskListRank,
			Map<String, ArrayList<Map<String, Object>>> comunicationsByRank, ArrayList<Map<String, Object>> listComunications) {
		

		Map<String,Object> firstTaskFound = getFirstComunicationInRank(task, comunicationsByRank); //buscamos en todos los ranks y nos quedamos con el que primero se lanza
		
		LOG.info("FIRS TASK FOUND :" +firstTaskFound);
		int rankOfFirsTask = Integer.parseInt(firstTaskFound.get(KEY_MY_RANK).toString());
		int indexComTask = 0;
		while(indexComTask < comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask ).size() ) {
			if(!comunicationsByRank.get(KEY_RANK_ +  firstTaskFound.get(KEY_MY_RANK)).get(indexComTask).equals(firstTaskFound)) indexComTask++;
			else break;
		}
		
		int indexOtherComTask = indexComTask+1;
		while(indexOtherComTask < comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).size() ) {
			LOG.info("TASK CONSULTED: " + comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexOtherComTask));
			
			if(Integer.parseInt(comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexComTask).get(KEY_ID).toString()) != 
					Integer.parseInt(comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexOtherComTask).get(KEY_ID).toString())) {
				indexOtherComTask++;
			}
			else break;
		}
		
		listComunications.add(getInfoComunication(task,otherTask,
				comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexComTask),comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexOtherComTask)));
		
		generateTaskInfo(task,otherTask,listComunications);
		generateTaskInfo(comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexComTask),comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).get(indexOtherComTask),listComunications);
		
		taskListRank.remove(indexOtherTask);
		taskListRank.remove(indexTask);
		comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).remove(indexOtherComTask);
		comunicationsByRank.get(KEY_RANK_ + rankOfFirsTask).remove(indexComTask);
		
		
	}

	/**
	 * Genera Informacion importante a partir de tareas de distintos workers que implican transmision de datos
	 * @param task
	 * @param indexTask
	 * @param otherTask
	 * @param indexOtherTask
	 * @param taskListRank
	 * @param comunicationsByRank
	 * @param listComunications2 
	 */
	private void generateComunicationInfo(Map<String, Object> task, int indexTask, Map<String, Object> otherTask,int indexOtherTask, ArrayList<Map<String, Object>> taskListRank, Map<String, ArrayList<Map<String, Object>>> comunicationsByRank, ArrayList<Map<String, Object>> listComunications) {
		//buscamos la tarea correspondiente a la comunicacion
		boolean found;
		String keyOtherRank = KEY_RANK_ + task.get(KEY_OTHER_RANK).toString();
		ArrayList<Map<String, Object>> rankListComunication = comunicationsByRank.get(keyOtherRank);
		
		
		int indexComTask = 0;
		for(indexComTask = 0; indexComTask < rankListComunication.size(); indexComTask++ ) { // recorremos listado de tareas del rank origen/destino
			Map<String,Object> comunicationOtherRank = rankListComunication.get(indexComTask);
			if(comunicationOtherRank.get(KEY_OTHER_RANK).equals(task.get(KEY_MY_RANK)) && comunicationOtherRank.get(KEY_SIZE).equals(task.get(KEY_SIZE)) ) {
				//LOG.info("RECOVER COMUNICATION TASK: " + comunicationOtherRank);
				found = false;
				for(int indexOtherTaskCom = 0 ; indexOtherTaskCom < comunicationsByRank.get(keyOtherRank).size() && !found; indexOtherTaskCom++ ) {
					//LOG.info("Compare:" );
					
					if(rankListComunication.get(indexOtherTaskCom).get(KEY_ID).equals(comunicationOtherRank.get(KEY_ID))
							&& !rankListComunication.get(indexOtherTaskCom).get(KEY_TASK_NAME).equals(comunicationOtherRank.get(KEY_TASK_NAME))) {
						//LOG.info("RECOVER OTHER COMUNICATION TASK: " + comunicationsByRank.get(keyOtherRank).get(indexOtherTaskCom));
						listComunications.add(getInfoComunication(task,otherTask,comunicationOtherRank,comunicationsByRank.get(keyOtherRank).get(indexOtherTaskCom)));
						generateTaskInfo(task,otherTask,listComunications);
						generateTaskInfo(comunicationOtherRank,rankListComunication.get(indexOtherTaskCom),listComunications);
						
						taskListRank.remove(indexOtherTask);
						taskListRank.remove(indexTask);
						comunicationsByRank.get(keyOtherRank).remove(indexOtherTaskCom);
						comunicationsByRank.get(keyOtherRank).remove(indexComTask);
						found = true;
						break;
					} 
				}
				if(found == true) break;
				
			}
			
		}
		
	}

	/**
	 * Genera un mapa con rank, nombre de funcion, marca de tiempo inicial y final. 
	 * @param task
	 * @param otherTask
	 * @param listComunications
	 */
	private void generateTaskInfo(Map<String, Object> task, Map<String, Object> otherTask, ArrayList<Map<String, Object>> listComunications) {
		HashMap<String,Object> info = new HashMap<String,Object>();
		HashMap<String,String> infoOfTask = new HashMap<String,String>();
		
		LOG.info("BuldTaskInformation generateTaskInfo() Task: " + task + ", otherTask: " + otherTask);
		DecimalFormat df = new DecimalFormat("###.########");
		infoOfTask.put("Rank", task.get(KEY_MY_RANK).toString());
		double timeEnd, timeIni;
		
		if(task.get(KEY_TASK_NAME).toString().contains("Ini")) {
			infoOfTask.put("Task", this.functions.get(task.get(KEY_TASK_NAME).toString().split("_",2)[1]));
			infoOfTask.put("xIni", df.format(Double.parseDouble(task.get(KEY_TIME).toString().replaceAll(",", "."))));
			infoOfTask.put("xEnd", df.format(Double.parseDouble(otherTask.get(KEY_TIME).toString().replaceAll(",", "."))));
			timeIni = Double.parseDouble(task.get(KEY_TIME).toString().replaceAll(",", "."));
			timeEnd = Double.parseDouble(otherTask.get(KEY_TIME).toString().replaceAll(",", "."));
			
		}
		else  {
			infoOfTask.put("Task", this.functions.get(otherTask.get(KEY_TASK_NAME).toString().split("_",2)[1]));
			infoOfTask.put("xIni", df.format(Double.parseDouble(otherTask.get(KEY_TIME).toString().replaceAll(",", "."))));
			infoOfTask.put("xEnd", df.format(Double.parseDouble(task.get(KEY_TIME).toString().replaceAll(",", "."))));
			timeIni = Double.parseDouble(otherTask.get(KEY_TIME).toString().replaceAll(",", "."));
			timeEnd = Double.parseDouble(task.get(KEY_TIME).toString().replaceAll(",", "."));
		}
		info.put("Function", infoOfTask);
		//por seguridad, revisamos que no haya un time mayor al timeEnd actual, si lo hay, actualizamos.
		if(timeEnd > this.timeEnd) this.timeEnd = timeEnd;
		if(timeIni < this.timeIni) this.timeIni = timeIni;
		
		
		
		if(this.minTimeTask != -1) { // si esta inicializaca, es mayor que 0 y menor que el minTime actual, se actualiza.
			if((timeEnd-timeIni) > 0 && (timeEnd-timeIni) < this.minTimeTask) this.minTimeTask = timeEnd - timeIni;
		}
		else { // inicializa la primera vez que sea mayor a 0
			if((timeEnd-timeIni) > 0 ) this.minTimeTask = timeEnd - timeIni;
		}
		LOG.info("BuldTaskInformation generateTaskInfo() INFO generated " + info);
		listComunications.add(info);
		
	}


	/**
	 * 
	 * @param task
	 * @param comunicationsByRank
	 * @return
	 */
	private Map<String, Object> getFirstComunicationInRank(Map<String, Object> task,Map<String, ArrayList<Map<String, Object>>> comunicationsByRank) {
		
		Map<String, Object> firstTask = null;
		int followRank = Integer.parseInt(task.get(KEY_MY_RANK).toString())+ 1;
		
		while(comunicationsByRank.get(KEY_RANK_ + followRank).isEmpty()) followRank++;
		
		
		boolean found = false;
		while (!found && followRank < comunicationsByRank.size()) {
			//LOG.info("SEARCH FIRST TASK IN LIST OF RANK: " + followRank + " LIST: " + comunicationsByRank.get(KEY_RANK_ + followRank) );
			for(int first = 0; first < comunicationsByRank.get(KEY_RANK_ + followRank).size() ; first++) { // recover firstTask founded in rank siguiente
				//LOG.info("CONSULTED: " + comunicationsByRank.get(KEY_RANK_ + followRank).get(first));
				if(comunicationsByRank.get(KEY_RANK_ + followRank).get(first).get(KEY_OTHER_RANK).equals(task.get(KEY_MY_RANK)) &&
						comunicationsByRank.get(KEY_RANK_ + followRank).get(first).get(KEY_SIZE).equals(task.get(KEY_SIZE))) { 
					firstTask = comunicationsByRank.get(KEY_RANK_ + followRank).get(first); 
					//LOG.info("FOUND FIRST TASK " + firstTask);
					found = true;
					break;
				}
			}
			followRank++;
		}
		
		for(int currentRank = followRank ; currentRank < comunicationsByRank.size() ;currentRank++) {
			
			//LOG.info("SEARCH IN LIST OF RANK: " + currentRank + " LIST INFO: " + comunicationsByRank.get(KEY_RANK_+ currentRank));
			
			int indexTask = 0;
			for(Map<String, Object> currentTask : comunicationsByRank.get(KEY_RANK_+ currentRank)) {
				//LOG.info("Current task:" + currentTask + ", my task: " + task);
				if(currentTask.get(KEY_OTHER_RANK).equals(task.get(KEY_MY_RANK)) && currentTask.get(KEY_SIZE).equals(task.get(KEY_SIZE)) ) {
					//LOG.info("FOUND TASK OF RANK: " + currentRank + " TASK INFO: " + currentTask);
					//LOG.info("Current task:" + currentTask);
					if(Double.parseDouble(currentTask.get(KEY_TIME).toString()) < Double.parseDouble(firstTask.get(KEY_TIME).toString())) {
						//LOG.info("RECOVER NEW FIRST TASK OF RANK: " + currentRank + "TASK INFO: " + currentTask);
						firstTask = currentTask;
					}
					else break;
				}

				indexTask++;
			}
			
		}
		
		
		return firstTask;
	}


	
	
	/**
	 * Metodo que recibe 4 tareas, task1 y task 2 son el envio, task3 y task4 son las que reciben
	 * @param task1 
	 * @param task2
	 * @param task3
	 * @param task4
	 * @return
	 */
	private  Map<String, Object> getInfoComunication(Map<String, Object> task1, Map<String, Object> task2,Map<String, Object> task3, Map<String, Object> task4) {
		
		Map<String,Object> comInfo = new HashMap<String,Object>();
		Map<String,Object> aux = new HashMap<String,Object>();
		Map<String,Object> aux2 = new HashMap<String,Object>();
		DecimalFormat df = new DecimalFormat("###.########");
		
		Iterator<String> it1 = this.taskNamesList.iterator();
		Iterator<String> it2 = this.taskNamesCompleteList.iterator();
		
		while(it1.hasNext() && it2.hasNext()) {
			if(task1.get(KEY_TASK_NAME).toString().contains(it1.next())) {
				String taskName = it2.next();
				//LOG.info("task:" + task1 + "taskName : " + taskName); 
				if(!taskName.equals("MPI_Send") && !taskName.equals("MPI_Recv")) {
					aux.put("Task", taskName);
					aux2.put("Task",taskName);
				}
				else {
					aux.put("Task", "MPI_Send");
					aux2.put("Task","MPI_Send");
				}
				break;
			}
			else it2.next();
		}
		
		//LOG.info("\n\nINFO TASK OF COMUNICATION : \n task1:" + task1+ "\n task2:" + task2 +  "\n task3:" +task3 +  "\n task4:"+ task4);
		
		if(task1.get(KEY_TASK_NAME).toString().contains(SEND) || task1.get(KEY_TASK_NAME).toString().contains(RECV)) {
			//System.out.println("INFO : " + task1.get(KEY_TIME).getClass() +" " + task1.get(KEY_TIME) ); 
			if(task1.get(KEY_TASK_NAME).toString().contains(INI)) { 
				aux.put("Rank", task1.get(KEY_MY_RANK).toString());
				aux.put("xIni", df.format(Double.parseDouble(task1.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux.put("xEnd", df.format(Double.parseDouble(task2.get(KEY_TIME).toString().replaceAll(",", "."))));
				if(task1.get(KEY_TASK_NAME).toString().contains(SEND)) comInfo.put("Source", aux);
				else comInfo.put("Dest", aux);
			}
			else { 
				aux.put("Rank", task2.get(KEY_MY_RANK).toString());
				aux.put("xIni", df.format(Double.parseDouble(task2.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux.put("xEnd", df.format(Double.parseDouble(task1.get(KEY_TIME).toString().replaceAll(",", "."))));
				if(task1.get(KEY_TASK_NAME).toString().contains(SEND)) comInfo.put("Source", aux);
				else  comInfo.put("Dest", aux);
			}

			if(task3.get(KEY_TASK_NAME).toString().contains(INI)) { //task3 ini, task4 end
				aux2.put("Rank", task3.get(KEY_MY_RANK).toString());
				aux2.put("xIni", df.format(Double.parseDouble(task3.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux2.put("xEnd", df.format(Double.parseDouble(task4.get(KEY_TIME).toString().replaceAll(",", "."))));
				if(task3.get(KEY_TASK_NAME).toString().contains(SEND)) comInfo.put("Source", aux2);
				else  comInfo.put("Dest", aux2);
			}else { 
				aux2.put("Rank", task4.get(KEY_MY_RANK).toString());
				aux2.put("xIni", df.format(Double.parseDouble(task4.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux2.put("xEnd", df.format(Double.parseDouble(task3.get(KEY_TIME).toString().replaceAll(",", "."))));
				if(task3.get(KEY_TASK_NAME).toString().contains(SEND)) comInfo.put("Source", aux2);
				else  comInfo.put("Dest", aux2);
			}
		}
		else { //broadCast, scatter and gatter
			
			
			
			if(task1.get(KEY_TASK_NAME).toString().contains(INI)) {  // task1 is Initial
				aux.put("Rank", task1.get(KEY_MY_RANK).toString());
				aux.put("xIni", df.format(Double.parseDouble(task1.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux.put("xEnd", df.format(Double.parseDouble(task2.get(KEY_TIME).toString().replaceAll(",", "."))));
				
			}
			else { 
				aux.put("Rank", task1.get(KEY_MY_RANK).toString()); // task2 is Initial
				aux.put("xIni", df.format(Double.parseDouble(task2.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux.put("xEnd", df.format(Double.parseDouble(task1.get(KEY_TIME).toString().replaceAll(",", "."))));
			}
			comInfo.put("Source", aux);
			if(task3.get(KEY_TASK_NAME).toString().contains(INI)) { //task3 initial
				
				aux2.put("Rank", task3.get(KEY_MY_RANK).toString());
				aux2.put("xIni", df.format(Double.parseDouble(task3.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux2.put("xEnd", df.format(Double.parseDouble(task4.get(KEY_TIME).toString().replaceAll(",", "."))));
				
			}else { //task4 initial
				
				aux2.put("Rank", task3.get(KEY_MY_RANK).toString());
				aux2.put("xIni", df.format(Double.parseDouble(task4.get(KEY_TIME).toString().replaceAll(",", "."))));
				aux2.put("xEnd", df.format(Double.parseDouble(task3.get(KEY_TIME).toString().replaceAll(",", "."))));
				
			}
			comInfo.put("Dest", aux2);
		}
		
		//LOG.info("INFO COMUNICATION GENERATED: " + comInfo);
		return comInfo;
	}

	/**
	 * Inicializa nombres de variables asociando nombre original en traza y nombre real de funcion
	 */
	private void initializeFunctionsNames() {
		
		this.functions = new HashMap<String,String>();
		functions.put("crea", "Creat");
		functions.put("open", "Open");
		functions.put("fope", "fOpen");
		functions.put("clos", "Close");
		functions.put("fclo", "fClose");
		functions.put("writ", "Write");
		functions.put("fwri", "fWrite");
		functions.put("pwri", "pWrite");
		functions.put("read", "Read");
		functions.put("frea", "fRead");
		functions.put("prea", "pRead");
		functions.put("send", "MPI_Send");
		functions.put("recv", "MPI_Recv");
		functions.put("bcas", "MPI_Broadcast");
		functions.put("scat", "MPI_Scatter");
		functions.put("gath", "MPI_Gather");
		
		
	}

	public Map<String, ArrayList<Map<String, Object>>> getTaskListByRank() {
		return taskListByRank;
	}


	public ArrayList<Map<String, Object>> getListTaskAndComunications() {
		return listTaskAndComunications;
	}
	
	
	public ArrayList<TaskData> getOrderListTask() {
		return orderListTask;
	}
	
	public int getRanks() {
		return ranks;
	}
	
	public double getMinTimeTask() {
		return minTimeTask;
	}
	
	public double getTimeEnd() {
		return timeEnd;
	}

	public double getTimeIni() {
		return timeIni;
	}
	
	public int getMaxTaskInRank() {
		return maxTaskInRank;
	}
	
	public String getPathfile() {
		return pathfile;
	}
	
	
}

