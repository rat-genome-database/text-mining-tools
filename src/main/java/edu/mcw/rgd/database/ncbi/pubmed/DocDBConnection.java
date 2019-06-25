package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mcw.rgd.common.utils.DataSourceFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import edu.mcw.rgd.nlp.utils.LibraryBase;

import javax.sql.DataSource;

public class DocDBConnection {
	static Connection DBConn;
	static String ConnectionString;
	private static DocDBConnection instance;

	protected static final Logger logger = Logger.getLogger(LibraryBase.class);

	protected DocDBConnection() {

		connect();
	}

	public static DocDBConnection getInstance() {
		if (instance == null) {
			instance = new DocDBConnection();
		}
		return instance;
	}

	public static DocDBConnection getInstance(String conn_string) {
		if (instance == null) {
			instance = new DocDBConnection(conn_string);
		}
		return instance;
	}

	protected DocDBConnection(String conn_string) {
		ConnectionString = conn_string;
		connect();
	}
	public static void connect()  {

		try{
			DataSource ds= DataSourceFactory.getMySQLDataSource();
			if(DBConn==null){
				DBConn=	ds.getConnection();
			}

		}catch (Exception e){
			e.printStackTrace();
		}

	}
/*	public static void connect() {
		if (DBConn == null) {
			ConnectionString ="jdbc:mysql://green.rgd.mcw.edu/pubmed" ;  // Connect to default database
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			try {
				DBConn = DriverManager.getConnection(
						ConnectionString +
								"?user=rattext&password=t3xt_mining_rgd2015r4t"
								+ "&useUnicode=true&characterEncoding=UTF-8"
				);
				if (DBConn == null)  {
					logger.error("Connection to DB can't be established!!!");
				} else {
					logger.info("Database connected!!!");
					executeQuery("SET NAMES utf8");
				}
			} catch (SQLException ex) {
				logger.error(ex.getMessage());
				logger.error(ex.getSQLState());
				logger.error(ex.getErrorCode());
			}
		}
	}*/

	public static void disconnect() {
		if (DBConn == null) {
			logger.info("Database not connected!!!");
			return;
		}

		try {
			DBConn.close();
		} catch (Exception e) {
			logger.error(e.toString());
		};
		DBConn = null;
		logger.info("Database disconnected!!!");
	}

	public static ResultSet executeQuery(String queryStatement) {
		Statement stmt = null;
		ResultSet rs = null;
		connect();
		if (DBConn == null) return rs;
		try {
			stmt = DBConn.createStatement();
//	           System.out.println("Executing SQL: " + queryStatement);
			if (stmt.execute(queryStatement)) {
				rs = stmt.getResultSet();
			}
		} catch (SQLException ex) {
			logger.error("Error SQL: " + ex.getMessage());
			logger.error("SQL statement: " + queryStatement);
			logger.error(ex.getSQLState());
			logger.error(ex.getErrorCode());
		}
		try {
			if (rs == null) stmt.close();
			return rs;
		} catch (Exception e) {
			return null;
		}
	}

	public static String escapeSQL(String raw_string) {
		String escaped_str = "";
		char[] cur_char = new char[2];
		char buf_char= ' ';
		boolean buf_empty = true;
		for (int i = 0; i < raw_string.length(); i++) {
			raw_string.getChars(i, i + 1, cur_char, 0);
			char ch = cur_char[0];
			// Check if it is a valid UTF8 char
			//if ((ch > 31 && ch < 253) || ch == '\t' || ch == '\n' || ch == '\r')
			{
				if (ch == '\\') {
					escaped_str += '\\';
				}
				escaped_str += ch;
			}

//			if (buf_empty) {
//				if (cur_char[0] == '\\') {
//					buf_char = cur_char[0];
//					buf_empty = false;
//				} else {
//					escaped_str += cur_char[0];
//				}
//			} else {
//				if (!(cur_char[0] == 'r' && cur_char[0] == 'n')) {
//					escaped_str += '\\';
//				}
//				escaped_str += buf_char;
//				escaped_str += cur_char[0];
//				buf_empty = true;
//			}
		}
		if (!buf_empty) {
			escaped_str += '\\';
			escaped_str += buf_char;
		}

		return StringEscapeUtils.escapeSql(escaped_str);
	}

	public static void closeRsStatement(ResultSet rs, Statement stmt) {
		try {
			if (rs != null) {
				if (stmt == null) stmt = rs.getStatement();
				rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void closeRsStatement(ResultSet rs) {
		closeRsStatement(rs, null);
	}

	public static void main(String[] args) throws Exception {

		ResultSet rs = DocDBConnection.executeQuery("select * from ont_term_connections where child_term like 'MP:%'");

		if (rs != null) {
			while(rs.next()){
				System.out.println(rs.getString(1)+ "\t"+ rs.getString(2));
			}
			closeRsStatement(rs);
		}
		DocDBConnection.disconnect();

	}
}

