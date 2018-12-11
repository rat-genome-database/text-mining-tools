package edu.mcw.rgd.common.utils;

public class CouchDAOBase {
	
	protected static CouchDBConnection connection;
	
	/**
	 * @return the connection
	 */
	public static CouchDBConnection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public static void setConnection(CouchDBConnection connection) {
		CouchDAOBase.connection = connection;
	}

	public static void initConnection(String dbHost, String dbName, String viewGroupName) {
		connection = new CouchDBConnection();
		connection.setDbHost(dbHost);
		connection.setDbName(dbName);
		connection.setViewGroupName(viewGroupName);
	}
}
