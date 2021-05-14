/**
 * 
 */
package ExecuteCommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import GetProperties.GetProperties;

/**
 * Clase encargada de ejecutar metodos de la libreria Runtime.
 * @author bryanrvvargas
 *
 */
public class Command {
	
	private Runtime run;
	
	public Command() {
		this.run = Runtime.getRuntime();
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	public Process execute(String command) {
		
		try {
			Process process = this.run.exec(new String[] {"bash", "-c", command });
			
			return process;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param command
	 * @param directory
	 * @return
	 */
	public Process execute(String command, String directory) {
		
		try {
			Process process = this.run.exec(new String[] {"bash", "-c", command }, null,new File(directory));
			
			return process;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		//System.out.println(System.getProperty("user.home"));
		
		GetProperties properties = new GetProperties();
		String[] commands = {"/bin/sh", "-c", "cd ","/Users/bryanrvvargas/Desktop/TFG/workspace/ApplicationChart/src/main/resources/Scripts ","ls -l"};
		
		String command = "cd /Users/bryanrvvargas/Desktop/pruebas/ ; mpiexec -np 4 /Users/bryanrvvargas/Desktop/pruebas/prueba"; 
		System.out.println(command);
		Command com = new Command();
		
		//Process process =com.execute(command);
		//com.execute("cd ..");
		//Process process = com.execute("/bin/sh -c cd /Users/bryanrvvargas/Desktop/pruebas/ ; ls -l ;mpiexec -np 4 /Users/bryanrvvargas/Desktop/pruebas/prueba");
		//ProcessBuilder pb = new ProcessBuilder("mpiexec", "-np 4 /Users/bryanrvvargas/Desktop/pruebas/prueba", "ls -l");
		//pb.directory(new File("/Users/bryanrvvargas/Desktop/pruebas/"));
		
		Process process = Runtime.getRuntime().exec("/Users/bryanrvvargas/Desktop/TFG/workspace/ApplicationChart/src/main/resources/Scripts/compileMPIFile.sh "+""
													+ "/Users/bryanrvvargas/Desktop/TFG/TraceLib/Programas/scatter_example.c "
													+"/Users/bryanrvvargas/Desktop/TFG/TraceLib/traceLib.o /Users/bryanrvvargas/Desktop/pruebas/pr", null, new File(System.getProperty("user.home")));
		
		//Process process = pb.start();
		process.waitFor();
		InputStream result = process.getErrorStream();
		
		BufferedReader buf = new BufferedReader(new InputStreamReader(result));
		
		try {
			String line = buf.readLine();
			while(line!= null) {
				System.out.println("HOLA:" + line);
				line = buf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
