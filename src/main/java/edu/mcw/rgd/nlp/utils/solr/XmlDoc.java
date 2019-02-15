package edu.mcw.rgd.nlp.utils.solr;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;

public class XmlDoc {
	public List<String> fields = new ArrayList<String>();
	public List<String> values = new ArrayList<String>();
	
	public void add(String field_name, String value) {
		fields.add(StringEscapeUtils.escapeXml(field_name));
		values.add(StringEscapeUtils.escapeXml(value));
	}
	
	public String getFieldXML(String field, String value) {
		return "<field name=\"" + field +
		"\">" + value + "</field>";
	}
	
	public String toString() {
		String return_str = "<doc>";
		Iterator<String> field_it = fields.iterator();
		Iterator<String> value_it = values.iterator();
		while (field_it.hasNext()) {
			return_str += getFieldXML(field_it.next(),
			value_it.next());
		}
		return_str += "</doc>";
		return return_str;
	}

	public static void main(String[] args) {
		XmlDoc doc = new XmlDoc();
		doc.add("title", "test title");
		doc.add("abstract", "abstract tested");
		doc.add("rgd_id", "rgd:223433");
		doc.add("rgd_id", "rgd:334223");
		System.out.println(doc.toString());
	}
}
