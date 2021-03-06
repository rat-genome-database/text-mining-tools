package edu.mcw.rgd.nlp.utils.ncbi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 *  
 * @author wliu
 *
 */


public class ExtractArticle {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends
	      Mapper<ImmutableBytesWritable, Result, Writable, Writable> {
	    
		  protected PubMedLibrary pml;
		  protected List<String> annotationSets;
		  protected String gateHome;
		  
		  public static final byte[] docColFamily = Bytes.toBytes("d");
		  protected int counter;
		  protected int counter_inner;
		  
	      @Override
	      protected void setup(Context context) throws IOException, InterruptedException {
	    	  counter = 0;
	    	  counter_inner = 0;
	      }
 
		  
	    @Override
	    protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
	        throws IOException, InterruptedException {
	    	if (counter == 0) {
	    		pml = new PubMedLibrary();
	    	}
	    	List<Cell> kvs = result.listCells();
	    	boolean hasArticle = false;
	    	for (Cell kv : kvs) {
	    		if (Bytes.toString(CellUtil.cloneQualifier(kv)).equals("x")) {
	    			hasArticle = true;
	    		}
	    	}
	    	
	    	if (hasArticle && pml.mrArticleDao.getArticleFromHResult(result)) {
//		    	System.err.println("PMID: " + Bytes.toString(rowKey.get()));
		    	String finalStr = pml.mrArticleDao.articleTitle + "\t"
		    			+ pml.mrArticleDao.articleAbstract + "\t"
		    			+ pml.mrArticleDao.meshTerms;
		    	finalStr = finalStr.replaceAll("\r\n", "||");
		    	String pmid = pml.mrArticleDao.pmid.toString();
		    	context.write(new Text(Bytes.toString(rowKey.get())), new Text(finalStr));
	    	}
	    	counter ++;
	    	counter_inner ++;
	    	if (counter_inner == 1000) {
	    		System.out.println(counter + " articles processed.");
	    		counter_inner = 0;
//	    		counter = 0;
	    		
	    	}
	    	
	    }
	  }

	  public static Job configureJob(Configuration conf, String [] args)
			  throws IOException {
		  		
			    conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));
			    conf.set(TableInputFormat.INPUT_TABLE, args[0]);
			    conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");
			//    conf.set("hbase.zookeeper.quorum", "gray03");
			    Job job = new Job(conf, "Exporting article text ");
			    job.setJarByClass(IndexBuilder.class);
			    job.setMapperClass(Map.class);
			    job.setNumReduceTasks(0);
			    job.setInputFormatClass(TableInputFormat.class);
//			    TableMapReduceUtil.initTableReducerJob(args[0], null, job);
			    job.setOutputFormatClass(TextOutputFormat.class);
			    FileOutputFormat.setOutputPath(job, new Path(args[1]));
			    return job;
			  }
	  
	  private static String convertScanToString(Scan scan) throws IOException {  
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        DataOutputStream dos = new DataOutputStream(out);  
//	        scan.write(dos);  
	        return Base64.getEncoder().encodeToString(out.toByteArray());
	    }  
	  
	  public static void main(String[] args) throws Exception {
		    Configuration conf = HBaseConfiguration.create();
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if(otherArgs.length < 1) {
		      System.err.println("2 parameters: <table name> <output path>");
		      System.exit(-1);
		    }
		    Job job = configureJob(conf, otherArgs);
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }

}
