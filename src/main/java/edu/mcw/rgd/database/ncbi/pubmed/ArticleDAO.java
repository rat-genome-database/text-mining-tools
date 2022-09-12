package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.common.utils.HbaseUtils;
import org.apache.hadoop.hbase.client.Result;

import edu.mcw.rgd.nlp.utils.ncbi.PubMedDoc;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDocSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;


public class ArticleDAO {

	public static DateFormat PUB_DATE_DF = new SimpleDateFormat(
			"yyyy/MM/dd");
	public Long pmid;
	public String articleTitle;
	public String articleAbstract;
	public Date articlePubDate;
	public String articleJournalDate;
	public String articleAuthors;
	public String articleCitation;
	public String meshTerms;
	public String keywords;
	public String chemicals;
	public Integer publicationYear;
	public String affiliation;
	public String[] publicationTypes;
	public String pmcId;
	public String doi;
	public String issn;

	public boolean getPreprintArticleFromHResult(Result result) {
		try {
			String jsonStr = HbaseUtils.getField(result, "d", "x");
			ObjectMapper mapper= new ObjectMapper();
			Map article=mapper.readValue(jsonStr, Map.class);
			if (jsonStr == null || jsonStr.length() == 0) return false;
			this.pmid = Long.parseLong(getArticleId(article));
			this.articleTitle = (String) article.get("rel_title");
			this.articleAbstract = article.get("rel_abs").toString();
			String dateStr = article.get("rel_date").toString().replace("-","/");
			Date jDate;
			if (dateStr != null) {
				try {
					jDate = new Date(PUB_DATE_DF.parse(dateStr).getTime());
				} catch (Exception e) {
					System.err.println("Error in [" + pmid + "]");
					System.err.println(dateStr);
					System.err.println("Can't parse [" + dateStr + "] as a date. Using 1800/01/01.");
					jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
				}
			} else {
				jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
			}
			this.articlePubDate = jDate;
			this.publicationYear = jDate.getYear() + 1900;
			this.articleAuthors = getAuthors(article);
			this.publicationTypes=new String[1];
			this.publicationTypes[0]="Preprint "+ article.get("rel_site");
			if(article.get("rel_doi")!=null)
				this.doi =article.get("rel_doi").toString();
			return true;
		} catch (Exception e) {

			System.err.println("Error in [" + pmid + "]");
			System.err.println("This is not a fatal error!");

			e.printStackTrace();
			return false;
		}
	}
	public String getAuthors(Map article){
		ArrayList<Object> authors= (ArrayList<Object>) article.get("rel_authors");
		StringBuilder sb=new StringBuilder();
		for(Object o:authors){
			Map<String, String> map= (Map<String, String>) o;
			sb.append(map.get("author_name")).append(";");

		}
		return sb.toString();
	}
	public static String getArticleId(Map article)  {

		String articleId=new String();
		String doi= article.get("rel_link").toString();
		int index= doi.lastIndexOf("/")+1;
		articleId=doi.substring(index).replace("\"","");
		if(articleId.contains(".")){
			return articleId.substring(articleId.lastIndexOf(".")+1);
		}
		return articleId;
	}
	public boolean getArticleFromHResult(Result result) {
		try {
			   PubMedDocSet pmds = new PubMedDocSet();
			   String xmlStr = HbaseUtils.getField(result, "d", "x");
               if (xmlStr == null || xmlStr.length() == 0) return false;
			   pmds.setDocXml(xmlStr);
			   pmds.parseDocSet();
			   PubMedDoc pmd = pmds.getDoc(0);
			    this.pmid = Long.parseLong(pmd.getPMID());
				this.articleTitle = pmd.getArticleTitle();
				this.articleAbstract = pmd.getArticleAbstract();
				String dateStr = pmd.getArticlePubDate();
				if (dateStr.equalsIgnoreCase("/01/01"))
				{
					dateStr="2015/01/01";
				}
				Date jDate;
			try {
                    jDate = new Date(PUB_DATE_DF.parse(dateStr).getTime());
                } catch (Exception e) {
                    System.err.println("Error in [" + pmid + "]");
                    jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
                }
				this.articlePubDate = jDate;
				this.publicationYear = jDate.getYear() + 1900;
				this.articleJournalDate = pmd.getArticleJournalDate();
				this.articleAuthors = pmd.getArticleAuthors();
				this.affiliation = pmd.getAffiliation(); 
				this.articleCitation = pmd.getArticleJournal() + ", " + (this.articleJournalDate != null ?
						this.articleJournalDate + ", " : "") + 
						(pmd.getArticleJournalVolume() != null ? pmd.getArticleJournalVolume() + "(" +
				pmd.getArticleJournalIssue() + "): " + pmd.getArticleJournalPage() : "");
				this.meshTerms = pmd.getMeshTerms();
				this.keywords = pmd.getKeywords();
				this.chemicals = pmd.getChemicals();
				this.publicationTypes = pmd.getPublicationTypes();
				this.pmcId = pmd.getPmcId();
				this.doi = pmd.getDoi();
				this.issn = pmd.getIssn();
			   return true;
		} catch (Exception e) {
			System.err.println("Error in [" + pmid + "]");
			System.err.println("This is not a fatal error!");
			e.printStackTrace();
			return false;
		}
	}
	public boolean getAgrArticleFromHResult(Result result) {
		try {
			String jsonStr = HbaseUtils.getField(result, "d", "x");
			ObjectMapper mapper= new ObjectMapper();
			Map article=mapper.readValue(jsonStr, Map.class);
			if (jsonStr == null || jsonStr.length() == 0) return false;
			this.pmid = Long.parseLong(getAgrArticleId(article));
			this.articleTitle = (String) article.get("title");
			this.articleAbstract = (String) article.get("abstract");
			String dateStr = ((String) article.get("issueDate"));
			Date jDate;
			if (dateStr != null) {
				dateStr = dateStr.replace("-","/");
				try {
					jDate = new Date(PUB_DATE_DF.parse(dateStr).getTime());
				} catch (Exception e) {
					System.err.println("Error in [" + pmid + "]");
					System.err.println(dateStr);
					jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
				}
			} else {
				jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
			}
			this.articlePubDate = jDate;
			this.publicationYear = jDate.getYear() + 1900;
			this.articleAuthors = getAgrAuthors(article);
			this.publicationTypes=getPublicationTypes(article);
			this.meshTerms = getMeshTerms(article);
			this.keywords = getKeywords(article);
			return true;
		} catch (Exception e) {
			System.err.println("Error in [" + pmid + "]");
			System.err.println("This is not a fatal error!");
			e.printStackTrace();
			return false;
		}
	}
	public boolean getPmcArticleFromHResult(Result result) {
		try {
			String xmlStr = HbaseUtils.getField(result, "d", "x");
			if (xmlStr == null || xmlStr.length() == 0) return false;
			Document xmlDoc= Jsoup.parse(xmlStr, "", Parser.xmlParser());

			this.pmid = Long.parseLong(PmcArticleDAO.getPMID(xmlDoc));
			this.articleTitle = PmcArticleDAO.getArticleTitle(xmlDoc);
			this.articleAbstract = PmcArticleDAO.getArticleAbstract(xmlDoc);
			String dateStr = PmcArticleDAO.getArticlePubDate(xmlDoc);
			if (dateStr.equalsIgnoreCase("/01/01"))
			{
				dateStr="2015/01/01";
			}
			Date jDate;
			try {
				jDate = new Date(PUB_DATE_DF.parse(dateStr).getTime());
			} catch (Exception e) {
				System.err.println("Error in [" + pmid + "]");
				jDate = new Date(PUB_DATE_DF.parse("1800/01/01").getTime());
			}
			this.articlePubDate = jDate;
			this.publicationYear = jDate.getYear() + 1900;
			this.articleAuthors = PmcArticleDAO.getArticleAuthors(xmlDoc);
			this.affiliation = PmcArticleDAO.getAffiliation(xmlDoc);
			this.pmcId = PmcArticleDAO.getPmcId(xmlDoc);
			this.doi = PmcArticleDAO.getDoi(xmlDoc);
			return true;
		} catch (Exception e) {
			System.err.println("Error in [" + pmid + "]");
			System.err.println("This is not a fatal error!");
			e.printStackTrace();
			return false;
		}
	}
	public static String getAgrArticleId(Map article)  {
		String primaryId= (String)article.get("primaryId");
		String articleId = primaryId.substring(primaryId.indexOf(":")+1,primaryId.length()-1);
		return articleId;
	}
	public String getAgrAuthors(Map article){
		ArrayList<Object> authors= (ArrayList<Object>) article.get("authors");
		StringBuilder sb=new StringBuilder();
		for(Object o:authors){
			Map<String, String> map= (Map<String, String>) o;
			sb.append(map.get("name")).append(";");

		}
		return sb.toString();
	}
	public String[] getPublicationTypes(Map article) {
		try {
			List<String> ptlt = (List<String>)article.get("pubMedType");
			String[] arr=new String[ptlt.size()];
			for(int i=0;i<arr.length;i++)
				arr[i]=ptlt.get(i);

			return arr;
		} catch (Exception e) {
			return null;
		}
	}
	public String getKeywords(Map article) {
		try {
			StringBuilder keyword_str = new StringBuilder("");
			List<String> keyword_lists = (List<String>)article.get("keywords");
			for (String keyword : keyword_lists) {
				if (keyword != null) {
					keyword_str.append(keyword).append("; ");
				}
			}
			return keyword_str.toString();
		} catch (Exception e) {
			return null;
		}
	}
	public String getMeshTerms(Map article) {
		try {
			List<Map> terms = (List<Map>)article.get("meshTerms") ;
			List<String> termsList = new ArrayList<>();
			for(Map o:terms){
				termsList.add(o.get("meshHeadingTerm").toString());
			}
			StringBuilder meshterm_str = new StringBuilder("");
			if (termsList != null) {
				for (String term: termsList) {
					meshterm_str.append(term).append("; ");
				}
			}
			return meshterm_str.toString();
		} catch (Exception e) {
			return null;
		}
	}
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("Specify at lease one PMID as argument");
			return;
		}
	}
}
