package edu.mcw.rgd.common.utils;

import java.io.BufferedReader;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.HttpRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.mcw.rgd.database.ncbi.pubmed.DocDBConnection;

public class BasicUtils {

	public static String strExceptionStackTrace(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
	
	public static String getUTFstr(String input_str) {
	byte[] input_bytes = input_str.getBytes();
	try {
		String output_str = new String(input_bytes, "UTF-8");
		return output_str;
	} catch (UnsupportedEncodingException e) {
		return "";
	}
	}
	
	public static String EscapeSQLStringValue(String input_value) {
		if (input_value != null) {
			return "'" + DocDBConnection.escapeSQL(input_value) + "'";
		} else {
			return "null";
		}
	}

	public static String EscapeSQLStringValueEmpty(String input_value) {
		if (input_value != null) {
			return "'" + DocDBConnection.escapeSQL(input_value) + "'";
		} else {
			return "";
		}
	}
	
	public static String restGet(String url, String contentType) throws Exception {
		  try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet getRequest = new HttpGet(url);
				if (contentType != null) getRequest.addHeader("accept", contentType);
		 
				HttpResponse response = httpClient.execute(getRequest);
		 
				if (response.getStatusLine().getStatusCode() != 200) {
					if (response.getStatusLine().getStatusCode() == 404) return "";
					throw new RuntimeException("Failed : HTTP error code : "
					   + response.getStatusLine().getStatusCode());
				}
		 
				BufferedReader br = new BufferedReader(
		                         new InputStreamReader((response.getEntity().getContent())));
		 
				String outputLine = "", output = "";
				while ((outputLine = br.readLine()) != null) {
					output += outputLine;
				}
		 
				httpClient.getConnectionManager().shutdown();
				return output;
		 
			  } catch (ClientProtocolException e) {
		 
				throw e;
		 
			  } catch (IOException e) {
		 
				throw e;
			  }
	}
	
	public static String restGet(String url) throws Exception {
		try {
			return restGet(url, "application/json");
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String restPut(String url, String contentType, String params) throws Exception {
		  try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPut putRequest = new HttpPut(url);
				if (contentType != null) putRequest.addHeader("Content-Type", contentType);
		 
				ByteArrayEntity requestEntity = new ByteArrayEntity(Bytes.toBytes(params));
				putRequest.setEntity(requestEntity);
				HttpResponse response = httpClient.execute(putRequest);
		 
				if (response.getStatusLine().getStatusCode() != 200) {
					if (response.getStatusLine().getStatusCode() == 404) return "";
					throw new RuntimeException("Failed : HTTP error code : "
					   + response.getStatusLine().getStatusCode());
				}
		 
				httpClient.getConnectionManager().shutdown();
				return "";
		 
			  } catch (ClientProtocolException e) {
		 
				throw e;
		 
			  } catch (IOException e) {
		 
				throw e;
			  }
	}
	
	public static String getNCBIQueryPara(String queryString, String paraName) {
		Pattern p = Pattern.compile("(" + paraName + "|" + paraName.toLowerCase() + 
				")=(.+?) [a-zA-Z]+?=");
		Matcher m = p.matcher(queryString);
		if (m.find()) return m.group(2);
		return "";
	}
	
	public static String removeComments(String inputStr) {
		String returnStr = inputStr.replaceAll("(?ms)/\\*.+?\\*/", "");
		returnStr = returnStr.replaceAll("(?ms)^\\s*//.+?$", "");
		return returnStr;
	}

	public static String removeEmptyLines(String inputStr) {
		String returnStr = inputStr.replaceAll("(?ms)^\\s*", "");
		return returnStr;
	}

	public static void main(String[] args) {
//		String a = "db=gene db=gene Cmd=Go Cmd=Go Term=(pc12 bdnf) AND \"Rattus norvegicus\"[porgn:__txid10116] Term=(pc12+bdnf)+AND+\"Rattus+norvegicus\"[porgn:__txid10116]";
//		System.out.println(BasicUtils.getNCBIQueryPara(a, "Db"));
		String a = "adfdf\r\n";
		a += "/* dkfjdkf \r\n";
		a += "kf*/ \r\n";
		a += "122333 \r\n";
		a += " \r\n";
		a += "  //122333 \r\n";
		a += "  wweehttp://122333 \r\n";
		a += "//122333 \r\n";
		a += "wwww122333 \r\n";
		a += " \r\n";
		a += "/* dkfjdkf \r\n";
		a += "kf*/ \r\n";
		a += "fffff122333 \r\n";
		a += " \r\n";
		a += " \r\n";
		System.out.println("-----");
		System.out.println(BasicUtils.removeEmptyLines(BasicUtils.removeComments(a)));
		System.out.println("-----");
	}
}
