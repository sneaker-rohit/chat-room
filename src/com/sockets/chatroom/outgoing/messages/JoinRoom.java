package com.sockets.chatroom.outgoing.messages;

public class JoinRoom {

	private String serverIP;
	private int port;
	private int roomId;
	private int joinId;

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getJoinId() {
		return joinId;
	}

	public void setJoinId(int joinId) {
		this.joinId = joinId;
	}
}
