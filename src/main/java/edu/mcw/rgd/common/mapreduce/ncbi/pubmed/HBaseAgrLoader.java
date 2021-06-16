package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedLibrary;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.regionserver.ConstantSizeRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


public class HBaseAgrLoader {
    public static class XMLMapper
            extends Mapper<Object, Text, ImmutableBytesWritable, Mutation> {

        private static byte[] colFamily = Bytes.toBytes("d");
        private static byte[] col = Bytes.toBytes("x");

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootArray = objectMapper.readTree(value.toString());
            Iterator it = rootArray.iterator();
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
            while (it.hasNext()) {
                ObjectNode article = (ObjectNode) it.next();
                String articleId = getArticleId(article);
                if(StringUtils.isNumeric(articleId)) {
                    long id = Long.parseLong(articleId);
                    String articleid_r = PubMedLibrary.pmidToHbaseKey(articleId);
                    System.out.println(articleid_r);
                    long ts = Long.parseLong(formatter.format(date));
                    Put put = new Put(Bytes.toBytes(articleid_r));
                    put.addColumn(colFamily, col, ts, Bytes.toBytes(article.toString()));
                    try {
                        context.write(new ImmutableBytesWritable(Bytes.toBytes(id)), put);
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
        //conf.addResource(new Path("/usr/local/hbase/conf/hbase-site.xml"));
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.client.retries.number", Integer.toString(1));
        conf.set("zookeeper.session.timeout", Integer.toString(60000));
        conf.set("zookeeper.recovery.retry", Integer.toString(0));

        Connection connection = ConnectionFactory.createConnection(conf);
        Admin hba = connection.getAdmin();

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: HBaseLoader <path> <table name>");
            System.exit(2);
        }
        if (!hba.tableExists(TableName.valueOf(otherArgs[1]))) {

            TableDescriptorBuilder ht = TableDescriptorBuilder.newBuilder(TableName.valueOf(otherArgs[1]));
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
        Job job = new Job(conf, "Import Agr papers to HBase:" + otherArgs[1] + " from " + otherArgs[0]);
        job.setJarByClass(HBaseAgrLoader.class);
        job.setMapperClass(HBaseAgrLoader.XMLMapper.class);
        job.setOutputValueClass(Result.class);
        JSONInputFormat.addInputPath(job, new Path(otherArgs[0]));

        TableMapReduceUtil.initTableReducerJob(otherArgs[1], null, job);
        job.setNumReduceTasks(0);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class JSONInputFormat extends TextInputFormat {
        @Override
        protected boolean isSplitable(JobContext context, Path file) {
            return true;
        }

    }

    public static String getArticleId(ObjectNode article)  {
        String primaryId= article.get("primaryId").toString();
        String articleId = primaryId.substring(primaryId.indexOf(":")+1,primaryId.length()-1);
        return articleId;
    }

}

