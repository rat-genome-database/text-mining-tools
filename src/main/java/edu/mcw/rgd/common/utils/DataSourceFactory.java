package edu.mcw.rgd.common.utils;

import com.mysql.cj.jdbc.MysqlDataSource;


import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by jthota on 1/18/2019.
 */
public class DataSourceFactory {
    public static String MYSQL_DB_URL;
    public static String MYSQL_DB_USERNAME;
    public static String MYSQL_DB_PASSWORD;
   public static DataSource getMySQLDataSource() throws Exception{
         MysqlDataSource ds=null;
            ds=new MysqlDataSource();
            ds.setURL(MYSQL_DB_URL);
            ds.setUser(MYSQL_DB_USERNAME);
            ds.setPassword(MYSQL_DB_PASSWORD);
        return ds;
    }

