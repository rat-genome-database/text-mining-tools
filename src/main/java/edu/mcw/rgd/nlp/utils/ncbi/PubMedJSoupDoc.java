package edu.mcw.rgd.nlp.utils.ncbi;


import java.util.ArrayList;
import java.util.List;


import org.apache.hadoop.mapreduce.v2.proto.MRProtos;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


public class PubMedJSoupDoc {

	public static void main(String[] args) {
	//	String text=ReadWrite.read("data/pubmed_result.xml");
		String text="<ns1:PubmedArticle><ns1:MedlineCitation Status=\"Publisher\" Owner=\"NLM\"><ns1:PMID Version=\"1\">26603200</ns1:PMID><ns1:DateCreated><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>25</ns1:Day></ns1:DateCreated><ns1:DateRevised><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>26</ns1:Day></ns1:DateRevised><ns1:Article PubModel=\"Print-Electronic\"><ns1:Journal><ns1:ISSN IssnType=\"Electronic\">1934-1563</ns1:ISSN><ns1:JournalIssue CitedMedium=\"Internet\"><ns1:PubDate><ns1:Year>1956-1957</ns1:Year><ns1:Month>Nov</ns1:Month><ns1:Day>18</ns1:Day></ns1:PubDate></ns1:JournalIssue><ns1:Title>PM &amp; R : the journal of injury, function, and rehabilitation</ns1:Title><ns1:ISOAbbreviation>PM R</ns1:ISOAbbreviation></ns1:Journal><ns1:ArticleTitle>Outcomes of Inpatient Rehabilitation in Patients with Simultaneous Bilateral Total Knee Arthroplasty.</ns1:ArticleTitle><ns1:Pagination><ns1:MedlinePgn/></ns1:Pagination><ns1:ELocationID EIdType=\"pii\">S1934-1482(15)01129-6</ns1:ELocationID><ns1:ELocationID EIdType=\"doi\">10.1016/j.pmrj.2015.11.005</ns1:ELocationID><ns1:Abstract><ns1:AbstractText Label=\"BACKGROUND\" NlmCategory=\"BACKGROUND\">The number of total knee arthroplasty (TKA) procedures performed in the United States is increasing each year, and the number of bilateral TKA procedures has also increased over the past 2 decades. However, there are limited studies in the literature that have investigated the rehabilitation outcomes of patients with bilateral TKA. This study was performed to provide information on the benefits and role of inpatient rehabilitation on patients after bilateral TKA.</ns1:AbstractText><ns1:AbstractText Label=\"OBJECTIVE\" NlmCategory=\"OBJECTIVE\">To investigate the functional outcomes, complications and transfer rates of patients in the inpatient rehabilitation setting who undergo simultaneous bilateral TKA.</ns1:AbstractText><ns1:AbstractText Label=\"DESIGN\" NlmCategory=\"METHODS\">Retrospective cohort study.</ns1:AbstractText><ns1:AbstractText Label=\"SETTING\" NlmCategory=\"METHODS\">Freestanding inpatient rehabilitation hospital.</ns1:AbstractText><ns1:AbstractText Label=\"PATIENTS\" NlmCategory=\"METHODS\">94 patients admitted to an inpatient rehabilitation hospital after simultaneous bilateral TKA from 2008-2013.</ns1:AbstractText><ns1:AbstractText Label=\"METHODS\" NlmCategory=\"METHODS\">Retrospective chart review of demographic, clinical and functional data for patients admitted to inpatient rehabilitation after simultaneous bilateral TKA.</ns1:AbstractText><ns1:AbstractText Label=\"MAIN OUTCOME MEASURES\" NlmCategory=\"METHODS\">Length of stay (LOS), admission and discharge Functional Independence Measure (FIM), FIM efficiency.</ns1:AbstractText><ns1:AbstractText Label=\"RESULTS\" NlmCategory=\"RESULTS\">The study included 27 (28.7%) male and 67 (71.3%) female patients ages 42.0 to 86.9 years, mean of 65.6 ?? 10.2 years. Mean length of time between surgery and admission to inpatient rehabilitation was 4.5 ?? 3.3 days. Mean LOS in rehabilitation was 11.7 ?? 4.2 days. Mean admission and discharge FIM scores were 87.3 ?? 11.7 and 113.4 ?? 4.8, respectively, with a mean FIM gain of 26.1 ?? 10.5. The mean FIM efficiency was 2.33 ?? 0.84. Eight patients required transfer to an acute care hospital. Complications leading to transfer to acute care facilities included sepsis, cardiac arrhythmias, knee dislocation and suspected small bowel obstruction. Eighty-eight patients were discharged home, 4 patients were discharged to skilled nursing facilities, and 2 patients were transferred to an acute care hospital and did not return to the inpatient rehabilitation hospital.</ns1:AbstractText><ns1:AbstractText Label=\"CONCLUSIONS\" NlmCategory=\"CONCLUSIONS\">Patients after simultaneous bilateral TKA demonstrate functional gains when admitted to inpatient rehabilitation facilities based on FIM gains and FIM efficiency scores. Eight and one-half percent of patients in this cohort required transfer to an acute care facility due to complications during inpatient rehabilitation, and 93.6% of patients were discharged home.</ns1:AbstractText><ns1:CopyrightInformation>Copyright ?? 2015 American Academy of Physical Medicine and Rehabilitation. Published by Elsevier Inc. All rights reserved.</ns1:CopyrightInformation></ns1:Abstract><ns1:AuthorList><ns1:Author><ns1:LastName>Chu</ns1:LastName><ns1:ForeName>Samuel K</ns1:ForeName><ns1:Initials>SK</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States. Electronic address: schu@ric.org.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author><ns1:LastName>Babu</ns1:LastName><ns1:ForeName>Ashwin N</ns1:ForeName><ns1:Initials>AN</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author><ns1:LastName>McCormick</ns1:LastName><ns1:ForeName>Zachary</ns1:ForeName><ns1:Initials>Z</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author><ns1:LastName>Mathews</ns1:LastName><ns1:ForeName>Amy</ns1:ForeName><ns1:Initials>A</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author><ns1:LastName>Toledo</ns1:LastName><ns1:ForeName>Santiago</ns1:ForeName><ns1:Initials>S</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author><ns1:LastName>Oswald</ns1:LastName><ns1:ForeName>Matthew</ns1:ForeName><ns1:Initials>M</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Physical Medicine and Rehabilitation, Northwestern University Feinberg, School of Medicine, and Rehabilitation Institute of Chicago, Chicago, IL, United States.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author></ns1:AuthorList><ns1:Language>ENG</ns1:Language><ns1:PublicationTypeList><ns1:PublicationType UI=\"\">JOURNAL ARTICLE</ns1:PublicationType></ns1:PublicationTypeList><ns1:ArticleDate DateType=\"Electronic\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>18</ns1:Day></ns1:ArticleDate></ns1:Article><ns1:MedlineJournalInfo><ns1:MedlineTA>PM R</ns1:MedlineTA><ns1:NlmUniqueID>101491319</ns1:NlmUniqueID><ns1:ISSNLinking>1934-1482</ns1:ISSNLinking></ns1:MedlineJournalInfo></ns1:MedlineCitation><ns1:PubmedData><ns1:History><ns1:PubMedPubDate PubStatus=\"received\"><ns1:Year>2015</ns1:Year><ns1:Month>6</ns1:Month><ns1:Day>29</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"revised\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>6</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"accepted\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>7</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"entrez\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>26</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"pubmed\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>26</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"medline\"><ns1:Year>2015</ns1:Year><ns1:Month>11</ns1:Month><ns1:Day>26</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate></ns1:History><ns1:PublicationStatus>aheadofprint</ns1:PublicationStatus><ns1:ArticleIdList><ns1:ArticleId IdType=\"pii\">S1934-1482(15)01129-6</ns1:ArticleId><ns1:ArticleId IdType=\"doi\">10.1016/j.pmrj.2015.11.005</ns1:ArticleId><ns1:ArticleId IdType=\"pubmed\">26603200</ns1:ArticleId></ns1:ArticleIdList></ns1:PubmedData></ns1:PubmedArticle>";
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
		String journal= new String();
		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		for(Element e:xmlDoc.getElementsByTag("ns1:medlinecitation"))
			for(Element e1:e.getElementsByTag("ns1:Article"))
				for (Element e2 : e1.getElementsByTag("ns1:Journal")) {
					medDate = e2.getElementsByTag("ns1:medlinedate").html();
					if(medDate==null || medDate.equals("")){
						for(Element	e3:e2.getElementsByTag("ns1:JournalIssue"))
							for(Element e4:e3.getElementsByTag("ns1:PubDate")){
								year=e4.getElementsByTag("ns1:Year").html();
								month=e4.getElementsByTag("ns1:Month").html();
							}

					}

				}

		if (medDate != null && !medDate.equals("")) {
				String[] seg = medDate.split(" ");
				year = seg[0];
				if(year.contains("-")){
					year=year.substring(0, year.indexOf("-"));
				}
				if(seg.length>1)
				month = seg[1].toLowerCase();
			else  month="/01";
			}else{
			if(year.contains("-")){
				year=year.substring(0, year.indexOf("-"));
			}
		}
		 		if (month.toLowerCase().contains("ja".toLowerCase()))
					month = "/01";
				else if (month.toLowerCase().contains("fe".toLowerCase()))
					month = "/02";
				else if (month.toLowerCase().contains("mar".toLowerCase()))
					month = "/03";
				else if (month.toLowerCase().contains("ap".toLowerCase()))
					month = "/04";
				else if (month.toLowerCase().contains("may".toLowerCase()))
					month = "/05";
				else if (month.toLowerCase().contains("jun".toLowerCase()))
					month = "/06";
				else if (month.toLowerCase().contains("jul".toLowerCase()))
					month = "/07";
				else if (month.toLowerCase().contains("au".toLowerCase()))
					month = "/08";
				else if (month.toLowerCase().contains("sep".toLowerCase()))
					month = "/09";
				else if (month.toLowerCase().contains("oc".toLowerCase()))
					month = "/10";
				else if (month.toLowerCase().contains("no".toLowerCase()))
					month = "/11";
				else if (month.toLowerCase().contains("de".toLowerCase()))
					month = "/12";
				if (month.isEmpty() || !month.contains("/"))
					month = "/01";

	//	}
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


