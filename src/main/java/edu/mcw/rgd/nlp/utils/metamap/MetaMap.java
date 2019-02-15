package edu.mcw.rgd.nlp.utils.metamap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.*;
//import PatientDocDB.DocDBConnection;
import java.sql.*;
import java.io.*;

public class MetaMap {
	/*

	String[] UTTCount_str = { "<Utterances Count=\"", "\">" };

	String[] UTT_str = { "<Utterance>", "</Utterance>" };

	String[] PhraseCount_str = { "<Phrases Count=\"", "\">" };

	String[] phrase_str = { "<Phrase>", "</Phrase>" };

	String[] ConceptCount_str = { "<Candidates Count=\"", "\">" };

	String[] concept_str = { "<Candidate>", "</Candidate>" };

	String input_fn = "input.txt";

	String output_fn = "output.xml";

	String XML_all;

	Utterance[] UTTs;
	
	Phrase[] phrases;
	
	Concept[] concepts;

	int UTTCount = 0;
	
	int phrase_count = 0;

	int concept_count = 0;

	public MetaMap() {
	}

	public static BufferedReader Shell_Run(String cmd) {
		String[] Command = { "sh", "-c", cmd};
//		String[] Command = { "metamap08", "--no_header_info -tdIcm --XML format input.txt output.xml" };
		System.out.println("Running command:" + cmd);
		BufferedReader buf = null;
		try {
			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(Command);
			pr.waitFor();
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			System.out.println(buf.readLine());
			System.out.println(buf.readLine());
//			buf.readLine();
//			buf.readLine();
		} catch (Exception e) {
			System.err.print("Read from program failed!");
		}
		return buf;
	}

	public static String EnQ(String input) {
		return input.replace("\"", "\\\"");
	}

	public void Process(String input) {
//		String opts = "tdIcm --XML format";
		String opts = "tdIcm --XML format";
		Process(input, opts);
		return;
	}

	public void Process(String input, String opts) {
		// String arg = "echo \"" + MetaMap.EnQ(input) + "\" | metamap
		// --no_header_info ";
		String arg = "/home/wliu/UMLS/public_mm/bin/metamap08 --no_header_info";
		if (opts.length() > 0)
			arg = arg + " -" + opts;
		arg = arg + " " + input_fn + " " + output_fn;

		try {
			FileWriter fw = new FileWriter(input_fn);
//			fw.write(" ");
			input = input.replace(" - ", " ; ");
			input = input.replaceAll("[^\\p{ASCII}]", " ");
			fw.write(input);
			fw.write(System.getProperty("line.separator"));
			fw.close();
			MetaMap.Shell_Run(arg);
			getStr();
			return;
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
	}

	public void getStr() {
		FileReader fr;
		try {
			fr = new FileReader(output_fn);
		} catch (Exception e) {
			System.err.println("Erro openting output:" + e);
			return;
		}
		BufferedReader buf = new BufferedReader(fr);

		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		String line;
		try {
			while ((line = buf.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			fr.close();
			XML_all = stringBuilder.toString();
		} catch (Exception e) {
			System.err.println(e);
		}
		return;
	}

	public int getUTTCount() {
		String str_tmp = getInBetween(XML_all, UTTCount_str[0],
				UTTCount_str[1]);
		if (str_tmp.length()>0) {
			UTTCount = Integer.parseInt(str_tmp);
		} else UTTCount = 0;
		return UTTCount;
	}

	public int getPhraseCount() {
		String str_tmp = getInBetween(XML_all, PhraseCount_str[0],
				PhraseCount_str[1]);
		if (str_tmp.length()>0) {
			phrase_count = Integer.parseInt(str_tmp);
		} else phrase_count = 0;
		return phrase_count;
	}

	public int getConceptCount() {
		String str_tmp = getInBetween(XML_all, ConceptCount_str[0],
				ConceptCount_str[1]);
		if (str_tmp.length()>0) {
			concept_count = Integer.parseInt(str_tmp);
		} else concept_count = 0;
		return concept_count;
	}

	public void getUTTXML() {
		UTTs = new Utterance[UTTCount];
		String[] strs = getInBetweenN(XML_all, UTT_str[0], UTT_str[1],
				UTTCount);
		for (int i = 0; i < UTTCount; i++) {
			UTTs[i] = new Utterance();
			UTTs[i].ProcessXML(strs[i]);
		}
	}

	public void getPhraseXML() {
		phrases = new Phrase[phrase_count];
		String[] strs = getInBetweenN(XML_all, phrase_str[0], phrase_str[1],
				phrase_count);
		for (int i = 0; i < phrase_count; i++) {
			phrases[i] = new Phrase();
			phrases[i].ProcessXML(strs[i]);
		}
	}

	public void getConceptXML() {
		concepts = new Concept[concept_count];
		String[] strs = getInBetweenN(XML_all, concept_str[0], concept_str[1],
				concept_count);
		for (int i = 0; i < concept_count; i++) {
			concepts[i] = new Concept();
			concepts[i].ProcessXML(strs[i]);
		}
	}

	public static String getInBetween(String input, String start, String end) {
		try {
			String mat_str = start + ".+?" + end;
			// System.out.println("Matching for " + mat_str);
			Pattern pat = Pattern.compile(mat_str, Pattern.MULTILINE);
			Matcher mat = pat.matcher(input);
			if (mat.find()) {
				String ret = mat.group();
				return ret.substring(start.length(), ret.length() - end.length());
			}
			return "";
			// System.out.println("Matched: " + mat.group());
		} catch (PatternSyntaxException e) {
			System.err.println(e);
			return "";
		}
	}

	public static String XMLtag(String input, String tag_name) {
		return getInBetween(input, "<" + tag_name + ">", "</" + tag_name + ">");
	}

	public static String[] getInBetweenN(String input, String start,
			String end, int n) {
		try {
			String[] strs = new String[n];
			String mat_str = start + ".+?" + end;
			// System.out.println("Matching for " + mat_str);
			Pattern pat = Pattern.compile(mat_str, Pattern.DOTALL
					| Pattern.MULTILINE);
			Matcher mat = pat.matcher(input);
			for (int i = 0; i < n; i++) {
				mat.find();
				// System.out.println("Matched: " + mat.group());
				String ret = mat.group();
				strs[i] = ret.substring(start.length(), ret.length()
						- end.length());
				// System.out.println("Matched: " + strs[i]);
			}
			;
			return strs;
		} catch (PatternSyntaxException e) {
			System.err.println(e);
			return null;
		}
	}

	public static void main(String[] args) {
		String SQL_str = new String();
		String utt_str = new String();
		try {
			int m_pid;
			int c_pid;
			SQL_str = "select max(par_id) from au_par;";
			ResultSet max_pid = DocDBConnection.executeQuery(SQL_str);
			max_pid.next();
			m_pid = Integer.parseInt(max_pid.getString(1));
			SQL_str = "select max(par_id) from au_utt;";
			ResultSet cur_pid = DocDBConnection.executeQuery(SQL_str);
			cur_pid.next();
			c_pid = cur_pid.getInt(1);
			c_pid ++;
			while (c_pid <= m_pid) {
				SQL_str = "select par_text from au_par where par_id=\""
				+ c_pid + "\";";
				ResultSet utts = DocDBConnection
						.executeQuery(SQL_str);
				MetaMap mm = new MetaMap();
				while (utts.next()) {
					String utt_text = utts.getString(1);
					utt_text = utt_text.replaceAll("\n", ". ");
					mm.Process(utt_text);
					// System.out.println("XML string:\r" + all);
					mm.getUTTCount();
					System.out.println("Total utterances: " + mm.UTTCount);
					mm.getUTTXML();
					for (int i = 0; i < mm.UTTCount; i++) {
						utt_str = MetaMap.EnQ(mm.UTTs[i].UText);
				     SQL_str = "insert into au_utt set utt_no=" + (i+1)
				     + ", par_id=" + c_pid
				     + ", utt_pos=" + mm.UTTs[i].UPos 
				     + ", utt_len=" + mm.UTTs[i].ULen
				     + ", utt_text=\"" 
				     + utt_str + "\";";
					 System.out.println("SQL:" + SQL_str);
					 DocDBConnection
						.executeQuery(SQL_str);
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

