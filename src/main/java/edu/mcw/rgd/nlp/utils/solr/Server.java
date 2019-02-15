package edu.mcw.rgd.nlp.utils.solr;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.solr.util.SimplePostTool;

public class Server {
	private String url;
	private URL postURL;
	public OutputStream outputStream = System.out;
	private SimplePostTool postTool = null;
	
	public Server (String service_url) throws Exception {
		url = service_url;
		try {
			postURL = new URL(url + "/update");
		} catch (MalformedURLException e) {
			throw e;
		};
	}
	
/*	public void updateDoc(XmlDoc doc) throws Exception {
		try {
			if (postTool == null) postTool = new SimplePostTool(postURL);

			postTool.postData(new ByteArrayInputStream(("<add>" + doc.toString() + "</add>").getBytes("UTF-8")), null, outputStream, SimplePostTool.DEFAULT_DATA_TYPE);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	public void deleteDocs(String query) {
		if (postTool == null) postTool = new SimplePostTool(postURL);
		postTool.postData(new ByteArrayInputStream(("<delete><query>" + query + "</query></delete>").getBytes()), null, outputStream, SimplePostTool.DEFAULT_DATA_TYPE);
	}
	*/
	public void commitChanges() {
		postTool.commit();
	}
	
	public static void main(String[] args) {
		XmlDoc doc = new XmlDoc();
		System.out.println(doc.toString());
		
		try {
			Server server = new Server("http://dev.ontomate.rgd.mcw.edu/solr/");
//			doc.add("pmid", "622");
//			doc.add("title", "test title");
//			doc.add("abstract", "abstract tested");
//			doc.add("onto_id", "RS:00000161");
//			doc.add("onto_id", "RS:00000162");
//			doc.add("xdb_id", "RGD:132528");
//			doc.add("xdb_id", "RGD:132529");
//
//			server.updateDoc(doc);
//			
//			doc = new XmlDoc();
//			doc.add("pmid", "621");
//			doc.add("title", "test title");
//			doc.add("abstract", "abstract tested");
//			doc.add("onto_id", "RS:00000163");
//			doc.add("onto_id", "RS:00000164");
//			doc.add("xdb_id", "RGD:132530");
//			doc.add("xdb_id", "RGD:132531");
//
			
			// Delete all docs
//			server.updateDoc(doc);
//			server.deleteDocs("*:*");
//			server.commitChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
}
