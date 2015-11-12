package edu.sjsu.cmpe207.server.protocol;

public enum EnumRequest {
	
	INITIATE(0),
	UPLOAD(1),
	DOWNLOAD(2),
	HELP(3);
	
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
