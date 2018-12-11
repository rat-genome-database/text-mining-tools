package edu.mcw.rgd.nlp.datamodel;

import edu.mcw.rgd.common.utils.BasicUtils;

public class NcbiQueryLogEntry {

	private Long id;
	private String db;
	private String cmd;
	private String dopt;
	private String query;
	private String timeStamp;
	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}
	/**
	 * @param db the db to set
	 */
	public void setDb(String db) {
		this.db = db;
	}
	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	/**
	 * @return the dopt
	 */
	public String getDopt() {
		return dopt;
	}
	/**
	 * @param dopt the dopt to set
	 */
	public void setDopt(String dopt) {
		this.dopt = dopt;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String toString() {
		String value = "";
		
		if (getTimeStamp() != null) {
			value += getTimeStamp();
		}
		value += "\t";
		
		if (getDb() != null) {
			value += getDb();
		}
		value += "\t";
		if (getCmd() != null) {
			value += getCmd();
		}
		value += "\t";
		if (getDopt() != null) {
			value += getDopt();
		}
		value += "\t";
		if (getQuery() != null) {
			value += getQuery();
		}
		value += "\t";
		
		return value;
	}
	
	public static NcbiQueryLogEntry parseRawLog(String rawLog) {
		NcbiQueryLogEntry logEntry = new NcbiQueryLogEntry();
		
		logEntry.setTimeStamp(rawLog.substring(1, rawLog.indexOf(']')));
		logEntry.setCmd(BasicUtils.getNCBIQueryPara(rawLog, "Cmd"));
		logEntry.setDb(BasicUtils.getNCBIQueryPara(rawLog, "Db"));
		logEntry.setDopt(BasicUtils.getNCBIQueryPara(rawLog, "Dopt"));
		logEntry.setQuery(BasicUtils.getNCBIQueryPara(rawLog, "Term"));
		
		return logEntry;
	}
}
