package edu.mcw.rgd.nlp.utils.ncbi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {

	public static String cleanQuery(String query) {
//		 Pattern p = Pattern.compile("(.* )OR .*|(.* )AND .*|(.*)NOT .*");
//		 Pattern p = Pattern.compile("(.*)NOT .*");
				String cleaned_query = "";
		while (query.length()>0 && query.contains("NOT ")) {
			int not_start = query.indexOf("NOT ");
			if (not_start >0) {
				cleaned_query += " " + query.substring(0, not_start-1);
			}
			query = query.substring(not_start+4).trim();
			int ind;
			if (query.startsWith("\"")) {
				ind = query.indexOf("\"", 1);
				query = query.substring(ind + 1);
			}
			ind = query.indexOf(' ');
			if (ind > 0) {
				 query = query.substring(ind+1);
			} else {
				ind = query.indexOf(')');
				if (ind > 0) {
					 query = query.substring(ind+1);
				} else {
					ind = query.indexOf("NOT ");
					if (ind > 0) {
						query = query.substring(ind);
					} else {
						ind = query.indexOf("AND ");
						if (ind > 0) {
							query = query.substring(ind);
						} else {
							ind = query.indexOf("OR ");
							if (ind > 0) {
								query = query.substring(ind);
							} else {
								query = "";
							}
						}
					}
				}
			}
		}
		cleaned_query += " " + query;
		return cleaned_query;
	}
	
	public static void main(String[] args) {
		String test_str;
		test_str = "adf NOT DFKDJF[DFDF] NOT B AND CCC[au] NOT DDF OR DDFF";
		test_str = QueryParser.cleanQuery(test_str);
		test_str = test_str.replaceAll("\\[.*\\]", "");
//		test_str = "((KCNA2) NOT \"Cricetulus griseus\"[porgn:__txid10029]";
		System.out.println(test_str);
	}
	
}
