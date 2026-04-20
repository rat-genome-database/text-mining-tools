package edu.mcw.rgd.database.ncbi.pubmed;

import java.util.HashMap;

public class AnnotationRecord {
	public int text_location;
	public String annotation_type;
	public String annotation_set;
	public int text_start;
	public int text_end;
	public String features;
	public HashMap features_table; 
	
	public void setFeatures(String value) {
		value = value.substring(1, value.length() - 1);
		features = value;
		if (features_table == null) {
			features_table = new HashMap();
		} else {
			features_table.clear();
		}
		String[] feature_array = features.split(", ");
		for (int i = 0; i < feature_array.length; i++) {
			String feature_str = feature_array[i];
			if (i < feature_array.length - 1) {
				if (feature_array[i+1].length() == 0) {
					feature_str += ",";
					i ++;
				}
			}
			int marker = feature_str.indexOf('=');
			if (marker > 0) {
				String feature_key = feature_str.substring(0, marker).trim();
				String feature_value = feature_str.substring(marker + 1, feature_str.length()).trim();
				features_table.put(feature_key, feature_value);
			}
		}
	}
	
	public static void main(String[] args) {
		AnnotationRecord a_record = new AnnotationRecord();
		a_record.setFeatures("{source=openNLP, string=,, category=,, stem=,}");
	}
}
