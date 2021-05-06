package Sockets;

import java.io.*;
import java.net.*;

import Controller.Controller;

public class Client {
	//Servidor y socket
	ServerSocket server;
	Socket socket;
	//data output
	
	//buffered readers
	byte[] receivedData;
	
	File mpiFile;
	String nameMPI;
	File[] libFiles;
	String[] nameLibs;
	String nProc;
	int in;
	int numLibsFiles = 0;
	
	
	Controller controller;
	
	public Client(Controller controller) {
		mpiFile = controller.getMPIFile();
		numLibsFiles = controller.getNumLibFiles();
		if(numLibsFiles > 0) {
			libFiles = controller.getMPILibFile();
			nameLibs = controller.getNameLibs();
		}
		nProc = controller.getNProc();
		
		nameMPI = mpiFile.getName();
		
		
		try {
			
			
			Socket socket = new Socket("127.0.0.1" , 5000);
						
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			//Send MPI name
			dos.writeUTF(nameMPI);
			
			//Send number of processors
			dos.writeUTF(nProc);
			
			//Send number of libs
			dos.writeUTF(String.valueOf(numLibsFiles));
			
			//Send libs name
			if(numLibsFiles > 0) {
				for(int i = 0; i < numLibsFiles; i++) {
					dos.writeUTF(nameLibs[i]);
				}
			}

			//Send MPI file
			sendBuffer(dos,bos,mpiFile);
			
			//Send libs file
			if(numLibsFiles > 0) {
				for(int i = 0; i < numLibsFiles ; i++) {
					sendBuffer(dos,bos,libFiles[i]);
				}
			}
	
			
			bos.close();
			
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void sendBuffer(DataOutputStream dos, BufferedOutputStream bos, File file) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte[] byteArray = new byte[8192];
		while((in = bis.read(byteArray)) != -1) {
			bos.write(byteArray,0,in);
		}
		bis.close();
	}
	
}
