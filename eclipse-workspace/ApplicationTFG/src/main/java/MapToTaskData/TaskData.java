/**
 * 
 */
package MapToTaskData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jfree.util.Log;

/**
 * Clase necesaria para comparar tareas segun sus marcas temporales iniciales
 * @author bryanrvvargas
 *
 */
public class TaskData  implements Comparable<TaskData>,Comparator<TaskData>{
	
	private double timeStamp;
	private HashMap<String,Object> task;
	
	@SuppressWarnings("unchecked")
	public TaskData(Map<String,Object> task) {
		//System.out.println("\nTASK DATA(); " + task);
		if(task.containsKey("Function")) {
			timeStamp = Double.parseDouble(((HashMap<String, Object>) task.get("Function")).get("xIni").toString().replaceAll(",", "."));
			this.task = new HashMap<String,Object>(task);
		}
		else {
			if(task.containsKey("Source") && task.containsKey("Dest")){
				double timeSource = Double.parseDouble(((HashMap<String, Object>) task.get("Source")).get("xEnd").toString().replaceAll(",", "."));
				double timeDest = Double.parseDouble(((HashMap<String, Object>) task.get("Dest")).get("xEnd").toString().replaceAll(",", "."));
				timeStamp = timeSource < timeDest ? timeSource : timeDest;
				this.task = new HashMap<String,Object>(task);
			}
		}
	}	

	public int compare(TaskData o1, TaskData o2) {
		// TODO Auto-generated method stub
		if(o1.getTimeStamp() < o2.getTimeStamp()) return -1;
		else if(o1.getTimeStamp() >= o2.getTimeStamp()) return 1;
		
		return 0;
	}

	public int compareTo(TaskData o) {
		// TODO Auto-generated method stub
		if(this.timeStamp < o.getTimeStamp()) return -1;
		else if(this.timeStamp >= o.getTimeStamp()) return 1;
		
		return 0;
	}

	public HashMap<String, Object> getTask() {
		return task;
	}
	
	public double getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String toString() {
		return "[timeStamp=" + timeStamp + ", task=" + task + "]";
	}
	
	

}
