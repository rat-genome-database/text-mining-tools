
package org.tartarus.snowball;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.tartarus.snowball.ext.englishStemmer;

public class Stemmer {

    private static SnowballStemmer stemmer = (SnowballStemmer) new englishStemmer();
    public static void main(String [] args) throws Throwable {

	String inputStr = "We retrospectively evaluated the expression and prognostic value of CD44 isoforms in transitional cell carcinoma of the renal pelvis and ureter.\n" +
"MATERIALS AND METHODS:" + 
"Using a labeled streptavidin-biotin-peroxidase method we performed immunohistochemical staining of archival tissue specimens from 62 men and 26 women with a mean age of 66.5 years who had transitional cell carcinoma of the renal pelvis and ureter." +
"RESULTS:" +
"The expression of CD44 standard (CD44s) and splice variant 6 (CD44v6) proteins was significantly decreased in relation to histological grade and pathological stage, while expression of the CD44 splice variant 3 (CD44v3) protein was decreased in relation to histological grade. The prognosis was more favorable in patients with greater versus less than 25% CD44s positive cells. CD44v3 and CD44v6 proteins had no prognostic value. Multivariate analysis indicated that only pathological stage has an independent prognostic value." +
"CONCLUSIONS:" +
"The expression of CD44 isoforms is closely associated with tumor differentiation and progression. However, it is not an independent marker and has no statistical significance greater than that of stage.";
	System.out.println(inputStr);
	System.out.println(stem(inputStr));
    }

    
    public static String stem(String inputStr) {
    	return stem(inputStr, true);
    }
    
    public static String stem(String inputStr, boolean withPadding) {
    	try {
    		Reader reader;
        	reader = new InputStreamReader(new StringInputStream(inputStr));
        	reader = new BufferedReader(reader);

        	StringBuffer input = new StringBuffer();
            StringBuffer output = new StringBuffer();

        	int character;
        	while ((character = reader.read()) != -1) {
        	    char ch = (char) character;
        	    if (stemable(ch)) {
        			input.append((ch));
        	    } else {
            		if (input.length() > 0) {
            		    stemmer.setCurrent(input.toString());
            			stemmer.stem();
            			if (withPadding) {
                		    output.append(StringUtils.rightPad(stemmer.getCurrent(), input.length()) + ch);
            			} else {
                		    output.append(stemmer.getCurrent() + ch);
            			}
            		    input.delete(0, input.length());
            		} else {
            			output.append(ch);
            		}
        	    }
        	}
    		if (input.length() > 0) {
    		    stemmer.setCurrent(input.toString());
    			stemmer.stem();
    			if (withPadding) {
        		    output.append(StringUtils.rightPad(stemmer.getCurrent(), input.length()));
    			} else {
        		    output.append(stemmer.getCurrent());
    			}
    		    input.delete(0, input.length());
    		};
        	return output.toString();
    	} catch (Exception e)
    	{
    		return "";
    	}
    }

    private static void stemString() {
    	
    }
    private static boolean stemable(char ch) {
    	if (Character.isLetterOrDigit(ch)) return true;
//		if (Character.isWhitespace((char) ch)) return false;
    	return false;
    }
}
