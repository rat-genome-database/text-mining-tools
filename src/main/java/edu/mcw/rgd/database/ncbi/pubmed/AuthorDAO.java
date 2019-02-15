package edu.mcw.rgd.database.ncbi.pubmed;

import java.sql.ResultSet;

import edu.mcw.rgd.common.utils.BasicUtils;
import edu.mcw.rgd.common.utils.DAOBase;


public class AuthorDAO extends DAOBase {

	public static String tableName = "authors";
	
	public static void insert(String pmid, String author_number, String author) {
		try {
			DocDBConnection.closeRsStatement(
			DocDBConnection.executeQuery("insert into " + tableName + " values (" 
					+ pmid + "," 
					+ author_number + ","
					+ BasicUtils.EscapeSQLStringValue(author)
					+");"
			));
		} catch (Exception e) {
			logger.error("Error inserting author " + pmid, e);
			return;
		}
	}
	
	public static String get(String pmid) {
		ResultSet rs = DocDBConnection.executeQuery("select * from "
				+ tableName + " where pmid=" + Long.parseLong(pmid) + " order by authornumber");
		String author_string = "";
		int author_num = 0;
		try {
			while (rs.next()) {
				if (author_num > 0) author_string += ", ";
				author_string += (rs.getString("LASTNAME") + " " + rs.getString("INITIALS"));
				author_num ++;
			}
			if (author_string.length() > 0) author_string += ".";
			DocDBConnection.closeRsStatement(rs);
			return author_string;
			
		} catch (Exception e)
		{
			logger.error("Error getting author " + pmid, e);
			return "";
		}
	}
}
