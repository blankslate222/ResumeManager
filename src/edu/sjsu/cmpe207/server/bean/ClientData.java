package edu.sjsu.cmpe207.server.bean;

public class ClientData {
	private String userId;
	private int operationId;
	private String fileVersion;

	public ClientData() {

	}

	public ClientData(String identifier) {
		this.operationId = Integer.parseInt(identifier.substring(0, 1));
		this.userId = identifier.substring(1, 11);
		this.fileVersion = identifier.substring(11);
		System.out.println("set values for:");
		System.out.println("op = "+operationId);
		System.out.println("user = "+userId);
		System.out.println("file ver = "+fileVersion);
	}

	public String getUserId() {
		return userId;
	}

	/*
	 * public void setUserId(String userId) { this.userId = userId; }
	 */
	public int getOperationId() {
		return operationId;
	}

	/*
	 * public void setOperationId(String operation) { this.operationId =
	 * Integer.parseInt(operation); }
	 */
	public String getFileVersion() {
		return fileVersion;
	}
	/*
	 * public void setFileVersion(String fileVersion) { this.fileVersion =
	 * fileVersion; }
	 */
}
