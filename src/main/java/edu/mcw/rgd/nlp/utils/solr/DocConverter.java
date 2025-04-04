package edu.mcw.rgd.nlp.utils.solr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.text.StrTokenizer;

public class DocConverter {
	
	
public static void convertOntoTsv(String[] args) {

	if (args.length != 2) {
		System.out.println("Two parameters are required: input_file output_file");
		return ;
	}
	System.out.println("Start converting file:" + args[0]);
		File inFile = new File(args[0]);
		File outFile = new File(args[1]);
		Long lineCount = new Long(0);
		Long termCount = new Long(0);
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(outFile));
			bw.write("<add>");
			String line;
			br.readLine();
			String lastId = "";
			XmlDoc doc = null, docCTD = null;
		while ((line = br.readLine()) != null && line.length() > 0)	{
			OntoInFileRow ontoRow = new DocConverter.OntoInFileRow(line);
			lineCount ++;
			if (!ontoRow.ontoId.equals(lastId)) {
				termCount ++;
				lastId = ontoRow.ontoId;
				if (doc != null)
				{
					bw.write(doc.toString());
//					if (docCTD != null) {
//						bw.write(docCTD.toString());
//						docCTD = null;
//					}
				}
				doc = new XmlDoc();
				doc.add("id", ontoRow.ontoId);
				doc.add("id_s", ontoRow.ontoId_s);
				doc.add("idl_s", ontoRow.ontoId_lower);
				doc.add("id_l", ontoRow.ontoId_l);
				doc.add("term", StringEscapeUtils.escapeHtml(ontoRow.ontoTerm));
				doc.add("cat", ontoRow.ontoCat);
				if (!ontoRow.ontoDef.equals("(null)")) doc.add("def", StringEscapeUtils.escapeHtml(ontoRow.ontoDef));
				if (!ontoRow.ontoAncTerms.equals("(null)")) doc.add("anc", StringEscapeUtils.escapeHtml(ontoRow.ontoAncTerms));
				StrTokenizer tokenizer = new StrTokenizer(ontoRow.ontoTerm);
				doc.add("term_len_l", Long.toString(1000-tokenizer.getTokenArray().length*5));
				//Add species if it's a human gene
				if (ontoRow.ontoId.startsWith("RGD_GENE:"))
					doc.add("species_s", ontoRow.species);
				
//				if (ontoRow.ontoId.startsWith("RDO:")) {
//					docCTD = new XmlDoc();
//					docCTD.add("id", ontoRow.ontoId.replace("RDO:", "CTD:"));
//					docCTD.add("id_s", ontoRow.ontoId_s.replace("RDO:", "CTD:"));
//					docCTD.add("idl_s", ontoRow.ontoId_lower.replace("rdo:", "ctd:"));
//					docCTD.add("id_l", ontoRow.ontoId_l.replace("RDO:", "CTD:"));
//					docCTD.add("term", ontoRow.ontoTerm);
//					docCTD.add("cat", "CTD");
//					docCTD.add("def", ontoRow.ontoDef);
//					docCTD.add("anc", ontoRow.ontoAncTerms);
//					docCTD.add("term_len_l", Long.toString(1000-ontoRow.ontoTerm.length()));
//				}; 
			}
			if (!ontoRow.ontoSynonym.equals("(null)"))  {
				doc.add("synonym", StringEscapeUtils.escapeHtml(ontoRow.ontoSynonym));
//				if (docCTD != null) docCTD.add("synonym", ontoRow.ontoSynonym);
			}
		}
		bw.write(doc.toString());
//		if (docCTD != null) bw.write(docCTD.toString());
		bw.write("</add>");
		bw.flush();
		bw.close();
		br.close();
		System.out.println("Finished converting file:" + args[0]);
		System.out.println("\r\nTotal lines processed: " + lineCount + "\r\nTotal terms processed: " + termCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Finished converting file:" + args[0]);
			System.out.println("\r\nTotal lines processed: " + lineCount + "\r\nTotal terms processed: " + termCount);
			e.printStackTrace();
		}
	}
	
	static class OntoInFileRow {
		public OntoInFileRow(String fileRow) {
			
			String[] fieldValues = fileRow.replaceAll("\\x16", "").split("\t");
			ontoId = fieldValues[0];
			ontoId_s = ontoId.substring(ontoId.indexOf(":")+1);
			ontoId_lower = ontoId.toLowerCase();
			try {
				Long id_l = Long.parseLong(ontoId_s);
				ontoId_l = id_l.toString();
			} catch( NumberFormatException ignore ) {
			}
			ontoCat = fieldValues[1];
			ontoTerm = fieldValues[2];
			ontoDef = fieldValues[3];
			ontoAncTerms = fieldValues[4];
			ontoSynonym = fieldValues[5];
			if (ontoId.startsWith("RGD_GENE:")) {
				species = fieldValues[6];
			}
		}
		
		String ontoId;
		String ontoId_s;
		String ontoId_l;
		String ontoId_lower;
		String ontoCat;
		String ontoTerm;
		String ontoSynonym;
		String ontoDef;
		String ontoAncTerms;
		String species;
	}
	
	public static void main(String[] args) {
		convertOntoTsv(args);
	}
}
