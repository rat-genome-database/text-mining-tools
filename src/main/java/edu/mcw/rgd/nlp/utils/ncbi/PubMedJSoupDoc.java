package edu.mcw.rgd.nlp.utils.ncbi;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PubMedJSoupDoc {

	public static void main(String[] args) {
	//	String text=ReadWrite.read("data/pubmed_result.xml");
		String text="<ns1:PubmedArticle><ns1:MedlineCitation Status=\"Publisher\" Owner=\"NLM\"><ns1:PMID Version=\"1\">30970238</ns1:PMID><ns1:DateRevised><ns1:Year>2019</ns1:Year><ns1:Month>04</ns1:Month><ns1:Day>10</ns1:Day></ns1:DateRevised><ns1:Article PubModel=\"Print-Electronic\"><ns1:Journal><ns1:ISSN IssnType=\"Electronic\">1090-2422</ns1:ISSN><ns1:JournalIssue CitedMedium=\"Internet\"><ns1:PubDate><ns1:Year>2019</ns1:Year><ns1:Month>Apr</ns1:Month><ns1:Day>07</ns1:Day></ns1:PubDate></ns1:JournalIssue><ns1:Title>Experimental cell research</ns1:Title><ns1:ISOAbbreviation>Exp. Cell Res.</ns1:ISOAbbreviation></ns1:Journal><ns1:ArticleTitle>Hypoxia-induced disruption of neural vascular barrier is mediated by the intracellular induction of Fe(II) ion.</ns1:ArticleTitle><ns1:ELocationID EIdType=\"pii\" ValidYN=\"Y\">S0014-4827(19)30153-3</ns1:ELocationID><ns1:ELocationID EIdType=\"doi\" ValidYN=\"Y\">10.1016/j.yexcr.2019.04.003</ns1:ELocationID><ns1:Abstract><ns1:AbstractText>Neural vascular barrier maintains the optimal tissue microenvironment of central nervous system in which neural cells can function normally. In various neural diseases, the decrease in oxygen concentration, hypoxia, of affected tissues is known to accelerate the disease progression through disruption of neural vascular barrier. Therefore, the clarification of mechanisms underlying hypoxia-induced disruption of neural vascular barrier would definitely lead to the establishment of new effective therapies for intractable neural diseases. In the present study, we first found that hypoxia disrupts neural vascular barrier through pathways independent of HIF-1α and HIF-2α. Then, with a specific fluorescence probe for ferrous, Fe(II) ion, we have obtained the interesting data showing that hypoxia increased the intracellular level of Fe(II) ion in endothelial cells of our in vitro model for neural vascular barrier, and that hypoxia-induced disruption of neural vascular barrier could be inhibited by chelating Fe(II) ion in endothelial cells. Furthermore, in the presence of a reducing reagent for reactive oxygen species (ROS), hypoxia could not disrupt the neural vascular barrier despite that the hypoxic increase in intracellular level of Fe(II) ion was confirmed in endothelial cells. These results indicate that hypoxia-triggered increase in the level of intracellular Fe(II) ion and subsequent production of ROS, probably through Fenton reaction, are the essential pathway mediating the disruption of neural vascular barrier under hypoxia.</ns1:AbstractText><ns1:CopyrightInformation>Copyright © 2019. Published by Elsevier Inc.</ns1:CopyrightInformation></ns1:Abstract><ns1:AuthorList CompleteYN=\"Y\"><ns1:Author ValidYN=\"Y\"><ns1:LastName>Cui</ns1:LastName><ns1:ForeName>Dan</ns1:ForeName><ns1:Initials>D</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Pathology, Yamaguchi University Graduate School of Medicine, 1-1-1 Minami-Kogushi, Ube, Yamaguchi, 755-8505, Japan.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author ValidYN=\"Y\"><ns1:LastName>Arima</ns1:LastName><ns1:ForeName>Mitsuru</ns1:ForeName><ns1:Initials>M</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Pathology, Yamaguchi University Graduate School of Medicine, 1-1-1 Minami-Kogushi, Ube, Yamaguchi, 755-8505, Japan; Department of Ophthalmology, Kyushu University Graduate School of Medical Sciences, 3-1-1 Maidashi, Higashi-ku, Fukuoka City, Fukuoka, 812-8582, Japan.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author ValidYN=\"Y\"><ns1:LastName>Hirayama</ns1:LastName><ns1:ForeName>Tasuku</ns1:ForeName><ns1:Initials>T</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Laboratory of Pharmaceutical and Medicinal Chemistry, Gifu Pharmaceutical University, 1-25-4, Daigaku-nishi, Gifu-shi, Gifu, 501-1196, Japan.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author><ns1:Author ValidYN=\"Y\"><ns1:LastName>Ikeda</ns1:LastName><ns1:ForeName>Eiji</ns1:ForeName><ns1:Initials>E</ns1:Initials><ns1:AffiliationInfo><ns1:Affiliation>Department of Pathology, Yamaguchi University Graduate School of Medicine, 1-1-1 Minami-Kogushi, Ube, Yamaguchi, 755-8505, Japan. Electronic address: ikedae@yamaguchi-u.ac.jp.</ns1:Affiliation></ns1:AffiliationInfo></ns1:Author></ns1:AuthorList><ns1:Language>eng</ns1:Language><ns1:PublicationTypeList><ns1:PublicationType UI=\"D016428\">Journal Article</ns1:PublicationType></ns1:PublicationTypeList><ns1:ArticleDate DateType=\"Electronic\"><ns1:Year>2019</ns1:Year><ns1:Month>04</ns1:Month><ns1:Day>07</ns1:Day></ns1:ArticleDate></ns1:Article><ns1:MedlineJournalInfo><ns1:Country>United States</ns1:Country><ns1:MedlineTA>Exp Cell Res</ns1:MedlineTA><ns1:NlmUniqueID>0373226</ns1:NlmUniqueID><ns1:ISSNLinking>0014-4827</ns1:ISSNLinking></ns1:MedlineJournalInfo><ns1:KeywordList Owner=\"NOTNLM\"><ns1:Keyword MajorTopicYN=\"N\">Claudin-5</ns1:Keyword><ns1:Keyword MajorTopicYN=\"N\">Fe(II) ion</ns1:Keyword><ns1:Keyword MajorTopicYN=\"N\">Hypoxia</ns1:Keyword><ns1:Keyword MajorTopicYN=\"N\">Neural vascular barrier</ns1:Keyword></ns1:KeywordList></ns1:MedlineCitation><ns1:PubmedData><ns1:History><ns1:PubMedPubDate PubStatus=\"received\"><ns1:Year>2018</ns1:Year><ns1:Month>12</ns1:Month><ns1:Day>31</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"revised\"><ns1:Year>2019</ns1:Year><ns1:Month>04</ns1:Month><ns1:Day>01</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"accepted\"><ns1:Year>2019</ns1:Year><ns1:Month>04</ns1:Month><ns1:Day>03</ns1:Day></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"entrez\"><ns1:Year>2019</ns1:Year><ns1:Month>4</ns1:Month><ns1:Day>11</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"pubmed\"><ns1:Year>2019</ns1:Year><ns1:Month>4</ns1:Month><ns1:Day>11</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"medline\"><ns1:Year>2019</ns1:Year><ns1:Month>4</ns1:Month><ns1:Day>11</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate></ns1:History><ns1:PublicationStatus>aheadofprint</ns1:PublicationStatus><ns1:ArticleIdList><ns1:ArticleId IdType=\"pubmed\">30970238</ns1:ArticleId><ns1:ArticleId IdType=\"pii\">S0014-4827(19)30153-3</ns1:ArticleId><ns1:ArticleId IdType=\"doi\">10.1016/j.yexcr.2019.04.003</ns1:ArticleId></ns1:ArticleIdList></ns1:PubmedData></ns1:PubmedArticle>";
	//		String text="<ns1:PubmedArticle><ns1:MedlineCitation Status=\"MEDLINE\" Owner=\"NLM\"><ns1:PMID Version=\"1\">30239366</ns1:PMID><ns1:DateCompleted><ns1:Year>2019</ns1:Year><ns1:Month>03</ns1:Month><ns1:Day>14</ns1:Day></ns1:DateCompleted><ns1:DateRevised><ns1:Year>2019</ns1:Year><ns1:Month>03</ns1:Month><ns1:Day>14</ns1:Day></ns1:DateRevised><ns1:Article PubModel=\"Print\"><ns1:Journal><ns1:ISSN IssnType=\"Electronic\">1536-9617</ns1:ISSN><ns1:JournalIssue CitedMedium=\"Internet\"><ns1:Volume>58</ns1:Volume><ns1:Issue>4</ns1:Issue><ns1:PubDate><ns1:MedlineDate>Fall 2018</ns1:MedlineDate></ns1:PubDate></ns1:JournalIssue><ns1:Title>International ophthalmology clinics</ns1:Title><ns1:ISOAbbreviation>Int Ophthalmol Clin</ns1:ISOAbbreviation></ns1:Journal><ns1:ArticleTitle>Optic Pathway Gliomas in Neurofibromatosis Type 1: Imaging and Monitoring.</ns1:ArticleTitle><ns1:Pagination><ns1:MedlinePgn>97-112</ns1:MedlinePgn></ns1:Pagination><ns1:ELocationID EIdType=\"doi\" ValidYN=\"Y\">10.1097/IIO.0000000000000241</ns1:ELocationID><ns1:AuthorList CompleteYN=\"Y\"><ns1:Author ValidYN=\"Y\"><ns1:LastName>Beres</ns1:LastName><ns1:ForeName>Shannon J</ns1:ForeName><ns1:Initials>SJ</ns1:Initials></ns1:Author></ns1:AuthorList><ns1:Language>eng</ns1:Language><ns1:PublicationTypeList><ns1:PublicationType UI=\"D016428\">Journal Article</ns1:PublicationType><ns1:PublicationType UI=\"D016454\">Review</ns1:PublicationType></ns1:PublicationTypeList></ns1:Article><ns1:MedlineJournalInfo><ns1:Country>United States</ns1:Country><ns1:MedlineTA>Int Ophthalmol Clin</ns1:MedlineTA><ns1:NlmUniqueID>0374731</ns1:NlmUniqueID><ns1:ISSNLinking>0020-8167</ns1:ISSNLinking></ns1:MedlineJournalInfo><ns1:ChemicalList><ns1:Chemical><ns1:RegistryNumber>0</ns1:RegistryNumber><ns1:NameOfSubstance UI=\"D015415\">Biomarkers</ns1:NameOfSubstance></ns1:Chemical></ns1:ChemicalList><ns1:CitationSubset>IM</ns1:CitationSubset><ns1:MeshHeadingList><ns1:MeshHeading><ns1:DescriptorName UI=\"D015415\" MajorTopicYN=\"N\">Biomarkers</ns1:DescriptorName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D005074\" MajorTopicYN=\"N\">Evoked Potentials, Visual</ns1:DescriptorName><ns1:QualifierName UI=\"Q000502\" MajorTopicYN=\"N\">physiology</ns1:QualifierName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D006801\" MajorTopicYN=\"N\">Humans</ns1:DescriptorName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D008279\" MajorTopicYN=\"N\">Magnetic Resonance Imaging</ns1:DescriptorName><ns1:QualifierName UI=\"Q000379\" MajorTopicYN=\"N\">methods</ns1:QualifierName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D009456\" MajorTopicYN=\"N\">Neurofibromatosis 1</ns1:DescriptorName><ns1:QualifierName UI=\"Q000150\" MajorTopicYN=\"Y\">complications</ns1:QualifierName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D020339\" MajorTopicYN=\"N\">Optic Nerve Glioma</ns1:DescriptorName><ns1:QualifierName UI=\"Q000175\" MajorTopicYN=\"Y\">diagnosis</ns1:QualifierName><ns1:QualifierName UI=\"Q000503\" MajorTopicYN=\"N\">physiopathology</ns1:QualifierName><ns1:QualifierName UI=\"Q000628\" MajorTopicYN=\"N\">therapy</ns1:QualifierName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D061848\" MajorTopicYN=\"N\">Optical Imaging</ns1:DescriptorName><ns1:QualifierName UI=\"Q000379\" MajorTopicYN=\"N\">methods</ns1:QualifierName></ns1:MeshHeading><ns1:MeshHeading><ns1:DescriptorName UI=\"D041623\" MajorTopicYN=\"N\">Tomography, Optical Coherence</ns1:DescriptorName><ns1:QualifierName UI=\"Q000379\" MajorTopicYN=\"N\">methods</ns1:QualifierName></ns1:MeshHeading></ns1:MeshHeadingList></ns1:MedlineCitation><ns1:PubmedData><ns1:History><ns1:PubMedPubDate PubStatus=\"entrez\"><ns1:Year>2018</ns1:Year><ns1:Month>9</ns1:Month><ns1:Day>22</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"pubmed\"><ns1:Year>2018</ns1:Year><ns1:Month>9</ns1:Month><ns1:Day>22</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate><ns1:PubMedPubDate PubStatus=\"medline\"><ns1:Year>2019</ns1:Year><ns1:Month>3</ns1:Month><ns1:Day>15</ns1:Day><ns1:Hour>6</ns1:Hour><ns1:Minute>0</ns1:Minute></ns1:PubMedPubDate></ns1:History><ns1:PublicationStatus>ppublish</ns1:PublicationStatus><ns1:ArticleIdList><ns1:ArticleId IdType=\"pubmed\">30239366</ns1:ArticleId><ns1:ArticleId IdType=\"doi\">10.1097/IIO.0000000000000241</ns1:ArticleId><ns1:ArticleId IdType=\"pii\">00004397-201805840-00006</ns1:ArticleId></ns1:ArticleIdList></ns1:PubmedData></ns1:PubmedArticle>";
	//	System.out.println(pubArticleDate(text));
		System.out.println(abstractText(text));
		//		pmId(text);

	}

	public static ArrayList<String> getArticles(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		ArrayList<String> list=new ArrayList<String>();
		String tag = "ns1:PubmedArticle";
		for(Element e1: xmlDoc.getElementsByTag(tag)) {
			list.add(e1.toString());
		}

		return list;

	}


	//------------------------------------------------------------------------
	public static List<String>    articleIdList(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		List<String> idList=new ArrayList<String>();

		for(Element e:xmlDoc.getElementsByTag("ns1:articleidList"))
			for(Element e1:e.getElementsByTag("ns1:articleid"))
				idList.add(e1.html().toString());

		return idList;
	}


	//-------------------------------------------------------------------
	public static String doi(String text){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		StringBuffer out=new StringBuffer();

		for(Element e:xmlDoc.getElementsByTag("ns1:ElocationID")){
				if(e.attr("EIDType").equals("doi")) {
					return e.html();
				}
		}

		return out.toString();
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
        PubMedDoc doc= new PubMedDoc();
		String year=new String();
		String month=new String();
		String medDate=new String();
		String journal= new String();
		String dateStr= new String();
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
			dateStr=getDate(medDate);
		}else{
			if (year.contains("-")) {
				year = year.substring(0, year.indexOf("-"));
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

			if (month.isEmpty() || !month.contains("/") )
				month = "/01";
			dateStr=year+month+"/01";
		}



		return dateStr;
	}
	public static String getDate(String medDate){
		String month=new String();
		String year= new String();

		if (medDate != null && !medDate.equals("")) {
			String[] seg = new String[3];
			if(medDate.contains(" ")){
				seg = medDate.split(" ");
			}else if (medDate.contains("-")){
				seg=medDate.split("-");

			}else if(medDate.contains("/")){
				seg=medDate.split("/");
			}else{
				int i=Integer.parseInt(medDate);
				if(i!=0){
					year=String.valueOf(i);
				}
				else{
					month= PubMedDoc.MONTH_TABLE.get(medDate);
				}
			}
			if(seg[0]!=null){
				String str=seg[0].toLowerCase().trim();
				try {
					int i = Integer.parseInt(str);
					year=str;
					month="/01";
				}catch(Exception e){

					String[] monSeg=str.split("/");
					if(monSeg[0]!=null) {
						month = PubMedDoc.MONTH_TABLE.get(monSeg[0]);
					}else
						month = PubMedDoc.MONTH_TABLE.get(str);
					if (month != null) {
						month = "/" + month;
						if (seg[1] != null)
							year = seg[1];
					}

				}

			}

		}
		if(month==null || Objects.equals(month, ""))
			month="/01";
		if(Objects.equals(year, "")){
			year="2019";
		}
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
	public static String[] parseBulkXml(String text, boolean pmc){

		Document xmlDoc=Jsoup.parse(text, "", Parser.xmlParser());
		List<String> list=new ArrayList<String>();
		String tag = "ns1:PubmedArticle";
		if(pmc)
			tag = "article";
		for(Element e1: xmlDoc.getElementsByTag(tag))
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


