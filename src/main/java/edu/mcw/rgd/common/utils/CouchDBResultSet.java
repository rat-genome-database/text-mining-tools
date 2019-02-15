package edu.mcw.rgd.common.utils;

import java.util.Collection;

public class CouchDBResultSet {
	
	public int total_rows;
	public int offset;
	public Collection<IntStringRow> rows;
	
	public class IntStringRow {
		public String id;
		public Integer key;
		public String value;
	}
}
