package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.ResultSet;


import edu.mcw.rgd.common.utils.DAOBase;

public class OntTermConnectionsDAO extends DAOBase {

	public static String tableName = "ont_term_connections";

	/**
	 * Check if term2 is a parent of term1 
	 * @param term1
	 * @param term2
	 * @return true if a parenthood relationship is found.
	 */
	public static boolean isParent(String term1, String term2) throws Exception {
		ResultSet rs = DocDBConnection.executeQuery("select SQL_NO_CACHE child_term from "
				+ tableName + " where child_term='" + term1 + "' and parent_term='"
				+ term2 + "' limit 1");
		try {
			boolean returnValue = false;
			if (rs != null) {
				returnValue = rs.first();
			}
			DocDBConnection.closeRsStatement(rs);

			return returnValue;

		} catch (Exception e) {
			System.err.print("ERROR: "+ term1+ ", "+ term2);
			logger.error("Error checking term parenthood", e);
			logger.info("Error: "+term1+", "+term2);
			DocDBConnection.disconnect();
			return false;
		}

	}

}
