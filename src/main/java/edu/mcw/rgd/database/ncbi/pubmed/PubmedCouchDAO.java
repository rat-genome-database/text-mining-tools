package edu.mcw.rgd.database.ncbi.pubmed;


import edu.mcw.rgd.common.utils.CouchDAOBase;

public class PubmedCouchDAO extends CouchDAOBase {

	public static void initConnection() {
		if (connection == null) initConnection("http://localhost:5984",
				  "pubmed18", "article_views");
	}
	
	public static String getDoc(String pmid) {
		initConnection();
		return connection.getDoc(pmid);
	}
	
	public static void main(String[] args) {
		System.out.println(PubmedCouchDAO.getDoc("12615697"));
		
	}
}
