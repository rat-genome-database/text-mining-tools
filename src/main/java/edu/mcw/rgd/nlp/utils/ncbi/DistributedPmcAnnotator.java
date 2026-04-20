package edu.mcw.rgd.nlp.utils.ncbi;

import edu.mcw.rgd.common.utils.HbaseUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/*
 *  
 * @author wliu
 *
 */


public class DistributedPmcAnnotator {

	  /**
	   * Internal Mapper to be run by Hadoop.
	   */
	  public static class Map extends TableMapper<ImmutableBytesWritable , Mutation> {
		  protected PubMedLibrary pml;
		  protected List<String> annotationSets;
		  protected String gateHome;

		  public static final byte[] colFamily = Bytes.toBytes("a");
		  public static final byte[] docColFamily = Bytes.toBytes("d");
		  private String colStr = "g";
		  private byte[] col = Bytes.toBytes(colStr);
		  protected boolean intialize = false;
		  protected boolean forcedTagging = false;
		  protected boolean useStemming = false;

	      @Override
	      protected void setup(Context context) throws IOException, InterruptedException {
	    	  Configuration conf = context.getConfiguration();
	    	  annotationSets = new ArrayList<String>();

			  Path[] localCache = context.getLocalCacheArchives();
	    	  gateHome = localCache[0].toString();
	    	  Collection<String> setList = conf.getStringCollection(PubMedLibrary.MR_ANN_SETS);
	    	  for (String annSet : setList) {
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

			if (intialize == false) {
				pml = new PubMedLibrary();
				pml.setAnnotationSets(annotationSets);
				pml.resetAnnotator(gateHome, useStemming);
				intialize = true;
			}
			String annTag = Bytes.toString(result.getValue(docColFamily, col));
			long docTS=0, annTS=0;
			boolean hasArticle = false;
			List<Cell> cells=result.listCells();
			for(Cell c:cells){
				String column=new String(CellUtil.cloneQualifier(c));
				if(column.equals("x")){
                    docTS=c.getTimestamp();
					hasArticle=true;
				}
				if (column.equals(colStr)) annTS = c.getTimestamp();
				if (docTS > 0 && annTS > 0) break;
			}

			if (hasArticle && (forcedTagging || annTS < docTS || annTag == null || !annTag.equals("Y"))) {
				List<String> annotations = pml.mrAnnotatePmcHResult(result, this.gateHome, useStemming);
				String finalStr = "";
                String pmid = pml.mrArticleDao.pmid.toString();
                for (String ann : annotations) {
                    finalStr += ann + "|";
                }
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
		}
	  }

	  public static Job configureJob(Configuration conf, String [] args) throws IOException {
		  Path hdfsGateAppPath = new Path(args[1]);
          Scan sc=new Scan();
		  conf.set(TableInputFormat.INPUT_TABLE, args[0]);
		  conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, "d");
		  String scanConvertedString=TableMapReduceUtil.convertScanToString(sc);
		  conf.set(TableInputFormat.SCAN, scanConvertedString );
          conf.set("annotation_useStemming", args[2]);
          conf.set("annotationColumn", args[3]);
          String annotationSets = args[4];
          conf.setStrings(PubMedLibrary.MR_ANN_SETS, annotationSets);
          Job job = new Job(conf, "Annotator using " + args[1]);
          job.setJarByClass(DistributedPmcAnnotator.class);
          job.setMapperClass(Map.class);
          job.setNumReduceTasks(0);
          job.setInputFormatClass(TableInputFormat.class);
          job.addCacheArchive(hdfsGateAppPath.toUri());
          TableMapReduceUtil.initTableReducerJob(args[0], null, job);
          return job;
      }

	  public static void main(String[] args) throws Exception {
		  Configuration conf = HbaseUtils.createConfig();
		  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		  if(otherArgs.length < 5) {
			  System.err.println("At least 5 parameters: <table name> <path to gate> <use stemming> <column name> <annotation set> ...");
			  System.exit(-1);
		  }
		  Job job = configureJob(conf, otherArgs);
		  System.exit(job.waitForCompletion(true) ? 0 : 1);
      }
}
