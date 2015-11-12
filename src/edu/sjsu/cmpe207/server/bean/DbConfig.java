package edu.sjsu.cmpe207.server.bean;

public class DbConfig {
	private String dbDriver;
	private String dbUrl;
	private String dbPort;
	private String dbUser;
	private String dbPassword;
	public String getDbDriver() {
		return dbDriver;
	}
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbPort() {
		return dbPort;
	}
	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
}
