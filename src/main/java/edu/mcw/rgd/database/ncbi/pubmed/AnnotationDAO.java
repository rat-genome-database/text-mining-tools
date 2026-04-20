package edu.mcw.rgd.database.ncbi.pubmed;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

import edu.mcw.rgd.nlp.utils.LibraryBase;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;



import edu.mcw.rgd.common.utils.BasicUtils;

import edu.mcw.rgd.nlp.utils.ncbi.DistributedAnnotator.Map;
import org.apache.log4j.Logger;

public class AnnotationDAO {
    protected static final Logger logger = Logger.getLogger(LibraryBase.class);
	public static List<AnnotationRecord> getAnnotationsFromResult(Result result) {
		List<AnnotationRecord> return_list = new ArrayList<>();
		try {
			NavigableMap<byte[], byte[]> values = result.getFamilyMap(Map.colFamily);
			java.util.Map.Entry<byte[], byte[]> curEntry = values.firstEntry();
			while (curEntry != null) {
				byte[] key =  curEntry.getKey();
				byte[] value =  curEntry.getValue();
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
		List<String> returnList = new ArrayList<>();
		try {
			NavigableMap<byte[], byte[]> values = result.getFamilyMap(Map.linkColFamily);
			java.util.Map.Entry<byte[], byte[]> curEntry = values.firstEntry();
			while (curEntry != null) {
				byte[] key =  curEntry.getKey();
				byte[] value =  curEntry.getValue();
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

	public static void main(String args[]) throws Exception {

	}
}
