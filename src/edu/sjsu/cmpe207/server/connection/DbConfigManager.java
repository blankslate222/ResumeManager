package edu.sjsu.cmpe207.server.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import edu.sjsu.cmpe207.server.bean.DbConfig;

public class DbConfigManager {
	
	public static DbConfig getDbConfig() {
		DbConfig dbConfig = null;
		Properties properties = null;
		String configFile = null;
		try {
			dbConfig = new DbConfig();
			configFile = System.getProperty("user.home");
			configFile = configFile + File.separator + "config.properties";
			properties = new Properties();
			properties.load(new FileInputStream(configFile));
			dbConfig.setDbDriver(properties.getProperty("db_driver"));
			dbConfig.setDbUrl(properties.getProperty("db_url"));
			dbConfig.setDbPort(properties.getProperty("db_port"));
			dbConfig.setDbUser(properties.getProperty("db_user"));
			dbConfig.setDbPassword(properties.getProperty("db_password"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return dbConfig;
	}
	
}
