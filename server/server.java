
import java.net.*;
import java.io.*;
import java.util.Properties;
import java.nio.file.Files;
class server{



	public static void main (String[] args){
	
		//Servidor y socket
		ServerSocket server;
		Socket socket;
		//data output
		DataOutputStream output;
		
		

		int in;
		
		String nProp;
		
		String nameMPI ="";
		int numLibs = 0;
		File mpiFile;
		
		try{
			//new server
			server = new ServerSocket(5000);
			while(true){
				//Select socket
				socket = server.accept();
				

				
				BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				//recibimos nombre del fichero
				
				nameMPI = dis.readUTF();
				nameMPI = nameMPI.substring(nameMPI.indexOf('\\')+1, nameMPI.length());
				System.out.println("nombre recibido: " + nameMPI);
				
				//recibimos numero de procesadores
				
				nProp = dis.readUTF();
				System.out.println("numero de procesadores recibido: " + nProp);
				
				//recibimos numero de libs
				
				numLibs = Integer.parseInt(dis.readUTF());
				
				//Recibimos nombres de ficheros libs
				String[] nLibs = new String[numLibs];
				for(int i = 0; i < numLibs; i++){
					nLibs[i] = dis.readUTF();
					nLibs[i] = nLibs[i].substring(nLibs[i].indexOf('\\')+1, nLibs[i].length());
					System.out.println("nombre recibido: " + nLibs[i]);
				}
				
				
				//Guardamos el fichero MPI
				
				getFile(bis,nameMPI);
				
				//Guardamos ficheros libs
				for(int i = 0; i < numLibs; i++){
					getFile(bis,nLibs[i]);
				}
				dis.close();
				
				
				//copy mpi file name
				
				//copyFiles(nameMPI);
				
				//copy lib files name
				
				//for(int i = 0; i < numLibs; i++){
				//	copyFiles(nLibs[i]);
				//}
				
				/* Ejecutando script */
				String path = "/home/pablomorer/Desktop/server/script/script.sh";
				String[] command = {path, nameMPI, nProp};
				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command(command);
				Process process = processBuilder.start();
				System.out.println("Fin de Script");



				
				
			}
		
		}catch(Exception e){
		
			System.err.println(e);
		
		}
		
	
	}
	
	private static void getFile(BufferedInputStream bis, String file){
	   try{
	   	String path = "/home/pablomorer/Desktop/server/ejecuciones/" + file;
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
		int in = 0;
		byte[] receivedData = new byte[1024];
		while((in = bis.read(receivedData)) != -1){
			bos.write(receivedData,0,in);
		}
		
		bos.close();
		
		}catch(Exception e){
		
			System.err.println(e);
		
		}
	
	}
	/*
       private static void copyFiles(String name){
       	try{
		String pathSource = "/home/pablomorer/Desktop/server/";
		String pathDest = "/home/pablomorer/Desktop/server/ejecuciones";
		File sourceFile= new File(pathSource + "/" +name); 
		File destFile= new File(pathDest + "/" +name); 
		Files.copy(sourceFile.toPath(), destFile.toPath());
		}catch(Exception e){
		
			System.err.println(e);
		
		}
	}
*/


}














