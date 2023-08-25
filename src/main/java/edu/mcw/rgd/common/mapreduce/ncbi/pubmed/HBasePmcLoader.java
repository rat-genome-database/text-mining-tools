package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;

import edu.mcw.rgd.common.utils.HbaseUtils;
import edu.mcw.rgd.database.ncbi.pubmed.PmcArticleDAO;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedLibrary;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HBasePmcLoader {
    public static class PMCMapper
            extends Mapper<Object, Text, ImmutableBytesWritable, Mutation> {

        private static Pattern articlePattern = Pattern.compile("(<ns1:article.+?>.+?</ns1:article>)");
        private static Pattern pmidPattern = Pattern.compile("<ns1:article-id pub-id-type=\"pmc\">(.+?)</ns1:article-id>");
        private static byte[] colFamily = Bytes.toBytes("d");//d for document
        private static byte[] col = Bytes.toBytes("x");//"x" for xml
        public static int x = 1;

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            System.out.println("Processing: [" + fileName + "]");
            Matcher m = articlePattern.matcher(value.toString());
            while (m.find()) {
                String articleXml = m.group();
                Matcher m1 = pmidPattern.matcher(articleXml);

                if (m1.find()) {
                    String pmcid = m1.group(1);
                    String pmcid_r = PubMedLibrary.pmidToHbaseKey(pmcid);
                    System.out.println("PMC ID: " + pmcid);
                    Put put = new Put(Bytes.toBytes(pmcid_r));
                    SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
                    long timeStamp = Long.parseLong(formatter.format(new Date()));
                    put.addColumn(colFamily, col, timeStamp, Bytes.toBytes(articleXml));


                    //put.addColumn(colFamily, Bytes.toBytes("e"),timeStamp,Bytes.toBytes("hello"));
                    try {
                        if (PmcArticleDAO.typeWanted(articleXml)){
                            context.write(new ImmutableBytesWritable(Bytes.toBytes(pmcid_r)), put);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HbaseUtils.createConfig();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: HBaseLoader <path> <table name>");
            System.exit(2);
        }
        HbaseUtils.createTable(otherArgs[1], conf);
        Job job = new Job(conf, "Import PMC XML to HBase:" + otherArgs[1] + " from " + otherArgs[0]);
        job.setJarByClass(XMLProcessor.class);
        job.setMapperClass(PMCMapper.class);
        job.setOutputValueClass(Result.class);
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
