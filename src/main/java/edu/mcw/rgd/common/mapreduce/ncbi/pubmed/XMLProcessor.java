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
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	System.out.println("Processing: [" + ((FileSplit)context.getInputSplit()).getPath().getName() + "]");
    }
  }
  public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();
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
