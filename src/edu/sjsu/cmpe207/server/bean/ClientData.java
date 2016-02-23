package edu.sjsu.cmpe207.server.bean;

/**
 * @author Nikhil
 *
 */
public class ClientData {
	/*
	 * POJO class for holding client requests
	 */
	private String input;
	private String userId;
	private int operationId;
	private String fileVersion;
	private int fileSize;

	public ClientData() {

	}

	public String getUserId() {
		return userId;
	}

	public void setInput(String identifier) {
		this.input = identifier;
		this.operationId = Integer.parseInt(identifier.substring(0, 1));
		this.userId = identifier.substring(1, 11);
		this.fileVersion = identifier.substring(11, 12);
		if (input.length() > 12) {
			this.fileSize = Integer.parseInt(input.substring(12));
		}
	}

	public int getOperationId() {
		return operationId;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public int getFileSize() {
		return fileSize;
	}
}
