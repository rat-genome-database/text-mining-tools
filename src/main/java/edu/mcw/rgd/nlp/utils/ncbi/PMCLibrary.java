/**
 * 
 */
package edu.mcw.rgd.nlp.utils.ncbi;

import edu.mcw.rgd.common.utils.FileList;
import edu.mcw.rgd.common.utils.SortedCountMap;
import edu.mcw.rgd.database.ncbi.pubmed.AnnotationDAO;
import edu.mcw.rgd.database.ncbi.pubmed.AnnotationRecord;
import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.database.ncbi.pubmed.PmcArticleDAO;
import edu.mcw.rgd.nlp.classifier.ArticleOrganismClassifier;
import edu.mcw.rgd.nlp.datamodel.Ontology;
import edu.mcw.rgd.nlp.datamodel.SolrOntologyEntry;
import edu.mcw.rgd.nlp.utils.Library;
import edu.mcw.rgd.nlp.utils.LibraryBase;
import edu.mcw.rgd.nlp.utils.gate.AnnieAnnotator;
import edu.mcw.rgd.nlp.utils.solr.PubMedSolrDoc;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.json.JSONObject;
import org.tartarus.snowball.Stemmer;

import java.util.*;

/**
 * @author wliu
 *
 */
public class PMCLibrary extends LibraryBase implements Library {

	protected FileList fileList = new FileList();
	protected FileList failedList = new FileList();
	protected FileList emptyList = new FileList();

	protected List<String> annSets;
    public static List<JSONObject> jsonObjects = new ArrayList<>();
	public static String MR_ANN_SETS = "mapred.input.annotation.sets";
	protected static AnnieAnnotator mrAnnoator;
	public static ArticleDAO mrArticleDao = new PmcArticleDAO();

	public PMCLibrary() {
		// TODO Auto-generated constructor stub

	}

	public void setAnnotationSets(List<String> annSets) {
		this.annSets = annSets;
	}

	public void resetAnnotator(String gateHome, boolean useStemming) {
		mrAnnoator = getAnnotator(gateHome, false, useStemming);
	}

	@Override
	public String getDocPath() {
		return getPathDoc();
	}

	@Override
	public void setPathDoc(String pathDoc) {
		super.setPathDoc(pathDoc);
		try {
			fileList.setFilePath(getPathDoc() + "/file_list.txt");
			failedList.setFilePath(getPathDoc() + "/file_list_failed.txt");
			emptyList.setFilePath(getPathDoc() + "/file_list_empty.txt");
		} catch (Exception e) {
			logger.error("Error in setting PathDoc", e);
		}
	}

	public static AnnieAnnotator getAnnotator(String annotator_path, boolean local, boolean useStemming) {
		AnnieAnnotator annotator = new AnnieAnnotator();
		try {
			if (local) {
				annotator.init(annotator_path);
			} else {
				annotator.initOnHDFS(annotator_path);
			}
			annotator.setUseStemming(useStemming);
			return annotator;
		} catch (Exception e) {

			logger.error("--->\t"+annotator_path);

			logger.error("Error in getting annotator", e);
			return null;
		}
	}

