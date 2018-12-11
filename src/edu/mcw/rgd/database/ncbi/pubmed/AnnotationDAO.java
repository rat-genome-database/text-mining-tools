package edu.mcw.rgd.database.ncbi.pubmed;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.jcraft.jsch.Logger;

import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.common.utils.DAOBase;
import edu.mcw.rgd.common.utils.HBaseConnection;
import edu.mcw.rgd.nlp.utils.ncbi.DistributedAnnotator;
import edu.mcw.rgd.nlp.utils.ncbi.DistributedAnnotator.Map;

public class AnnotationDAO extends DAOBase {

	public static String tableName = "annotations";
	
	public static void insertRecord(String pmid, int text_location, String annotation_type,
			String annotation_set, Long text_start, Long text_end, String features) {
		try {
			ResultSet rs = DocDBConnection.executeQuery("insert into " + tableName + " value ('" + pmid + "','" +
					text_location + 
					"','" + annotation_type + 
					"','" + annotation_set + 
					"','" + text_start + 
					"','" + text_end + 
					"','" + StringEscapeUtils.escapeJavaScript(features) +
					"');" );
					DocDBConnection.closeRsStatement(rs);
		} catch (Exception e) {
			logger.error("Error inserting annotation " + pmid, e);
			return;
		}
		
	}

	public static void deleteRecords(String pmid) {
		try {
			DocDBConnection.closeRsStatement(DocDBConnection.executeQuery("delete from " + tableName + 
				" where pmid=" + pmid));
		} catch (Exception e) {
			logger.error("Error deleting annotations " + pmid, e);
			return;
		}
	}
	
	public static void deleteRecords(String pmid, String annotation_set, int text_location) {
		try {
			DocDBConnection.closeRsStatement(DocDBConnection.executeQuery("delete from " + tableName + 
					" where pmid=" + pmid + " and text_location=" + text_location + " and " +
					"annotation_set=" + BasicUtils.EscapeSQLStringValue(annotation_set)));
		} catch (Exception e) {
			logger.error("Error deleting annotations " + pmid, e);
			return;
		}
	}
	
	public static void deleteRecord(String pmid, List<String> annotation_sets, int text_location) {
		String set_str = "";
		for (String ann_set : annotation_sets) {
			set_str += (BasicUtils.EscapeSQLStringValue(ann_set) + ",");
		}
		set_str = set_str.substring(0, set_str.length()-1);
		try {
			DocDBConnection.closeRsStatement(DocDBConnection.executeQuery("delete from " + tableName + 
					" where pmid=" + pmid + " and text_location=" + text_location + " and " +
					"annotation_set in (" + set_str + ")"));
		} catch (Exception e) {
			logger.error("Error deleting annotations " + pmid, e);
			return;
		}

	}

	
	public static void insertRecord(String pmid, int text_location,
			String annotation_set, gate.Annotation annotation) {
		insertRecord(pmid, text_location, annotation.getType(), annotation_set, annotation.getStartNode().getOffset(),
				annotation.getEndNode().getOffset(), annotation.getFeatures().toString());
	}
	
