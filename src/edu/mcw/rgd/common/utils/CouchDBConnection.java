package edu.mcw.rgd.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

public class CouchDBConnection {
	protected String dbHost; 
	protected String dbName;
	protected String viewGroupName;
	protected String viewName;

	public String getBaseURL(String dbHost, String dbName) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		return dbHost + "/" + dbName + "/";
	}
	
	public String getBaseURL() {
		return getBaseURL(dbHost, dbName);
	}

	public String getViewURL(String dbHost, String dbName, String viewGroupName,
			String viewName) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.viewGroupName = viewGroupName;
		this.viewName = viewName;
		return dbHost + "/" + dbName + "/_design/" + viewGroupName + "/_view/"
				+ viewName + "?";
	}
	
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public void setViewGroupName(String viewGroupName) {
		this.viewGroupName = viewGroupName;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public String getViewURL(String dbName, String viewGroupName,
			String viewName) {
		return getViewURL(dbHost, dbName, viewGroupName, viewName);
	}

	public String getViewURL(String viewGroupName,	String viewName) {
		return getViewURL(dbHost, dbName, viewGroupName, viewName);
	}
	
	public String getViewURL(String viewName) {
		return getViewURL(dbHost, dbName, viewGroupName, viewName);
	}
	
	public String queryView(String viewName, String parameters) {
		  try {
			  
				return BasicUtils.restGet(getViewURL(viewName) + parameters);
		 
			  } catch (Exception e) {
		 
				e.printStackTrace();
		 
				return null;
			  }
	}
	
	public String getDoc(String key) {
		  try {
			  
				return BasicUtils.restGet(getBaseURL() + key);
		 
			  } catch (Exception e) {
		 
				e.printStackTrace();
		 
				return null;
			  }
		
	}
	
	public static void main(String[] args) {
		CouchDBConnection conn = new CouchDBConnection();
		conn.setDbHost("http://localhost:5984");
		conn.setDbName("pubmed_test");
		conn.setViewGroupName("test_view");
		String json_output = conn.queryView("mesh_terms", "startkey=0&endkey=22615797");
//		System.out.println(json_output);
		Gson gs = new Gson();
		CouchDBResultSet rs = gs.fromJson(json_output, CouchDBResultSet.class);
		for (CouchDBResultSet.IntStringRow row : rs.rows) {
			if (row.value != null && row.value.contains("Rat"))
			System.out.println("key: " + row.key + "---value: " + row.value);
		}
		return;
	}
}
