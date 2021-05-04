package Sockets;

import java.io.*;
import java.net.*;

import Controller.Controller;

public class Client {
	//Servidor y socket
	ServerSocket server;
	Socket socket;
	//data output
	DataOutputStream output;
	
	//buffered readers
	BufferedInputStream bis;
	BufferedOutputStream bos;
	byte[] receivedData;
	
	File mpiFile;
	File[] libFiles;
	int nProc;
	int in;
	byte[] byteArray;
	
	Controller controller;

	public Client(Controller controller) {
		mpiFile = controller.getMPIFile();
		libFiles = controller.getMPILibFile();
		nProc = controller.getNProc();
		
		try {
			
			
			Socket socket = new Socket("127.0.0.1" , 5000);
			
			bis = new BufferedInputStream(new FileInputStream(mpiFile));
			
			bos = new BufferedOutputStream(socket.getOutputStream());
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			dos.writeUTF(String.valueOf(nProc));
			
			while((in = bis.read(byteArray)) != -1){
				bos.write(byteArray,0,in);
			}
			
			bis.close();
			bos.close();
			
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
}
