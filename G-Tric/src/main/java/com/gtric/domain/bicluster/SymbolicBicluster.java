package com.gtric.domain.bicluster;

import java.util.Set;
import java.util.SortedSet;

import com.gtric.types.PatternType;

public class SymbolicBicluster extends Bicluster{
	
	private String[][] seed;
	
	public SymbolicBicluster(SortedSet<Integer> rows, SortedSet<Integer> cols, PatternType rowPattern, PatternType columnPattern) {
		
		super(rows, cols, rowPattern, columnPattern);
	}

	public void setSeed(String[][] seed) {
		this.seed = seed;
	}
	
	public String[][] getSeed() {
		return this.seed;
	}

	@Override
	public String toString() {
		
		Set<Integer> rows = getRows();
		Set<Integer> columns = getColumns();
		
		StringBuilder res = new StringBuilder();
		res.append(" (" + rows.size() + "," + columns.size() + "), X=[");
		for (int i : rows)
			res.append(i + ",");
		res.append("], Y=[");
		for (int i : columns)
			res.append(i + ",");
		res.append("],");
		if (seed != null) {
			/*
			res.append(" Seed=[");
			for (String i : seed)
				res.append(i + ",");
			res.append("],");
			*/
			res.append(" Seed=" + seed + ",");
		}
		res.append(" RowPattern=" + getRowPattern() + ",");
		res.append(" ColumnPattern=" + getColumnPattern());
		return res.toString().replace(",]", "]");
	}
		
	
	
}
