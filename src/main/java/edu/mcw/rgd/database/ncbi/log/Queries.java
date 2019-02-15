package edu.mcw.rgd.database.ncbi.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.common.utils.DateUtils;
import edu.mcw.rgd.common.utils.FileEntry;
import edu.mcw.rgd.common.utils.SortedCountMap;
import edu.mcw.rgd.database.ncbi.pubmed.AnnotationDAO;
import edu.mcw.rgd.database.ncbi.pubmed.AnnotationRecord;
import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.database.ncbi.pubmed.DocDBConnection;
import edu.mcw.rgd.nlp.classifier.ArticleOrganismClassifier;
import edu.mcw.rgd.nlp.datamodel.NcbiQueryLogEntry;
import edu.mcw.rgd.nlp.datamodel.Ontology;
import edu.mcw.rgd.nlp.datamodel.QueryClause;
import edu.mcw.rgd.nlp.datamodel.SolrOntologyEntry;
import edu.mcw.rgd.nlp.utils.ncbi.QueryParser;

//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorType;

public class Queries {

	public static String tableName = "NCBI_queries";
	public static String annotationTableName = "annotations_ncbi";
	
	public static void insert(NcbiQueryLogEntry logEntry) {
		try {
			String date = logEntry.getTimeStamp().substring(0, 10);
			String time = logEntry.getTimeStamp().substring(11);
			date = DateUtils.getMySQLDate(date);
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("insert into " + tableName + " values (" 
					+ "null" + "," 
					+ BasicUtils.EscapeSQLStringValue(date + " " + time) + ","
					+ BasicUtils.EscapeSQLStringValue(logEntry.getDb()) + ","
					+ BasicUtils.EscapeSQLStringValue(logEntry.getCmd()) + ","
					+ BasicUtils.EscapeSQLStringValue(logEntry.getDopt()) + ","
					+ BasicUtils.EscapeSQLStringValue(logEntry.getQuery())
					+");"
				));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static NcbiQueryLogEntry get(Long id) {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ tableName + " where id=" + id);
		NcbiQueryLogEntry result = null;
		try {
			while (rs.next()) {
				result = new NcbiQueryLogEntry();
				result.setCmd(rs.getString("cmd"));
				result.setQuery(rs.getString("query"));
				result.setDb(rs.getString("db"));
				result.setDopt(rs.getString("dopt"));
				result.setTimeStamp(rs.getString("time_stamp"));
			}
			DocDBConnection.closeRsStatement(rs);
			return result;
			
		} catch (Exception e)
		{
			return null;
		}
	}

