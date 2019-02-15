package edu.mcw.rgd.nlp.utils.metamap;

import java.sql.ResultSet;

//import PatientDocDB.DocDBConnection;

public class Phrase {
/*
	String xml_text;
	String PText;
	int PPos;
	int PLen;
	int cands;
	
	
	public Phrase() {
		xml_text = new String();
		PText = new String();
	}
	
	public void ProcessXML(String xml_str) {
		xml_text = xml_str;
		PText = MetaMap.XMLtag(xml_text, "PText");
		PPos = Integer.parseInt(MetaMap.XMLtag(xml_text, "PStartPos"));
		PLen = Integer.parseInt(MetaMap.XMLtag(xml_text, "PSpanLen"));
		cands = Integer.parseInt(MetaMap.getInBetween(xml_text,
				"<Candidates Count=\"", "\""));
//		mappings = Integer.parseInt(MetaMap.getInBetween(xml_text,
//				"<Mappings Count=\"", "\""));
	}
	
	public String getPText() {
		return PText;
	}
	public static void main(String[] args) {

		String SQL_str = new String();
		String con_str = new String();
		String con_prefer_str = new String();
		String opts = "I --XML format";
		try {
			int m_pid;
			int c_pid;
			SQL_str = "select max(pha_id) from au_phr;";
			ResultSet max_pid = DocDBConnection.executeQuery(SQL_str);
			max_pid.next();
			m_pid = Integer.parseInt(max_pid.getString(1));
			SQL_str = "select max(pha_id) from au_con;";
			ResultSet cur_pid = DocDBConnection.executeQuery(SQL_str);
			cur_pid.next();
			c_pid = cur_pid.getInt(1);
			c_pid ++;
			while (c_pid <= m_pid) {
				SQL_str = "select pha_text, cands from au_phr where pha_id=\""
				+ c_pid + "\";";
				ResultSet phrs = DocDBConnection
						.executeQuery(SQL_str);
				MetaMap mm = new MetaMap();
				while (phrs.next()) {
					String pha_text = phrs.getString(1);
					int cands = phrs.getInt(2);
					if (cands > 0) {
					
					mm.Process(pha_text, opts);
					// System.out.println("XML string:\r" + all);
					mm.getConceptCount();
					System.out.println("Total concepts: " + mm.concept_count);
					mm.getConceptXML();
					for (int i = 0; i < mm.concept_count; i++) {
						con_str = MetaMap.EnQ(mm.concepts[i].UMLS_concept);
						con_prefer_str = MetaMap.EnQ(mm.concepts[i].UMLS_preferred);
				     SQL_str = "insert into au_con set con_no=" + (i+1)
				     + ", pha_id=" + c_pid
				     + ", CUI=\"" + MetaMap.EnQ(mm.concepts[i].CUI) + "\""
				     + ", ISHEAD=" + (mm.concepts[i].IsHead ? "true" : "false")
				     + ", ISOVERMATCH=" + (mm.concepts[i].IsOverMatch ? "true" : "false")
				     + ", con_pos=" + mm.concepts[i].PPos 
				     + ", con_length=" + mm.concepts[i].PLen
//				     + ", mappings=" + mm.phrases[i].mappings
				     + ", score=" + mm.concepts[i].Score
				     + ", umls_concept=\"" 
				     + con_str + "\""
				     + ", sem_type=\""
				     + mm.concepts[i].SEM_TYPE + "\""
				     + ", umls_preferred=\"" 
				     + con_prefer_str + "\";";
					 System.out.println("SQL:" + SQL_str);
					 DocDBConnection
						.executeQuery(SQL_str);
					 }
					}
				}
				c_pid ++;
			}
			System.out.println("Done!!!");
		} catch (Exception e) {
			System.err.println(e);
		}

	}
	*/
}
