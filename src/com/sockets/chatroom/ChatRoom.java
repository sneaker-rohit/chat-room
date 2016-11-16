package com.sockets.chatroom;

import java.util.HashMap;
import java.util.Map;

import com.sockets.chatroom.incoming.messages.JoinChatRequest;

public class ChatRoom {

	private int id;

	private String name;

	private static Map<Integer, ServiceConnection> participants;

	private static int membersId;

	ChatRoom(int id, String name) {
		this.id = id;
		this.name = name;
		participants = new HashMap<Integer, ServiceConnection>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int addParticipant(ServiceConnection thread, JoinChatRequest request) {
		membersId++;
		participants.put(membersId, thread);
		return membersId;
	}

	public void removeParticipant(int id) {
		participants.remove(id);
	}

	public void multicastMessage(String message) {
		for (ServiceConnection connection : participants.values()) {
			connection.writeMessage(message);
		}
	}
}
