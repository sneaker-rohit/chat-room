package com.sockets.chatroom;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.sockets.chatroom.incoming.messages.JoinChatRequest;

public class ChatServer {

	private static List<ChatRoom> chatRooms;
	private static String serverIP;
	private static int serverPort;
	private static List<ServiceConnection> requester;
	private static int roomCount = 0;

	public static void main(String args[]) {

		int port = Integer.parseInt(args[0]);

		chatRooms = new ArrayList<ChatRoom>();
		requester = new ArrayList<ServiceConnection>();
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			setServerIP(InetAddress.getLocalHost().getHostAddress());
			setPort(serverSocket.getLocalPort());
			System.out.println("My IP : " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("My Port : " + serverSocket.getLocalPort());
			while (true) {
				Socket newSocket = serverSocket.accept();
				System.out.println("Client '" + newSocket.getInetAddress() + "' connected\n");
				ServiceConnection serviceThread = new ServiceConnection(newSocket);
				serviceThread.start();
				requester.add(serviceThread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ChatRoom createChatRoom(JoinChatRequest joinRequest) {
		roomCount++;
		ChatRoom room = new ChatRoom(roomCount, joinRequest.getRoomName());
		chatRooms.add(room);
		return room;
	}

	public static ChatRoom findChatRoomToJoin(String roomName) {
		for (ChatRoom room : chatRooms) {
			if (room.getName().equals(roomName))
				return room;
		}
		return null;
	}

	public static void setServerIP(String ip) {
		serverIP = ip;
	}

	public static String getServerIp() {
		return serverIP;
	}

	public static void setPort(int port) {
		serverPort = port;
	}

	public static int getPort() {
		return serverPort;
	}
}
