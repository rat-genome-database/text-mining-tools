package edu.mcw.rgd.nlp.utils.metamap;

import java.sql.*;
//import PatientDocDB.DocDBConnection;

public class Utterance {
	/*
	String[] phraseCount_str = { "<Phrases Count=\"", "\">" };

	String[] phrase_str = { "<Phrase>", "</Phrase>" };

	Phrase[] phrases;

		String xml_text;
		String UText;
		int UPos;
		int ULen;
		
		public Utterance() {
			xml_text = new String();
			UText = new String();
		}
		
		public void ProcessXML(String xml_str) {
			xml_text = xml_str;
			UText = MetaMap.XMLtag(xml_text, "UText");
			UPos = Integer.parseInt(MetaMap.XMLtag(xml_text, "UStartPos"));
			ULen = Integer.parseInt(MetaMap.XMLtag(xml_text, "USpanLen"));
		}
		
		public String getUText() {
			return UText;
		}
		
		public static void main(String[] args) {

			String SQL_str = new String();
			String pha_str = new String();
			String opts = "I --XML format";
			try {
				int m_uid;
				int c_uid;
				SQL_str = "select max(utt_id) from au_utt;";
				ResultSet max_uid = DocDBConnection.executeQuery(SQL_str);
				max_uid.next();
				m_uid = Integer.parseInt(max_uid.getString(1));
				SQL_str = "select max(utt_id) from au_phr;";
				ResultSet cur_uid = DocDBConnection.executeQuery(SQL_str);
				cur_uid.next();
				c_uid = cur_uid.getInt(1);
				c_uid ++;
				while (c_uid <= m_uid) {
					SQL_str = "select sentence_text from sentence_utt where utt_id=\""
					+ c_uid + "\";";
					ResultSet utts = DocDBConnection
							.executeQuery(SQL_str);
					MetaMap mm = new MetaMap();
					while (utts.next()) {
						String utt_text = utts.getString(1);
						mm.Process(utt_text, opts);
						// System.out.println("XML string:\r" + all);
						mm.getPhraseCount();
						System.out.println("Total phrases: " + mm.phrase_count);
						mm.getPhraseXML();
						for (int i = 0; i < mm.phrase_count; i++) {
							pha_str = MetaMap.EnQ(mm.phrases[i].PText);
					     SQL_str = "insert into phrases set phrase_no=" + (i+1)
					     + ", sentence_id=" + c_uid
					     + ", phrase_pos=" + mm.phrases[i].PPos 
					     + ", phrase_len=" + mm.phrases[i].PLen
//					     + ", mappings=" + mm.phrases[i].mappings
					     + ", candidates=" + mm.phrases[i].cands
					     + ", phrase_text=\"" 
					     + pha_str + "\";";
						 System.out.println("SQL:" + SQL_str);
						 DocDBConnection
							.executeQuery(SQL_str);
						 }
					}
					c_uid ++;
				}
				System.out.println("Done!!!");
			} catch (Exception e) {
				System.err.println(e);
			}

		}
*/		
}
