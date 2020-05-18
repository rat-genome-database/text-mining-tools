package edu.mcw.rgd.nlp.utils.ncbi;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class TagsUpdater {
    public static class Map extends TableMapper<ImmutableBytesWritable, Mutation>
    {

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
            System.out.println("Write to column a:" + colStr +" BY THOTA");
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
            long docTS = 0;
            Mutation  dbComm;
            List<Cell> kvs = result.listCells();
            boolean hasArticle = false;
            boolean tagged=false;
            for (Cell kv : kvs) {
                if (Bytes.toString(CellUtil.cloneQualifier(kv)).equals("x")) {
                    docTS = kv.getTimestamp();
                    hasArticle = true;

                }

            }
            if (hasArticle) {
                List<String> annotations = pml.mrAnnotateHResult(result, this.gateHome, useStemming);

                String finalStr = "";
                String pmid = PubMedLibrary.mrArticleDao.pmid.toString();
                for (String ann : annotations) {
                    finalStr += ann + "|";
                }
                if (finalStr.length() > 0) {
                    Put put = new Put(rowKey.get());
                    put.addColumn(colFamily, col, docTS, Bytes.toBytes(finalStr));
                    dbComm = put;

                } else {
                    Delete delete = new Delete(rowKey.get());
                    delete.addColumn(colFamily, col);
                    dbComm = delete;  //------------------------------------------
                }
                try {

                    if (dbComm instanceof Put) {
                        ((Put) dbComm).addColumn(docColFamily, col, docTS, Bytes.toBytes("Y"));
                        context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);

                    } else {

                        context.write(new ImmutableBytesWritable(rowKey.get()), dbComm);
                        Put tagPut = new Put(rowKey.get());
                        tagPut.addColumn(docColFamily, col, docTS, Bytes.toBytes("Y"));
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
                System.out.println(counter + " articles processed.");
                counter_inner = 0;
//	    		counter = 0;

            }

            //	}
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
    //   sc.setTimeRange(20200401, 20200430);
        sc.setTimeRange(Long.parseLong(args[5]), Long.parseLong(args[6])); //args[5], args[6] are timestamps
        conf.set(TableInputFormat.SCAN, convertScanToString(sc));

        conf.set(TableInputFormat.INPUT_TABLE, args[0]);
        conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");

        conf.set("annotation_useStemming", args[2]);
        conf.set("annotationColumn", args[3]);
        String annotationSets = args[4];

			/*	for (int i = 5; i < args.length; i++) {
			    	annotationSets += "," + args[i];
			    }
			   */
        conf.setStrings(PubMedLibrary.MR_ANN_SETS, annotationSets);
        Job job = new Job(conf, "Annotator using " + args[1]);
        job.setJarByClass(TagsUpdater.class);
        job.setMapperClass(TagsUpdater.Map.class);
        job.setNumReduceTasks(0);
        job.setInputFormatClass(TableInputFormat.class);
        TableMapReduceUtil.initTableReducerJob(args[0], null, job);

        return job;
    }

    public static String convertScanToString(Scan scan) throws IOException {
        return Base64.getEncoder().encodeToString(ProtobufUtil.toScan(scan).toByteArray());
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
