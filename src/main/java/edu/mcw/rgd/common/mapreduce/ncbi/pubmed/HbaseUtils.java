package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.ConstantSizeRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseUtils {
  public static void createTable(String tableName, Configuration conf) throws Exception{

      Connection connection = ConnectionFactory.createConnection(conf);
      Admin hba = connection.getAdmin();
      if (!hba.tableExists(TableName.valueOf(tableName))) {

          TableDescriptorBuilder ht = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
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

          System.out.println("New Table Created :"+ hba.tableExists(TableName.valueOf(tableName)));
      } else {
          System.out.println("Table Exists :");
      }
  }

    public static Configuration createConfig() {
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
        conf.set("hbase.client.retries.number", Integer.toString(1));
        conf.set("zookeeper.session.timeout", Integer.toString(60000));
        conf.set("zookeeper.recovery.retry", Integer.toString(0));
        return conf;
    }
}
