package edu.mcw.rgd.common.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;



public class HBaseConnection {
	protected String dbHost;
	protected String dbName;
	protected String key;
	protected String column;
	protected Configuration config;
	protected Table table;

	public String getBaseURL(String dbHost, String dbName) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		return dbHost + "/" + dbName + "/";
	}
	
	public String getBaseURL() {
		return getBaseURL(dbHost, dbName);
	}

	public String getRecordURL(String dbHost, String dbName, String key,
			String column) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.key = key;
		this.column = column;
		return dbHost + "/" + dbName + "/" + key + "/" + column;
	}
	
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}

	
	/* The URLs were for accessing HBAse with the REST service,
	 *  since we are using HBase Java API, there is not need to
	 *  generate the URLs.
	 
	public String getRecordURL(String dbName, String key,
			String column) {
		return getRecordURL(dbHost, dbName, key, column);
	}

	public String getRecordURL(String key, String column) {
		return getRecordURL(dbHost, dbName, key, column);
	}
	
	public String getRecordURL(String key) {
		return getRecordURL(dbHost, dbName, key, column);
	}
*/
	public Result getRecord(String key, String tableName) {
		  try {
			  if (config == null) init(tableName);
			   Get get = new Get(Bytes.toBytes(key));
			   Result result = table.get(get);
			  
			   return result; 
		 
			  } catch (Exception e) {
		 
				e.printStackTrace();
		 
				return null;
			  }
	}
	
	public static String getField(Result result, String columnFamily, String column) {
		  try {
			  if (result == null) return null;
			   byte[] value = result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
			  
			   return value == null ? null : new String(value); 
		 
			  } catch (Exception e) {
		 
				e.printStackTrace();
		 
				return null;
			  }
	}
	
	protected void init(String tableName) {
//       config = HBaseConfiguration.create();
       
      // config.addResource(new Path("/home/rgdpub/Tools/hbase.gray/conf/hbase-site.xml"));
//       config.addResource(new Path("/var/lib/ambari-server/resources/common-services/HBASE/0.96.0.2.0/configuration/hbase-site.xml"));

		config = HBaseConfiguration.create();
	    
//       config.set("hbase.zookeeper.quorum", this.dbHost);

	   try {
	  	Connection conn = ConnectionFactory.createConnection(config);
	   	table =  conn.getTable(TableName.valueOf(tableName));
      } catch (Exception e) {
	      e.printStackTrace();
	   }
	}

	public static void main(String[] args) {
		// You need a configuration object to tell the client where to connect.
		   // But don't worry, the defaults are pulled from the local config file.

		   // This instantiates an HTable object that connects you to the "myTable"
		   // table.

		   // To do any sort of update on a row, you use an instance of the BatchUpdate
		   // class. A BatchUpdate takes a row and optionally a timestamp which your
		   // updates will affect.
//		   BatchUpdate batchUpdate = new BatchUpdate("myRow");

		   // The BatchUpdate#put method takes a Text that describes what cell you want
		   // to put a value into, and a byte array that is the value you want to
		   // store. Note that if you want to store strings, you have to getBytes()
		   // from the string for HBase to understand how to store it. (The same goes
		   // for primitives like ints and longs and user-defined classes - you must
		   // find a way to reduce it to bytes.)
//		   batchUpdate.put("myColumnFamily:columnQualifier1",
//		     "columnQualifier1 value!".getBytes());

		   // Deletes are batch operations in HBase as well.
//		   batchUpdate.delete("myColumnFamily:cellIWantDeleted");

		   // Once you've done all the puts you want, you need to commit the results.
		   // The HTable#commit method takes the BatchUpdate instance you've been
		   // building and pushes the batch of changes you made into HBase.
//		   table.commit(batchUpdate);

		   
		   // Now, to retrieve the data we just wrote. The values that come back are
		   // Cell instances. A Cell is a combination of the value as a byte array and
		   // the timestamp the value was stored with. If you happen to know that the
		   // value contained is a string and want an actual string, then you must
		   // convert it yourself.
		HBaseConnection hbc = new HBaseConnection();
		hbc.setDbHost("gray07");
		   try {
				ArticleDAO art = new ArticleDAO();
					   Result result = hbc.getRecord(args[0], args[1]);
					   if (result != null) {
						   art.getArticleFromHResult(result);
						   
						   System.out.println("PMID: " + art.pmid + " Title: " + art.articleTitle );
						   System.out.println("PMID: " + art.pmid + " Abstract: " + art.articleAbstract );
						   System.out.println("PMID: " + art.pmid + " Mesh Terms: " + art.meshTerms );
					   }
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		return;
	}
}
