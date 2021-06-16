package edu.mcw.rgd.nlp.utils.ncbi;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.IdentityTableReducer;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/*
 *  
 * @author wliu
 *
 */


public class DistributedAnnotator {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends TableMapper<ImmutableBytesWritable , Mutation>{
			 // TableMapper<Text, Text> {

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

	    	//  Path[] localCache = DistributedCache.getLocalCacheArchives(conf);
			  Path[] localCache = context.getLocalCacheArchives();
	    	  gateHome = localCache[0].toString();
			//  System.out.println("local cache gate home: "+ gateHome);
	    	  Collection<String> setList = conf.getStringCollection(PubMedLibrary.MR_ANN_SETS);
	    	  for (String annSet : setList) {
	    //		  System.out.println("Collecting: " + annSet);
	    		  annotationSets.add(annSet);
	    	  }
			  //setList.clear();
		//	  System.out.println("ANNOTATION SETS: "+ annotationSets);
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
			//	System.out.println("COUNTER is ZERO: ");
				pml = new PubMedLibrary();
				pml.setAnnotationSets(annotationSets);
				pml.resetAnnotator(gateHome, useStemming);
			}

			String annTag = Bytes.toString(result.getValue(docColFamily, col));
			long docTS=0, annTS=0;
			boolean hasArticle = false;
			List<Cell> cells=result.listCells();
			for(Cell c:cells){
				//  System.out.println(c);
				String r=new String(CellUtil.cloneRow(c));
				String family= new String(CellUtil.cloneFamily(c));
				String column=new String(CellUtil.cloneQualifier(c));
				String val= new String(CellUtil.cloneValue(c));
				if(column.equals("x")){
					docTS=c.getTimestamp();
					hasArticle=true;
				}
				if (column.equals(colStr)) annTS = c.getTimestamp();
				if (docTS > 0 && annTS > 0) break;

			}

			if (hasArticle && (forcedTagging || annTS < docTS || annTag == null || !annTag.equals("Y"))) {
				List<String> annotations = pml.mrAnnotateHResult(result, this.gateHome, useStemming);
				String finalStr = "";

					String pmid = pml.mrArticleDao.pmid.toString();
					for (String ann : annotations) {
						finalStr += ann + "|";
					}

					//annotations.clear();
					//	System.out.println("FINAL STRING: "+ finalStr);
					Mutation dbComm = null;

					if (finalStr.length() > 0) {
						Put put = new Put(rowKey.get());
						put.addColumn(colFamily, col, docTS, Bytes.toBytes(finalStr));

						dbComm = put; //------------------------------------------

					} else {
						Delete delete = new Delete(rowKey.get());
						delete.addColumn(colFamily, col);
						dbComm = delete;  //------------------------------------------
					}
					try {

						if (dbComm instanceof Put) {
//		            	  System.err.println("Adding annotations...");
							((Put) dbComm).addColumn(docColFamily, col, docTS, Bytes.toBytes("Y"));
							context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);
						} else {
//		            	  System.err.println("Deleting annotations...");
							context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);
							Put tagPut = new Put(rowKey.get());
							tagPut.addColumn(docColFamily, col, docTS, Bytes.toBytes("Y"));
//				              context.write(new ImmutableBytesWritable(rowKey.get()), tagPut);  //--------------------------------------

							context.write(new ImmutableBytesWritable(rowKey.get()), tagPut);
						}
					} catch (InterruptedException e) {
						System.err.println("Error in saving to HBase:" + pmid);
						e.printStackTrace();
					}
				}

				counter++;
				counter_inner++;
				if (counter_inner == 1000) {
					//System.out.println(counter + " articles processed.");
					counter_inner = 0;
					//    		counter = 0;

				}

		}
	  }

	  public static String gateHomePath = "s3://emr-repository/tmp/gate/";
	  public static Path hdfsGateAppPath ;

	  public static Job configureJob(Configuration conf, String [] args) throws IOException {
		 /* String path="/home/rgdpub/Work/Gate/STRUCTURE_7.0.zip";
		  Path localGateAppPath = new Path(path);
		  gateHomePath = gateHomePath + "STRUCTURE_7.0.zip";*/

		 /* Path localGateAppPath = new Path(args[1]);
		  gateHomePath = gateHomePath + localGateAppPath.getName();
		 */ Path hdfsGateAppPath = new Path(args[1]);//gateHomePath);
		/*  if (!args[1].equals("lastApp")) {
			  		FileSystem fs = FileSystem.get(conf);
			  		fs.copyFromLocalFile(localGateAppPath, hdfsGateAppPath);
		  }
		  */Scan sc=new Scan();
		  conf.set(TableInputFormat.INPUT_TABLE, args[0]);
		  conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");

		  String scanConvertedString=TableMapReduceUtil.convertScanToString(sc);
		  conf.set(TableInputFormat.SCAN, scanConvertedString );


			 /*   conf.set("hbase.zookeeper.quorum", "tucker.rgd.mcw.edu");
			    conf.set("zookeeper.znode.parent", "/hbase-unsecure");*/

			    conf.set("annotation_useStemming", args[2]);
			    conf.set("annotationColumn", args[3]);
			    String annotationSets = args[4];

			    conf.setStrings(PubMedLibrary.MR_ANN_SETS, annotationSets);
			    Job job = new Job(conf, "Annotator using " + args[1]);
			    job.setJarByClass(DistributedAnnotator.class);
			    job.setMapperClass(Map.class);
				job.setNumReduceTasks(0);
		  		job.setInputFormatClass(TableInputFormat.class);
		  		job.addCacheArchive(hdfsGateAppPath.toUri());
			    TableMapReduceUtil.initTableReducerJob(args[0], null, job);

			    return job;
			  }



	  public static void main(String[] args) throws Exception {
		  Configuration conf = HBaseConfiguration.create();
		  conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
		 // conf.addResource(new Path("/usr/local/hbase/conf/hbase-site.xml"));
		  conf.set("hbase.zookeeper.property.clientPort", "2181");
		  conf.set("hbase.client.retries.number", Integer.toString(1));
		  conf.set("zookeeper.session.timeout", Integer.toString(60000));
		  conf.set("zookeeper.recovery.retry", Integer.toString(0));

		 //conf.set("mapred.child.java.opts","-Xmx6144m -Xmx12288m -XX:+UseSerialGC -XX:-OmitStackTraceInFastThrow");
		 // conf.set("mapred.job.map.memory.mb","12288");
		 // conf.set("mapred.job.reduce.memory.mb","8192");
		  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		  if(otherArgs.length < 5) {
			  System.err.println("At least 5 parameters: <table name> <path to gate> <use stemming> <column name> <annotation set> ...");
			  System.exit(-1);
		  }
		  Job job = configureJob(conf, otherArgs);
		  System.exit(job.waitForCompletion(true) ? 0 : 1);


		  }

}
