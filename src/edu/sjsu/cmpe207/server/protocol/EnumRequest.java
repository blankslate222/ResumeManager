package edu.sjsu.cmpe207.server.protocol;

/**
 * @author Nikhil
 *
 */
public enum EnumRequest {
	/*
	 * Enum for request types
	 */
	INITIATE(0), UPLOAD(1), DOWNLOAD(2), HELP(3);

	private int value;

	EnumRequest(int val) {
		setValue(val);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
