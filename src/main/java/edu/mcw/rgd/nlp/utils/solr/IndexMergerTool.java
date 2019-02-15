package edu.mcw.rgd.nlp.utils.solr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexMergerTool {
	 public static void main(String[] args) throws IOException {
        if (args.length < 4) {
          System.err.println("Usage: <numberOfSegments> <mergedIndex> <index1> <index2> [index3] ...");
	      System.exit(1);
	    }
        int nSegments = Integer.parseInt(args[0]);
	    FSDirectory mergedIndex = FSDirectory.open(new File(args[1]));
	    IndexWriter writer = new IndexWriter(mergedIndex, new IndexWriterConfig(Version.LUCENE_41, null).setOpenMode(OpenMode.CREATE));
	
	    Directory[] indexes = new Directory[args.length - 2];
	    for (int i = 2; i < args.length; i++) {
	      indexes[i  - 2] = FSDirectory.open(new File(args[i]));
	    }
	
	    System.out.println("Merging...");
	    writer.addIndexes(indexes);
	
	    if (nSegments > 0){
		    System.out.println("Full merge...");
	    	writer.forceMerge(nSegments);
	    }
	    	
	    writer.close();
	    System.out.println("Done.");
	  }
}
