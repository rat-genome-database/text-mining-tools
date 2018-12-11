package edu.mcw.rgd.nlp.utils;

import java.util.Date;

public interface Crawler {
	public void initialize();
	public void prepare();
	public void execute() throws Exception;
	public void process();
	public String crawlByDate(Date date) throws Exception ;
}
