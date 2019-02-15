/**
 * 
 */
package edu.mcw.rgd.nlp.utils.ncbi;

import org.apache.noggit.JSONUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.common.utils.DateUtils;
import edu.mcw.rgd.common.utils.JSONUtils;
import edu.mcw.rgd.nlp.utils.Document;
import edu.mcw.rgd.nlp.utils.DocumentBase;

/**
 * @author wliu
 * 
 */
public class PubMedCouchDoc extends DocumentBase implements Document {

	private JSONObject pubMedArticle;

	public void setPubMedArticle(String pubMedArticle) {
		try {
			Object jsonObj = JSONSerializer.toJSON(pubMedArticle);
			this.pubMedArticle = (jsonObj instanceof JSONNull) ? null : (JSONObject) jsonObj; 
		} catch (Exception e) {
			this.pubMedArticle = null;
		}
	}

	public String getPMID() {
		try {
			return pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("PMID").getString("$");
		} catch (Exception e) {
			return "";
		}
	}

	public String getArticleTitle() {
		try {
			return BasicUtils.getUTFstr(pubMedArticle
					.getJSONObject("MedlineCitation").getJSONObject("Article")
					.getString("ArticleTitle"));
		} catch (Exception e) {
			return "";
		}
	}

	public String getArticleAbstract() {
		String return_str = "";
		try {

			Object obj = pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("Abstract").get("AbstractText");
			return_str = "";
			if (obj instanceof JSONArray) {
				JSONArray abstractArray = (JSONArray) obj;
				for (int i = 0; i < abstractArray.size(); i++) {
					String abstract_label = ((JSONObject) abstractArray.get(i))
							.getString("Label");
					if (abstract_label != null && abstract_label.length() > 0) {
						return_str += abstract_label;
						return_str += "      ";
					}
					return_str += ((JSONObject) abstractArray.get(i))
							.getString("$");
					return_str += "\r\n";
				}
			} else {
				return_str = (String) obj;
			}

			return return_str;
		} catch (Exception e) {
			return "";
		}
	}

	public String getArticlePubDate() {
		try {
			JSONObject epub_date = pubMedArticle
					.getJSONObject("MedlineCitation").getJSONObject("Article")
					.getJSONObject("Journal").getJSONObject("JournalIssue")
					.getJSONObject("PubDate");
			Object medlineData = epub_date.get("MedlineDate");
			if (medlineData != null) {
				return DateUtils.getEndData((String) medlineData);
			}
			if (epub_date.getString("Year") != null) {
				return DateUtils.getDateString(
						(String) epub_date.getString("Year"),
						(String) epub_date.get("Month"),
						(String) epub_date.get("Day"),
						(String) epub_date.get("Season"));
			}
			return null;
		} catch (Exception e) {
			System.out.println("pmid: " + this.getPMID());
			e.printStackTrace();
			return null;
		}
	}

	public String getArticleJournalDate() {
		try {
			JSONObject epub_date = pubMedArticle
					.getJSONObject("MedlineCitation").getJSONObject("Article")
					.getJSONObject("Journal").getJSONObject("JournalIssue")
					.getJSONObject("PubDate");
			Object medlineData = epub_date.get("MedlineDate");
			if (medlineData != null) {
				return (String) medlineData;
			}
			if (epub_date.getString("Year") != null) {
				return DateUtils.getJournalDate(
						(String) epub_date.getString("Year"),
						(String) epub_date.get("Month"),
						(String) epub_date.get("Day"),
						(String) epub_date.get("Season"));
			}
			return null;
		} catch (Exception e) {
			System.out.println("PMID: " + this.getPMID());
			e.printStackTrace();
			return null;
		}
	}

	public String getArticleJournal() {
		try {
			return (String) pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("Journal")
					.get("Title");
		} catch (Exception e) {
			return null;
		}
	}

	public String getArticleJournalVolume() {
		try {
			return pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("Journal")
					.getJSONObject("JournalIssue").getString("Volume");
		} catch (Exception e) {
			return null;
		}
	}

	public String getArticleJournalIssue() {
		try {
			return pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("Journal")
					.getJSONObject("JournalIssue").getString("Issue");
		} catch (Exception e) {
			return null;
		}
	}

	public String getArticleJournalPage() {
		try {
			return pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("Pagination")
					.getString("MedlinePgn");
		} catch (Exception e) {
			return null;
		}
	}

	public JSONArray getArticleAuthorList() {
		try {
			return JSONUtils.toJSONArray(pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("Article").getJSONObject("AuthorList")
					.get("Author"));
		} catch (Exception e) {
			return null;
		}
	}

	public JSONArray getMeshHeadingList() {
		try {
			return JSONUtils.toJSONArray(pubMedArticle.getJSONObject("MedlineCitation")
					.getJSONObject("MeshHeadingList").get("MeshHeading"));
		} catch (Exception e) {
			return null;
		}
	}

	public String getArticleAuthors() {
		try {
			String authors_str = "";
			JSONArray authors = getArticleAuthorList();
			for (int i = 0; i < authors.size(); i++) {
				JSONObject authorObj = authors.getJSONObject(i);
				authors_str += authorObj.getString("LastName");
				authors_str += ", ";
				authors_str += authorObj.getString("ForeName");
				authors_str += ";  ";
			}
			return authors_str;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getMeshTerms() {
		try {
			String meshterm_str = "";
			JSONArray terms = getMeshHeadingList();
			for (int i = 0; i < terms.size(); i++) {
				JSONObject authorObj = terms.getJSONObject(i);
				meshterm_str += authorObj.getJSONObject("DescriptorName").getString("$");
				meshterm_str += "; ";
			}
			return meshterm_str;
		} catch (Exception e) {
			return null;
		}
	}

	// public String getArticleJournalPage() {

	// try {
	// return
	// pubMedArticle.getPubmedArticle().getMedlineCitation().getArticle().getArticleTypeChoice_type0().getArticleTypeSequence_type0().getPagination().getMedlinePgn();
	// } catch (Exception e) {
	// return "";
	// }
	// }

	/**
	 * 
	 */
	public PubMedCouchDoc() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
