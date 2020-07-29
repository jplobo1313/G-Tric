package com.gtric.domain.tricluster;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gtric.domain.bicluster.NumericBicluster;
import com.gtric.domain.dataset.Dataset;
import com.gtric.domain.dataset.NumericDataset;
import com.gtric.types.PatternType;
import com.gtric.types.PlaidCoherency;

public class NumericTricluster<T extends Number> extends Tricluster {

	private NumericBicluster<T> template;
	private List<Slice<T>> contexts;

	public NumericTricluster(int id, NumericBicluster<T> template, PatternType contextPattern, PlaidCoherency plaidPattern, int[] tricContexts) {
		super(contextPattern, plaidPattern, id);
		this.template = template;
		this.contexts = new ArrayList<>();

		for(int c : tricContexts)
			contexts.add(new Slice<T>(c));

	}

	public void addContext(int contextID, T factor) {
		this.contexts.add(new Slice<>(contextID, factor));
	}

	public void addContext(int contextID) {
		this.contexts.add(new Slice<>(contextID));
	}

	public void addContext(int contextID, T[][] patternSeed) {
		this.contexts.add(new Slice<>(contextID, patternSeed));
	}

	// ** setters **

	public void setSeed(T seed) {
		this.template.setSeed(seed);
	}

	public void setSeed(T[][] seed) {
		this.template.setSeed(seed);
	}

	public void setContextFactor(int context, T factor) {
		this.contexts.get(context).setFactor(factor);
	}

	public void setContextPattern(int context, T[][] patternSeed) {
		this.contexts.get(context).setPatternSeed(patternSeed);
	}

	public void setRowFactor(int row, T factor) {
		this.template.setRowFactor(row, factor);
	}

	public void setRowFactors(T[] factors) {
		this.template.setRowFactors(factors);
	}

	public void setColumnFactors(T[] factors) {
		this.template.setColumnFactors(factors);
	}

	public void setColumnFactor(int col, T factor) {
		this.template.setColumnFactor(col, factor);
	}

	// ****

	//** getters **

	public int getNumContexts() {
		return this.contexts.size();
	}

	public int getNumRows() {
		return this.template.numRows();
	}

	public int getNumCols() {
		return this.template.numColumns();
	}

	public T getContextFactor(int context) {
		return this.contexts.get(context).getFactor();
	}

	public T getRowFactor(int row) {
		return this.template.getRowFactor(row);
	}

	public T getColumnFactor(int col) {
		return this.template.getColumnFactor(col);
	}

	@Override
	public PatternType getRowPattern() {
		return this.template.getRowPattern();
	}
	
	@Override
	public PatternType getColumnPattern() {
		return this.template.getColumnPattern();
	}
	
	@Override
	public Set<Integer> getRows(){
		return this.template.getRows();
	}

	@Override
	public Set<Integer> getColumns(){
		return this.template.getColumns();
	}

	@Override
	public Set<Integer> getContexts(){
		SortedSet<Integer> ctxs = new TreeSet<>();

		for(Slice<T> s : this.contexts)
			ctxs.add(s.getContextID());

		return ctxs;
	}

	// ****

	@Override
	public String toString() {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		Set<Integer> rows = template.getRows();
		Set<Integer> columns = template.getColumns();
		T numericSeed = template.getNumericSeed();
		T[][] patternSeed = template.getPatternSeed();

		StringBuilder res = new StringBuilder();
		res.append("Tricluster #" + this.getId() + "\n");
		res.append(" (" + rows.size() + ", " + columns.size() + ", " + contexts.size() + "), X=[");
		for (int i : rows)
			res.append(i + ",");
		res.append("], Y=[");
		for (int i : columns)
			res.append(i + ",");
		res.append("], Z=[");
		for (Slice<T> s : contexts)
			res.append(s.getContextID() + ",");
		res.append("],");
		
		res.append(" RowPattern=" + template.getRowPattern() + ",");
		res.append(" ColumnPattern=" + template.getColumnPattern() + ",");
		res.append(" ContextPattern=" + super.getContextPattern() + ",");
		
		if (numericSeed != null) {
			
			res.append(" Seed=" + df.format(numericSeed) + ", ");

			//res.append(" RowPattern=" + template.getRowPattern() + ",");

			if(template.getRowFactors().length > 0) {
				res.append(" RowFactors=[");
				for (T i : template.getRowFactors())
					res.append(df.format(i) + ",");
				res.append("],");
			}

			//res.append(" ColumnPattern=" + template.getColumnPattern() + ",");

			if(template.getColumnFactors().length > 0) {
				res.append(" ColumnFactors=[");
				for (T i : template.getColumnFactors())
					res.append(df.format(i) + ",");
				res.append("],");
			}

			//res.append(" ContextPattern=" + super.getContextPattern() + ",");

			if(this.contexts.size() > 0) {
				res.append(" ContextFactors=[");
				for (Slice<T> s : this.contexts)
					res.append(df.format(s.getFactor()) + ",");
				res.append("]");
			}
		}
		/*
		else {
			if(patternSeed != null) 
				res.append(" Seed=" + matrixToString(patternSeed) + ", ");
			else if(this.contexts.get(0).getPatternSeed() != null) {
				res.append(" Seed={");
				for(Slice<T> s : this.contexts)
					res.append(s.getContextID() + ":" + matrixToString(s.getPatternSeed()) + ", ");
				res.delete(res.length() - 2, res.length());
				res.append("}, ");
			}

			res.append(" RowPattern=" + template.getRowPattern() + ",");
			res.append(" ColumnPattern=" + template.getColumnPattern() + ",");
			res.append(" ContextPattern=" + super.getContextPattern() + ",");
		}
		*/
		double missingsPerc = ((double) this.getNumberOfMissings()) / ((double) this.getSize()) * 100;
		double noisePerc = ((double) this.getNumberOfNoisy()) / ((double) this.getSize()) * 100;
		double errorsPerc = ((double) this.getNumberOfErrors()) / ((double) this.getSize()) * 100;
		
		
		res.append(" %Missings=" + df.format(missingsPerc) + ",");
		res.append(" %Noise=" + df.format(noisePerc) + ",");
		res.append(" %Errors=" + df.format(errorsPerc));

		return res.toString().replace(",]", "]");
	}

