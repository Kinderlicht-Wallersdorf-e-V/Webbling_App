package de.kettl.webserver;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSet {
	
	private HashMap<String, Integer> labels;
	private ArrayList<String[]> rows;
	private int pointer;
	private int length;
	private int labelCount;
	
	private final String LINE_SEPERATOR = "<<#newline#>>";
	private final String COLUMN_SEPERATOR = "<#newcolumn#>";
	
	public ResultSet(String python) {
		String[] rows = python.split(LINE_SEPERATOR);
		this.labels = new HashMap<>();
		this.rows = new ArrayList<>();
		if (rows.length > 0) {
			pointer = 0;
			String[] labels = rows[0].split(COLUMN_SEPERATOR);
			labelCount = labels.length;
			for(int i = 0; i < labelCount; i++) {
				this.labels.put(labels[i], i);
			}
			for(int i = 1; i < rows.length; i++) {
				this.rows.add(rows[i].split(COLUMN_SEPERATOR));
			}
			length = this.rows.size();
			
		}
	}
	
	public boolean hasNext() {
		return pointer < length;
	}
	
	public void next() {
		pointer++;
	}
	
	public String[] getLabels() {
		String[] messed = labels.keySet().toArray(new String[] {});
		String[] sorted = new String[messed.length];
		
		for(String s: messed) {
			sorted[labels.get(s)] = s;
		}
		return sorted;
	}
	
	public String get(int label) {
		return rows.get(pointer)[label];
	}
	
	public String get(String label) {
		return get(labels.get(label));
	}
	
	public int getInt(String label) {
		return Integer.parseInt(get(label));
	}
	
	public int getInt(int label) {
		return Integer.parseInt(get(label));
	}
	
	public String[] getRow(int row) {
		if(row >= length) {
			throw new IndexOutOfBoundsException();
		} else {
			return rows.get(row);
		}
	}

	public int getLabelCount() {
		return labelCount;
	}
	
	public int rowCount() {
		return length;
	}


}
