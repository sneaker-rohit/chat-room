package com.sockets.chatroom.incoming.messages;

public class LeaveRoomRequest implements Request {

	private int roonId;
	private int joinId;
	private String clientName;

	public int getRoomId() {
		return roonId;
	}

	public void setRoonId(int roonId) {
		this.roonId = roonId;
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
}
