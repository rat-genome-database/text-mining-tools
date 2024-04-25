/**
 * Changed by oghiasvand@mcw.edu
 * changed methods are marked by ****************
 */
package edu.mcw.rgd.nlp.utils.ncbi;

import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.nlp.utils.Document;
import edu.mcw.rgd.nlp.utils.DocumentBase;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wliu
 * 
 */
public class PubMedDoc extends DocumentBase implements Document {

	private String pubMedArticle;
	static public Map<String, String> MONTH_TABLE;

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
	static {
		if (MONTH_TABLE == null) {
			Map<String, String>	TABLE = new HashMap<String, String>();
			TABLE.put("1", "01");
			TABLE.put("2", "02");
			TABLE.put("3", "03");
			TABLE.put("4", "04");
			TABLE.put("5", "05");
			TABLE.put("6", "06");
			TABLE.put("7", "07");
			TABLE.put("8", "08");
			TABLE.put("9", "09");
			TABLE.put("10", "10");
			TABLE.put("11", "11");
			TABLE.put("12", "12");

			TABLE.put("01", "01");
			TABLE.put("02", "02");
			TABLE.put("03", "03");
			TABLE.put("04", "04");
			TABLE.put("05", "05");
			TABLE.put("06", "06");
			TABLE.put("07", "07");
			TABLE.put("08", "08");
			TABLE.put("09", "09");
			TABLE.put("10", "10");
			TABLE.put("11", "11");
			TABLE.put("12", "12");

			TABLE.put(("Jan").toLowerCase(), "01");
			TABLE.put(("Feb").toLowerCase(), "02");
			TABLE.put(("Mar").toLowerCase(), "03");
			TABLE.put(("Apr").toLowerCase(), "04");
			TABLE.put(("May").toLowerCase(), "05");
			TABLE.put(("Jun").toLowerCase(), "06");
			TABLE.put(("Jul").toLowerCase(), "07");
			TABLE.put(("Aug").toLowerCase(), "08");
			TABLE.put(("Sep").toLowerCase(), "09");
			TABLE.put(("Oct").toLowerCase(), "10");
			TABLE.put(("Nov").toLowerCase(), "11");
			TABLE.put(("Dec").toLowerCase(), "12");

			TABLE.put(("January").toLowerCase(), "01");
			TABLE.put(("February").toLowerCase(), "02");
			TABLE.put(("March").toLowerCase(), "03");
			TABLE.put(("April").toLowerCase(), "04");
			TABLE.put(("May").toLowerCase(), "05");
			TABLE.put(("June").toLowerCase(), "06");
			TABLE.put(("July").toLowerCase(), "07");
			TABLE.put(("August").toLowerCase(), "08");
			TABLE.put(("September").toLowerCase(), "09");
			TABLE.put(("October").toLowerCase(), "10");
			TABLE.put(("November").toLowerCase(), "11");
			TABLE.put(("December").toLowerCase(), "12");

			TABLE.put(("Spring").toLowerCase(), "03");
			TABLE.put(("Summer").toLowerCase(), "06");
			TABLE.put(("Fall").toLowerCase(), "09");
			TABLE.put(("Autumn").toLowerCase(), "09");
			TABLE.put(("Winter").toLowerCase(), "12");
			MONTH_TABLE= Collections.unmodifiableMap(TABLE);
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
