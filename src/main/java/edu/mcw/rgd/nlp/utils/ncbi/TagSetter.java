package edu.mcw.rgd.nlp.utils.ncbi;

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
import java.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

/**
 *  
 * @author wliu
 *
 */


public class TagSetter {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends
	      Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Writable> {
	    
		  public static final byte[] docColFamily = Bytes.toBytes("d");
		  private byte[] col = Bytes.toBytes("g");
		  private byte[] value;
		  
	      @Override
	      protected void setup(Context context) throws IOException, InterruptedException {
	    	  Configuration conf = context.getConfiguration();
	    	  col = Bytes.toBytes(conf.get("annotationColumn"));
	    	  value = Bytes.toBytes(conf.get("columnValue"));
	    	  System.out.println("Write to column d:" + conf.get("annotationColumn"));
	      }
 
		  
	    @Override
	    protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
	        throws IOException, InterruptedException {
    		Put tagPut = new Put(rowKey.get());
            tagPut.addColumn(docColFamily, col, result.listCells().get(0).getTimestamp(), value);
            try {
            	context.write(new ImmutableBytesWritable(rowKey.get()),(Writable) tagPut);

	          } catch (InterruptedException e) {
	        	  System.err.println("Error in saving to HBase:" + Bytes.toString(rowKey.get()));
	            e.printStackTrace();
	          }
	    	
	    }
	  }

	  public static Job configureJob(Configuration conf, String [] args)
			  throws IOException {
//			    conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));
			    conf.set(TableInputFormat.INPUT_TABLE, args[0]);
			    conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");
		//	    conf.set("hbase.zookeeper.quorum", "gray03");
			    conf.set("annotationColumn", args[1]);
			    conf.set("columnValue", args[2]);
			    Job job = new Job(conf, "Setting tag d:" + args[1] + " of " + args[0] + " to " + args[2]);
			    job.setJarByClass(IndexBuilder.class);
			    job.setMapperClass(Map.class);
			    job.setNumReduceTasks(0);
			    job.setInputFormatClass(TableInputFormat.class);
			    TableMapReduceUtil.initTableReducerJob(args[0], null, job);
			    return job;
			  }
	  
	  private static String convertScanToString(Scan scan) throws IOException {  
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        DataOutputStream dos = new DataOutputStream(out);  
//	        scan.write(dos);  
	        return Base64.getEncoder().encode(out.toByteArray()).toString();
	    }  
	  
	  public static void main(String[] args) throws Exception {
		  Configuration conf = HBaseConfiguration.create();
		  conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
		  conf.set("hbase.zookeeper.property.clientPort", "2181");
		  conf.set("hbase.client.retries.number", Integer.toString(1));
		  conf.set("zookeeper.session.timeout", Integer.toString(60000));
		  conf.set("zookeeper.recovery.retry", Integer.toString(0));
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if(otherArgs.length != 3) {
		      System.err.println("Need three parameters: <table name> <column name> <value>");
		      System.exit(-1);
		    }
		    Job job = configureJob(conf, otherArgs);
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }

}
