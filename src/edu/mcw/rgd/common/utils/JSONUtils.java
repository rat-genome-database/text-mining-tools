package edu.mcw.rgd.common.utils;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.codehaus.jackson.map.ObjectMapper;

import edu.mcw.rgd.database.ncbi.pubmed.PubmedCouchDAO;
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubmedArticleSetChoiceE;
public class JSONUtils {

	public static void main(String[] args) throws Exception {
	    String jsonInput = PubmedCouchDAO.getDoc("10000");
//	    jsonInput = jsonInput.replace("\"$\"", "\"Value\"");
 
	    System.out.println(jsonInput);
        
//        XMLSerializer serializer = new XMLSerializer(); 
        JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonInput ); 
//        String xml = serializer.write( json );  
//        JSONObject obj = json.getJSONObject("PubmedData").getJSONObject("ArticleIdList").getJSONObject("ArticleId");
//        Object obj = json.getJSONObject("MedlineCitation").getJSONObject("Article").getJSONObject("Abstract").get("AbstractText");
        Object obj = json.getJSONObject("MedlineCitation").getJSONObject("Article")
				.getJSONObject("Journal").getJSONObject("JournalIssue").getJSONObject("PubDate");
        if (obj == null)  {
        	System.out.print("no such object");
        	return;
        }
        if (obj instanceof JSONArray) {
        	JSONArray ja = (JSONArray) obj;
        	for (int i = 0; i < ja.size(); i++) {
        		JSONObject obj1 = ja.getJSONObject(i);
                System.out.println(obj1.getString("Label"));             
                System.out.println(obj1.getString("$"));             
        	}
        } else if (obj instanceof String) {
            System.out.println((String) obj);             
        };
        
	}
	
	public static JSONArray toJSONArray(Object object) {
		try {
			if (object instanceof JSONArray) {
				return (JSONArray) object;
			} else {
				JSONArray returnList = new JSONArray();
				returnList.add(object);
				return returnList;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
