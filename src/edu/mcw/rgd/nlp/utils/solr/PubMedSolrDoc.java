package edu.mcw.rgd.nlp.utils.solr;

import org.apache.solr.common.SolrInputDocument;

public class PubMedSolrDoc extends SolrInputDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addField(String fieldName, Object value) {
		if (value != null) super.addField(fieldName, value);
	}

}
