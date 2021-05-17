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
		GetProperties properties = new GetProperties();		
		String nProp;
		AppLog LOG = new AppLog();
		int numLibs = 0;

		
		try{
			//new server
			server = new ServerSocket(5000);
			while(true){
				//Select socket 
				socket = server.accept();
						
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
				//InputStream in = socket.getInputStream();
				if(numLibs > 0) {
					//Recibimos nombres de ficheros libs
					 nLibs = new String[numLibs];
					for(int i = 0; i < numLibs; i++){
						nLibs[i] = dis.readUTF();
						nLibs[i] = nLibs[i].substring(nLibs[i].indexOf('\\')+1, nLibs[i].length());
						System.out.println("nombre recibido: " + nLibs[i]);
					}
					
					for(int i = 0; i < numLibs; i++){
						getBuffer(socket, properties,nLibs[i], dis);
					}
				}
				

				
				
				//Guardamos el fichero MPI
				
				getBuffer(socket, properties,mpiFilename, dis);

				

				
				
				
				boolean sol = compileMPIFile(numLibs, nProp, properties, mpiFilename, nLibs, LOG);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				//If compile successfuly then send trace
				if(sol) {				
					
					//Send trace txt name
					dos.writeUTF(properties.getProperty("traceFile"));
					File traceFile = new File(properties.getProperty("pathTraceTxt") + "trace_complete.txt");
					sendBuffer(traceFile, socket, dos);
					
					
				}else {
					//if compile is not succesfuly then send 0
					System.out.println("Error en la solucion del server");
					dos.writeUTF("0");
					
				}
				//in.close();
				dis.close();
				bis.close();
			}
		
		}catch(Exception e){
		
			System.err.println("error en main " +  e);
		
		}
		
	
	}

	
	public static void getBuffer(Socket socket, GetProperties properties, String nameFile, DataInputStream dis ) throws IOException {
		
		OutputStream out = new FileOutputStream(properties.getProperty("pathMpi") + nameFile);

		byte[] bytes = new byte[1024];
		
		int count;
		while((count = dis.read(bytes)) > 0) {
			out.write(bytes,0,count);
			if(count < 1024) {
				out.flush();
				break;
			}
		}
		out.close();
		//in.close();
	}

	private static void sendBuffer(File mpiFile, Socket socket, DataOutputStream dos) throws IOException {

		
		byte[] bytes = new byte[1024];
		InputStream in = new FileInputStream(mpiFile);
		int count;
		while((count = in.read(bytes))> 0) {
			dos.write(bytes, 0, count);
		}
		dos.flush();
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
		

		String command = "gcc -o " + properties.getProperty("pathLib") + properties.getProperty("nameLibO")+ " -I" + properties.getProperty("pathIncludeMpi") + " -c "+ properties.getProperty("pathLib") + 
				 properties.getProperty("nameLibC");
		System.out.println(command);
		String message = execCommand(command, LOG);
		
		//check if error exits
		if(!message.isEmpty()) return false;	 
		//copy traceLib.h
		command = "cp " + properties.getProperty("pathLib") + properties.getProperty("nameLibH") +" " + properties.getProperty("pathMpi") + properties.getProperty("nameLibH"); 
		System.out.println(command);
		message = execCommand(command, LOG);
		
		//check if error exits
		if(!message.isEmpty()) return false;	

		 String path = properties.getProperty("pathScript") + properties.getProperty("scriptMPIExecute");
		 String pathMpi =  properties.getProperty("pathMpi") + mpiFileName;
		 String pathTrace =  properties.getProperty("pathLib") + properties.getProperty("nameLibO");
		 String exe = properties.getProperty("pathEjecutables") + "exe";
		 String machines = properties.getProperty("pathmachines");
		 String dest = properties.getProperty("pathTraceTxt");

		
		if (libs > 0) {
			for(int i = 0; i < libs; i++) {
				pathTrace += " " + properties.getProperty("pathMpi") + nLibs[i]; //cambiado pablo
			}
		} 
		
		String[] commandMPI = {path, pathMpi, pathTrace, exe, nProp, machines, dest};

		//Exec Script to compile MPI app
		message = execCommand( commandMPI , LOG);
		//Comprobar si son warnings o errores
		if(!message.isEmpty()) return false;
		

		
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
	
	private static String execCommand(String[] command, AppLog LOG ) {
		Command com = new Command();
		Process process = com.executeMPI(command);

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
	


}













