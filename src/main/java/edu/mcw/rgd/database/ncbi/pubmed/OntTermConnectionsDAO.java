package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import edu.mcw.rgd.common.utils.DAOBase;
import org.json.JSONArray;
import org.json.JSONObject;

public class OntTermConnectionsDAO extends DAOBase {

	public static String tableName = "ont_term_connections";

	public static HashMap<String,String> _relationMap = new HashMap<>();

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

	public static HashMap<String,List<String>> getTerms(String term) throws Exception{
		try {

			String ont=term.replace(":","%3A");
			URL url = new URL("https://rest.rgd.mcw.edu/rgdws/ontology/ont/"+ont);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			InputStreamReader is = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(is);

			String output = br.readLine();
			is.close();
			br.close();
			conn.disconnect();
			JSONObject data = new JSONObject(output);

			HashMap<String,List<String>> dataMap = new HashMap<>();
			ArrayList<String> listdata = new ArrayList<String>();
			JSONArray childTerms = (JSONArray) data.get("childTerms");
			if (childTerms != null) {
				for (int i=0;i<childTerms.length();i++){
					listdata.add(childTerms.getString(i));
				}
			}
			dataMap.put("childTerms",listdata);


			listdata = new ArrayList<String>();
			JSONArray parentTerms = (JSONArray) data.get("parentTerms");
			if (parentTerms != null) {
				for (int i=0;i<parentTerms.length();i++){
					listdata.add(parentTerms.getString(i));
				}
			}
			dataMap.put("parentTerms",listdata);


			return dataMap;


		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}

}
