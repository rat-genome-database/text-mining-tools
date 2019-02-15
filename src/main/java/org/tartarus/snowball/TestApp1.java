
package org.tartarus.snowball;


import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;


public class TestApp1 {

    public static void main(String [] args) throws Throwable {
	Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

	Reader reader;
	reader = new InputStreamReader(System.in);
	reader = new BufferedReader(reader);

	StringBuffer input = new StringBuffer();

        OutputStream outstream;

	    outstream = System.out;
	Writer output = new OutputStreamWriter(outstream);
	output = new BufferedWriter(output);

	int character;
	while ((character = reader.read()) != -1) {
		input.append((char) character);
	}
	output.write(Stemmer.stem(input.toString(), false));
	output.flush();
    }
}
