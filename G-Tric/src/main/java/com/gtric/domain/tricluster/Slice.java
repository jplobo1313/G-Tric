package com.gtric.domain.tricluster;

public class Slice<T> {

	private int contextID;
	private T factor;
	private T[][] patternSeed;
	
	public Slice(int contextID) {
		this.contextID = contextID;
		this.factor = null;
		this.patternSeed = null;
	}
	
	public Slice(int contextID, T factor) {
		this(contextID);
		this.factor = factor;
	}
	
	public Slice(int contextID, T[][] patternSeed) {
		this(contextID);
		this.patternSeed = patternSeed;
	}

	public int getContextID() {
		return contextID;
	}

	public void setContextID(int contextID) {
		this.contextID = contextID;
	}

	public T getFactor() {
		return factor;
	}

	public void setFactor(T factor) {
		this.factor = factor;
	}

	public T[][] getPatternSeed() {
		return patternSeed;
	}

	public void setPatternSeed(T[][] patternSeed) {
		this.patternSeed = patternSeed;
	}
	
	
}
