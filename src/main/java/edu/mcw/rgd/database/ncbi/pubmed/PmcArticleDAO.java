package edu.mcw.rgd.database.ncbi.pubmed;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.common.utils.HbaseUtils;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDoc;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDocSet;
import org.apache.hadoop.hbase.client.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class PmcArticleDAO extends ArticleDAO{



	public static String getPMID(Document xmlDoc){
		String id="";
		for(Element e:xmlDoc.getElementsByTag("article-meta"))
			for(Element e1:e.getElementsByTag("article-id")){
			String iid=e1.attr("pub-id-type").toString();
			if(iid.contains("pmid"))
				return e1.html();
		}

		return null;
	}
	public static String getPmcId(Document xmlDoc){
		for(Element e:xmlDoc.getElementsByTag("article-meta"))
			for(Element e1:e.getElementsByTag("article-id")){
				String iid=e1.attr("pub-id-type").toString();
				if(iid.contains("pmc"))
					return e1.html();
			}

		return null;
	}
	public static String getDoi(Document xmlDoc){
		for(Element e:xmlDoc.getElementsByTag("article-meta"))
			for(Element e1:e.getElementsByTag("article-id")){
				String iid=e1.attr("pub-id-type").toString();
				if(iid.contains("doi"))
					return e1.html();
			}

		return null;
	}
	public static String getArticleAuthors(Document xmlDoc){
		StringBuilder sb=new StringBuilder();

		for(Element e:xmlDoc.getElementsByTag("contrib")){
			for(Element e1:e.getElementsByTag("name")){
				String name=e1.getElementsByTag("surname").html();
				name+=", "+e1.getElementsByTag("given-names").html();
				sb.append(name);
				sb.append(";");
			}
		}
		return sb.toString();
	}
	public static String getAffiliation(Document xmlDoc){

		for(Element e: xmlDoc.getElementsByTag("article-meta"))
			for(Element e1: e.getElementsByTag("aff")){
				return e1.getElementsByTag("institution").html();
			}
		return null;
	}
	public static String getArticleTitle(Document xmlDoc){
		StringBuffer out=new StringBuffer();
		for(Element e1:xmlDoc.getElementsByTag("title-group"))
			out.append(e1.getElementsByTag("article-title").html());

		return out.toString();
	}
	public static String getArticleAbstract(Document xmlDoc){
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("article")){
			for(Element e1:e.getElementsByTag("body")){
				out.append("\t"+e1.html().toString()+"\r\n");
			}
		}

		return out.toString();
	}
	public static String getArticlePubDate(Document xmlDoc){
		String year=new String();
		String month=new String();
		String dateStr= new String();
			for(Element e1:xmlDoc.getElementsByTag("article"))
				for (Element e2 : e1.getElementsByTag("pub-date")) {
					String iid=e2.attr("pub-type").toString();
					if(iid.contains("pmc-release")) {
								year=e2.getElementsByTag("year").html();
								month=e2.getElementsByTag("month").html();
								dateStr=e2.getElementsByTag("day").html();
							}

					}
		dateStr = year+"/"+month+"/"+dateStr;
		return dateStr;
	}

}
