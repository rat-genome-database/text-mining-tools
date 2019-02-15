/**
 * 
 */
package edu.mcw.rgd.nlp.utils;

import org.apache.log4j.Logger;

/**
 * @author wliu
 *
 */
public abstract class LibraryBase implements Library {
	
	protected String pathDoc;
	protected static final Logger logger = Logger.getLogger(LibraryBase.class);

	/**
	 * @return the _pathDoc
	 */
	public String getPathDoc() {
		return pathDoc;
	}

	/**
	 *
	 */
	public void setPathDoc(String pathDoc) {
		this.pathDoc = pathDoc + ((pathDoc.charAt(pathDoc.length() - 1) == '/') ?  "" : "/");
	}
	
	public String getDocPath() {
		return getPathDoc();
	}

	/**
	 * 
	 */
	public LibraryBase() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
