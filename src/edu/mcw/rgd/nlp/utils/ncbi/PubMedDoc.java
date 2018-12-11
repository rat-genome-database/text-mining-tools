/**
 * Changed by oghiasvand@mcw.edu
 * changed methods are marked by ****************
 */
package edu.mcw.rgd.nlp.utils.ncbi;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jcraft.jsch.Logger;

import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.nlp.utils.Document;
import edu.mcw.rgd.nlp.utils.DocumentBase;

import edu.mcw.rgd.nlp.utils.ncbi.PubMedJSoupDoc;
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.ArticleIdType; changed=> getPMCid() getDoi()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorListType;  nothing happened
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AbstractTextType; Changed => getArticleAbstract()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.ArticleDateType; nothing happened
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorType; changed=>getArticleAuthors()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.ChemicalListType; changed=>getChemicals()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.IdType_type0; nothing changed
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.JournalIssueType; nothing changed
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.KeywordListType; changed=>getKeywords()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.KeywordType; nothing changed
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.MeshHeadingType; changed=>getMeshTerms()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubDateType; changed=>getArticlePubDate(), getArticleJournalDate()
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubMedPubDateType; changed=>getPmcId() 
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PublicationTypeListType; changed=>getPublicationTypes()

//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubmedArticleSetChoiceE;

/**
 * @author wliu
 * 
 */
public class PubMedDoc extends DocumentBase implements Document {

	private String pubMedArticle;
	static public HashMap<String, String> MONTH_TABLE = null;

	public String getPubMedArticle() {
		return pubMedArticle;
	}
//***********************************************************
	public void setPubMedArticle(String pubMedArticle) {
		this.pubMedArticle = pubMedArticle;
	}