	public static List<AnnotationRecord> getAnnotations(long pmid) {
		ResultSet rs = DocDBConnection.executeQuery("select * from " + tableName + " where pmid="+Long.toString(pmid));
		List<AnnotationRecord> return_list = new ArrayList<AnnotationRecord>();
		AnnotationRecord a_record;
		String annotation_str = "";
		try {
			if (rs == null) return return_list;
			while (rs.next()) {
				a_record = new AnnotationRecord();
				a_record.PMID = pmid;
				a_record.text_location = rs.getInt("TEXT_LOCATION");
				a_record.annotation_type = rs.getString("ANNOTATION_TYPE");
				a_record.annotation_set = rs.getString("ANNOTATION_SET");
				a_record.text_start = rs.getInt("TEXT_START");
				a_record.text_end = rs.getInt("TEXT_END");
				annotation_str = rs.getString("FEATURES");
				a_record.setFeatures(annotation_str);
				return_list.add(a_record);
			};
			DocDBConnection.closeRsStatement(rs);
			return return_list;
		} catch (Exception e) {
			logger.error("Error in processing annotations [pmid:" + Long.toString(pmid) + "] [" + annotation_str + "] " + e.getMessage() + "at" + BasicUtils.strExceptionStackTrace(e));
			return return_list;
		}
	}
	
	
	public static List<AnnotationRecord> getAnnotationsFromResult(Result result) {
		List<AnnotationRecord> return_list = new ArrayList<AnnotationRecord>();
		try {
			NavigableMap<byte[], byte[]> values = result.getFamilyMap(Map.colFamily);
			java.util.Map.Entry<byte[], byte[]> curEntry = values.firstEntry();
			while (curEntry != null) {
				byte[] key = (byte[]) curEntry.getKey();
				byte[] value = (byte[]) curEntry.getValue();
				if (value != null && !Bytes.toString(key).equals("c")) {
					String annotations = new String(value);
					if (annotations.length() > 0) {
						String[] annotationArray = annotations.split("\\|");
						for (String annotation : annotationArray) {
							if (annotation != null && annotation.length() > 0) {
								AnnotationRecord a_record;
								String[] annFields = annotation.split("\\t");
								a_record = new AnnotationRecord();
							
								try {
									if (annFields.length == 5) {
										a_record.text_location = Integer.parseInt(annFields[0]);
										a_record.annotation_set = annFields[1];
										a_record.text_start = Integer.parseInt(annFields[2]);
										a_record.text_end = Integer.parseInt(annFields[3]);
										a_record.setFeatures(annFields[4]);
									} else if (annFields.length == 6) {
										a_record.text_location = Integer.parseInt(annFields[0]);
										a_record.annotation_type = annFields[1];
										a_record.annotation_set = annFields[2];
										a_record.text_start = Integer.parseInt(annFields[3]);
										a_record.text_end = Integer.parseInt(annFields[4]);
										a_record.setFeatures(annFields[5]);
									}
									return_list.add(a_record);
								} catch (Exception e) {
									System.err.println(annotations);
									System.err.println(annotation);
									e.printStackTrace();
								}
							}
						}
					}
				}
				curEntry = values.higherEntry(key);
			}
			return return_list;
		} catch (Exception e) {
			logger.error("Error in processing annotations " + e.getMessage() + "at" + BasicUtils.strExceptionStackTrace(e));
			return return_list;
		}
	}
	
	public static List<String> getLinksFromResult(Result result) {
		List<String> returnList = new ArrayList<String>();
		try {
			NavigableMap<byte[], byte[]> values = result.getFamilyMap(Map.linkColFamily);
			java.util.Map.Entry<byte[], byte[]> curEntry = values.firstEntry();
			while (curEntry != null) {
				byte[] key = (byte[]) curEntry.getKey();
				byte[] value = (byte[]) curEntry.getValue();
				if (value != null) {
					String links = new String(value);
					links = links.substring(1, links.length()-1);
					if (links.length() > 0) {
						String[] linkArray = links.split(",");
						for (String link : linkArray) {
							returnList.add(link.substring(1,link.length()-1));
						}
					}
				}
				curEntry = values.higherEntry(key);
			}
			return returnList;
		} catch (Exception e) {
			logger.error("Error in processing links " + e.getMessage() + "at" + BasicUtils.strExceptionStackTrace(e));
			return null;
		}
	}
	
	public static List<AnnotationRecord> getAllAnnotations(long pmid) {
		tableName = "annotations_gene";
		List<AnnotationRecord> return_list = getAnnotations(pmid);
		tableName = "annotations_organismtagger";
		return_list.addAll(getAnnotations(pmid));
		tableName = "annotations_other_onto";
		return_list.addAll(getAnnotations(pmid));
		return return_list;
	}
	
	public static List<AnnotationRecord> getAllAnnotationsForNcbiLog(long pmid) {
		ResultSet rs = DocDBConnection.executeQuery("select * from annotations_ncbi where pmid="+Long.toString(pmid));
		List<AnnotationRecord> return_list = new ArrayList<AnnotationRecord>();
		AnnotationRecord a_record;
		String annotation_str = "";
		try {
			if (rs == null) return return_list;
			while (rs.next()) {
				a_record = new AnnotationRecord();
				a_record.PMID = pmid;
				a_record.text_location = rs.getInt("TEXT_LOCATION");
				a_record.annotation_type = rs.getString("ANNOTATION_TYPE");
				a_record.annotation_set = rs.getString("ANNOTATION_SET");
				a_record.text_start = rs.getInt("TEXT_START");
				a_record.text_end = rs.getInt("TEXT_END");
				annotation_str = rs.getString("FEATURES");
				a_record.setFeatures(annotation_str);
				return_list.add(a_record);
			};
			DocDBConnection.closeRsStatement(rs);
			return return_list;
		} catch (Exception e) {
			logger.error("Error in processing annotations [pmid:" + Long.toString(pmid) + "] [" + annotation_str + "] " + e.getMessage() + "at" + BasicUtils.strExceptionStackTrace(e));
			return return_list;
		}
	}
	
	public static void main(String args[]) {
		List<AnnotationRecord> a_list = AnnotationDAO.getAnnotations(21338450);
		Iterator<AnnotationRecord> it = a_list.iterator();
		while (it.hasNext()) {
			AnnotationRecord a_record = (AnnotationRecord) it.next();
			System.out.println("features: " + a_record.features);
		}
	}
}
