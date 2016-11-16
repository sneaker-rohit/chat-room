package com.sockets.chatroom;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.sockets.chatroom.incoming.messages.DisconnectRoomRequest;
import com.sockets.chatroom.incoming.messages.JoinChatRequest;
import com.sockets.chatroom.incoming.messages.LeaveRoomRequest;
import com.sockets.chatroom.incoming.messages.MessageInput;

public class ServiceConnection extends Thread {

	private Socket socket;
	private static Object requestInput;
	private ConnectionWriteThread writethread;
	private Map<Integer, ChatRoom> myChatRooms;

	ServiceConnection(Socket socket) {
		this.socket = socket;
		writethread = new ConnectionWriteThread(this.socket);
		writethread.setDaemon(true);
		writethread.start();
		myChatRooms = new HashMap<Integer, ChatRoom>();
	}

	public ConnectionWriteThread getWriteThread() {
		return this.writethread;
	}

	public void writeMessage(String message) {
		writethread.setMessage(message);
	}

	public void run() {
		ObjectInputStream inStream;
		try {
			inStream = new ObjectInputStream(socket.getInputStream());
			while (true) {
				String message = (String) inStream.readObject();
				int type = decodeMessage(message);
				switch (type) {
				case 1:
					JoinChatRequest joinRequest = (JoinChatRequest) requestInput;
					ChatRoom roomJoin = ChatServer.findChatRoomToJoin(joinRequest.getRoomName());

					if (roomJoin == null)
						roomJoin = ChatServer.createChatRoom((JoinChatRequest) requestInput);

					int joinId = roomJoin.addParticipant(this, (JoinChatRequest) requestInput);
					myChatRooms.put(joinId, roomJoin);
					sendJoinedMessage(joinRequest, roomJoin, joinId);
					break;

				case 2:
					LeaveRoomRequest leaveRequest = (LeaveRoomRequest) requestInput;
					int id = -1;
					ChatRoom roomLeave = null;
					for (int key : myChatRooms.keySet()) {
						ChatRoom room = myChatRooms.get(key);
						if (room.getId() == leaveRequest.getRoomId()) {
							id = key;
							roomLeave = room;
							break;
						}
					}
					myChatRooms.remove(id);
					roomLeave.removeParticipant(id);
					sendLeaveMessage(leaveRequest, roomLeave, id);
					break;
				case 3:
					for (int key : myChatRooms.keySet()) {
						myChatRooms.get(key).removeParticipant(key);
					}
					socket.close();
					break;
				case 4:
					MessageInput messageInput = (MessageInput) requestInput;
					ChatRoom myChatRoom = myChatRooms.get(messageInput.getJoinId());
					String multicastMessage = createMulticastMessage(messageInput);
					myChatRoom.multicastMessage(multicastMessage);
					break;
				default:
					sendErrorMessage();
					socket.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendErrorMessage() {
		String outputMessage = "ERROR_CODE: 0\nERROR_DESCRIPTION: Error Description";
		writethread.setMessage(outputMessage);
	}

	private String createMulticastMessage(MessageInput messageInput) {
		String outputMessage = "CHAT: " + messageInput.getRoomId() + "CLIENT_NAME: " + messageInput.getClientName()
				+ "MESSAGE: " + messageInput.getMessage();
		return outputMessage;
	}

	private void sendLeaveMessage(LeaveRoomRequest leaveRequest, ChatRoom roomLeave, int joinId) {
		String outputMessage = "LEFT_CHATROOM: " + roomLeave.getId() + "\nJOIN_ID: " + joinId;
		writethread.setMessage(outputMessage);
	}

	private void sendJoinedMessage(JoinChatRequest joinRequest, ChatRoom roomJoin, int joinId) {
		String outputMessage = "JOINED_CHATROOM: " + roomJoin.getName() + "\nSERVER_IP: " + ChatServer.getServerIp()
				+ "\nPORT :" + ChatServer.getPort() + "\nROOM_REF: " + roomJoin.getId() + "\nJOIN_ID: " + joinId;
		writethread.setMessage(outputMessage);
	}

	private static int decodeMessage(String message) {
		try {
			String[][] messageArray = returnArray(message);
			if (messageArray[0][0].trim().equals("JOIN_CHATROOM")) {
				if (!messageArray[1][0].trim().equals("CLIENT_IP") || !messageArray[2][0].trim().equals("PORT")
						|| !messageArray[3][0].trim().equals("CLIENT_NAME")) {
					return -1;
				}
				JoinChatRequest joinChat = new JoinChatRequest();
				joinChat.setRoomName(messageArray[0][1].trim());
				joinChat.setClientIP(messageArray[1][1].trim());
				joinChat.setPort(Integer.parseInt(messageArray[2][1].trim()));
				joinChat.setClientName(messageArray[3][1].trim());
				requestInput = joinChat;
				return 1;
			} else if (messageArray[0][0].trim().equals("LEAVE_CHATROOM")) {
				if (!messageArray[1][0].trim().equals("JOIN_ID") || !messageArray[2][0].trim().equals("CLIENT_NAME")) {
					return -1;
				}
				LeaveRoomRequest leaveRoom = new LeaveRoomRequest();
				leaveRoom.setRoonId(Integer.parseInt(messageArray[0][1].trim()));
				leaveRoom.setJoinId(Integer.parseInt(messageArray[1][1].trim()));
				leaveRoom.setClientName(messageArray[2][1].trim());
				requestInput = leaveRoom;
				return 2;
			} else if (messageArray[0][0].trim().equals("DISCONNECT")) {
				if (!messageArray[1][0].trim().equals("PORT") || !messageArray[2][0].trim().equals("CLIENT_NAME")) {
					return -1;
				}
				DisconnectRoomRequest disconnect = new DisconnectRoomRequest();
				disconnect.setClientIP(messageArray[0][1].trim());
				disconnect.setPort(Integer.parseInt(messageArray[1][1].trim()));
				disconnect.setClientName(messageArray[2][1].trim());
				requestInput = disconnect;
				return 3;
			} else if (messageArray[0][0].trim().equals("CHAT")) {
				if (!messageArray[1][0].trim().equals("JOIN_ID") || !messageArray[2][0].trim().equals("CLIENT_NAME")
						|| !messageArray[3][0].trim().equals("MESSAGE")) {
					return -1;
				}
				MessageInput chatMessage = new MessageInput();
				chatMessage.setRoomId(Integer.parseInt(messageArray[0][1].trim()));
				chatMessage.setJoinId(Integer.parseInt(messageArray[1][1].trim()));
				chatMessage.setClientName(messageArray[2][1].trim());
				chatMessage.setMessage(messageArray[3][1].trim());
				requestInput = chatMessage;
				return 4;
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			return -1;
		}
	}

	private static String[][] returnArray(String message) throws Exception {
		message = message.replace("\\n", "~");
		int rowNo = message.split("~").length;
		String[][] x = new String[rowNo][2];
		int i = 0;
		for (String row : message.split("~")) {
			x[i] = row.split(":");
			if (x[i].length != 2)
				throw new Exception();
			i++;
		}
		return x;
	}
}
