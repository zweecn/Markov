package com.event;

import java.io.*;
import java.net.*;

import com.zw.Configs;

public class TCPClient {
//	public static void main(String[] args) {  
//		TCPClient c = new TCPClient();  
//		try {  
//			c.callServer();  
//		} catch (UnknownHostException e) {  
//			e.printStackTrace();  
//		} catch (IOException e) {  
//			e.printStackTrace();  
//		}  
//
//	}  

	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	
	public boolean connect() {
		try {
			socket = new Socket(Configs.TCP_HOST, Configs.TCP_PORT);
			dis = new DataInputStream(socket.getInputStream());  
			dos = new DataOutputStream(socket.getOutputStream());
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}

	public boolean sent(EventData eventData) {
		try {
			dos.write(eventData.getBytes());
			dos.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public EventData read() {
		byte[] b = new byte[EventData.EVENT_BYTE_SIZE];
		try {
			dis.read(b);
			EventData eventData = new EventData();
			eventData.setBytes(b);
			eventData.decode();
			return eventData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public boolean close() {
		try {
			dis.close();
			dos.close();
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
//	private void callServer() throws UnknownHostException, IOException {  
//
//		int reqInt = 23;  
//		Socket socket = new Socket(Configs.TCP_HOST, Configs.TCP_PORT);
//
//		DataInputStream dis = new DataInputStream(socket.getInputStream());  
//		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());  
//
//		dos.writeInt(reqInt);  
//		dos.flush();  
//		int serverInt = dis.readInt();  
//		System.out.println("server answer "+reqInt+"*18 = "+serverInt);  
//		dis.close();  
//		dos.close();
//	}  
}
