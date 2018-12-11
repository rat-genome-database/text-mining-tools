package edu.mcw.rgd.nlp.utils.ncbi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


public class PubMedJSoupDoc {

	public static void main(String[] args) {
		String text=ReadWrite.read("omid.xml");
		System.out.println(pubArticleDate(text));
		//		pmId(text);

	}
	//------------------------------------------------------------------------
	public static List<String> articleIdList(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		List<String> idList=new ArrayList<String>();

		for(Element e:xmlDoc.getElementsByTag("ns1:articleidList"))
			for(Element e1:e.getElementsByTag("ns1:articleid"))
				idList.add(e1.html().toString());

		return idList;
	}
	//-------------------------------------------------------------------	
	public static String abstractText(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:abstract")){
			for(Element e1:e.getElementsByTag("ns1:abstracttext")){
				out.append(e1.attr("label"));
				out.append("\t"+e1.html().toString()+"\r\n");
			}
		}

		return out.toString();
	}
	//----------------------------------------------------------------------
	public static List<String> authorList(String text){
		List<String> list=new ArrayList<String>();

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:authorList")){
			for(Element e1:e.getElementsByTag("ns1:author")){
				String name=e1.getElementsByTag("ns1:lastname").html();
				name+=", "+e1.getElementsByTag("ns1:forename").html();
				list.add(name);
			}
		}

		return list;
	}
	//----------------------------------------------------------------------
	public static List<String> chemicalList(String text){
		List<String> list=new ArrayList<String>();

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:chemicallist")){
			for(Element e1:e.getElementsByTag("ns1:chemical")){
				String name=e1.getElementsByTag("ns1:NameOfSubstance").html();
				list.add(name);
			}
		}

		return list;
	}
	//----------------------------------------------------------------------
	public static List<String> keywordList(String text){
		List<String> list=new ArrayList<String>();
		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:keywordlist")){
			for(Element e1:e.getElementsByTag("ns1:keyword")){
				list.add(e1.html().toString());
			}
		}

		return list;
	}
	//----------------------------------------------------------------------
	public static List<String> meshHeadingList(String text){
		List<String> list=new ArrayList<String>();

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:meshheadingList")){
			for(Element e1:e.getElementsByTag("ns1:meshheading")){
				String name=e1.getElementsByTag("ns1:descriptorname").html();
				list.add(name);
			}
		}
		return list;
	}
	//----------------------------------------------------------------------
	public static String pubJournalDate(String text){

		String year="";
		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:pubmedpubdate")){
			year=e.getElementsByTag("ns1:year").html();
			year+=" "+e.getElementsByTag("ns1:month").html();
			year+=" "+e.getElementsByTag("ns1:day").html();

		}
		return year;
	}
	//----------------------------------------------------------------------
