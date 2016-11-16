package com.sockets.chatroom.outgoing.messages;

public class ErrorMessage {

	private String errorCode;
	private String description;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
