package com.sockets.chatroom.incoming.messages;

public class MessageInput implements Request {

	private int roomId;
	private int joinId;
	private String clientName;
	private String message;

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

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	} 
}
