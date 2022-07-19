package edu.mcw.rgd.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.Frequency;

public class SortedCountMap {
	private HashMap<Object, Object> _unsortedMap;
	/**
	 * @return the _unsortedPositions
	 */
	public HashMap<Object, String> get_unsortedPositions() {
		return _unsortedPositions;
	}

	/**
	 * @param _unsortedPositions the _unsortedPositions to set
	 */
	public void set_unsortedPositions(HashMap<Object, String> _unsortedPositions) {
		this._unsortedPositions = _unsortedPositions;
	}

	private HashMap<Object, Long> _unsortedCounts;
	private HashMap<Object, String> _unsortedPositions;

	private List<Long> _sortedCounts;
	private List<Object> _sortedKeys;
	private Frequency _keyFrquency;


	
	public SortedCountMap() {
		init();
	}
	
	private void init() {
		_unsortedMap = new HashMap<Object, Object>();
		_unsortedCounts = new HashMap<Object, Long>();
		_unsortedPositions = new HashMap<Object, String>();
		_sortedCounts = new ArrayList<Long>();
		_sortedKeys = new ArrayList<Object>();
		_keyFrquency = new Frequency();


	}
	
	@SuppressWarnings("deprecation")
	public void add(Object key, Object value, String valuePos) {
		add(key, value);
		if (_unsortedPositions.get(key) == null) {
			_unsortedPositions.put(key, new String(valuePos));
		} else {
			String pos = _unsortedPositions.get(key);
			pos += "|" + valuePos;
			_unsortedPositions.put(key,pos);
		}
	}

	@SuppressWarnings("deprecation")
	public void add(Object key, Object value) {
		_unsortedMap.put(key, value);
		_keyFrquency.addValue((String)key);
	}
	public void sort() throws Exception {
		sort(false,null);
	}
	public void sort(boolean removeParents,HashMap<String,List<String>> data) throws Exception {
		// Generate key counts
		Iterator<Comparable<?>> it = _keyFrquency.valuesIterator();

		List<Object> validKeys = new ArrayList<Object>();
		List<Object> invalidKeys = new ArrayList<Object>();
		while (it.hasNext()) {
			invalidKeys.clear();
			Object cur_key = it.next();
			long cur_freq = _keyFrquency.getCount((String)cur_key);
			if (removeParents) {
				boolean keyIsValid = true;
				for (Object obj : validKeys) {
					if (data.get((String) obj) != null && data.get((String) obj).contains((String)cur_key)) {
						keyIsValid = false;
						break;
							} else if (data.get((String)cur_key) != null && data.get((String)cur_key).contains((String) obj)) {
						invalidKeys.add(obj);
					}
				}
				for (Object obj : invalidKeys) {
					_unsortedCounts.remove(obj);
					validKeys.remove(obj);
				}
				if (keyIsValid) {
					_unsortedCounts.put(cur_key, new Long(cur_freq));
					validKeys.add(cur_key);
				}
			} else
			{
				_unsortedCounts.put(cur_key, new Long(cur_freq));
			}
		}

		while (_unsortedCounts.size() > 0) {
			Object max_obj_key = null;
			Long max_value = new Long(0);
			for (Object key : _unsortedCounts.keySet()) {
				if (max_value <= _unsortedCounts.get(key)) {
					max_value = _unsortedCounts.get(key);
					max_obj_key = key;
				}
			}
			_sortedKeys.add(max_obj_key);
			_sortedCounts.add(max_value);
			_unsortedCounts.remove(max_obj_key);
		}

	}
	public void appendVirtualEntry(Object key) {
		if (_unsortedPositions.get(key) != null) return;
		add(key, key);
		_sortedKeys.add(key);
		_sortedCounts.add(new Long(0));
		String pos = "3;0-0";
		_unsortedPositions.put(key,pos);
	}
	
	public List<Long> getSortedCounts() {
		return _sortedCounts;
	}
	
	public List<Object> getSortedKeys() {
		return _sortedKeys;
	}
	
	public HashMap<Object, Object> getUnsortedMap() {
		return _unsortedMap;
	}
	
	public static void main(String[] args) throws Exception {
		SortedCountMap sm = new SortedCountMap();
		sm.add("RDO:0000001", "", "0;2-3");
		sm.add("RDO:0000001", "", "0;5-6");
		sm.add("RDO:0000001", "", "1;2-3");
		sm.add("RDO:0005741", "", "0;2-3");
		sm.add("RDO:0005741", "", "0;2-3");
		sm.add("RDO:0005837", "", "0;2-3");
		sm.add("RDO:0005837", "", "0;2-3");
		sm.add("RDO:0005837", "", "0;2-3");
		sm.add("RDO:0006036", "", "0;2-3");
		sm.add("RDO:0006036", "", "0;2-3");
		sm.add("RDO:0005741", "", "0;2-3");
		sm.add("RDO:0000004", "", "0;2-3");
		sm.appendVirtualEntry("RDO:0000004");
		sm.appendVirtualEntry("RDO:8888888");
		System.out.println("sorted result");
		List<Long> sorted_counts = sm.getSortedCounts();
		List<Object> sorted_keys = sm.getSortedKeys();
		HashMap<Object, Object> unsorted_map = sm.getUnsortedMap();
		HashMap<Object, String> unsorted_pos = sm.get_unsortedPositions();
		Iterator<Object> key_it = sorted_keys.iterator();
		Iterator<Long> count_it = sorted_counts.iterator();
		while (key_it.hasNext()) {
			Object key = key_it.next();
			Long count = count_it.next();
			System.out.println((String)key + "  " + 
					unsorted_map.get(key) + "  " + count + " " + unsorted_pos.get(key));
		}
	}
}