	@Override
	public JSONObject toStringJSON(Dataset generatedDataset) {

		JSONObject tricluster = new JSONObject();
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		Set<Integer> rows = template.getRows();
		Set<Integer> columns = template.getColumns();
		T numericSeed = template.getNumericSeed();
		
		tricluster.put("#rows", rows.size());
		tricluster.put("#columns", columns.size());
		tricluster.put("#contexts", contexts.size());
		
		tricluster.put("X", rows);
		tricluster.put("Y", columns);
		tricluster.put("Z", this.getContexts());
		
		tricluster.put("RowPattern", new String(template.getRowPattern().toString()));
		tricluster.put("ColumnPattern", new String(template.getColumnPattern().toString()));
		tricluster.put("ContextPattern", new String(this.getContextPattern().toString()));
		
		if (numericSeed != null) {
	
			tricluster.put("Seed", df.format(numericSeed));

			if(template.getRowFactors().length > 0) {
				
				T[] rowFactors = template.getRowFactors();
				String[] s = new String[template.numRows()];
				
				for(int i = 0; i < template.numRows(); i++) 
					s[i] = df.format(rowFactors[i]);
				
				tricluster.put("RowFactors", Arrays.toString(s));
			}

			if(template.getColumnFactors().length > 0) {
				
				T[] colFactors = template.getColumnFactors();
				String[] s = new String[template.numColumns()];
				
				for(int i = 0; i < template.numColumns(); i++) 
					s[i] = df.format(colFactors[i]);
				
				tricluster.put("ColumnFactors", Arrays.toString(s));
			}

			if(this.contexts.size() > 0) {
				String[] s = new String[this.getNumContexts()];
				
				for(int i = 0; i < this.getNumContexts(); i++)
					s[i] = df.format(this.getContextFactor(i));
				
				tricluster.put("ContextFactors", Arrays.toString(s));
			}
		}
		
		double missingsPerc = ((double) this.getNumberOfMissings()) / ((double) this.getSize()) * 100;
		double noisePerc = ((double) this.getNumberOfNoisy()) / ((double) this.getSize()) * 100;
		double errorsPerc = ((double) this.getNumberOfErrors()) / ((double) this.getSize()) * 100;

		tricluster.put("%Missings", df.format(missingsPerc));
		tricluster.put("%Noise", df.format(noisePerc));
		tricluster.put("%Errors", df.format(errorsPerc));
		
		tricluster.put("PlaidCoherency", new String(super.getPlaidCoherency().toString()));
		
		JSONObject data = new JSONObject();
		
		Integer[] rowsArray = new Integer[rows.size()];
	    rows.toArray(rowsArray);
		
		Integer[] colsArray = new Integer[columns.size()];
	    columns.toArray(colsArray);
		
	    for(int ctx : this.getContexts()) {
	    	
	    	JSONArray contextData = new JSONArray();
	    	
	    	for(int row = 0; row < rowsArray.length; row++){
	    		JSONArray rowData = new JSONArray();
				for(int col = 0; col < colsArray.length; col ++) {
					double value = ((NumericDataset)generatedDataset).getMatrixItem(ctx, rowsArray[row], colsArray[col]).doubleValue();
					if(Double.compare(value, Integer.MIN_VALUE) == 0)
						rowData.put("");
					else
						rowData.put(df.format(value));
					
				}
				contextData.put(rowData);
	    	}
	    	data.putOpt(String.valueOf(ctx), contextData);
	    }
	
	    tricluster.put("Data", data);
	    
		return tricluster;
	}

	public String matrixToString(Object[][] matrix) {

		StringBuilder str = new StringBuilder("[");

		for(int row = 0; row < matrix.length; row++) {
			if(row == matrix.length - 1)
				str.append(formatResult(matrix[row]) + "]");
			else
				str.append(formatResult(matrix[row]) + ", ");
		}

		return str.toString();
	}

	private String formatResult(Object[] row) {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		StringBuilder sb = new StringBuilder("[");

		for(int i = 0; i < row.length; i++)
			sb.append(df.format(row[i]) + ", ");

		return sb.replace(sb.length() - 2, sb.length(), "]").toString();
	}

	@Override
	public int getSize() {
		
		return this.getNumRows() * this.getNumCols() * this.getNumContexts();
	}
}
