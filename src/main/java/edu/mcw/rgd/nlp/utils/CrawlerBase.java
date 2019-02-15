/**
 * 
 */
package edu.mcw.rgd.nlp.utils;

import org.apache.log4j.Logger;

/**
 * @author wliu
 *
 */
public abstract class CrawlerBase implements Crawler {
	
	protected String _hostName;
	protected static final Logger logger = Logger.getLogger(CrawlerBase.class);
	protected String crawlResult = "";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the crawlResult
	 */
	public String getCrawlResult() {
		return crawlResult;
	}

	/**
	 * @param crawlResult the crawlResult to set
	 */
	public void setCrawlResult(String crawlResult) {
		this.crawlResult = crawlResult;
	}

}
