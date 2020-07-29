package com.gtric.domain.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.gtric.domain.tricluster.Tricluster;
import com.gtric.types.Background;

public abstract class Dataset {

	private static final double MISSING = 999999;

	
	private int numRows;
	private int numCols;
	private int numContexts;
	
	private Background background;

	private HashMap<Integer, List<String>> elements;
	private Map<String, List<Integer>> elementsReversed;
	
	private Set<String> missingElements;
	private Set<String> noisyElements;
	private Set<String> errorElements;


	/********************************
	 ********* CONSTRUCTORS *********
	 ********************************/
	
	public Dataset(int numRows, int numCols, int numContexts, Background background) {
		
		this.numRows = numRows;
		this.numCols = numCols;
		this.numContexts = numContexts;
		this.background = background;
		this.elements = new HashMap<>();
		this.elementsReversed = new HashMap<>();
		this.missingElements = new TreeSet<>();
		this.noisyElements = new TreeSet<>();
		this.errorElements = new TreeSet<>();
		
	}
	public int getSize() {
		return this.numRows * this.numCols * this.numContexts;
	}
	
	public int getBackgroundSize() {
		return this.getSize() - this.getElements().size();
	}
	
	public void addMissingElement(String e) {
		this.missingElements.add(e);
	}
	
	public boolean isMissing(String e) {
		return this.missingElements.contains(e);
	}
	
	public void addNoisyElement(String e) {
		this.noisyElements.add(e);
	}
	
	public boolean isNoisy(String e) {
		return this.noisyElements.contains(e);
	}
	
	public void addErrorElement(String e) {
		this.errorElements.add(e);
	}
	
	public boolean isError(String e) {
		return this.errorElements.contains(e);
	}
	
	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}
	
	public int getNumContexts() {
		return numContexts;
	}

	public int getNumTrics() {
		return this.elements.size();
	}


	public Background getBackground() {
		return background;
	}

	public int getNumberOfMissings() {
		return this.missingElements.size();
	}

	public int getNumberOfNoisy() {
		return this.noisyElements.size();
	}
	
	public int getNumberOfErrors() {
		return this.errorElements.size();
	}
	
	public Set<String> getMissingElements(){
		return this.missingElements;
	}
	
	public Set<String> getNoisyElements(){
		return this.noisyElements;
	}
	
	public Set<String> getErrorElements(){
		return this.errorElements;
	}
	
	public void addElement(String e, int k) {
		
		if(!this.elements.containsKey(k)) {
			List<String> elems = new ArrayList<>();
			elems.add(e);
			this.elements.put(k, elems);
		}
		else
			this.elements.get(k).add(e);
		
		if(!this.elementsReversed.containsKey(e)) {
			List<Integer> trics = new ArrayList<>();
			trics.add(k);
			this.elementsReversed.put(e, trics);
		}
		else
			this.elementsReversed.get(e).add(k);
	}
	
	public abstract Tricluster getTriclusterById(int id);
	
	public List<String> getTriclusterElements(int id){
		return this.elements.get(id);
	}
	
	public Set<String> getElements() {
		return elementsReversed.keySet();
	}
	
	public Set<Integer> getTriclusters(){
		return elements.keySet();
	}
	
	public List<Integer> getTricsByElem(String e){
		return this.elementsReversed.get(e);
	}
	
	public boolean isPlanted(String e) {
		return this.elementsReversed.containsKey(e);
	}
	
	public abstract void plantMissingElements(double percMissing, double percTricluster);

	public abstract String getTricsInfo(); 
	
}
