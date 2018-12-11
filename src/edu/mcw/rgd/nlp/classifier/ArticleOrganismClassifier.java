package edu.mcw.rgd.nlp.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.math.stat.Frequency;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class ArticleOrganismClassifier {
	
	private static HashMap<Long, String> idNameMap = null;
	private final static String ID_NAME_MAP_PATH = "/user/rgdpub/data/organism_tagger/id2sciName.lst";
	private Frequency idFrequency = new Frequency();
	
	public ArticleOrganismClassifier() {
//		reset();
	}
	
	public void reset() {
		idFrequency.clear();
		if (idNameMap == null) {
			idNameMap = new HashMap<Long, String>();
			File file = new File(ID_NAME_MAP_PATH);
			try {
				LineIterator it = FileUtils.lineIterator(file);
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] line_values = line.split("\t");
					idNameMap.put(Long.parseLong(line_values[0]), line_values[1]);
				}
				it.close();
			} catch (Exception e) {
				System.err.println("Error loading organism ID list from " + ID_NAME_MAP_PATH);
				e.printStackTrace();
			}
		}
	}
	
	public void LoadFromHDFS(Context context) {
		idFrequency.clear();
		if (idNameMap == null) {
			idNameMap = new HashMap<Long, String>();
			try {
				Path path = new Path(ID_NAME_MAP_PATH);
				FileSystem fs = path.getFileSystem(context.getConfiguration());
				FSDataInputStream fsis = fs.open(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(fsis));
				String line = "";
				while ((line = br.readLine()) != null) {
					String[] line_values = line.split("\t");
					idNameMap.put(Long.parseLong(line_values[0]), line_values[1]);
				}
				br.close();
			} catch (Exception e) {
				System.err.println("Error loading organism ID list from " + ID_NAME_MAP_PATH);
				e.printStackTrace();
			}
		}
	}
	
	public void AddID(long value) {
		idFrequency.addValue(value);
	}
	
	public long getResult() {
		Iterator<Comparable<?>> it = idFrequency.valuesIterator();
		long return_value = 0;
		long maxCount = 0; 
		while (it.hasNext()) {
			long cur_value = (Long) it.next();
			long cur_freq = idFrequency.getCount(cur_value);
			if (maxCount <= cur_freq) {
				return_value = cur_value;
				maxCount = cur_freq;
			}
		}
		return return_value;
	}
	
	public static String getNameByID(long id) {
		if (idNameMap == null) {
			new ArticleOrganismClassifier();
		}
		return (String) idNameMap.get(id);
	}
	
	public static void main(String[] args) {
		ArticleOrganismClassifier aoc = new ArticleOrganismClassifier();
		aoc.AddID(10090);
		aoc.AddID(10090);
		aoc.AddID(10090);
		aoc.AddID(10090);
		aoc.AddID(10116);
		aoc.AddID(10116);
		aoc.AddID(10116);
		aoc.AddID(10116);
		aoc.AddID(10116);
		aoc.AddID(10116);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		aoc.AddID(9606);
		long result_id = aoc.getResult();
		System.out.println("Final value: " + result_id);
		System.out.println("Final name: " + ArticleOrganismClassifier.getNameByID(result_id));
	}
}
