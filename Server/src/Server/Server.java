package Server;


import java.net.*;
import java.io.*;



import ExecuteCommands.Command;
import GetProperties.GetProperties;
import Logger.AppLog;



 class Server{

		



	public static void main (String[] args){
	
		System.out.println("***********Starting Server Execution*********** ");
		//Servidor y socket
		ServerSocket server;
		Socket socket;
		//data output
		System.out.println("1 ");
		GetProperties properties = new GetProperties();		
		String nProp;
		AppLog LOG = new AppLog();
		System.out.println("App log created");
		int numLibs = 0;

		
		try{
			//new server
			server = new ServerSocket(5000);
			while(true){
				//Select socket 
				socket = server.accept();
				
				System.out.println("ini");
				
				BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				//recibimos nombre del fichero
				
				String mpiFilename = dis.readUTF();
				mpiFilename = mpiFilename.substring(mpiFilename.indexOf('\\')+1, mpiFilename.length());
				System.out.println("nombre recibido: " + mpiFilename);
				
				//recibimos numero de procesadores
				
				nProp = dis.readUTF();
				System.out.println("numero de procesadores recibido: " + nProp);
				
				//recibimos numero de libs
				
				numLibs = Integer.parseInt(dis.readUTF());
				System.out.println("numero de librerias recibido: " + numLibs);
				String[] nLibs = null;
				if(numLibs > 0) {
					//Recibimos nombres de ficheros libs
					 nLibs = new String[numLibs];
					for(int i = 0; i < numLibs; i++){
						nLibs[i] = dis.readUTF();
						nLibs[i] = nLibs[i].substring(nLibs[i].indexOf('\\')+1, nLibs[i].length());
						System.out.println("nombre recibido: " + nLibs[i]);
					}
					
					for(int i = 0; i < numLibs; i++){
						getBuffer(socket, properties,nLibs[i]);
					}
				}
				

				
				
				//Guardamos el fichero MPI
				
				getBuffer(socket, properties,mpiFilename);

				

				dis.close();
				
				
				boolean sol = compileMPIFile(numLibs, nProp, properties, mpiFilename, nLibs, LOG);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				//If compile successfuly then send trace
				if(sol) {				
					
					//Send trace txt name
					dos.writeUTF(properties.getProperty("traceFile"));
					File traceFile = new File(properties.getProperty("pathTraceTxt") + properties.getProperty("traceFile"));
					sendBuffer(traceFile, socket);
					
					
				}else {
					//if compile is not succesfuly then send 0
					System.out.println("Error en la solucion del server");
					dos.writeUTF("0");
					
				}
				//copy mpi file name
				
				//copyFiles(nameMPI);
				
				//copy lib files name
				
				//for(int i = 0; i < numLibs; i++){
				//	copyFiles(nLibs[i]);
				//}
				
				/* Ejecutando script 
				this.path = "/home/pablomorer/Desktop/Scripts/script.sh";
				String[] command = {path, this.mpiFile, nProp};
				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command(command);
				Process process = processBuilder.start();
				System.out.println("Fin de Script");
				 	*/


				
				
			}
		
		}catch(Exception e){
		
			System.err.println("error en main " +  e);
		
		}
		
	
	}

	
	public static void getBuffer(Socket socket, GetProperties properties, String nameFile) throws IOException {
		InputStream in = socket.getInputStream();
		OutputStream out = new FileOutputStream(properties.getProperty("pathMpi") + nameFile);
		byte[] bytes = new byte[16*1024];
		
		int count;
		while((count = in.read(bytes)) > 0) {
			out.write(bytes,0,count);
		}
		out.close();
		in.close();
	}
	
	private static void sendBuffer(File mpiFile, Socket socket) throws IOException {

		
		byte[] bytes = new byte[16 * 1024];
		InputStream in = new FileInputStream(mpiFile);
		OutputStream out= socket.getOutputStream();
		
		int count;
		while((count = in.read(bytes))> 0) {
			out.write(bytes, 0, count);
		}
		out.close();
		in.close();
		
	}
	
	/**
	 * Compile MPI and generate a executable File and save in @destination path
	 * @param destination Destination directory.
	 * @param libs Complementary library to compile, it is not required 
	 * @return Message with the result of the compilation
	 */
	public static boolean compileMPIFile(int libs, String nProp, GetProperties properties, String mpiFileName, String[] nLibs, AppLog LOG) {
		
		System.out.println("compileMPIFile");
		
		//Get mpi file path
		//File mpiFile =  new File(properties.getProperty("pathMpi") + mpiFileName); 
		//Get traceLib file path
		//File libpath = new File(properties.getProperty("pathLib") + properties.getProperty("nameLibHeader"));
		//execute compile lib.o
		String command = "gcc -o " + properties.getProperty("pathLib") + properties.getProperty("nameLibO")+ " -I" + properties.getProperty("pathIncludeMpi") + " -c "+ properties.getProperty("pathLib") + 
				 properties.getProperty("nameLibC");
		System.out.println(command);
		String message = execCommand(command, LOG);
		
		//check if error exits
		if(!message.isEmpty()) return false;	 
		//copy traceLib.h
		command = "cp " + properties.getProperty("pathLib") + properties.getProperty("nameLibH") +" " + properties.getProperty("pathMpi"); 
		System.out.println(command);
		message = execCommand(command, LOG);
		
		//check if error exits
		if(!message.isEmpty()) return false;	
		//Compile MPI File
		 command = "mpicc -o " + properties.getProperty("pathEjecutables") + "exe -I" +  properties.getProperty("pathIncludeMpi") + " " + properties.getProperty("pathMpi") + 
				mpiFileName +" " +   properties.getProperty("pathLib") + properties.getProperty("nameLibO"); 
		
		if (libs > 0) {
			for(int i = 0; i < libs; i++) {
				command += " " + properties.getProperty("pathMpi") + nLibs[i]; //cambiado pablo
			}
		} 
		System.out.println(command);
		//Exec Script to compile MPI app
		message = execCommand( command , LOG);
		//Comprobar si son warnings o errores
		if(!message.isEmpty() && message.contains("Error") && message.contains("error")) return false;
		
		//MPI execute
		command = "mpiexec -hostfile " + properties.getProperty("pathmachines") + " -np " + nProp + " " + properties.getProperty("pathEjecutables") + "exe";
		System.out.println(command);
		message = execCommand(command , LOG) ;
		if(!message.isEmpty() && message.contains("Error") && message.contains("error")) return false;
		
		//Generate Trace
		command = "cat *simulation_mpi_* | sort > " + properties.getProperty("pathTraceTxt") + properties.getProperty("traceFile");
		System.out.println(command);
		
		message = execCommand(command, LOG);
		if(!message.isEmpty() && message.contains("Error") && message.contains("error")) return false;
		
		command = "rm *simulation_mpi_*";
		System.out.println(command);
		message = execCommand(command, LOG);
		
		if(!message.isEmpty() && message.contains("Error") && message.contains("error")) return false;
		
		
		return true;
	}
	
	/**
	 * Execute a command
	 * @param command Command
	 * @return Error message or success message
	 */
	private static String execCommand(String command, AppLog LOG ) {
		Command com = new Command();
		Process process = com.execute(command);

		InputStream result = process.getInputStream(); 
		
		BufferedReader buf = new BufferedReader(new InputStreamReader(result));
		try {
			String line = buf.readLine();
			while(line!= null) {
				LOG.info(line);
				line = buf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("error en execCommand " + e);
			e.printStackTrace();
		}
		
		result = process.getErrorStream(); //comprobamos si hay error
		
		buf = new BufferedReader(new InputStreamReader(result));
		String message = "";
		try {
			String line = buf.readLine();
			while(line!= null) {
				message += line + "\n";
				LOG.info(line);
				line = buf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return message;
	}
	
	/**
	 * Execute command in the @directory
	 * @param command Command to execute
	 * @param directory Directory where the command will be executed
	 * @return Error message or success message

	private String execCommand(String command, String directory) {
		
		LOG.info("Command to execute: " + command);
		Process process = com.execute(command,directory);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			LOG.error(e);
			e.printStackTrace();
		}
		InputStream result = process.getInputStream(); 
		
		
		
		BufferedReader buf = new BufferedReader(new InputStreamReader(result));
		try {
			String line = buf.readLine();
			while(line!= null) {
				LOG.info(line);
				line = buf.readLine();
			}
		} catch (IOException e) {
			process.destroy();
			LOG.error(e);
			//e.printStackTrace();
		}
		
		result = process.getErrorStream(); //comprobamos si hay error
		
		
		buf = new BufferedReader(new InputStreamReader(result));
		String message = "";
		try {
			String line = buf.readLine();
			while(line!= null) {
				message += line + "\n";
				LOG.info(line);
				line = buf.readLine();
			}
		} catch (IOException e) {
			process.destroy();
			LOG.error(e);
			//e.printStackTrace();
		}
		
		process.destroy();
		return message;
	}

	 */

}













