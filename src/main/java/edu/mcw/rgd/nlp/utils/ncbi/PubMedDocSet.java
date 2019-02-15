/**
 * 
 */
package edu.mcw.rgd.nlp.utils.ncbi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.stream.XMLInputFactory;

import edu.mcw.rgd.common.utils.FileEntry;
import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.nlp.utils.DocumentSet;
import edu.mcw.rgd.nlp.utils.DocumentSetBase;

//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub;
//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubmedArticleSetChoiceE;

/**
 * @author wliu
 * 
 */
public class PubMedDocSet extends DocumentSetBase implements DocumentSet {

	private String docSetXML;
	//private PubmedArticleSetChoiceE[] articleSet;
	private String[] articleSet;
	/**
	 * @return the articleSet
	 */
	public String[] getArticleSet() {
		return articleSet;
	}

	/**
	 * @param articleSet
	 *            the articleSet to set
	 */
	public void setArticleSet(String[] articleSet) {
		this.articleSet = articleSet;
	}

	public final String FILE_EXT = "xml";

	/**
	 * @return the docSetString
	 */
	public String getDocSetXML() {
		return docSetXML;
	}

	public void setDocSetXML(String docSetXML) {
		this.docSetXML = docSetXML;
	}

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int saveDoc(String file_path) throws Exception {
		try {
			File file = new File(getRealFileName(file_path));
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(docSetXML.getBytes("UTF-8"));
			fo.close();
			return docSetXML.getBytes("UTF-8").length;
		} catch (Exception e) {
			logger.error("[Save file error]", e);
			throw e;
		}
	}

	public int loadDoc(String file_path) throws Exception {
		try {
			FileEntry file = new FileEntry(getRealFileName(file_path));
			file.load();
			docSetXML = file.getFileContent();
			return docSetXML.getBytes().length;
		} catch (Exception e) {
			logger.error("[Load file error]", e);
			throw e;
		}
	}

	private final static String docSetXmlHead = "<ns1:eFetchResult xmlns:ns1=\"http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed\"><ns1:PubmedArticleSet>";
	private final static String docSetXmlTail = "</ns1:PubmedArticleSet></ns1:eFetchResult>";
	public int setDocXml(String docXml) throws Exception {
		try {
			docSetXML =  docSetXmlHead + docXml + docSetXmlTail;
			return docSetXML.getBytes().length;
		} catch (Exception e) {
			logger.error("[Setting doc XML error]", e);
			throw e;
		}
	}

	public int parseDocSet() {
		XMLInputFactory f = XMLInputFactory.newInstance();
		//		ReadWrite f=new ReadWrite();
		try {
//			javax.xml.stream.XMLStreamReader r = f
//					.createXMLStreamReader(new ByteArrayInputStream(docSetXML
//							.getBytes("UTF-8")));
//
//			EFetchPubmedServiceStub.EFetchResult rs = EFetchPubmedServiceStub
//					.EFetchResult.Factory.parse(r);
//
//			articleSet = rs.getPubmedArticleSet().getPubmedArticleSetChoice();
			articleSet=PubMedJSoupDoc.parseBulkXml(docSetXML);
			 return articleSet.length;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return 0;
		}
	}

	public int importToDB() {
		PubMedDoc pmd = new PubMedDoc();
		try {
			for (int i = 0; i < articleSet.length; i++) {
				pmd.setPubMedArticle(articleSet[i]);
				// Skip records without PMID or abstract
				if (pmd.getPMID() != null && pmd.getPMID().length() > 0
						&& pmd.getArticleAbstract() != null &&
						pmd.getArticleAbstract().length() > 1) {
					ArticleDAO.deleteRecord(pmd);
					ArticleDAO.insertRecord(pmd);
					logger.info(pmd.getPMID() + " imported");
					// logger.info(pmd.getPMID() + "---" + pmd.getArticleTitle()
					// + "---" + pmd.getArticleAbstract());
				} else {
					logger.info(pmd.getPMID() + "--- skipped");
				}
			}
			return articleSet.length;
		} catch (Exception e) {
			logger.error("Error handling [" + pmd.getPMID() + "]. Passed!!!");
			return -1;
		}
	}

	public int importToHBase() {
		return 0;
	}

	public int getLength() {
		return articleSet.length;
	}

	public String getRealFileName(String file_path) {
		return (file_path + "/" + fileName + "." + FILE_EXT);
	}

	/**
	 * 
	 */
	public PubMedDocSet() {
	}

	public PubMedDoc getDoc(int docNumber) {



		if (articleSet!=null){

			if (docNumber < articleSet.length) {
				PubMedDoc pmd = new PubMedDoc();
				pmd.setPubMedArticle(articleSet[docNumber]);
				return pmd;
			} else return null;
		}
		else return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {



		PubMedDocSet pmds = new PubMedDocSet();
		//		pmds.setFileName("2012_05_09_000");
		pmds.setFileName("2012_04_24_000");
		//		pmds.setFileName("2011_07_16_000");
		try {
			pmds.loadDoc("/data/pubmed/2012");
			int n = pmds.parseDocSet();
			System.out.println("length: " + n);
			for (int i = 0; i < n; i++) {
				PubMedDoc pmd = new PubMedDoc();
				pmd.setPubMedArticle(pmds.getArticleSet()[i]);
				System.out.println(pmd.getPMID() + "---" +
						pmd.getArticleTitle() + "---" + pmd.getArticleAbstract());

				//				if (pmd.getPMID().equals("22147277"))
				//				System.out.println(pmd.getPMID() + "----"
				//						+ pmd.getArticlePubDate() + "----" +pmd.getArticleJournalDate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
