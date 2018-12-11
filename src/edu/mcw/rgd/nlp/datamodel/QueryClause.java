package edu.mcw.rgd.nlp.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryClause {
	
	public static String NOT_STR = "NOT ";
	private String clause;
	private String term;
	private boolean isNeg;
	private String field;
	private String value;

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * @return the isNeg
	 */
	public boolean isNeg() {
		return isNeg;
	}

	/**
	 * @param isNeg the isNeg to set
	 */
	public void setNeg(boolean isNeg) {
		this.isNeg = isNeg;
	}

	/**
	 * @return the clause
	 */
	public String getClause() {
		return clause;
	}

	/**
	 * @param clause the clause to set
	 */
	public void setClause(String clause) {
		this.clause = clause.trim();
		analyzeClause();
	}
	
	public QueryClause(String clause) {
		setClause(clause);
	}
	
	private void analyzeClause() {
		
		if (clause.startsWith(NOT_STR)) {
			setNeg(true);
			setTerm(clause.substring(NOT_STR.length()).trim());
		} else {
			setTerm(clause);
		}
		analyzeTerm();
	}
	
	private void analyzeTerm() {
		if (term.endsWith("]") && term.lastIndexOf('[')>0) {
			field = term.substring(term.lastIndexOf('[')+1, term.length()-1).trim();
			value = term.substring(0, term.lastIndexOf('['));
		} else
		{
			value = term;
		}
	}
	
	public static void main(String args[]) {
		QueryClause qc = new QueryClause("NOT humans[MH]");
		System.out.println("term: "+qc.getTerm());
		
		String subjectString = "(a +d) OR B AND C";
		subjectString = subjectString.replaceAll("\\(|\\)", "");
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("(.*)(AND|OR)(.*)");
		Matcher regexMatcher = regex.matcher(subjectString);
		while (regexMatcher.find()) {
		    if (regexMatcher.group(1) != null) {
		        // Add double-quoted string without the quotes
//		        matchList.add(regexMatcher.group(1));
		    } 
		    if (regexMatcher.group(3) != null) {
		        // Add single-quoted string without the quotes
		        matchList.add(regexMatcher.group(3));
				subjectString = subjectString.substring(0,  regexMatcher.start(2));
				regexMatcher = regex.matcher(subjectString);
		    } 
//		        matchList.add(regexMatcher.group());
		} 
		matchList.add(subjectString);
		for (String m : matchList) {
			System.out.println(m.trim());
		}
	}
}