	public List<String> annotateFromHResult(AnnieAnnotator annotator,
		List<String> annotation_sets, Result result, ArticleDAO dao) {
		List<String> annResult = new ArrayList<String>();
		try {
				// Annotate title
				if (dao.articleTitle != null && dao.articleTitle.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.articleTitle, 0,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate abstract
				if (dao.articleAbstract != null
						&& dao.articleAbstract.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.articleAbstract, 1,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate body
				if (dao.articleBody != null && dao.articleBody.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.articleBody, 2,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate table captions ('captions' being the text describing a table)
				if (dao.tableCaptions != null && dao.tableCaptions.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.tableCaptions, 3,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate figure captions ('captions' being the text describing a figure)
				if (dao.figureCaptions != null && dao.figureCaptions.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.figureCaptions, 4,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate Keywords
				if (dao.keywords != null && dao.keywords.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.keywords, 5,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate MeSH terms
				if (dao.meshTerms != null && dao.meshTerms.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.meshTerms, 6,
							annotation_sets, annotator.isUseStemming()));
				}
				// Annotate chemicals
				if (dao.chemicals != null && dao.chemicals.length() > 0) {
					annResult.addAll(annotateToString(annotator, Long.parseLong(dao.pmcId), dao.chemicals, 7,
							annotation_sets, annotator.isUseStemming()));
				}


		} catch (Exception e) {
			logger.error("Error in [" + Long.parseLong(dao.pmcId) + "]", e);
			return annResult;
		}
		return annResult;
	}
	public enum OutputType {
        TO_FILE, TO_HBASE
	}

	public List<String> annotateToOutput(OutputType output, AnnieAnnotator annotator, long pmid,
			String text_to_annotate, int text_location,
			List<String> annotation_sets, boolean useStemming) {
		//System.out.println("annotate to output: " + annotation_sets.size());
//		for (String i: annotation_sets){
//			System.out.println(i);
//		}
		Document doc;
		if (useStemming) {
			doc= annotator.process(Stemmer.stem(text_to_annotate));
		} else {
			doc= annotator.process1(text_to_annotate, pmid);
		}
		List<String> outputStr = new ArrayList<String>();
		if (doc != null) {
			try {
				for (String anns_set_name : annotation_sets) {
					AnnotationSet anns = doc.getAnnotations(anns_set_name);
					for (Annotation ann : anns) {
						System.out.println("first node: " + anns.firstNode());
						System.out.println("last node: " + anns.lastNode());
						switch (output) {
						case TO_FILE:
							outputStr.add(pmid+ "\t" + text_location + "\t" + ann.getType() + "\t"
									+ anns_set_name + "\t" + ann.getStartNode().getOffset() + "\t"
									+ ann.getEndNode().getOffset() + "\t" + ann.getFeatures().toString());
						case TO_HBASE:
							outputStr.add(text_location + "\t"  + ann.getType() + "\t"
									+ anns_set_name + "\t" + ann.getStartNode().getOffset() + "\t"
									+ ann.getEndNode().getOffset() + "\t" + ann.getFeatures().toString());
						default:
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error in annotating: [" + text_to_annotate + "]");
				logger.error(e);
			}
		}
		annotator.clear();
		return outputStr;
	}
	public List<String> annotateToString(AnnieAnnotator annotator, long pmid,
			String text_to_annotate, int text_location,
			List<String> annotation_sets, boolean useStemming) {
		return annotateToOutput(OutputType.TO_HBASE, annotator, pmid, text_to_annotate, text_location, annotation_sets, useStemming);
	}
	public List<String> mrAnnotateHResult(Result result, String gateHome, boolean useStemming) {
		if (PMCLibrary.mrAnnoator == null) {
			PMCLibrary.mrAnnoator = getAnnotator(gateHome, false, useStemming);
		}
        List<String> annResult = new ArrayList<String>();
        if (mrArticleDao.getArticleFromHResult(result)) {
            annResult =  annotateFromHResult(PMCLibrary.mrAnnoator, annSets, result, mrArticleDao);
        }
        return annResult;
	}
	public List<String> mrAnnotatePrePrintHResult(Result result, String gateHome, boolean useStemming) {
		if (PMCLibrary.mrAnnoator == null) {
			System.out.println("MR ANNOTATOR is null");
			PMCLibrary.mrAnnoator = getAnnotator(gateHome, false, useStemming);
		}
        List<String> annResult = new ArrayList<String>();
        if (mrArticleDao.getPreprintArticleFromHResult(result)) {
            annResult = annotateFromHResult(PMCLibrary.mrAnnoator, annSets, result, mrArticleDao);
        }
        return annResult;
	}
	public List<String> mrAnnotateAgrHResult(Result result, String gateHome, boolean useStemming) {
		if (PMCLibrary.mrAnnoator == null) {
			System.out.println("MR ANNOTATOR is null");
			PMCLibrary.mrAnnoator = getAnnotator(gateHome, false, useStemming);
		}
        List<String> annResult = new ArrayList<String>();
        if (mrArticleDao.getAgrArticleFromHResult(result)) {
            annResult = annotateFromHResult(PMCLibrary.mrAnnoator, annSets, result, mrArticleDao);
        }
        return annResult;
	}
	public List<String> mrAnnotatePmcHResult(Result result, String gateHome, boolean useStemming) {
		if (PMCLibrary.mrAnnoator == null) {
			PMCLibrary.mrAnnoator = getAnnotator(gateHome, false, useStemming);
		}
		List<String> annResult = new ArrayList<String>();
		if (mrArticleDao.getPmcArticleFromHResult(result)) {
			annResult =  annotateFromHResult(PMCLibrary.mrAnnoator, annSets, result, mrArticleDao);
		}
		return annResult;
	}
	public static void addMaptoDoc(SolrInputDocument solr_doc,
			SortedCountMap map, String key_field, String value_field,
			String count_field, String pos_field) {

		List<Long> sorted_counts = map.getSortedCounts();
		List<Object> sorted_keys = map.getSortedKeys();
		HashMap<Object, Object> unsorted_map = map.getUnsortedMap();
		HashMap<Object, String> unsorted_pos = map.get_unsortedPositions();
		Iterator<Long> count_it = sorted_counts.iterator();
		for (Object key : sorted_keys) {
			Long count = count_it.next();
			if (key_field != null){
				solr_doc.addField(key_field, (String) key);
			}
			if (value_field != null){
				solr_doc.addField(value_field, (String) unsorted_map.get(key));
			}
			if (count_field != null) {
				solr_doc.addField(count_field, StringEscapeUtils.escapeXml(count.toString()));
			}
			if (pos_field != null) {
				solr_doc.addField(pos_field, StringEscapeUtils.escapeXml(unsorted_pos.get(key)));
			}
		}

	}

	public static Boolean indexArticle(Result result,HashMap<String,List<String>> data) throws Exception {
		ArticleDAO art = new ArticleDAO();
		Boolean indexable = true;

		if (art.getPmcArticleFromHResult(result)) {
			if (art.pmid != null) {
				List<AnnotationRecord> annotations = AnnotationDAO.getAnnotationsFromResult(result);
				List<String> links = AnnotationDAO.getLinksFromResult(result);
				String pmidStr = Long.toString(art.pmid);
				PubMedSolrDoc solr_doc = new PubMedSolrDoc();

				solr_doc.addField("pmcid", art.pmcId);
				solr_doc.addField("pmcabstract", art.articleAbstract);
				solr_doc.addField("pmctitle", art.articleTitle);
				solr_doc.addField("body", art.articleBody);
				solr_doc.addField("table", art.tableCaptions);
				solr_doc.addField("figure", art.figureCaptions);

			SortedCountMap gene_map = new SortedCountMap();
			SortedCountMap rgd_gene_map = new SortedCountMap();
			SortedCountMap organism_map = new SortedCountMap();
			HashMap<String, SortedCountMap> onto_maps = new HashMap<String, SortedCountMap>();
			Set<String> organism_common = new HashSet<>();
			for (String onto_name : Ontology.getRgdOntologies()) {
				onto_maps.put(onto_name, new SortedCountMap());
			}

			for (AnnotationRecord annotation : annotations) {
				String ann_section = "";
				switch (annotation.text_location) {
					case 0:
						ann_section = art.articleTitle;
						break;
					case 1:
						ann_section = art.articleAbstract;
						break;
					case 2:
						ann_section = art.articleBody;
						break;
					case 3:
						ann_section = art.tableCaptions;
						break;
					case 4:
						ann_section = art.figureCaptions;
						break;
					case 5:
						ann_section = art.meshTerms;
						break;
					case 6:
						ann_section = art.keywords;
						break;
					case 7:
						ann_section = art.chemicals;
						break;
				}
				String ann_text, ann_pos;

				ann_pos = String.format("%d;%d-%d", annotation.text_location,
						annotation.text_start,
						annotation.text_end);


				if (annotation.annotation_set == null)
					annotation.annotation_set = "Nulllll";

				if (!annotation.annotation_set.equals("OrganismTagger")) {
					try {
						ann_text = ann_section.substring(annotation.text_start,
								annotation.text_end);

					} catch (Exception e) {

						System.err.println("Error getting text: [" + pmidStr + ":" + annotation.annotation_set
								+ "] " + annotation.text_location + ":" + annotation.text_start + ", " + annotation.text_end + " from ["
								+ ann_section + "]");
						ann_text = "";
						indexable = false;
						break;
					}
				} else {
					ann_text = "";
				}

				// Simply put annotations together and index them.
				// Assuming they are independent to each other
				// Complex algorithms can be used later.
				if (annotation.annotation_set.equals("GENES") && !annotation.features_table.get("type").equals("CellLine")
						&& !annotation.features_table.get("type").equals("CellType")
						&& !annotation.features_table.get("type").equals("RNA")) {
					gene_map.add(ann_text, "", ann_pos);
					// solr_doc.add("gene", ann_text);
				} else if (annotation.annotation_set.equals("RGDGENE")) {
					rgd_gene_map.add(annotation.features_table.get("RGD_ID"),
							ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("OrganismTagger")) {
					String commonName = (String) annotation.features_table
							.get("Species");
					if (commonName != null && !commonName.equals(""))
						organism_common.add(commonName.trim().toLowerCase());
					String organism_id = (String) annotation.features_table
							.get("ncbiId");
					organism_map.add(organism_id, ArticleOrganismClassifier
							.getNameByID(Long.parseLong(organism_id)), ann_pos);
				} else if (annotation.annotation_set.equals("Ontologies")) {
					String onto_name = (String) annotation.features_table
							.get("minorType");
					onto_maps.get(onto_name).add(
							annotation.features_table.get("ONTO_ID"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("Mutations")) {
					onto_maps.get("MT").add((String) annotation.features_table
							.get("wNm"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("SNP")) {
					String snpStr = (String) annotation.features_table
							.get("string");
					try {
						onto_maps.get("MT").add(snpStr.replaceAll("\\s", ""), ann_text, ann_pos);
					} catch (Exception e) {
						System.err.println("Error in getting SNP annotations of " + pmidStr);
						e.printStackTrace();
						throw e;
					}
				}
			}
			if (!indexable) {
				return false;
			}

			gene_map.sort();
			addMaptoDoc(solr_doc, gene_map, "gene", null, "gene_count", "gene_pos");

			rgd_gene_map.sort();
			addMaptoDoc(solr_doc, rgd_gene_map, "rgd_obj_id", "rgd_obj_term",
					"rgd_obj_count", "rgd_obj_pos");

			organism_map.sort();
			addMaptoDoc(solr_doc, organism_map, "organism_ncbi_id",
					"organism_term", "organism_count", "organism_pos");
			for (String name : organism_common) {
				System.out.println("COMMON NAME: " + name);
				solr_doc.addField("organism_common_name", name);
			}

			for (String onto_name : Ontology.getRgdOntologies()) {
				SortedCountMap onto_map = onto_maps.get(onto_name);

				onto_map.sort(true, data);
				if (onto_name.equals("MT") && links != null) {
					for (String linkId : links) {
						onto_map.appendVirtualEntry("rs" + linkId);
					}
				}
				SolrOntologyEntry solr_entry = Ontology.getSolrOntoFields()
						.get(onto_name);
				addMaptoDoc(solr_doc, onto_map, solr_entry.getIdFieldName(),
						solr_entry.getTermFieldName(),
						solr_entry.getCountFieldName(),
						solr_entry.getPosFieldName());
			}


			try {
				JSONObject obj = new JSONObject(); // new JSONObject(doc);
				// we have to take apart the document
				Iterator<SolrInputField> itr = solr_doc.iterator();
				String key;
				SolrInputField field;
				while (itr.hasNext()) {
					field = itr.next();
					key = field.getName();
					obj.put(key, solr_doc.getFieldValues(key));
				}
				jsonObjects.add(obj);

				return true;

			} catch (Exception e) {

				System.err.println("Error when indexing:" + pmidStr);
				e.printStackTrace();
				return true;
			}
		}
	}

		return true;
	}
	public static Boolean indexPreprintArticle(Result result,HashMap<String,List<String>> data) throws Exception {
		ArticleDAO art = new ArticleDAO();
		Boolean indexable = true;

		if (art.getPreprintArticleFromHResult(result)) {
			List<AnnotationRecord> annotations = AnnotationDAO.getAnnotationsFromResult(result);
			List<String> links = AnnotationDAO.getLinksFromResult(result);
			String pmidStr = Long.toString(art.pmid);
			PubMedSolrDoc solr_doc = new PubMedSolrDoc();
			solr_doc.addField("pmid", pmidStr);
			solr_doc.addField("title", art.articleTitle);
			solr_doc.addField("abstract", art.articleAbstract);
			solr_doc.addField("p_date", art.articlePubDate);
			solr_doc.addField("authors", art.articleAuthors);
			solr_doc.addField("p_year", art.publicationYear);
			if (art.doi != null) solr_doc.addField("doi_s", art.doi);
			if (art.publicationTypes != null) {
				for (String pt : art.publicationTypes) {
					solr_doc.addField("p_type", pt);
				}
			}
			SortedCountMap gene_map = new SortedCountMap();
			SortedCountMap rgd_gene_map = new SortedCountMap();
			SortedCountMap organism_map = new SortedCountMap();
			List<String> nonSearchableTaxons=new ArrayList<>(Arrays.asList(
					"7955", "7227","6239","60711","10181","559292"));
			Set<String> rgdObjects=new HashSet<>();
			HashMap<String, SortedCountMap> onto_maps = new HashMap<String, SortedCountMap>();
			for (String onto_name : Ontology.getRgdOntologies()) {
				onto_maps.put(onto_name, new SortedCountMap());
			}

			for (AnnotationRecord annotation : annotations) {
				String ann_section = "";
				switch (annotation.text_location) {
					case 0:
						ann_section = art.articleTitle;
						break;
					case 1:
						ann_section = art.articleAbstract;
						break;
					case 2:
						ann_section = art.meshTerms;
						break;
					case 3:
						ann_section = art.keywords;
						break;
					case 4:
						ann_section = art.chemicals;
						break;
				}
				String ann_text, ann_pos;

				ann_pos = String.format("%d;%d-%d", annotation.text_location,
						annotation.text_start,
						annotation.text_end);
				if(annotation.annotation_set==null)
					annotation.annotation_set="Nulllll";

				if (!annotation.annotation_set.equals("OrganismTagger")) {
					try {
						ann_text = ann_section.substring(annotation.text_start,
								annotation.text_end);

					} catch (Exception e) {

						System.err.println("Error getting text: [" + pmidStr + ":" + annotation.annotation_set
								+ "] " + annotation.text_location + ":" + annotation.text_start + ", " + annotation.text_end + " from ["
								+ ann_section + "]");
						ann_text = "";
						indexable = false;
						break;
					}
				} else {
					ann_text = "";
				}
//System.out.println("ANNOTATION SET: "+annotation.annotation_set);

				if (annotation.annotation_set.equals("GENES") && !annotation.features_table.get("type").equals("CellLine")
						&& !annotation.features_table.get("type").equals("CellType")
						&& !annotation.features_table.get("type").equals("RNA")) {
					gene_map.add(ann_text, "", ann_pos);
					// solr_doc.add("gene", ann_text);
				} else if (annotation.annotation_set.equals("RGDGENE")) {
					//	System.out.println("RGDGENE RGD_ID: "+annotation.features_table.get("RGD_ID")+"\t"+ann_text+"\t"+ ann_pos);
					if(!rgdObjects.contains(ann_text.trim().toLowerCase())) {
						rgdObjects.add(ann_text.trim().toLowerCase());
						String termType= (String) annotation.features_table.get("term_type");
						if(!nonSearchableTaxons.contains(termType)) {
							rgd_gene_map.add(annotation.features_table.get("RGD_ID"), ann_text , ann_pos);
						}else{
							StringBuilder sb=new StringBuilder();
							sb.append(ann_text).append(" (TAXON:").append(annotation.features_table.get("term_type")).append(")");
							if(annotation.features_table.get("RGD_ID")!=null)
								gene_map.add(sb.toString(),annotation.features_table.get("RGD_ID") , ann_pos);
							else
								gene_map.add(sb.toString() ,"0",  ann_pos);
						}
					}

				} else if (annotation.annotation_set.equals("OrganismTagger")) {
					String organism_id = (String) annotation.features_table
							.get("ncbiId");
					organism_map.add(organism_id, ArticleOrganismClassifier
							.getNameByID(Long.parseLong(organism_id)), ann_pos);
				} else if (annotation.annotation_set.equals("Ontologies")) {
					String onto_name = (String) annotation.features_table
							.get("minorType");
					if(onto_maps.get(onto_name)!=null)
					onto_maps.get(onto_name).add(
							annotation.features_table.get("ONTO_ID"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("Mutations")) {
					onto_maps.get("MT").add((String) annotation.features_table
							.get("wNm"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("SNP")) {
					String snpStr = (String) annotation.features_table
							.get("string");
					try {
						onto_maps.get("MT").add(snpStr.replaceAll("\\s",""), ann_text, ann_pos);
					} catch (Exception e) {
						System.err.println("Error in getting SNP annotations of " + pmidStr);
						e.printStackTrace();
						throw e;
					}
				}
			}
		if (!indexable){

				return false;
			}

			gene_map.sort();
			addMaptoDoc(solr_doc, gene_map, "gene", "xdb_id", "gene_count", "gene_pos");

			rgd_gene_map.sort();
			addMaptoDoc(solr_doc, rgd_gene_map, "rgd_obj_id", "rgd_obj_term",
					"rgd_obj_count", "rgd_obj_pos");

			organism_map.sort();
			addMaptoDoc(solr_doc, organism_map, "organism_ncbi_id",
					"organism_term", "organism_count", "organism_pos");

			for (String onto_name : Ontology.getRgdOntologies()) {
				SortedCountMap onto_map = onto_maps.get(onto_name);
				onto_map.sort(true,data);
				if (onto_name.equals("MT") && links != null) {
					for (String linkId : links) {
						onto_map.appendVirtualEntry("rs"+linkId);
					}
				}
				SolrOntologyEntry solr_entry = Ontology.getSolrOntoFields()
						.get(onto_name);
				addMaptoDoc(solr_doc, onto_map, solr_entry.getIdFieldName(),
						solr_entry.getTermFieldName(),
						solr_entry.getCountFieldName(),
						solr_entry.getPosFieldName());
			}

			try {
				JSONObject obj = new JSONObject(); // new JSONObject(doc);
				// we have to take apart the document
				Iterator<SolrInputField>itr = solr_doc.iterator();
				String key;
				SolrInputField field;
				while (itr.hasNext()) {
					field = itr.next();
					key = field.getName();
					obj.put(key, solr_doc.getFieldValues(key));
				}
				jsonObjects.add(obj);

				return true;

			} catch (Exception e) {

				System.err.println("Error when indexing:" + pmidStr);
				e.printStackTrace();
				return true;
			}
		}

		return true;
	}
	public static Boolean indexPmcArticle(Result result,HashMap<String,List<String>> data) throws Exception {
		PmcArticleDAO art = new PmcArticleDAO();
		Boolean indexable = true;

		if (art.getPmcArticleFromHResult(result) && (art.pmid != null)) {

			List<AnnotationRecord> annotations = AnnotationDAO
					.getAnnotationsFromResult(result);
			List<String> links = AnnotationDAO.getLinksFromResult(result);
			String pmidStr = Long.toString(art.pmid);
			PubMedSolrDoc solr_doc = new PubMedSolrDoc();
			//SolrInputDocument solr_doc = new SolrInputDocument();

			solr_doc.setField("pmid", pmidStr);
			solr_doc.setField("pmcid", art.pmcId);
			solr_doc.setField("pmc_id_s", art.pmcId);//original field for pmc id
			solr_doc.setField("pmcabstract",art.articleAbstract);
			solr_doc.setField("pmctitle", art.articleTitle);
			solr_doc.setField("body", art.articleBody);
			solr_doc.setField("table", art.tableCaptions);
			solr_doc.setField("figure", art.figureCaptions);

//			Map<String, String> fieldModifier = new HashMap<String, String>();
//			fieldModifier.put("set", art.pmcId);
//			solr_doc.setField("pmcid", fieldModifier);
//			solr_doc.setField("pmc_id_s", fieldModifier);//original field for pmc id
//			Map<String, String> fieldModifier2 = new HashMap<String, String>();
//			fieldModifier2.put("set", art.articleAbstract);
//			solr_doc.setField("pmcabstract", fieldModifier2);
//			Map<String, String> fieldModifier3 = new HashMap<String, String>();
//			fieldModifier3.put("set", art.articleTitle);
//			solr_doc.setField("pmctitle", fieldModifier3);
//			Map<String, String> fieldModifier4 = new HashMap<String, String>();
//			fieldModifier4.put("set", art.articleBody);
//			solr_doc.setField("body", fieldModifier4);
//			Map<String, String> fieldModifier5 = new HashMap<String, String>();
//			fieldModifier5.put("set", art.tableCaptions);
//			solr_doc.setField("table", fieldModifier5);
//			Map<String, String> fieldModifier6 = new HashMap<String, String>();
//			fieldModifier6.put("set", art.figureCaptions);
//			solr_doc.setField("figure", fieldModifier6);


			SortedCountMap gene_map = new SortedCountMap();
			SortedCountMap rgd_gene_map = new SortedCountMap();
			SortedCountMap organism_map = new SortedCountMap();
			HashMap<String, SortedCountMap> onto_maps = new HashMap<String, SortedCountMap>();
			Set<String> organism_common= new HashSet<>();
			for (String onto_name : Ontology.getRgdOntologies()) {
				onto_maps.put(onto_name, new SortedCountMap());
			}

			for (AnnotationRecord annotation : annotations) {
				String ann_section = "";
				switch (annotation.text_location) {
					case 0:
						ann_section = art.articleTitle;
						break;
					case 1:
						ann_section = art.articleAbstract;
						break;
					case 2:
						ann_section = art.articleBody;
						break;
					case 3:
						ann_section = art.tableCaptions;
						break;
					case 4:
						ann_section = art.figureCaptions;
						break;
					case 5:
						ann_section = art.meshTerms;
						break;
					case 6:
						ann_section = art.keywords;
						break;
					case 7:
						ann_section = art.chemicals;
						break;
				}
				String ann_text, ann_pos;

				ann_pos = String.format("%d;%d-%d", annotation.text_location,
						annotation.text_start,
						annotation.text_end);


				if(annotation.annotation_set==null)
					annotation.annotation_set="Nulllll";

				if (!annotation.annotation_set.equals("OrganismTagger")) {
					try {
						ann_text = ann_section.substring(annotation.text_start,
								annotation.text_end);

					} catch (Exception e) {

						System.err.println("Error getting text: [" + pmidStr + ":" + annotation.annotation_set
								+ "] " + annotation.text_location + ":" + annotation.text_start + ", " + annotation.text_end + " from ["
								+ ann_section + "]");
						ann_text = "";
						indexable = false;
						break;
					}
				} else {
					ann_text = "";
				}

				// Simply put annotations together and index them.
				// Assuming they are independent to each other
				// Complex algorithms can be used later.
				if (annotation.annotation_set.equals("GENES") && !annotation.features_table.get("type").equals("CellLine")
						&& !annotation.features_table.get("type").equals("CellType")
						&& !annotation.features_table.get("type").equals("RNA")) {
					gene_map.add(ann_text, "", ann_pos);
					// solr_doc.add("gene", ann_text);
				} else if (annotation.annotation_set.equals("RGDGENE")) {
					rgd_gene_map.add(annotation.features_table.get("RGD_ID"),
							ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("OrganismTagger")) {
					String commonName = (String) annotation.features_table
							.get("Species");
					if(commonName != null && !commonName.equals(""))
						organism_common.add(commonName.trim().toLowerCase());
					String organism_id = (String) annotation.features_table
							.get("ncbiId");
					organism_map.add(organism_id, ArticleOrganismClassifier
							.getNameByID(Long.parseLong(organism_id)), ann_pos);
				} else if (annotation.annotation_set.equals("Ontologies")) {
					String onto_name = (String) annotation.features_table
							.get("minorType");
					onto_maps.get(onto_name).add(
							annotation.features_table.get("ONTO_ID"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("Mutations")) {
					onto_maps.get("MT").add((String) annotation.features_table
							.get("wNm"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("SNP")) {
					String snpStr = (String) annotation.features_table
							.get("string");
					try {
						onto_maps.get("MT").add(snpStr.replaceAll("\\s",""), ann_text, ann_pos);
					} catch (Exception e) {
						System.err.println("Error in getting SNP annotations of " + pmidStr);
						e.printStackTrace();
						throw e;
					}
				}
			}
			if (!indexable){
				return false;
			}

			gene_map.sort();
			addMaptoDoc(solr_doc, gene_map, "gene_pmc", null, "gene_count_pmc", "gene_pos_pmc");

			rgd_gene_map.sort();
			addMaptoDoc(solr_doc, rgd_gene_map, "rgd_obj_id_pmc", "rgd_obj_term_pmc",
					"rgd_obj_count_pmc", "rgd_obj_pos_pmc");

			organism_map.sort();
			addMaptoDoc(solr_doc, organism_map, "organism_ncbi_id_pmc",
					"organism_term_pmc", "organism_count_pmc", "organism_pos_pmc");
//			for(String name: organism_common){
//				System.out.println("COMMON NAME: "+ name);
//				solr_doc.addField("organism_common_name",name );
//			}

			for (String onto_name : Ontology.getRgdOntologies()) {
				SortedCountMap onto_map = onto_maps.get(onto_name);

				onto_map.sort(true,data);
				if (onto_name.equals("MT") && links != null) {
					for (String linkId : links) {
						onto_map.appendVirtualEntry("rs"+linkId);
					}
				}
				SolrOntologyEntry solr_entry = Ontology.getSolrOntoFields()
						.get(onto_name);
				addMaptoDoc(solr_doc, onto_map, solr_entry.getIdFieldName()+"_pmc",
						solr_entry.getTermFieldName()+"_pmc",
						solr_entry.getCountFieldName()+"_pmc",
						solr_entry.getPosFieldName()+"_pmc");
			}


			try {
				JSONObject obj = new JSONObject(); // new JSONObject(doc);
				// we have to take apart the document
				Iterator<SolrInputField>itr = solr_doc.iterator();
				String key;
				SolrInputField field;
				while (itr.hasNext()) {
					field = itr.next();
					key = field.getName();
					Collection<Object> value = solr_doc.getFieldValues(key);
					if (value.size() > 1) {
						Map<String, Collection<Object>> fieldModifier7 = new HashMap<String, Collection<Object>>();
						fieldModifier7.put("set", value);
						obj.put(key,fieldModifier7);
//						jsonObjects.add(obj);
					} else if ((value.size() == 1) && (!value.toArray()[0].equals(""))){
						if (key.equals("pmid")){
							obj.put(key, value.toArray()[0]);
						} else{
							Map<String, Object> fieldModifier8 = new HashMap<String, Object>();
							fieldModifier8.put("set", value.toArray()[0]);
							obj.put(key, fieldModifier8);
						}
//						jsonObjects.add(obj);
					}
//					else{
//						Map<String,String> fieldModifier8 = new HashMap<String, String>();
//						fieldModifier8.put("set","");
//						obj.put(key,fieldModifier8);
//					}
				}
				jsonObjects.add(obj);
				return true;

			} catch (Exception e) {

				System.err.println("Error when indexing:" + pmidStr);
				e.printStackTrace();
				return true;
			}
		}

		return true;
	}
	public static Boolean indexAgrArticle(Result result,HashMap<String,List<String>> data) throws Exception {
		ArticleDAO art = new ArticleDAO();
		Boolean indexable = true;

		if (art.getAgrArticleFromHResult(result)) {

			List<AnnotationRecord> annotations = AnnotationDAO
					.getAnnotationsFromResult(result);
			List<String> links = AnnotationDAO.getLinksFromResult(result);
			String pmidStr = Long.toString(art.pmid);
			PubMedSolrDoc solr_doc = new PubMedSolrDoc();

			solr_doc.addField("pmid", pmidStr);
			solr_doc.addField("title", art.articleTitle);
			solr_doc.addField("abstract", art.articleAbstract);
			solr_doc.addField("p_date", art.articlePubDate);
			solr_doc.addField("authors", art.articleAuthors);
			solr_doc.addField("mesh_terms", art.meshTerms);
			solr_doc.addField("keywords", art.keywords);
			solr_doc.addField("p_year", art.publicationYear);
			if (art.pmcId != null) solr_doc.addField("pmc_id_s", art.pmcId);
			if (art.doi != null) solr_doc.addField("doi_s", art.doi);

//			logger.info("1.1  In indexArticle=> pmid=\t"+pmidStr+"\t"+art.pmid);


			if (art.publicationTypes != null) {
				for (String pt : art.publicationTypes) {
					solr_doc.addField("p_type", pt);
				}
			}
//			logger.info("1.2  In indexArticle=> pmid=\t"+pmidStr+"\t"+art.pmid);

			SortedCountMap gene_map = new SortedCountMap();
			SortedCountMap rgd_gene_map = new SortedCountMap();
			SortedCountMap organism_map = new SortedCountMap();
			HashMap<String, SortedCountMap> onto_maps = new HashMap<String, SortedCountMap>();
			Set<String> organism_common=new HashSet<>();
			for (String onto_name : Ontology.getRgdOntologies()) {
				onto_maps.put(onto_name, new SortedCountMap());
			}
//			logger.info("1.3  In indexArticle=> pmid=\t"+pmidStr+"\t"+art.pmid);

			for (AnnotationRecord annotation : annotations) {
				String ann_section = "";
				switch (annotation.text_location) {
					case 0:
						ann_section = art.articleTitle;
						break;
					case 1:
						ann_section = art.articleAbstract;
						break;
					case 2:
						ann_section = art.meshTerms;
						break;
					case 3:
						ann_section = art.keywords;
						break;
				}
				String ann_text, ann_pos;

				ann_pos = String.format("%d;%d-%d", annotation.text_location,
						annotation.text_start,
						annotation.text_end);


				if (annotation.annotation_set == null)
					annotation.annotation_set = "Nulllll";

				if (!annotation.annotation_set.equals("OrganismTagger")) {
					try {
						ann_text = ann_section.substring(annotation.text_start,
								annotation.text_end);

					} catch (Exception e) {
						System.err.println("Error getting text: [" + pmidStr + ":" + annotation.annotation_set
								+ "] " + annotation.text_location + ":" + annotation.text_start + ", " + annotation.text_end + " from ["
								+ ann_section + "]");
						ann_text = "";
						indexable = false;
						break;
					}
				} else {
					ann_text = "";
				}

				// Simply put annotations together and index them.
				// Assuming they are independent to each other
				// Complex algorithms can be used later.
				if (annotation.annotation_set.equals("GENES") && !annotation.features_table.get("type").equals("CellLine")
						&& !annotation.features_table.get("type").equals("CellType")
						&& !annotation.features_table.get("type").equals("RNA")) {
					gene_map.add(ann_text, "", ann_pos);
					// solr_doc.add("gene", ann_text);
				} else if (annotation.annotation_set.equals("RGDGENE")) {
					rgd_gene_map.add(annotation.features_table.get("RGD_ID"),
							ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("OrganismTagger")) {
					String commonName = (String) annotation.features_table
							.get("Species");
					if (commonName != null && !commonName.equals(""))
						organism_common.add(commonName.trim().toLowerCase());
					String organism_id = (String) annotation.features_table
							.get("ncbiId");
					organism_map.add(organism_id, ArticleOrganismClassifier
							.getNameByID(Long.parseLong(organism_id)), ann_pos);
				} else if (annotation.annotation_set.equals("Ontologies")) {
					String onto_name = (String) annotation.features_table
							.get("minorType");
					if(onto_maps.get(onto_name) == null)
						onto_maps.put(onto_name, new SortedCountMap());

					onto_maps.get(onto_name).add(
								annotation.features_table.get("ONTO_ID"), ann_text, ann_pos);

				} else if (annotation.annotation_set.equals("Mutations")) {
					onto_maps.get("MT").add((String) annotation.features_table
							.get("wNm"), ann_text, ann_pos);
				} else if (annotation.annotation_set.equals("SNP")) {
					String snpStr = (String) annotation.features_table
							.get("string");
					try {
						onto_maps.get("MT").add(snpStr.replaceAll("\\s", ""), ann_text, ann_pos);
					} catch (Exception e) {
						System.err.println("Error in getting SNP annotations of " + pmidStr);
						e.printStackTrace();
						throw e;
					}
				}
			}
			if (!indexable){

				return false;
			}

			gene_map.sort();
			addMaptoDoc(solr_doc, gene_map, "gene", null, "gene_count", "gene_pos");

			rgd_gene_map.sort();
			addMaptoDoc(solr_doc, rgd_gene_map, "rgd_obj_id", "rgd_obj_term",
					"rgd_obj_count", "rgd_obj_pos");

			organism_map.sort();
			addMaptoDoc(solr_doc, organism_map, "organism_ncbi_id",
					"organism_term", "organism_count", "organism_pos");
			for(String name: organism_common){
				System.out.println("COMMON NAME: "+ name);
				solr_doc.addField("organism_common_name",name );
			}

			for (String onto_name : Ontology.getRgdOntologies()) {
				SortedCountMap onto_map = onto_maps.get(onto_name);

				onto_map.sort(true,data);
				if (onto_name.equals("MT") && links != null) {
					for (String linkId : links) {
						onto_map.appendVirtualEntry("rs"+linkId);
					}
				}
				SolrOntologyEntry solr_entry = Ontology.getSolrOntoFields()
						.get(onto_name);
				addMaptoDoc(solr_doc, onto_map, solr_entry.getIdFieldName(),
						solr_entry.getTermFieldName(),
						solr_entry.getCountFieldName(),
						solr_entry.getPosFieldName());
			}


			try {

				JSONObject obj = new JSONObject(); // new JSONObject(doc);
				// we have to take apart the document
				Iterator<SolrInputField>itr = solr_doc.iterator();
				String key;
				SolrInputField field;
				while (itr.hasNext()) {
					field = itr.next();
					key = field.getName();
					obj.put(key, solr_doc.getFieldValues(key));
				}
				jsonObjects.add(obj);

				return true;

			} catch (Exception e) {

				System.err.println("Error when indexing:" + pmidStr);
				e.printStackTrace();
				return true;
			}
		}

		return true;
	}

	public static String pmidToHbaseKey(String pmid) {
		return new StringBuilder(pmid).reverse().toString();
	}

	public static String hbaseKeyToPmid(String key) {
		return new StringBuilder(key).reverse().toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}