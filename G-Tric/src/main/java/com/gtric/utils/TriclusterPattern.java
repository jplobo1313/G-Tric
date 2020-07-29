package com.gtric.utils;

import com.gtric.types.PatternType;

public class TriclusterPattern {

	PatternType rows;
	PatternType columns;
	PatternType contexts;
	
	public TriclusterPattern(PatternType rows, PatternType columns, PatternType contexts) {
		
		this.rows = rows;
		this.columns = columns;
		this.contexts = contexts;
	}
	
	public PatternType getRowsPattern() {
		return this.rows;
	}
	
	public PatternType getColumnsPattern() {
		return this.columns;
	}
	
	public PatternType getContextsPattern() {
		return this.contexts;
	}
	
	public boolean contains(PatternType pattern) {
		return (this.rows.equals(pattern) || this.columns.equals(pattern) || this.contexts.equals(pattern));
	}
}
