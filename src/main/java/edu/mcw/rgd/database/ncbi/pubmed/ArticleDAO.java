package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.Date;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import org.apache.hadoop.hbase.client.Result;

import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.common.utils.DAOBase;
import edu.mcw.rgd.common.utils.HBaseConnection;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedCouchDoc;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDoc;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedDocSet;



public class ArticleDAO extends DAOBase {

	final public static String tableName = "articles";
	public static DateFormat PUB_DATE_DF = new SimpleDateFormat(
			"yyyy/MM/dd");


	public static void insertRecord(String pmid, String article_title,
			String article_abstract) {
//		String escaped_text = DocDBConnection.escapeSQL(article_abstract);
		try {
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("insert into " + tableName + " values ('"
					+ Long.parseLong(pmid) + "','"
					+ DocDBConnection.escapeSQL(article_title) + "','"
					+ DocDBConnection.escapeSQL(article_abstract) + "');"));
		} catch (Exception e) {
			logger.error("Error inserting " + pmid, e);
			return;
		}
	}

	public static void insertRecord(String pmid, String article_title,
			String article_abstract, String article_date) {
		try {
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("insert into " + tableName + " values ('"
					+ Long.parseLong(pmid) + "','"
					+ DocDBConnection.escapeSQL(article_title) + "','"
					+ DocDBConnection.escapeSQL(article_abstract) + "','"
					+ article_date + "');"));
		} catch (Exception e) {
			logger.error("Error inserting " + pmid, e);
			return;
		}
	}

	public static void deleteRecord(PubMedDoc pmd) {
		try {
			deleteAuthors(pmd);
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("delete from " + tableName + " where pmid="
					+ DocDBConnection.escapeSQL(pmd.getPMID()) + ";"));
		} catch (Exception e) {
			logger.error("Error inserting " + pmd.getPMID(), e);
			return;
		}
	}
	
	public static void insertRecord(PubMedDoc pmd) {
//		String date_str = pmd.getArticleDate();

//		if (date_str.length() == 0) {
//			insertRecord(pmd.getPMID(), pmd.getArticleTitle(),
//					pmd.getArticleAbstract());
//		} else {
//			insertRecord(pmd.getPMID(), pmd.getArticleTitle(),
//					pmd.getArticleAbstract(), date_str);
//		}
		String sql_string = "insert into " + tableName + " values ('"
				+ DocDBConnection.escapeSQL(pmd.getPMID()) + "','"
				+ DocDBConnection.escapeSQL(pmd.getArticleTitle()) + "','"
				+ DocDBConnection.escapeSQL(pmd.getArticleAbstract()) + "',"
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticlePubDate()) + ","
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticleJournal()) + ","
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticleJournalVolume()) + ","
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticleJournalIssue()) + ","
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticleJournalPage()) + ","
				+ BasicUtils.EscapeSQLStringValue(pmd.getArticleJournalDate()) +
				");";
		
		try {
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery(sql_string));
			insertAuthors(pmd);
		} catch (Exception e) {
			logger.error("Error inserting " + pmd.getPMID(), e);
			return;
		}
	}

	public static void insertAuthors(PubMedDoc pmd) {
		try {
			String[] authors = pmd.getArticleAuthorList();
			for (int i = 0; i < authors.length; i++) {
				AuthorDAO.insert(pmd.getPMID(), String.format("%d", i) , authors[i]);
			}
		} catch (Exception e) {
			logger.error("Error inserting authros " + pmd.getPMID(), e);
			return;
		}
	}

	public static void deleteAuthors(PubMedDoc pmd) {
		try {
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("delete from " + AuthorDAO.tableName + " where " +
					"pmid=" + DocDBConnection.escapeSQL(pmd.getPMID()) + ";"));
		} catch (Exception e) {
			logger.error("Error deleting " + pmd.getPMID(), e);
			return;
		}
	}

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

	public boolean getArticle(String pmid) {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ tableName + " where pmid=" + Long.parseLong(pmid));
		try {
			if (!rs.first()) {
				DocDBConnection.closeRsStatement(rs);
				return false;
			}
			this.pmid = Long.parseLong(pmid);
			this.articleTitle = new String(rs.getBytes("TITLE"), "UTF-8");
			this.articleAbstract = new String(rs.getBytes("ABSTRACT"), "UTF-8");
			this.articlePubDate = rs.getDate("PUB_DATE");
			this.articleJournalDate = rs.getString("JOURNAL_DATE");
			this.articleAuthors = AuthorDAO.get(pmid);
			this.articleCitation = rs.getString("JOURNAL") + ", " + (this.articleJournalDate != null ?
					this.articleJournalDate + ", " : "") + (rs.getString("VOLUME") != null ? rs.getString("VOLUME") + "(" +
			rs.getString("ISSUE") + "): " + rs.getString("PAGE") : "");
			DocDBConnection.closeRsStatement(rs);
//			meshTerms = PubmedCouchDAO.getMeshTerms(pmid);
			return true;
		} catch (Exception e) {
			logger.error("Error in get article from DB [pmid:" + pmid + "] "
					+ BasicUtils.strExceptionStackTrace(e));
			return false;
		}
	}

	public boolean getArticleFromCouch(String pmid) {
	    String jsonArticle = PubmedCouchDAO.getDoc(pmid);
	    PubMedCouchDoc doc = new PubMedCouchDoc();
	    doc.setPubMedArticle(jsonArticle);
        
		try {
			this.pmid = Long.parseLong(pmid);
			this.articleTitle = doc.getArticleTitle();
			this.articleAbstract = doc.getArticleAbstract();
			this.articlePubDate = new Date(PUB_DATE_DF.parse(doc.getArticlePubDate()).getTime());
			this.articleJournalDate = doc.getArticleJournalDate();
			this.articleAuthors = doc.getArticleAuthors();
			this.articleCitation = doc.getArticleJournal() + ", " + (this.articleJournalDate != null ?
					this.articleJournalDate + ", " : "") + 
					(doc.getArticleJournalVolume() != null ? doc.getArticleJournalVolume() + "(" +
			doc.getArticleJournalIssue() + "): " + doc.getArticleJournalPage() : "");
			this.meshTerms = doc.getMeshTerms();
			return true;
		} catch (Exception e) {
			logger.error("Error in get article from DB [pmid:" + pmid + "] "
					+ BasicUtils.strExceptionStackTrace(e));
			return false;
		}
	}
	
	public boolean getArticleFromHResult(Result result) {
		try {
			   PubMedDocSet pmds = new PubMedDocSet();
			   String xmlStr = HBaseConnection.getField(result, "d", "x");

	//		logger.info("XMLSTR: " + xmlStr);
			   if (xmlStr == null || xmlStr.length() == 0) return false;
			   pmds.setDocXml(xmlStr);
			   pmds.parseDocSet();
			   PubMedDoc pmd = pmds.getDoc(0);

			   
			   	
//			   	this.pmid = Long.valueOf(pmd.getPMID());
			   	
			   this.pmid = Long.parseLong(pmd.getPMID());
			   
			   
			   this.articleTitle = pmd.getArticleTitle();
				this.articleAbstract = pmd.getArticleAbstract();
				String dateStr = pmd.getArticlePubDate();
		//		System.out.println("PubMED ARTICLE DATE STR: "+ dateStr);
		if (dateStr.equalsIgnoreCase("/01/01"))
				{
					dateStr="2015/01/01";
				}

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
		//	System.out.println("JDATE: "+ jDate);
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

	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("Specify at lease one PMID as argument");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			ArticleDAO ar = new ArticleDAO();
			ar.getArticleFromCouch(args[i]);
//			System.out.println("PMID: " + ar.pmid);
//			System.out.println("Journal date: " + ar.articleJournalDate);
//			System.out.println("Title: " + ar.articleTitle);
//			System.out.println("Abstract: " + ar.articleAbstract);
//			System.out.println("MeSH terms: " + ar.meshTerms);
		}
	}
}
