/**
 * Tricluster Structure Class
 * 
 * @author Joao Lobo - jlobo@lasige.di.fc.ul.pt
 * @version 1.0
 */
package com.gtric.utils;

import com.gtric.types.Contiguity;
import com.gtric.types.Distribution;

public class TriclusterStructure {

	Distribution rowsDist;
	Distribution columnsDist;
	Distribution contextsDist;
	double rowsParam1;
	double rowsParam2;
	double columnsParam1;
	double columnsParam2;
	double contextsParam1;
	double contextsParam2;
	Contiguity contiguity;
	
	/**
	 * Constructor
	 */
	public TriclusterStructure() {}
	
	/**
	 * Constructor
	 * @param rowsDist The row distribution (normal or uniform)
	 * @param columnsDist The column distribution (normal or uniform)
	 * @param contextsDist The context distribution (normal or uniform)
	 * @param rowsParam1 The first parameter of the row distribution (mean or min)
	 * @param rowsParam2 The second parameter of the row distribution (std or max)
	 * @param columnsParam1 The first parameter of the column distribution (mean or min)
	 * @param columnsParam2 The second parameter of the column distribution (std or max)
	 * @param contextsParam1 The first parameter of the context distribution (mean or min)
	 * @param contextsParam2 The second parameter of the context distribution (std or max)
	 * @param contiguity which dimension is contiguous (COLUMNS or CONTEXTS) or NONE
	 */
	public TriclusterStructure(Distribution rowsDist, Distribution columnsDist, Distribution contextsDist,
			double rowsParam1, double rowsParam2, double columnsParam1, double columnsParam2, double contextsParam1,
			double contextsParam2, Contiguity contiguity) {
		this.rowsDist = rowsDist;
		this.columnsDist = columnsDist;
		this.contextsDist = contextsDist;
		this.rowsParam1 = rowsParam1;
		this.rowsParam2 = rowsParam2;
		this.columnsParam1 = columnsParam1;
		this.columnsParam2 = columnsParam2;
		this.contextsParam1 = contextsParam1;
		this.contextsParam2 = contextsParam2;
		this.contiguity = contiguity;
	}

	/**
	 * Sets the contiguity
	 * @param cont Which dimension is contiguous (COLUMNS or CONTEXTS) or NONE
	 */
	public void setContiguity(Contiguity cont) {
		this.contiguity = cont;
	}
	
	/**
	 * Get the contiguity
	 * @return contiguity dimension
	 */
	public Contiguity getContiguity() {
		return this.contiguity;
	}
	
	/**
	 * Get the row distribution
	 * @return the row distribution
	 */
	public Distribution getRowsDistribution() {
		return rowsDist;
	}

	/**
	 * Set the row distribution
	 * @param rowsDist The row distribution (NORMAL or UNIFORM)
	 */
	public void setRowsDistribution(Distribution rowsDist) {
		this.rowsDist = rowsDist;
	}

	/**
	 * Get the column distribution
	 * @return the column distribution
	 */
	public Distribution getColumnsDistribution() {
		return columnsDist;
	}

	/**
	 * Set the column distribution
	 * @param columnsDist The column distribution (NORMAL or UNIFORM)
	 */
	public void setColumnsDistribution(Distribution columnsDist) {
		this.columnsDist = columnsDist;
	}

	/**
	 * Get the context distribution
	 * @return the context distribution
	 */
	public Distribution getContextsDistribution() {
		return contextsDist;
	}

	/**
	 * Set the context distribution
	 * @param contextsDist The context distribution (NORMAL or UNIFORM)
	 */
	public void setContextsDistribution(Distribution contextsDist) {
		this.contextsDist = contextsDist;
	}

	/**
	 * Get rows distribution first parameter
	 * @return first parameter
	 */
	public double getRowsParam1() {
		return rowsParam1;
	}

	/**
	 * Set row distribution and parameters
	 * @param dist The distribution (NORMAL or UNIFORM)
	 * @param param1 The distribution's first parameter (mean or min)
	 * @param param2 The distribution's second parameter (std or max)
	 */
	public void setRowsSettings(Distribution dist, double param1, double param2) {
		this.rowsDist = dist;
		this.rowsParam1 = param1;
		this.rowsParam2 = param2;
	}
	
	/**
	 * Set column distribution and parameters
	 * @param dist The distribution (NORMAL or UNIFORM)
	 * @param param1 The distribution's first parameter (mean or min)
	 * @param param2 The distribution's second parameter (std or max)
	 */
	public void setColumnsSettings(Distribution dist, double param1, double param2) {
		this.columnsDist = dist;
		this.columnsParam1 = param1;
		this.columnsParam2 = param2;
	}
	
	/**
	 * Set context distribution and parameters
	 * @param dist The distribution (NORMAL or UNIFORM)
	 * @param param1 The distribution's first parameter (mean or min)
	 * @param param2 The distribution's second parameter (std or max)
	 */
	public void setContextsSettings(Distribution dist, double param1, double param2) {
		this.contextsDist = dist;
		this.contextsParam1 = param1;
		this.contextsParam2 = param2;
	}
	
	/**
	 * Set row distribution first parameter
	 * @param rowsParam1 first parameter
	 */
	public void setRowsParam1(double rowsParam1) {
		this.rowsParam1 = rowsParam1;
	}

	/**
	 * Get rows distribution second parameter
	 * @return second parameter
	 */
	public double getRowsParam2() {
		return rowsParam2;
	}

	/**
	 * Set row distribution second parameter
	 * @param rowsParam2 second parameter
	 */
	public void setRowsParam2(double rowsParam2) {
		this.rowsParam2 = rowsParam2;
	}

	/**
	 * Get columns distribution first parameter
	 * @return first parameter
	 */
	public double getColumnsParam1() {
		return columnsParam1;
	}

	/**
	 * Set column distribution first parameter
	 * @param columnsParam1 first parameter
	 */
	public void setColumnsParam1(double columnsParam1) {
		this.columnsParam1 = columnsParam1;
	}

	/**
	 * Get columns distribution second parameter
	 * @return second parameter
	 */
	public double getColumnsParam2() {
		return columnsParam2;
	}

	/**
	 * Set column distribution second parameter
	 * @param columnsParam2 second parameter
	 */
	public void setColumnsParam2(double columnsParam2) {
		this.columnsParam2 = columnsParam2;
	}

	/**
	 * Get contexts distribution first parameter
	 * @return first parameter
	 */
	public double getContextsParam1() {
		return contextsParam1;
	}

	/**
	 * Set context distribution first parameter
	 * @param contextsParam1 first parameter
	 */
	public void setContextsParam1(double contextsParam1) {
		this.contextsParam1 = contextsParam1;
	}

	/**
	 * Get contexts distribution second parameter
	 * @return second parameter
	 */
	public double getContextsParam2() {
		return contextsParam2;
	}

	/**
	 * Set context distribution second parameter
	 * @param contextsParam2 second parameter
	 */
	public void setContextsParam2(double contextsParam2) {
		this.contextsParam2 = contextsParam2;
	}
}
