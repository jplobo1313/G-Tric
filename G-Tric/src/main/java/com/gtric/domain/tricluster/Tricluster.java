/**
 * @author Joao Lobo
 * @contact jlobo@lasige.di.fc.ul.pt
 * @version 1.0
 */

package com.gtric.domain.tricluster;

import java.util.Set;

import org.json.JSONObject;

import com.gtric.domain.dataset.Dataset;
import com.gtric.types.PatternType;
import com.gtric.types.PlaidCoherency;

public abstract class Tricluster {
	
	private int id;
	
	private PatternType contextPattern;
	private PlaidCoherency plaidPattern;
	
	private int numOfMissings;
	private int numOfNoisy;
	private int numOfErrors;
	
	public Tricluster(PatternType contextPattern, PlaidCoherency plaidPattern, int id) {
		
		this.id = id;
		this.contextPattern = contextPattern;
		this.plaidPattern = plaidPattern;
		this.numOfMissings = 0;
		this.numOfNoisy = 0;
		this.numOfErrors = 0;
	}
	
	public abstract int getSize();
	public abstract Set<Integer> getRows();
	public abstract Set<Integer> getColumns();
	public abstract Set<Integer> getContexts();
	
	public int getId() {
		return this.id;
	}
	
	public abstract PatternType getRowPattern();
	
	public abstract PatternType getColumnPattern();
	
	public PatternType getContextPattern() {
		return this.contextPattern;
	}
	
	public PlaidCoherency getPlaidCoherency() {
		return this.plaidPattern;
	}
	
	public int getNumberOfMissings() {
		return this.numOfMissings;
	}
	
	public void addMissing(){
		this.numOfMissings++;
	}
	
	public int getNumberOfNoisy() {
		return this.numOfNoisy;
	}
	
	public void addNoisy(){
		this.numOfNoisy++;
	}
	
	public int getNumberOfErrors() {
		return this.numOfErrors;
	}
	
	public void addError(){
		this.numOfErrors++;
	}
	
	public abstract String matrixToString(Object[][] matrix);
	
	public abstract String toString();
	
	public abstract JSONObject toStringJSON(Dataset generatedDataset);
}
