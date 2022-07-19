package edu.mcw.rgd.nlp.utils.gate;

import gate.Document;
import gate.Corpus;
import gate.CorpusController;

import gate.Gate;
import gate.Factory;

import gate.util.GateException;

import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.fs.Path;

public class AnnieAnnotator {
	private CorpusController application = null;
	private Corpus corpus = null;
	private boolean useStemming = false;
	
	public void init(String gappFile) throws Exception {
		Gate.init();
		application = (CorpusController) PersistenceManager
				.loadObjectFromFile(new File(gappFile));
		corpus = Factory.newCorpus("Annotator Corpus");
		application.setCorpus(corpus);
	}

	public void initOnHDFS(String gateHome) throws GateException, IOException {
		Gate.runInSandbox(true);
		Gate.setGateHome(new File(gateHome));
		Gate.setPluginsHome(new File(gateHome, "plugins"));
		Gate.init();
		
		URL applicationURL = new URL("file:" + new Path(gateHome, "application.xgapp").toString());
		
		System.out.println("applicationURL: \t"+applicationURL);
		System.out.println("gate home: \t" + gateHome );
		
		application = (CorpusController) PersistenceManager
				.loadObjectFromUrl(applicationURL);
		
		corpus = Factory.newCorpus("Annotator Corpus");
		application.setCorpus(corpus);
		System.out.println("HDFS Gate in " + gateHome + " initialized.");
	}
	
	public Document process(String input_str) {
		try {
			clear();
			Document doc = Factory.newDocument(input_str);
			corpus.add(doc);
			application.execute();
			return doc;
		} catch (Exception e) {
			System.err.println("Error in annotating:-------");
			System.err.println("input_str");
			System.err.println("----------------------");
			e.printStackTrace();
			return null;
		}
	}
	public Document process1(String input_str, Long pmid) {
		try {
			clear();
			Document doc = Factory.newDocument(input_str);
			corpus.add(doc);
			application.execute();
			return doc;
		} catch (Exception e) {
			System.err.println("Error in annotating:-------" + pmid);
			System.err.println("input_str");
			System.err.println("----------------------");
			e.printStackTrace();
			return null;
		}
	}

	public void clear() {
		if (corpus.size() > 0) {
			Document doc = (Document) corpus.get(0);
			corpus.remove(doc);
			doc.cleanup();
			corpus.clear();
			corpus.cleanup();
			Factory.deleteResource(doc);
		}
	}

	/**
	 * @return the useStemming
	 */
	public boolean isUseStemming() {
		return useStemming;
	}

	/**
	 * @param useStemming the useStemming to set
	 */
	public void setUseStemming(boolean useStemming) {
		this.useStemming = useStemming;
	}

}
