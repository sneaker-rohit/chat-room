package com.sockets.chatroom.incoming.messages;

public class DisconnectRoomRequest implements Request {

	private String clientIP;
	private int port;
	private String clientName;

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
