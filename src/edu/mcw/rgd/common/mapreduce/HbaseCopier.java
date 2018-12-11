package edu.mcw.rgd.common.mapreduce;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import edu.mcw.rgd.nlp.utils.ncbi.IndexBuilder;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedLibrary;

/**
 *  
 * @author wliu
 *
 */


public class HbaseCopier {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends
	      Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Writable> {
	    
		  protected int counter;
		  protected int counter_inner;
		  
	      @Override
	      protected void setup(Context context) throws IOException, InterruptedException {
	    	  counter = 0;
	    	  counter_inner = 0;
	    	  Configuration conf = context.getConfiguration();
	      }
 
		  
	    @Override
	    protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
	        throws IOException, InterruptedException {
	    	String pmid = Bytes.toString(rowKey.get());
//    		Put put = new Put(Bytes.toBytes(PubMedLibrary.pmidToHbaseKey(pmid)));
    		Put put = new Put(rowKey.get());
	    	KeyValue[] kvs = result.raw();
	    	for (KeyValue kv : kvs) {
	    		put.add(kv.getFamily(), kv.getQualifier(), kv.getTimestamp(), kv.getValue());
	    	}
            try {
		            context.write(new ImmutableBytesWritable(rowKey.get()), (Writable)put);
          } catch (InterruptedException e) {
	        	  System.err.println("Error in saving to HBase:" + pmid);
	            e.printStackTrace();
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
			//    conf.set("hbase.zookeeper.quorum", "gray03");
			    Job job = new Job(conf, "Copying from " + args[0] + " to " + args[1]);
			    job.setJarByClass(HbaseCopier.class);
			    job.setMapperClass(Map.class);
			    job.setNumReduceTasks(0);
			    job.setInputFormatClass(TableInputFormat.class);
			    TableMapReduceUtil.initTableReducerJob(args[1], null, job);
			    return job;
			  }
	  
	  private static String convertScanToString(Scan scan) throws IOException {  
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        DataOutputStream dos = new DataOutputStream(out);  
//	        scan.write(dos);  
	        return Base64.encodeBytes(out.toByteArray());  
	    }  
	  
	  public static void main(String[] args) throws Exception {
		    Configuration conf = HBaseConfiguration.create();
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    Job job = configureJob(conf, otherArgs);
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }

}
