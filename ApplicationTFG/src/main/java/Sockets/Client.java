package Sockets;

import java.io.*;
import java.net.*;

import Controller.Controller;
import GetProperties.GetProperties;

public class Client {
	//Servidor y socket
	ServerSocket server;
	Socket socket;
	//data output
	
	//buffered readers
	byte[] receivedData;
	

	String nameMPI;
	File[] libFiles;
	String[] nameLibs;
	String nProc;
	int in;
	int numLibsFiles = 0;
	String nameTrace;
	
	
	Controller controller;
	
	public Client(Controller controller) {
		File mpiFile = controller.getMPIFile();
		int numLibsFiles = controller.getNumLibFiles();
		if(numLibsFiles > 0) {
			libFiles = controller.getMPILibFile();
			nameLibs = controller.getNameLibs();
		}
		nProc = controller.getNProc();
		
		nameMPI = mpiFile.getName();
		
		
		try {
			
			System.out.print("Conectamos Socket");
			Socket socket = new Socket("127.0.0.1" , 5000);
			
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			System.out.println("Creamos BufferedOutputStream ");
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("Creamos DataOutputStream ");
			//Send MPI name
			
			dos.writeUTF(nameMPI);
			System.out.println("enviado nombreMPI");
			//Send number of processors
			dos.writeUTF(nProc);
			System.out.println("enviado numero Proc");
			//Send number of libs
			dos.writeUTF(String.valueOf(numLibsFiles));
			System.out.println("enviado numero de libs");
			
			if(numLibsFiles > 0) {
				//Send libs name
				for(int i = 0; i < numLibsFiles; i++) {
					dos.writeUTF(nameLibs[i]);
				}
				for(int i = 0; i < numLibsFiles ; i++) {
					sendBuffer(libFiles[i], socket);
				}
			}

			//Send MPI file
			sendBuffer(mpiFile, socket);

			


			//Receive text name
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			this.nameTrace = dis.readUTF();
			System.out.println("trace txt name: " + this.nameTrace);
			//Receive text contain
			getBuffer(socket, nameTrace);

			
			controller.setTraceLib("/home/pablomorer/Desktop/trace/" + this.nameTrace);
			bos.close();
			dos.close();
			
		}catch(Exception e){
			System.out.print("error en Client ");
			System.err.println(e);
		}
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
	
	public static void getBuffer(Socket socket, String nameFile) throws IOException {
		InputStream in = socket.getInputStream();
		OutputStream out = new FileOutputStream("/home/pablomorer/Desktop/trace/"+ nameFile);
		byte[] bytes = new byte[16*1024];
		
		int count;
		while((count = in.read(bytes)) > 0) {
			out.write(bytes,0,count);
		}
		out.close();
		in.close();
	}
	

	
}