	public static void exportResult(Long id, BufferedWriter bw) {
		try {
		// ArticleOrganismClassifier aoc = new ArticleOrganismClassifier();
			NcbiQueryLogEntry en = Queries.get(id);
			String queryString = Queries.getQueryTerm(id);
			
			// Skip records caused by RGD pipelines
			if (en.getDb().equals("gene") && (en.getQuery().equals("RAT") || en.getQuery().equals("RAT[gene]"))) {
				return;
			}
			bw.write(id+"\t");

			bw.write(en.toString());
			bw.write(queryString + "\t");
			
			List<AnnotationRecord> annotations = AnnotationDAO
					.getAllAnnotationsForNcbiLog(id);
			// XmlDoc solr_doc = new XmlDoc();
			// solr_doc.add("pmid", Long.toString(pmid));
			// solr_doc.add("title", art.articleTitle);
			// solr_doc.add("abstract", art.articleAbstract);

			SortedCountMap gene_map = new SortedCountMap();
			SortedCountMap rgd_gene_map = new SortedCountMap();
			SortedCountMap rdo_map = new SortedCountMap();
			SortedCountMap rs_map = new SortedCountMap();
			SortedCountMap organism_map = new SortedCountMap();
			boolean hasRat = false; 
			HashMap<String, SortedCountMap> onto_maps = new HashMap<String, SortedCountMap>();
			for (String onto_name : Ontology.getRgdOntologies()) {
				onto_maps.put(onto_name, new SortedCountMap());
			}
			
			if (queryString.contains("\"Rattus\"")) {
				hasRat = true;
				organism_map.add("10114", "Rattus");
			}
			
			for (AnnotationRecord annotation : annotations) {
				String ann_section = queryString;
				String ann_text;

				if (!annotation.annotation_set.equals("OrganismTagger")) {
					ann_text = ann_section.substring(annotation.text_start,
							annotation.text_end);
				} else {
					ann_text = "";
				}

				// Simply put annotations together and index them.
				// Assuming they are independent to each other
				// Complex algorithms can be used later.
				if (annotation.annotation_set.equals("GENES")) {
					gene_map.add(ann_text, "");
					// solr_doc.add("gene", ann_text);
				} else if (annotation.annotation_set.equals("RGDGENE")) {
					rgd_gene_map.add(annotation.features_table.get("RGD_ID"),
							ann_text);
					// solr_doc.add("rgd_obj_term", ann_text);
					// solr_doc.add("rgd_obj_id", "RGD:" +
					// annotation.features_table.get("RGD_ID"));
				} 
				// Disease is now in ontologies annotation
//				else if (annotation.annotation_set.equals("RDO_TERMS")) {
//					rdo_map.add(annotation.features_table.get("ONTO_ID"),
//							ann_text);
//					// solr_doc.add("ctd_term", ann_text);
//					// solr_doc.add("ctd_id", (String)
//					// annotation.features_table.get("ONTO_ID"));
//				}
				// Rat Strain is now in ontologies annotation
//				else if (annotation.annotation_set.equals("RATSTRAIN")) {
//					rs_map.add(annotation.features_table.get("ONTO_ID"),
//							ann_text);
//					// solr_doc.add("rs_term", ann_text);
//					// solr_doc.add("rs_id", (String)
//					// annotation.features_table.get("ONTO_ID"));
//				} 
				else if (annotation.annotation_set.equals("OrganismTagger")) {
					String organism_id = (String) annotation.features_table
							.get("ncbiId");
					String organism_name = ArticleOrganismClassifier
							.getNameByID(Long.parseLong((organism_id)));
					if (organism_name.toLowerCase().startsWith("rattus")) hasRat = true;
					
					organism_map.add(organism_id, organism_name);
					// long ncbiID = Long.parseLong((String)
					// annotation.features_table.get("ncbiId"));
					// aoc.AddID(ncbiID);
				} else if (annotation.annotation_set.equals("Ontologies") ||
						annotation.annotation_set.equals("CHEBI")) {
					String onto_name = (String) annotation.features_table.get("minorType");
					onto_maps.get(onto_name).add(annotation.features_table.get("ONTO_ID"),
							ann_text);
				}
			}

			bw.write(hasRat ? "Y\t" : "\t");

			gene_map.sort();
			addMaptoDoc(bw, gene_map, "gene", null, "gene_count", true);

//			rgd_gene_map.sort();
//			addMaptoDoc(bw, rgd_gene_map, "rgd_obj_id", "rgd_obj_term",
//					"rgd_obj_count");

			addOntofield("MF", onto_maps, false, bw);
			addOntofield("BP", onto_maps, false, bw);
			addOntofield("CC", onto_maps, true, bw);

			organism_map.sort();
			addMaptoDoc(bw, organism_map, "organism_ncbi_id",
					"organism_term", "organism_count", true);

			// RatStrain is now in ontologies annotation
//			rs_map.sort();
//			addMaptoDoc(bw, rs_map, "rs_id", "rs_term", "rs_count",true);

			addOntofield("RS", onto_maps, true, bw);

			// Disease is now in ontologies annotation
//			rdo_map.sort();
//			addMaptoDoc(bw, rdo_map, "rdo_id", "rdo_term", "rdo_count",true);

			addOntofield("RDO", onto_maps, true, bw);
			addOntofield("HP", onto_maps, false, bw);
			addOntofield("MP", onto_maps, true, bw);

			addOntofield("NBO", onto_maps, true, bw);

			addOntofield("XCO", onto_maps, true, bw);

			addOntofield("CHEBI", onto_maps, true, bw);
			
			addOntofield("ZFA", onto_maps, true, bw); // recently added for zebrafish -----------------------

			
			bw.write(getAuthorFromDB(id));
			bw.write("\t");

			bw.write(getTitleFromDB(id));
			bw.write("\t");

			addOntofield("CMO", onto_maps, false, bw);
			addOntofield("MMO", onto_maps, true, bw);

			addOntofield("MA", onto_maps, false, bw);
			addOntofield("CLO", onto_maps, true, bw);

			addOntofield("SO", onto_maps, false, bw);
			addOntofield("PW", onto_maps, true, bw);
			
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addOntofield(String onto_name, HashMap<String, SortedCountMap> onto_maps,
			boolean new_field, BufferedWriter bw){
		try {
		SortedCountMap onto_map = onto_maps.get(onto_name);
		if (onto_map == null) {
			bw.write(new_field ? "\t":"");
			return;
		}
		onto_map.sort();
		SolrOntologyEntry solr_entry = Ontology.getSolrOntoFields().get(onto_name);
		addMaptoDoc(bw, onto_map, solr_entry.getIdFieldName(),
				solr_entry.getTermFieldName(), solr_entry.getCountFieldName(), new_field);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addMaptoDoc(BufferedWriter bw,
			SortedCountMap map, String key_field, String value_field,
			String count_field, boolean newField) {

		// Map sorted_counts = map.getSortedCounts();
		// Map sorted_map = map.getSortedMap();
		// for (Object key : sorted_counts.keySet()) {
		// if (key_field != null)
		// solr_doc.add(key_field, (String) key);
		// if (value_field != null)
		// solr_doc.add(value_field, (String) sorted_map.get(key));
		// if (count_field != null)
		// {
		// Object count_value = (Object)sorted_counts.get(key);
		// solr_doc.add(count_field, count_value.toString());
		// }
		// }

		List<Long> sorted_counts = map.getSortedCounts();
		List<Object> sorted_keys = map.getSortedKeys();
		HashMap<Object, Object> unsorted_map = map.getUnsortedMap();
		Iterator<Long> count_it = sorted_counts.iterator();
		String connect_str = "";
		try {
		for (Object key : sorted_keys) {
			Long count = count_it.next();
//			if (key_field != null)
//				System.out.print( (String) key);
				
				if (value_field != null) {
					bw.write(connect_str + (String) unsorted_map.get(key));
					connect_str = ", ";
				} else if (key_field != null){
					bw.write(connect_str+(String)key);
					connect_str = ", ";
				} 
				
//			if (count_field != null) {
//				Object count_value = count;
//				System.out.print(
//						StringEscapeUtils.escapeXml(count_value.toString()));
			

		}
		
		if (newField) bw.write("\t");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getAuthorFromDB(Long id) {
		try {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ "NCBI_query_authors" + " where id=" + id);
		if (rs.next()) {
			String author = rs.getString("author_name");
			DocDBConnection.closeRsStatement(rs);
			if (author != null) return author;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}
	
	public static String getTitleFromDB(Long id) {
		try {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ "NCBI_query_title" + " where id=" + id);
		if (rs.next()) {
			String title = rs.getString("title");
			DocDBConnection.closeRsStatement(rs);
			if (title != null) return title;
		}
		} catch (Exception e) {
			return "";
		}
		return "";
	}
	
	public static void getTitleAuthor(Long id) {
		NcbiQueryLogEntry en = Queries.get(id);
		List<QueryClause> clauseList = getClauses(en.getQuery());
		String title = "";
		for (QueryClause qc : clauseList) {
			if (qc.getField() != null && qc.getField().equalsIgnoreCase("title")) {
				title += qc.getValue() + " ";
			}
		}
		if (title != null && title.length()>0)
			try {
				DocDBConnection.closeRsStatement(
				DocDBConnection.executeQuery("insert into NCBI_query_title values (" +
				id+","+BasicUtils.EscapeSQLStringValue(title)+")"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		String author = "";
		for (QueryClause qc : clauseList) {
			if (qc.getField() != null && (qc.getField().equals("au")
					|| qc.getField().equalsIgnoreCase("author")
					|| qc.getField().equalsIgnoreCase("corporate author")
				
					)) {
				author += qc.getValue() + " ";
			}
		}
		if (author != null && author.length()>0)
			try {
				DocDBConnection.closeRsStatement(
				DocDBConnection.executeQuery("insert into NCBI_query_authors values (" +
				id+","+BasicUtils.EscapeSQLStringValue(author)+")"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	public static List<QueryClause> getClauses(String query) {
		query.replaceAll("\\(|\\)", "");
		List<QueryClause> return_list = new ArrayList<QueryClause>();
		Pattern regex = Pattern.compile("(.*)(AND|OR)(.*)");
		Matcher regexMatcher = regex.matcher(query);
		while (regexMatcher.find()) {
		    if (regexMatcher.group(1) != null) {
		        // Add double-quoted string without the quotes
//		        matchList.add(regexMatcher.group(1));
		    } 
		    if (regexMatcher.group(3) != null) {
		        // Add single-quoted string without the quotes
		    	return_list.add(0,new QueryClause(regexMatcher.group(3)));
		        query = query.substring(0,  regexMatcher.start(2));
				regexMatcher = regex.matcher(query);
		    } 
//		        matchList.add(regexMatcher.group());
		} 
		return_list.add(0, new QueryClause(query));
		return return_list;
	}

	public static void exportAll(Long start, Long end) {

/*export result
 */
		String filePath = "/shared/users/wliu/tmp/NCBI_log_result.txt";
		try {
			AnnotationDAO.tableName = annotationTableName;

		BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(filePath));

		bw.write("ID\tTime Stamp\tDb\tCmd\tDopt\tTerm\tCleaned Term\thasRat\tGene\tGene Function\t"+
		"Species\tStrain\tDisease\tPhyiological/Morphological Phenotype\t"+"" +
				"Behavioral Phenotype\tExperimental condition\tDrug/Small molecule\t" +
		"Author\tTitle\tExperimental Methods\t"+
		"Anatomy/Cell Type\tOthers");
		bw.newLine();
		for (Long i = start; i <= end; i++) {
			Queries.exportResult(i, bw);
		}
		bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
System.out.println("Result exported to " + filePath);
				
	}
	
	public static void importFile(String fileName) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null && line.length() > 0)
		{
			NcbiQueryLogEntry entry = NcbiQueryLogEntry.parseRawLog(line);
			insert(entry);
		}
		br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getQueryTerm(Long id) {
		try {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ "NCBI_query_terms" + " where id=" + id);
		if (rs.next()) {
			String author = rs.getString("query_terms");
			DocDBConnection.closeRsStatement(rs);
			if (author != null) return author;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}
	
	public static void main(String[] args) {
//		int total = 30463;
		
		if (args.length == 0) {
			System.out.println("Valid commands:\r\n"+
		"import [file_name] \r\n"+
		"clean [start_id] [end_id]\r\n"+
		"get_title_author [start_id] [end_id]\r\n"+
		"export [start_id] [end_id]\r\n");
			System.exit(0);
		}
		if (args[0].equals("clean")) {
			for (Long i = new Long(args[1]); i <= new Long(args[2]); i++) {
				NcbiQueryLogEntry en = Queries.get(i);
				String cl_q = QueryParser.cleanQuery(en.getQuery());
				System.out.println("Ori:" + en.getQuery());
				System.out.println("cleaned: " + cl_q);
				DocDBConnection.executeQuery("insert into NCBI_query_terms values (" +
				i+","+BasicUtils.EscapeSQLStringValue(cl_q)+")");;
			}
			
		} else if (args[0].equals("export")){
			exportAll(new Long(args[1]), new Long(args[2]));
			
		} else if (args[0].equals("get_title_author")){
			/* get title and author 
		 * */

			for (Long i = new Long(args[1]); i <= new Long(args[2]); i++) {
				System.out.println(i);
				Queries.getTitleAuthor(i);
			}
			
		} else if (args[0].equals("import")){
			importFile(args[1]);
		}
/* clean data
		*/
/*		
		File tsv_file = new File("/shared/users/wliu/tmp/NCBI_log.txt");
		BufferedReader br = new BufferedReader(new FileReader(tsv_file));
		String line;
		while ((line = br.readLine()) != null && line.length() > 0)
		{
			fileList.add(new FileEntry(line));
		}
		br.close();
*/

//		exportAll();
	}
}
