package com.sockets.chatroom;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionWriteThread extends Thread {

	private Socket socket;

	private String message;

	ConnectionWriteThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		ObjectOutputStream outStream;
		try {
			socket.setTcpNoDelay(true);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			while (true) {
				sleep(0);
				if (message == null)
					continue;
				outStream.writeObject(message);
				outStream.flush();
				message = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