	public String getPMID() {
		try {
			return PubMedJSoupDoc.pmId(pubMedArticle);
		} catch (Exception e) {
			return "";
		}
	}
//****************************************************************
	public String getArticleTitle() {
		try {
			return PubMedJSoupDoc.articleTitle(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting Title from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return "";
		}
	}
	//**************************************************************************
	public String getArticleAbstract() {
		return PubMedJSoupDoc.abstractText(pubMedArticle.toString());
	}
	//********************************************************************************
	public String getArticlePubDate() {
			return PubMedJSoupDoc.pubArticleDate(pubMedArticle.toString());
	}
	//********************************************************************************
	public String validateDate(String extractedDate, String dateStr) {
		try {
			Date testDate = new Date(ArticleDAO.PUB_DATE_DF.parse(extractedDate).getTime());
		} catch (Exception e) {
			Pattern p4 = Pattern.compile("(\\d{4}).*");
			Matcher m4 = p4.matcher(dateStr);
			if (m4.find()) {
				String year = m4.group(1).trim();
				return year + "/01/01"; // returns if date is: yyyy-xxxxxxx
			}
		}
		return extractedDate;
	}
//***********************************************************************
	public String getArticleJournalDate() {
		return PubMedJSoupDoc.pubJournalDate(pubMedArticle.toString());
	}
//***********************************************************************
	public String getArticleJournal() {
		try {
			return PubMedJSoupDoc.journalTitle(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting ArticleJournal from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//			.getPMID().toString());
			return null;
		}
	}

	public String getArticleJournalVolume() {
		try {
			return PubMedJSoupDoc.journalVolume(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting JournalVolume from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return null;
		}
	}

	public String getArticleJournalIssue() {
		try {
			return PubMedJSoupDoc.journalIssue(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting JournalIssue from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return null;
		}
	}
//	****************************************************************
	public String getArticleJournalPage() {
		try {
			return PubMedJSoupDoc.journalPage(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting Journal from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return null;
		}
	}
//	****************************************************************
	public String getAffiliation() {
		try {
			return PubMedJSoupDoc.affiliation(pubMedArticle);
		} catch (Exception e) {
			//System.err.println("Error in getting Journal from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return null;
		}
	}
	//*****************************************************************
	public String[] getArticleAuthorList() {
		try {
			String authors_str = "";
			List<String> authors = PubMedJSoupDoc.authorList(pubMedArticle.toString());
			String[] authorsArr=new String[authors.size()];
			
			for (int i=0;i<authors.size();i++) 
				authorsArr[i]=authors.get(i);
			
			return authorsArr;

		} catch (Exception e) {
			//System.err.println("Error in getting AuthorList from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//		.getPMID().toString());
			return null;
		}
	}
//****************************************************************
	public String getArticleAuthors() {

		String authors_str = "";
		List<String> authors = PubMedJSoupDoc.authorList(pubMedArticle.toString());

		for (String name: authors) {
			authors_str += name;
			authors_str += ";  ";
		}
		return authors_str;

	}
	//****************************************************************************************

//	public PublicationTypeListType getPublicationTypeList() {
//		try {
//			return pubMedArticle.getPubmedArticle().getMedlineCitation()
//					.getArticle().getPublicationTypeList();
//		} catch (Exception e) {
//			//System.err.println("Error in getting AuthorList from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
//			//		.getPMID().toString());
//			return null;
//		}
//	}

	public String[] getPublicationTypes() {
		try {
			List<String> ptlt = PubMedJSoupDoc.pubTypeList(pubMedArticle.toString());
			String[] arr=new String[ptlt.size()];
			for(int i=0;i<arr.length;i++)
				arr[i]=ptlt.get(i);
			
			return arr;
		} catch (Exception e) {
			//System.err.println("Error in getting Authors from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//				.getPMID().toString());
			return null;
		}
	}
//***************************************************************************************
	public String getMeshTerms() {
		try {
			List<String> termsList = PubMedJSoupDoc.meshHeadingList(pubMedArticle.toString());
			StringBuilder meshterm_str = new StringBuilder("");
			if (termsList != null) {
				for (String term: termsList) {
					meshterm_str.append(term).append("; ");
				}
			}
			return meshterm_str.toString();
		} catch (Exception e) {
			//System.err.println("Error in getting MeshTerms from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//					.getPMID().toString());
			return null;
		}
	}
//**************************************************************************************
	public String getKeywords() {
		try {
			StringBuilder keyword_str = new StringBuilder("");
			List<String> keyword_lists = PubMedJSoupDoc.keywordList(pubMedArticle.toString());
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
	//***************************************************************************
	public String getChemicals() {
		try {
			StringBuilder chemicals_str = new StringBuilder("");
			List<String> chemical_list = PubMedJSoupDoc.chemicalList(pubMedArticle.toString());

			if (chemical_list != null) {
				for (String s:chemical_list) {
					chemicals_str.append(s).append("; ");
				}
			}
			return chemicals_str.toString();
		} catch (Exception e) {
			return null;
		}
	}
	//***************************************************************************
	public String getPmcId() {
		try {
//			PubMedPubDateType[] pubDates = pubMedArticle.getPubmedArticle().getPubmedData().getHistory().getPubMedPubDate();
//			// First check if PubStatus has "pmc-release". Skip the PMC_ID if there is
//			for (PubMedPubDateType date : pubDates) {
//				if (date.getPubStatus().getValue().equals("pmc-release")) {
//					return null;
//				}
//			}
			List<String> ids = PubMedJSoupDoc.articleIdList(pubMedArticle);
			for (String id : ids) {
				if (id.contains("PMC")) {
					return id;
				}
			};
			return null;
		} catch (Exception e) {
			//System.err.println("Error in getting Authors from PMID:" + pubMedArticle.getPubmedArticle().getMedlineCitation()
			//				.getPMID().toString());
			return null;
		}
	}
	//*************************************************************************
	public String getDoi() {
		try {
			//			ArticleIdType[] ids = pubMedArticle.getPubmedArticle().getPubmedData().getArticleIdList().getArticleId();
			List<String> ids = PubMedJSoupDoc.articleIdList(pubMedArticle);
			for (String id : ids) {
				if (id.contains(".")) {
					return id;
				}
			};
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	//***************************************************************************
	public String getIssn() {
		try {
			return PubMedJSoupDoc.issn(pubMedArticle);
		} catch (Exception e) {
			return null;
		}
	}
	//***************************************************************************
	/**
	 * 
	 */
	public PubMedDoc() {
		if (MONTH_TABLE == null) {
			MONTH_TABLE = new HashMap<String, String>();
			MONTH_TABLE.put("1", "01");
			MONTH_TABLE.put("2", "02");
			MONTH_TABLE.put("3", "03");
			MONTH_TABLE.put("4", "04");
			MONTH_TABLE.put("5", "05");
			MONTH_TABLE.put("6", "06");
			MONTH_TABLE.put("7", "07");
			MONTH_TABLE.put("8", "08");
			MONTH_TABLE.put("9", "09");
			MONTH_TABLE.put("10", "10");
			MONTH_TABLE.put("11", "11");
			MONTH_TABLE.put("12", "12");

			MONTH_TABLE.put("01", "01");
			MONTH_TABLE.put("02", "02");
			MONTH_TABLE.put("03", "03");
			MONTH_TABLE.put("04", "04");
			MONTH_TABLE.put("05", "05");
			MONTH_TABLE.put("06", "06");
			MONTH_TABLE.put("07", "07");
			MONTH_TABLE.put("08", "08");
			MONTH_TABLE.put("09", "09");
			MONTH_TABLE.put("10", "10");
			MONTH_TABLE.put("11", "11");
			MONTH_TABLE.put("12", "12");

			MONTH_TABLE.put(("Jan").toLowerCase(), "01");
			MONTH_TABLE.put(("Feb").toLowerCase(), "02");
			MONTH_TABLE.put(("Mar").toLowerCase(), "03");
			MONTH_TABLE.put(("Apr").toLowerCase(), "04");
			MONTH_TABLE.put(("May").toLowerCase(), "05");
			MONTH_TABLE.put(("Jun").toLowerCase(), "06");
			MONTH_TABLE.put(("Jul").toLowerCase(), "07");
			MONTH_TABLE.put(("Aug").toLowerCase(), "08");
			MONTH_TABLE.put(("Sep").toLowerCase(), "09");
			MONTH_TABLE.put(("Oct").toLowerCase(), "10");
			MONTH_TABLE.put(("Nov").toLowerCase(), "11");
			MONTH_TABLE.put(("Dec").toLowerCase(), "12");

			MONTH_TABLE.put(("January").toLowerCase(), "01");
			MONTH_TABLE.put(("February").toLowerCase(), "02");
			MONTH_TABLE.put(("March").toLowerCase(), "03");
			MONTH_TABLE.put(("April").toLowerCase(), "04");
			MONTH_TABLE.put(("May").toLowerCase(), "05");
			MONTH_TABLE.put(("June").toLowerCase(), "06");
			MONTH_TABLE.put(("July").toLowerCase(), "07");
			MONTH_TABLE.put(("August").toLowerCase(), "08");
			MONTH_TABLE.put(("September").toLowerCase(), "09");
			MONTH_TABLE.put(("October").toLowerCase(), "10");
			MONTH_TABLE.put(("November").toLowerCase(), "11");
			MONTH_TABLE.put(("December").toLowerCase(), "12");

			MONTH_TABLE.put(("Spring").toLowerCase(), "03");
			MONTH_TABLE.put(("Summer").toLowerCase(), "06");
			MONTH_TABLE.put(("Fall").toLowerCase(), "09");
			MONTH_TABLE.put(("Autumn").toLowerCase(), "09");
			MONTH_TABLE.put(("Winter").toLowerCase(), "12");
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
