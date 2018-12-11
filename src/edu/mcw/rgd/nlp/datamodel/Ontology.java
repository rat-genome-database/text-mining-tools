package edu.mcw.rgd.nlp.datamodel;

import java.util.HashMap;
import java.util.HashSet;

public class Ontology {
	
	static private HashSet<String> rgdOntologies = null;
	static private HashMap<String, SolrOntologyEntry> solrOntoFields = null; 
	
	static public HashSet<String> getRgdOntologies() {
		if (rgdOntologies == null) {
			rgdOntologies = new HashSet<String>();
			rgdOntologies.add("BP");
			rgdOntologies.add("CC");
			rgdOntologies.add("CL");
			rgdOntologies.add("CMO");
			rgdOntologies.add("HP");
			rgdOntologies.add("MA");
			rgdOntologies.add("MF");
			rgdOntologies.add("MMO");
			rgdOntologies.add("MP");
			rgdOntologies.add("NBO");
			rgdOntologies.add("PW");
			rgdOntologies.add("SO");
			rgdOntologies.add("XCO");
			rgdOntologies.add("RS");
//			rgdOntologies.add("CTD");  replaced with RDO
			rgdOntologies.add("RDO");
			rgdOntologies.add("VT");
			rgdOntologies.add("CHEBI");
			rgdOntologies.add("ZFA");   // recently added for zebrafish ----------------
			rgdOntologies.add("MT");  // For Mutation Type
//			rgdOntologies.add("SNP");  // For SNPs
		}
		
		return rgdOntologies;
	}
	
	static public HashMap<String, SolrOntologyEntry> getSolrOntoFields() {
		if (solrOntoFields == null) {
			solrOntoFields = new HashMap<String, SolrOntologyEntry>();
			for (String onto_name : rgdOntologies) {
				String onto_name_lc = onto_name.toLowerCase();
				solrOntoFields.put(onto_name, new SolrOntologyEntry(onto_name_lc + "_id",
						onto_name_lc + "_term", onto_name_lc + "_count", onto_name_lc + "_pos"));
			}
		}
		
		return solrOntoFields;
	}
}
