
package org.tartarus.snowball;


import java.io.File;
import java.io.FileReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;


public class OntFileStemmer {

    public static void main(String [] args) throws Throwable {
    	if (args.length < 2) {
    		System.out.println("Two parameters are required: input_file_name output_file_name");
    		return;
    	}
	Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

		try {
			File inFile = new File(args[0]);
			if (!inFile.exists()) {
				System.err.println("Source file [" + args[0] + "] doesn't exist!");
				return;
			}
			
			File outFile = new File(args[1]);
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(outFile));
			
			String line;
//			if (hasCount) br.readLine();  // Skip the count which is not being used.
			while ((line = br.readLine()) != null && line.length() > 0)
			{
			    String fields[] = line.split("\\|");

			    try {
			    	bw.write(Stemmer.stem(fields[0], false));
			    	bw.write("|"+fields[1]+"|"+fields[2]+"\r\n");
			    	bw.flush();
			    } catch (Exception e) {
			    }
			}
			br.close();
			bw.close();
			
		} catch (Exception e) {
			throw e;
		}
		return;
        
    }
    
}
