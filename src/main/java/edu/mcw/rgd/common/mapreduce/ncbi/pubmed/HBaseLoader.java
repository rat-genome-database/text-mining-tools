package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.regionserver.ConstantSizeRegionSplitPolicy;
import org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Writables;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import edu.mcw.rgd.nlp.utils.ncbi.PubMedLibrary;

public class HBaseLoader {

  public static class XMLMapper 
       extends Mapper<Object, Text, ImmutableBytesWritable, Mutation>{
    
	private static Pattern articlePattern = Pattern.compile("(<ns1:PubmedArticle>.+?</ns1:PubmedArticle>)");
	private static Pattern pmidPattern = Pattern.compile("<ns1:PMID.+?>(.+?)</ns1:PMID>");
    private static byte[] colFamily = Bytes.toBytes("d");
    private static byte[] col = Bytes.toBytes("x");
    private static byte[] col1 = Bytes.toBytes("o");
    private static byte[] col2 = Bytes.toBytes("s");
    private static byte[] col3 = Bytes.toBytes("m");
    private static byte[] col4 = Bytes.toBytes("g");
    private static byte[] col5 = Bytes.toBytes("c");
    private static byte[] valueN = Bytes.toBytes("N");
    public static int x=1;
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	String fileName = ((FileSplit)context.getInputSplit()).getPath().getName();
    	System.out.println("Processing: [" + fileName + "]");
    	Long timeStamp;
    	if (fileName.contains("_")) {
    		String importDate = fileName.substring(0, 10).replaceAll("_", "");
    		timeStamp = Long.parseLong(importDate);
    	} else {
    		String fileSeq = fileName.substring(0, fileName.indexOf('.'));
    		timeStamp = Long.parseLong(fileSeq);
    	}
		System.out.println("Time Stamp: " + timeStamp.toString());

		Matcher m = articlePattern.matcher(value.toString());
		
		while (m.find()) {
    		String articleXml = m.group();

    		Matcher m1 = pmidPattern.matcher(articleXml);
    		if (m1.find()) {
        		String pmid = m1.group(1);
        		String pmid_r = PubMedLibrary.pmidToHbaseKey(pmid);
        		System.out.println("PMID: " + pmid);
	              Put put = new Put(Bytes.toBytes(pmid_r));  // az halate comment kharej kon
	              
	            	
	              put.addColumn(colFamily, col, timeStamp, Bytes.toBytes(articleXml));
	              
	              // Reset some annotation flags for mistakenly overwritten records  
//	              put.add(colFamily, col1, timeStamp, valueN);
//	              put.add(colFamily, col2, timeStamp, valueN);
//	              put.add(colFamily, col3, timeStamp, valueN);
//	              put.add(colFamily, col4, timeStamp, valueN);
//	              put.add(colFamily, col5, timeStamp, valueN);
	              try {
		              context.write(new ImmutableBytesWritable(Bytes.toBytes(pmid_r)),  put);
	                } catch (InterruptedException e) {
	                  e.printStackTrace();
	                }

    		}
    	}
    	
    }
  }
  

  public static void main(String[] args) throws Exception {
	  Configuration conf = HBaseConfiguration.create();
	  conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
	 // conf.set("hbase.zookeeper.property.clientPort", "2181");
	  conf.set("hbase.client.retries.number", Integer.toString(1));
	  conf.set("zookeeper.session.timeout", Integer.toString(60000));
	  conf.set("zookeeper.recovery.retry", Integer.toString(0));

	 /* conf.set("hbase.hregion.max.filesize","10737418240");
	  conf.set("hbase.hregion.memstore.block.multiplier",Integer.toString(4));
	  conf.set("hbase.hregion.memstore.flush.size",Integer.toString(134217728));
	  conf.set("hbase.hstore.blockingStoreFiles", Integer.toString(10));
*/
	  Connection connection = ConnectionFactory.createConnection(conf);
	  Admin hba = connection.getAdmin();


    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: HBaseLoader <path> <table name>");
      System.exit(2);
    }
	  if (!hba.tableExists(TableName.valueOf(otherArgs[1]))) {

		  TableDescriptorBuilder  ht = TableDescriptorBuilder.newBuilder(TableName.valueOf(otherArgs[1]));
		  ht.setRegionSplitPolicyClassName(ConstantSizeRegionSplitPolicy.class.getName());

		  ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
				  .newBuilder(Bytes.toBytes("d")).build();
		  ht.setColumnFamily(columnFamilyDescriptor);
		  columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
				  .newBuilder(Bytes.toBytes("a")).build();
		  ht.setColumnFamily(columnFamilyDescriptor);
		  columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
				  .newBuilder(Bytes.toBytes("c")).build();
		  ht.setColumnFamily(columnFamilyDescriptor);
		  columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
				  .newBuilder(Bytes.toBytes("l")).build();
		  ht.setColumnFamily(columnFamilyDescriptor);
		  columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
				  .newBuilder(Bytes.toBytes("s")).build();
		  ht.setColumnFamily(columnFamilyDescriptor);

		   hba.createTable(ht.build());

		  System.out.println("New Table Created :"+ hba.tableExists(TableName.valueOf(otherArgs[1])));
	  } else {
		  System.out.println("Table Exists :");
	  }

    Job job = new Job(conf, "Import PubMed XML to HBase:" + otherArgs[1] + " from " + otherArgs[0]);
    job.setJarByClass(XMLProcessor.class);
    job.setMapperClass(XMLMapper.class);
	  job.setOutputValueClass(Result.class);
    
//    job.setInputFormatClass(XMLInputFormat.class);
    
    XMLInputFormat.addInputPath(job, new Path(otherArgs[0]));

    TableMapReduceUtil.initTableReducerJob(otherArgs[1], null, job);
    job.setNumReduceTasks(0);
   
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

  public static class XMLInputFormat extends TextInputFormat {
	  @Override
	  protected boolean isSplitable(JobContext context, Path file) {
		  return false;
	  }
	  
  }
}
