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

//import org.apache.hadoop.hbase.client.Admin;
//import org.apache.hadoop.hbase.client.Connection;
//import org.apache.hadoop.hbase.client.ConnectionFactory;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

//import org.apache.hadoop.hbase.client.Table;

//import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
//import org.apache.hadoop.hbase.TableName;

//import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.Scan;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.hadoop.mapreduce.Mapper.Context;


/**
 *  
 * @author wliu
 *
 */


public class DistributedAnnotator {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends
	      Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Writable> {
	    
		  protected PubMedLibrary pml;
		  protected List<String> annotationSets;
		  protected String gateHome;
		  
		  public static final byte[] colFamily = Bytes.toBytes("a");
		  public static final byte[] docColFamily = Bytes.toBytes("d");
		  public static final byte[] linkColFamily = Bytes.toBytes("l");
		  private String colStr = "g";
		  private byte[] col = Bytes.toBytes(colStr);
		  protected int counter;
		  protected int counter_inner;
		  protected boolean forcedTagging = false;
		  protected boolean useStemming = false;
		  
	      @Override
	      protected void setup(Context context) throws IOException, InterruptedException {
	    	  
	    	  	
	    	  counter = 0;
	    	  counter_inner = 0;
	    	  Configuration conf = context.getConfiguration();
	    	  annotationSets = new ArrayList<String>();
	    	  Path[] localCache = DistributedCache.getLocalCacheArchives(conf);
	    	  gateHome = localCache[0].toString();
	    	  Collection<String> setList = conf.getStringCollection(PubMedLibrary.MR_ANN_SETS);
	    	  for (String annSet : setList) {
	    		  System.out.println("Collecting: " + annSet);
	    		  annotationSets.add(annSet);
	    	  }
	    	  colStr = conf.get("annotationColumn");
	    	  if (colStr.equals(colStr.toUpperCase())) forcedTagging = true;
	    	  col = Bytes.toBytes(colStr.toLowerCase());
	    	  String stemmingArg = conf.get("annotation_useStemming");
	    	  if (stemmingArg.equalsIgnoreCase("Y") || stemmingArg.equalsIgnoreCase("Yes") || stemmingArg.equalsIgnoreCase("true")) useStemming = true;
	    	  System.out.println("Write to column a:" + colStr);
	    	  System.out.println("Use stemming:" + useStemming);
	      }
 
		  
	    @Override
	    protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
	        throws IOException, InterruptedException {
	    	
			if (counter == 0) {
				pml = new PubMedLibrary();
				pml.setAnnotationSets(annotationSets);
				pml.resetAnnotator(gateHome, useStemming);
			}
			String annTag = Bytes.toString(result.getValue(docColFamily, col));
			long docTS = 0, annTS = 0;
			KeyValue[] kvs = result.raw();
			boolean hasArticle = false;
			for (KeyValue kv : kvs) {
				if (Bytes.toString(kv.getQualifier()).equals("x")) {
					docTS = kv.getTimestamp();
					hasArticle = true;
	    		}
	    		if (Bytes.toString(kv.getQualifier()).equals(colStr)) annTS = kv.getTimestamp();
//	    		if (docTS > 0 && annTS < 20170000) break;

	    		 if (docTS > 0 && annTS > 0) break;  //original ---------
				}

	    	if (hasArticle && (forcedTagging || annTS < docTS || annTag == null || !annTag.equals("Y"))) {  //---------- original
	    	
//	    	if (hasArticle && (forcedTagging || annTS > 20170000 || annTag == null || !annTag.equals("Y"))) {
//		    	System.err.println("PMID: " + Bytes.toString(rowKey.get()));
		    	List<String> annotations = pml.mrAnnotateHResult(result, this.gateHome, useStemming);
					String finalStr = "";
					String pmid = pml.mrArticleDao.pmid.toString();
					for (String ann : annotations) {
						finalStr += ann + "|";
					}
		    	Writable dbComm = null;
//	    		System.err.println("PMID: " + pmid + "\r\nAnnotations: "+ finalStr);
	      		if (finalStr.length() > 0) {
//		    		System.out.println("PMID: " + pmid + "\r\nAnnotations: "+ finalStr);
						Put put = new Put(rowKey.get());
//		        	System.out.println("TS: " + result.raw()[0].getTimestamp());
		  				put.add(colFamily, col, docTS, Bytes.toBytes(finalStr));
//		            put.add(colFamily, col,result.raw()[0].getTimestamp(), Bytes.toBytes("test value"));
//		            put.add(Bytes.toBytes("a"), Bytes.toBytes("m"), Bytes.toBytes("test value"));

//		            dbComm = put;
		            dbComm = (Writable)put; //------------------------------------------
					} else {
						Delete	delete = new Delete(rowKey.get());
						delete.deleteColumn(colFamily, col);
		            
//		            dbComm = delete;
		            dbComm = (Writable)delete;  //------------------------------------------
					}
					try {

						if (dbComm instanceof Put) {
	            	//  		System.err.println("Adding annotations...");
		            	  ((Put) dbComm).add(docColFamily, col, docTS, Bytes.toBytes("Y"));
							context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);
						} else {
	           // 	  System.err.println("Deleting annotations...");
							context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);
							Put tagPut = new Put(rowKey.get());
							tagPut.add(docColFamily, col, docTS, Bytes.toBytes("Y"));
//				              context.write(new ImmutableBytesWritable(rowKey.get()), tagPut);  //--------------------------------------

							context.write(new ImmutableBytesWritable(rowKey.get()), (Writable) tagPut);
						}
					} catch (InterruptedException e) {
						System.err.println("Error in saving to HBase:" + pmid);
						e.printStackTrace();
					}
		}
				counter++;
				counter_inner++;
				if (counter_inner == 1000) {
					System.out.println(counter + " articles processed.");
					counter_inner = 0;
//	    		counter = 0;

				}

		}
	  }

	  public static String gateHomePath = "tmp/gate/"; 
	  public static Path hdfsGateAppPath ;
	  public static Job configureJob(Configuration conf, String [] args)
			  throws IOException {
		  		
		  	   Path localGateAppPath = new Path(args[1]);
		  	 gateHomePath = gateHomePath + localGateAppPath.getName();
		  	 
 			   Path hdfsGateAppPath = new Path(gateHomePath);
		  		if (!args[1].equals("lastApp")) {
			  		FileSystem fs = FileSystem.get(conf);
			  		fs.copyFromLocalFile(localGateAppPath, hdfsGateAppPath);
		  		}
		  		
		  		DistributedCache.addCacheArchive(hdfsGateAppPath.toUri(), conf);
		  		
		  		Scan sc=new Scan();
		  	//	sc.setCaching(400);
			    conf.set(TableInputFormat.SCAN, convertScanToString(sc));
			    
//			    conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));
			    
			    conf.set(TableInputFormat.INPUT_TABLE, args[0]);
			    conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");
			    
			    conf.set("hbase.zookeeper.quorum", "gray01");  ///--------------------------------------
			    
			    
			    //-----------------------------------
			    conf.set("zookeeper.znode.parent", "/hbase-unsecure");
			    
			    conf.set("annotation_useStemming", args[2]);
			    conf.set("annotationColumn", args[3]);
			    String annotationSets = args[4];
			    

			    
				for (int i = 5; i < args.length; i++) {
			    	annotationSets += "," + args[i];
			    }
			    
			    conf.setStrings(PubMedLibrary.MR_ANN_SETS, annotationSets);
			    Job job = new Job(conf, "Annotator using " + args[1]);
			    job.setJarByClass(IndexBuilder.class);
			    job.setMapperClass(Map.class);
			    job.setNumReduceTasks(0);
			    job.setInputFormatClass(TableInputFormat.class);
			    TableMapReduceUtil.initTableReducerJob(args[0], null, job);
			    
			    return job;
			  }
	  
	
	  
//	  private static String convertScanToString(Scan scan) throws IOException {  
//	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
//	        DataOutputStream dos = new DataOutputStream(out);
//	        scan.write(dos);  // ------------------------------------------
//	        return Base64.encodeBytes(out.toByteArray());  
//	    }  
	  
	  public static String convertScanToString(Scan scan) throws IOException {
		   return Base64.encodeBytes( ProtobufUtil.toScan(scan).toByteArray() );
		 }
	  
	  
	  public static void main(String[] args) throws Exception {
		    
		  
		  Configuration conf = HBaseConfiguration.create();
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if(otherArgs.length < 5) {
		      System.err.println("At least 5 parameters: <table name> <path to gate> <use stemming> <column name> <annotation set> ...");
		      System.exit(-1);
		    }
				  Job job = configureJob(conf, otherArgs);
		    
		    
		    
				  System.exit(job.waitForCompletion(true) ? 0 : 1);


		  }

}
