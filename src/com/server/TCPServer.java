package com.server;

import java.io.*;
import java.net.*;

import com.event.EventData;
import com.zw.Configs;

public class TCPServer {
	public static void main(String[] args){  
		TCPServer ms = new TCPServer();  
		ms.listen();  
		ms.read();
	}

	private ServerSocket server;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	public boolean listen() {
		try {
			server = new ServerSocket(Configs.TCP_PORT);
			System.out.println("Listing...");
			return true;
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

	public boolean read() {
		byte[] b = new byte[EventData.EVENT_BYTE_SIZE];
		try {
			while (true) {
				socket = server.accept();  
				dis = new DataInputStream(socket.getInputStream());  
				dos = new DataOutputStream(socket.getOutputStream());  
				dis.read(b);
				EventData eventData = new EventData();
				eventData.setBytes(b);
				eventData.decode();
				System.out.println(eventData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
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
}
