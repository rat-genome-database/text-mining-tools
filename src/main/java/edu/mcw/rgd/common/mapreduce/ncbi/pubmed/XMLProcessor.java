package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class XMLProcessor {

  public static class XMLMapper 
       extends Mapper<Object, Text, ImmutableBytesWritable, Writable>{
    
	    private final static IntWritable one = new IntWritable(1);
	    private final static IntWritable zero = new IntWritable(0);
    private Text word = new Text("combined-files");
    private String XMLText = "";
//    private final static String lineBreakStr = "\r\n"; 
    private final static String lineBreakStr = ""; 
	private static Pattern articlePattern = Pattern.compile("(<ns1:PubmedArticle>.+?</ns1:PubmedArticle>)");
	private static Pattern pmidPattern = Pattern.compile("<ns1:PMID.+?>(.+?)</ns1:PMID>");
    private static byte[] colFamily = Bytes.toBytes("d");
    private static byte[] col = Bytes.toBytes("x");
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	System.out.println("Processing: [" + ((FileSplit)context.getInputSplit()).getPath().getName() + "]");
/* Import to MySQL. Not used now because HBase is the article storage now.  
       	PubMedDocSet pmds = new PubMedDocSet();
       	pmds.setDocSetXML(value.toString());
       	pmds.parseDocSet();
       	pmds.importToDB();
 */
    	/*
    	Matcher m = articlePattern.matcher(value.toString());
    	while (m.find()) {
    		String articleXml = m.group();
    		System.out.println("\r\nXML: "+ articleXml);
    		Matcher m1 = pmidPattern.matcher(articleXml);
    		if (m1.find()) {
        		String pmid = m1.group(1);
        		System.out.println("\r\nPMID: " + pmid);
	              Put put = new Put(Bytes.toBytes(pmid));
	              put.add(colFamily, col, Bytes.toBytes(articleXml));
	              try {
		              context.write(new ImmutableBytesWritable(Bytes.toBytes(pmid)), put);
	                } catch (InterruptedException e) {
	                  e.printStackTrace();
	                }

	                // Set status every checkpoint lines
//	                if(++count % checkpoint == 0) {
//	                  context.setStatus("Emitting Put " + count);
//	                }

    		}
    	}
    	*/
    }
  }
  public static class IntSumReducer 
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }
  
  public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();
//    conf.set("mapred.job.map.memory.mb", "4096");
//    conf.set("mapred.job.reduce.memory.mb", "4096");
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 1) {
      System.err.println("Usage: XMLProcessor <in>");
      System.exit(2);
    }
    
    Job job = new Job(conf, "Import PubMedl XML to HBase");
    job.setJarByClass(XMLProcessor.class);
    job.setNumReduceTasks(0);
    job.setMapperClass(XMLMapper.class);
    job.setInputFormatClass(XMLInputFormat.class);
    XMLInputFormat.addInputPath(job, new Path(otherArgs[0]));

//    TableMapReduceUtil.initTableReducerJob("pubmed_test", null, job);
   
    job.setOutputFormatClass(NullOutputFormat.class);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

  public static class XMLInputFormat extends TextInputFormat {
	  @Override
	  protected boolean isSplitable(JobContext context, Path file) {
		  return false;
	  }
	  
  }
}
