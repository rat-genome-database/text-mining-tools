package edu.mcw.rgd.nlp.utils.ncbi;


import java.io.*;
import java.net.URI;
import java.util.*;


import edu.mcw.rgd.nlp.utils.solr.PubMedSolrDoc;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;

import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

import edu.mcw.rgd.nlp.classifier.ArticleOrganismClassifier;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.json.JSONObject;


/**
 * Indexer class that utilizes HBase as PubMed data storage 
 * @author wliu
 *
 */


public class IndexBuilder {

	/**
	 * Internal Mapper to be run by Hadoop.
	 */

String address="";
	public static class Map extends
	Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Mutation> {

        int counter;
		HashMap<String,List<String>> data = new HashMap<>();

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			ArticleOrganismClassifier aoc = new ArticleOrganismClassifier();
			aoc.LoadFromHDFS(context);
            counter = 0;


		}


@Override
protected void cleanup(Context context) throws IOException{
        String pathString = "s3://emr-repository/output/";

        FileSystem fs = FileSystem.get(java.net.URI.create("s3://emr-repository/output/"), context.getConfiguration());
    //FSDataOutputStream output ;

                Path path = new Path(pathString+context.getTaskAttemptID()+".json");
                if(PubMedLibrary.jsonObjects.size() != 0) {
                    //output = fs.create(path);
					BufferedWriter output = new BufferedWriter
							(new OutputStreamWriter(fs.create(path)));
					for (JSONObject j : PubMedLibrary.jsonObjects) {
						//ClientUtils.writeXML(j,output);
                    	output.write(j.toString());
						output.write("\n");
					}
                    //out.writeBytes(solr_doc.getFieldValue("pmid")+","+solr_doc.getFieldValue("title") + "\n");
					output.close();
                    fs.close();
                    PubMedLibrary.jsonObjects.clear();
                }

}

		@Override
		protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
				throws IOException, InterruptedException {
			try {
				Configuration conf=context.getConfiguration();
				//DataSourceFactory.MYSQL_DB_URL=conf.get("MYSQL_DB_URL");
				//DataSourceFactory.MYSQL_DB_USERNAME=conf.get("MYSQL_DB_USERNAME");
				//DataSourceFactory.MYSQL_DB_PASSWORD=conf.get("MYSQL_DB_PASSWORD");
				if(data.size() == 0){
				Path[] localCache = context.getLocalCacheArchives();
				BufferedReader br = new BufferedReader(new FileReader(localCache[0].toString()));
					String line;
					List<String> terms;
					while ((line=br.readLine()) != null){
						String[] arr= line.split(",");
						if(data.size() == 0 || !data.containsKey(arr[0]))
							terms = new ArrayList<>();
						else terms = data.get(arr[0]);
						terms.add(arr[1]);

						data.put(arr[0],terms);
					}
					System.out.println("Data size: "+ data.size());
					br.close();

				}
				if (!PubMedLibrary.indexArticle(result,data))
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

		String tableName=args[0];

		//conf.setStrings("MYSQL_DB_URL", args[1]);
		//conf.setStrings("MYSQL_DB_USERNAME", args[2]);
		//conf.setStrings("MYSQL_DB_PASSWORD", args[3]);
		//conf.setStrings("HOST_NAME", args[4]);
		conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));

		conf.set(TableInputFormat.INPUT_TABLE, tableName);

		Path filePath = new Path("s3://emr-repository/terms.csv");
		Job job = new Job(conf, "Indexing HBase:" + tableName + " to Solr");

		job.setJarByClass(IndexBuilder.class);
		job.setMapperClass(Map.class);
		job.setNumReduceTasks(0);
		job.setInputFormatClass(TableInputFormat.class);
		job.addCacheArchive(filePath.toUri());
		TableMapReduceUtil.initTableReducerJob(tableName, null, job);

		return job;
	}


	
	public static String convertScanToString(Scan scan) throws IOException {
		   return Base64.getEncoder().encodeToString(ProtobufUtil.toScan(scan).toByteArray());
		 }


	
	public static void main(String[] args) throws Exception {
		
		System.out.println("============================================================");

		Configuration conf = HBaseConfiguration.create();
		conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
		//conf.addResource(new Path("/usr/local/hbase/conf/hbase-site.xml"));
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.client.retries.number", Integer.toString(1));
		conf.set("zookeeper.session.timeout", Integer.toString(60000));
		conf.set("zookeeper.recovery.retry", Integer.toString(0));

        System.out.println(conf.get("dfs.support.append"));
        conf.set("dfs.support.append","true");
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
