package edu.mcw.rgd.nlp.datamodel;

public class SolrOntologyEntry {
	/**
	 * @return the idFieldName
	 */
	public String getIdFieldName() {
		return idFieldName;
	}

	/**	"rs_id", "rs_term", "rs_count"

	 * @param idFieldName the idFieldName to set
	 */
	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	/**
	 * @return the termFieldName	"rs_id", "rs_term", "rs_count"

	 */
	public String getTermFieldName() {
		return termFieldName;
	}

	/**
	 * @param termFieldName the termFieldName to set
	 */
	public void setTermFieldName(String termFieldName) {
		this.termFieldName = termFieldName;
	}

	/**
	 * @return the countFieldName
	 */
	public String getCountFieldName() {
		return countFieldName;
	}

	/**
	 * @param countFieldName the countFieldName to set
	 */
	public void setCountFieldName(String countFieldName) {
		this.countFieldName = countFieldName;
	}

	private String idFieldName;
	private String termFieldName;
	private String countFieldName;
	private String posFieldName;
	
	/**
	 * @return the posFieldName
	 */
	public String getPosFieldName() {
		return posFieldName;
	}

	/**
	 * @param posFieldName the posFieldName to set
	 */
	public void setPosFieldName(String posFieldName) {
		this.posFieldName = posFieldName;
	}

	public SolrOntologyEntry(String idFieldName, String termFieldName, String countFieldName,
			String posFieldName) {
		setIdFieldName(idFieldName);
		setTermFieldName(termFieldName);
		setCountFieldName(countFieldName);
		setPosFieldName(posFieldName);
	}
}
