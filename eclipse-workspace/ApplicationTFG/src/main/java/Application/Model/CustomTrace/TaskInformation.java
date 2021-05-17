package Application.Model.CustomTrace;

/**
 * Clase encargada de almacenar la linea y el timeStamp de una tarea.
 * Implementa la interfaz Comparable con el fin de ordenar las tareas teniendo en cuenta sus timeStamp
 * @author bryanrvvargas
 *
 */
public class TaskInformation implements Comparable<TaskInformation> {

	private String line;
	private double timeStamp;
	
	public TaskInformation(double timeStamp, String line) {
		this.line = line;
		this.timeStamp = timeStamp;
	}
	
	public int compareTo(TaskInformation o) {
		// TODO Auto-generated method stub
		if(this.timeStamp < o.getTimeStamp()) return -1;
		else if(this.timeStamp > o.getTimeStamp()) return 1;
		
		return 0;
	}

	public String getLine() {
		return line;
	}

	public double getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String toString() {
		return line ;
	}

	
	
}

