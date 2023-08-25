package edu.mcw.rgd.database.ncbi.pubmed;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.common.utils.HbaseUtils;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDoc;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDocSet;
import org.apache.hadoop.hbase.client.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
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
		for(Element e:xmlDoc.getElementsByTag("ns1:article-meta"))
			for(Element e1:e.getElementsByTag("ns1:article-id")){
			String iid=e1.attr("pub-id-type").toString();
			if(iid.contains("pmid"))
				return e1.text();
		}

		return null;
	}
	public static String getPmcId(Document xmlDoc){
		for(Element e:xmlDoc.getElementsByTag("ns1:article-meta"))
			for(Element e1:e.getElementsByTag("ns1:article-id")){
				String iid=e1.attr("pub-id-type").toString();
				if(iid.contains("pmc"))
					return e1.text();
			}

		return null;
	}
	public static String getDoi(Document xmlDoc){
		for(Element e:xmlDoc.getElementsByTag("ns1:article-meta"))
			for(Element e1:e.getElementsByTag("ns1:article-id")){
				String iid=e1.attr("pub-id-type").toString();
				if(iid.contains("doi"))
					return e1.html();
			}

		return null;
	}
	public static String getArticleAuthors(Document xmlDoc){
		StringBuilder sb=new StringBuilder();

		for(Element e:xmlDoc.getElementsByTag("ns1:contrib")){
			for(Element e1:e.getElementsByTag("ns1:name")){
				String name=e1.getElementsByTag("ns1:surname").html();
				name+=", "+e1.getElementsByTag("ns1:given-names").html();
				sb.append(name);
				sb.append(";");
			}
		}
		return sb.toString();
	}
	public static String getAffiliation(Document xmlDoc){

		for(Element e: xmlDoc.getElementsByTag("ns1:article-meta"))
			for(Element e1: e.getElementsByTag("ns1:aff")){
				return e1.getElementsByTag("ns1:institution").html();
			}
		return null;
	}
	public static String getArticleTitle(Document xmlDoc){
		StringBuffer out=new StringBuffer();
		for(Element e1:xmlDoc.getElementsByTag("ns1:title-group"))
			out.append(e1.getElementsByTag("ns1:article-title").text());

		return out.toString();
	}
	/*public static String getArticleAbstract(Document xmlDoc){
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:article")){
			for(Element e1:e.getElementsByTag("ns1:abstract")){
				for(Element e2:e1.children()){
					if (!e2.ownText().trim().equals("")) {
						out.append("\t"+e2.ownText() + "\r\n");
					}
				}
			}
		}
		return out.toString();
	}*/
	public static String getArticleAbstract(Document xmlDoc) {
		StringBuffer out = new StringBuffer();
		for (Element e : xmlDoc.getElementsByTag("ns1:article"))
			for (Element e1 : e.getElementsByTag("ns1:abstract")){
				for (Element e2: e1.children()){
					out.append(textSifter(e2));
					if (e1.getElementsByTag("ns1:sec").size() > 0) {
						for (Element e3: e2.children()){
							out.append(textSifter(e3));
						}
					}
				}
			}
		return out.toString();
	}
	/*public static String getArticleBody(Document xmlDoc){
		StringBuffer out=new StringBuffer();
		for(Element e:xmlDoc.getElementsByTag("ns1:article")){
			for(Element e1:e.getElementsByTag("ns1:body")){
				for(Element e2:e1.children()){
					for (Element e3:e2.children()){
					if (!e3.ownText().trim().equals("")) {
						out.append("\t"+e3.ownText() + "\r\n");
					}
				}
			}
		}
		}
		return out.toString();
	}*/

	public static String textSifter(Element e){
		StringBuffer result = new StringBuffer();
		if (!e.ownText().trim().equals("")) {
			if (e.getElementsByTag("ns1:sub") != null  || e.getElementsByTag("ns1:sup") != null  || e.getElementsByTag("ns1:italic") != null ) {
				result.append("\t");
				for (Node i : e.childNodes()) {
					if (i instanceof TextNode) {
						TextNode textNode = (TextNode) i;
						if (!textNode.text().replaceAll("[^a-zA-Z0-9]", " ").trim().equals("")){
							result.append(textNode.text().trim());
						}
					} else if (i instanceof Element) {
						if ((i.nodeName().equals("ns1:sub"))) {
							result.append(" {" + ((Element) i).ownText().trim() + "} ");
						} else if ((i.nodeName().equals("ns1:italic"))){
							String iid = i.attr("toggle").toString();
//							if (iid.contains("yes")) {
							result.append(" *" + ((Element) i).ownText().trim() + "* ");
							//}
						}else if ((i.nodeName().equals("ns1:sup"))&& !(i.parent().nodeName().equals("ns1:xref"))) {
							if (i.childNode(0).nodeName().equals("ns1:italic")) {
								String iid = i.childNode(0).attr("toggle").toString();
								if (iid.contains("yes")) {
									result.append(" ^*" + ((Element) i.childNode(0)).ownText().trim() + "*^ ");
								}
							} else if ((!((Element) i).ownText().replaceAll("[^a-zA-Z0-9]", " ").trim().equals("")) && !(i.parent().nodeName().equals("ns1:xref")) && !i.childNode(0).nodeName().equals("ns1:xref")) {
								result.append(" ^" + ((Element) i).ownText().trim() + "^ ");
							}
						}
					}
				}
				result.append("\r\n");
			} else {
				result.append("\t" + e.ownText().trim() + "\r\n");
			}
		}
		return result.toString();
	}

	/*public static String getArticleBody(Document xmlDoc) {
		StringBuffer out = new StringBuffer();
		for (Element e : xmlDoc.getElementsByTag("ns1:article"))
			for (Element e1 : e.getElementsByTag("ns1:body"))
				for (Element e2 : e1.children())
					for (Element e3 : e2.children()) {
						if (!e3.ownText().trim().equals("")) {
							if (e3.getElementsByTag("ns1:sub") != null) {
								out.append("\t");
								for (Node i : e3.childNodes()) {
									if (i instanceof TextNode) {
										TextNode textNode = (TextNode) i;
										if (!textNode.text().replaceAll("[^a-zA-Z0-9]", " ").trim().equals("")){
											out.append(textNode.text());
										}
									} else if (i instanceof Element) {
										if ((i.nodeName().equals("ns1:sub"))) {
											out.append(" {" + ((Element) i).ownText() + "} ");
										} else if ((i.nodeName().equals("ns1:italic"))){
											String iid = i.attr("toggle").toString();
//											if (iid.contains("yes")) {
											out.append(" *" + ((Element) i).ownText() + "* ");
											//}

										}else if ((i.nodeName().equals("ns1:sup"))&& !(i.parent().nodeName().equals("ns1:xref"))) {
											if (i.childNode(0).nodeName().equals("ns1:italic")) {
												String iid = i.childNode(0).attr("toggle").toString();
												if (iid.contains("yes")) {
													out.append(" ^*" + ((Element) i.childNode(0)).ownText() + "*^ ");
												}
											} else if ((!((Element) i).ownText().replaceAll("[^a-zA-Z0-9]", " ").trim().equals("")) && !(i.parent().nodeName().equals("ns1:xref")) && !i.childNode(0).nodeName().equals("ns1:xref")) {
												out.append(" ^" + ((Element) i).ownText() + "^ ");
											}
										}
									}
								}
								out.append("\r\n");
							} else {
								out.append("\t" + e3.ownText() + "\r\n");
							}
						}
					}

		return out.toString().replaceAll("\\(\\s*[,-;–\\s]*\\)","").replaceAll("\\[\\s*[,-;–\\s]*\\]","");
	}*/
	public static String getArticleBody(Document xmlDoc) {
		StringBuffer out = new StringBuffer();
		for (Element e : xmlDoc.getElementsByTag("ns1:article"))
			for (Element e1 : e.getElementsByTag("ns1:body")){
				for (Element e2: e1.children()){
					out.append(textSifter(e2));
					if (e1.getElementsByTag("ns1:sec").size() > 0) {
						for (Element e3: e2.children()){
							out.append(textSifter(e3));
							if (e3.tagName().equals("ns1:sec")){
								for (Element e4: e3.children()){
									out.append(textSifter(e4));
								}
							}
						}
					}
				}
			}
		return out.toString().replaceAll("\\(\\s*[,-;–\\s]*\\)","").replaceAll("\\[\\s*[,-;–\\s]*\\]","");
	}

	public static String getTables(Document xmlDoc) {
		StringBuffer out = new StringBuffer();
		for (Element e : xmlDoc.getElementsByTag("ns1:article"))
			for (Element e1 : e.getElementsByTag("ns1:table-wrap")) {
				for (Element e2 : e1.getElementsByTag("ns1:p")) {
					out.append("\t" + e2.text() + "\r\n");
				}
			}
		return out.toString();
	}
	public static String getFigures(Document xmlDoc){
		StringBuffer out = new StringBuffer();
		for (Element e : xmlDoc.getElementsByTag("ns1:article"))
			for (Element e2 : e.getElementsByTag("ns1:fig")) {
				for (Element e3 : e2.getElementsByTag("ns1:p")) {
					out.append("\t" + e3.text() + "\r\n");
				}
			}
		return out.toString();
	}

	public static String getKeyWords(Document xmlDoc){
		StringBuffer out = new StringBuffer();
		for (Element e: xmlDoc.getElementsByTag("ns1:article")){
			out.append("\t");
			for (Element e2 : e.getElementsByTag("ns1:kwd")){
				if (!e2.ownText().trim().equals("")){
					out.append(e2.ownText()+", ");
				}
			}
			out.append("\r\n");
		}
		int lastIndex = out.toString().lastIndexOf(", ");

		if (lastIndex >= 0) {
			// Replace the last comma with an empty string
			return out.toString().substring(0, lastIndex) + out.toString().substring(lastIndex + 2);
		}
		return out.toString();
	}



	public static String getArticlePubDate(Document xmlDoc){
		String year=new String();
		String month=new String();
		String dateStr= new String();
			for(Element e1:xmlDoc.getElementsByTag("ns1:article"))
				for (Element e2 : e1.getElementsByTag("ns1:pub-date")) {
					String iid=e2.attr("pub-type").toString();
					if(iid.contains("pmc-release")) {
								year=e2.getElementsByTag("ns1:year").html();
								month=e2.getElementsByTag("ns1:month").html();
								dateStr=e2.getElementsByTag("ns1:day").html();
							}

					}
		dateStr = year+"/"+month+"/"+dateStr;
			//System.err.println(dateStr);
		return dateStr;
	}

	private static String getArticleType(Document xmlDoc){
		String out = new String();

		for (Element e: xmlDoc.getElementsByTag("ns1:article")){
			String iid = e.attr("article-type").toString();
			System.out.println(iid);
			//if(types.contains(iid)){
				out = iid;
			//}
		}
		return out;
	}

	public static boolean typeWanted(String articleXml){
		Document xmlDoc= Jsoup.parse(articleXml, "", Parser.xmlParser());
		List<String> types = new ArrayList<>();
		types.add("research-article");
		types.add("retraction");
		types.add("letter");
		types.add("correction");
		types.add("brief-reports");
		types.add("addendum");
		if (types.contains(getArticleType(xmlDoc))){
			return true;
		}
		return false;
	}


}
