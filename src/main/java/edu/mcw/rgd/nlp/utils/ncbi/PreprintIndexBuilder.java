package edu.mcw.rgd.nlp.utils.ncbi;

import com.mysql.cj.jdbc.MysqlDataSource;
import edu.mcw.rgd.common.utils.DataSourceFactory;
import edu.mcw.rgd.nlp.classifier.ArticleOrganismClassifier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Base64;
import java.util.NavigableMap;

public class PreprintIndexBuilder {
    public static class Map extends
            Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Mutation> {

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration conf=context.getConfiguration();
            DataSourceFactory.MYSQL_DB_URL=conf.get("MYSQL_DB_URL");
            DataSourceFactory.MYSQL_DB_USERNAME=conf.get("MYSQL_DB_USERNAME");
            DataSourceFactory.MYSQL_DB_PASSWORD=conf.get("MYSQL_DB_PASSWORD");
            PubMedLibrary.HOST_NAME=conf.get("HOST_NAME");
            ArticleOrganismClassifier aoc = new ArticleOrganismClassifier();
            aoc.LoadFromHDFS(context);
        }

        @Override
        protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
                throws IOException, InterruptedException {
            try {

                if (!PubMedLibrary.indexPreprintArticle(result))
                {
                    System.out.println("Resetting taggs of " + Bytes.toString(rowKey.get()));

                    System.out.println("1. in indexBuilder map: "+ Bytes.toString(rowKey.get()));

                    Delete delete = new Delete(rowKey.get());
                    byte[] valueN = Bytes.toBytes("N");
                    try {
                        NavigableMap<byte[], byte[]> values = result.getFamilyMap(DistributedAnnotator.Map.docColFamily);
                        java.util.Map.Entry<byte[], byte[]> curEntry = values.firstEntry();
                        while (curEntry != null) {
                            byte[] key = (byte[]) curEntry.getKey();
                            String keyStr = Bytes.toString(key);
                            if (!keyStr.equals("x")) {
                                delete.addColumn(DistributedAnnotator.Map.docColFamily, key);
                            }
                            curEntry = values.higherEntry(key);

                            System.out.println("2. in indexBuilder map: "+ Bytes.toString(rowKey.get()));
                        }
                        context.write(new ImmutableBytesWritable(rowKey.get()), delete);

                    } catch (InterruptedException e1) {
                        System.out.println("3. in indexBuilder map: Error in saving to HBase: "+ Bytes.toString(rowKey.get()));

                        System.err.println("Error in saving to HBase:" + Bytes.toString(rowKey.get()));
                        e1.printStackTrace();
                    }
                }
            } catch (Exception e) {

                System.out.println("4. in indexBuilder map: Error");

                e.printStackTrace();
//				throw new IOException();
            }
        }
    }

    public static Job configureJob(Configuration conf, String [] args)
            throws IOException {


        conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));
        String tableName=args[0];
        conf.setStrings("MYSQL_DB_URL", args[1]);
        conf.setStrings("MYSQL_DB_USERNAME", args[2]);
        conf.setStrings("MYSQL_DB_PASSWORD", args[3]);
        conf.setStrings("HOST_NAME", args[4]);
        conf.set(TableInputFormat.INPUT_TABLE, tableName);

        Job job = new Job(conf, "Indexing HBase Preprint :" + tableName + " to Solr");

        job.setJarByClass(PreprintIndexBuilder.class);
        job.setMapperClass(PreprintIndexBuilder.Map.class);
        job.setNumReduceTasks(10);
        job.setInputFormatClass(TableInputFormat.class);

        TableMapReduceUtil.initTableReducerJob(tableName, null, job);

        return job;
    }

    public static String convertScanToString(Scan scan) throws IOException {
        return Base64.getEncoder().encodeToString(ProtobufUtil.toScan(scan).toByteArray());
    }

    public static void main(String[] args) throws Exception {

        System.out.println("============================================================");
        DataSourceFactory.initMySQLDataSource();
        Configuration conf = HBaseConfiguration.create();
<<<<<<< HEAD
=======
       /* conf.set("hbase.zookeeper.quorum", "gray03.rgd.mcw.edu");
        conf.set("hbase.master", "gray01.rgd.mcw.edu:60000");
        conf.set("hbase.zookeeper.property.clientPort", "2181");*/
>>>>>>> e1d508f (updted datasourc init)
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        //		    if(otherArgs.length < 3) {
        //		      System.err.println("Only " + otherArgs.length + " arguments supplied, required: 3");
        //		      System.err.println("Usage: IndexBuilder <TABLE_NAME> <COLUMN_FAMILY> <ATTR> [<ATTR> ...]");
        //		      System.exit(-1);
        //		    }
        Job job = configureJob(conf, otherArgs);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