/*	public static String pubArticleDate(String text){

		String out="";
		String year="2015";
		String month="/01";
		String medDate="/01";
		
		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:MedlineCitation"))
			for(Element e1:e.getElementsByTag("ns1:Article"))
				for(Element e2:e1.getElementsByTag("ns1:Journal"))
					for(Element e3:e2.getElementsByTag("ns1:PubDate")){
						year=e3.getElementsByTag("ns1:year").html();
						month=e3.getElementsByTag("ns1:month").html();
						medDate=e3.getElementsByTag("ns1:medlinedate").html();
					}

		if(month!=null){
			month=month.toLowerCase();
			if(month.contains("ja"))
				month="/01";
			else if(month.contains("fe"))
				month="/02";
			else if(month.contains("mar"))
				month="/03";
			else if(month.contains("ap"))
				month="/04";
			else if(month.contains("may"))
				month="/05";
			else if(month.contains("jun"))
				month="/06";
			else if(month.contains("jul"))
				month="/07";
			else if(month.contains("au"))
				month="/08";
			else if(month.contains("sep"))
				month="/09";
			else if(month.contains("oc"))
				month="/10";
			else if(month.contains("no"))
				month="/11";
			else if(month.contains("de"))
				month="/12";
		}
		else
			month="/01";
		
		if (year!=null){
			if(year.equals("2015"))
			{
				String[] seg=medDate.split(" ");
				year=seg[0];
				month="/01";
			}
		}
		else
			year="2015";
		
		if(month==null || month.isEmpty() || !month.contains("/"))
			month="/01";
		
		
		return year+month+"/01";
	}*/
	//----------------------------------------------------------------------
	
	public static String pubArticleDate(String text){

		String year=new String();
		String month=new String();
		String medDate=new String();

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:MedlineCitation"))
			for(Element e1:e.getElementsByTag("ns1:Article"))
				for(Element e2:e1.getElementsByTag("ns1:Journal"))
					for(Element e3:e2.getElementsByTag("ns1:PubDate")){
						medDate=e3.getElementsByTag("ns1:medlinedate").html();

					}

		if(medDate!=null){
			String[] seg=medDate.split(" ");
			year=seg[0];
			month= seg[1].toLowerCase();
			}
		if(month.contains("ja"))
            month="/01";
        else if(month.contains("fe"))
            month="/02";
        else if(month.contains("mar"))
            month="/03";
        else if(month.contains("ap"))
            month="/04";
        else if(month.contains("may"))
            month="/05";
        else if(month.contains("jun"))
            month="/06";
        else if(month.contains("jul"))
            month="/07";
        else if(month.contains("au"))
            month="/08";
        else if(month.contains("sep"))
            month="/09";
        else if(month.contains("oc"))
            month="/10";
        else if(month.contains("no"))
            month="/11";
        else if(month.contains("de"))
            month="/12";
		if(month.isEmpty() || !month.contains("/"))
			month="/01";
		return year+month+"/01";
	}
	public static List<String> pubTypeList(String text){

		List<String> list=new ArrayList<String>();

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:PublicationTypeList")){
			String name=e.getElementsByTag("ns1:PublicationType").html();
			list.add(name);
		}
		return list;
	}
	//----------------------------------------------------------------------
	public static String pmId(String text){

		String id="";
		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:articleidlist"))
			for(Element e1:e.getElementsByTag("ns1:articleid")){
				String iid=e1.attr("idtype").toString();
				if(iid.contains("pubmed"))
					return e1.html();
			}

		return null;
	}
	//----------------------------------------------------------------------
	public static String articleTitle(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		return xmlDoc.getElementsByTag("ns1:articletitle").html();
	}
	//----------------------------------------------------------------------
	public static String journalTitle(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:journal"))
			return e.getElementsByTag("ns1:title").html();
		return null;
	}
	//----------------------------------------------------------------------
	public static String journalVolume(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:journal"))
			return e.getElementsByTag("ns1:volume").html();
		return null;
	}
	//----------------------------------------------------------------------
	public static String journalIssue(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:journal"))
			return e.getElementsByTag("ns1:issue").html();
		return null;
	}
	//----------------------------------------------------------------------
	public static String journalPage(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:pagination"))
			return e.getElementsByTag("ns1:MedlinePgn").html();
		return null;
	}
	//----------------------------------------------------------------------
	public static String affiliation(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:authorlist"))
			for(Element e1: e.getElementsByTag("ns1:author")){
				return e1.getElementsByTag("ns1:affiliation").html();
			}
		return null;
	}
	//----------------------------------------------------------------------
	public static String issn(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());

		for(Element e: xmlDoc.getElementsByTag("ns1:article"))
			for(Element e1: e.getElementsByTag("ns1:journal")){
				return e1.getElementsByTag("ns1:issn").html();
			}
		return null;
	}

	//----------------------------------------------------------------------
	public static String[] parseBulkXml(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		List<String> list=new ArrayList<String>();

		for(Element e1: xmlDoc.getElementsByTag("ns1:PubmedArticle"))
			list.add(e1.toString());

		String[] arr=new String[list.size()];
		for(int i=0;i<list.size();i++)
			arr[i]=list.get(i);

		if(list.size()>0)
			return arr;
		else
			return null;
	}
}


