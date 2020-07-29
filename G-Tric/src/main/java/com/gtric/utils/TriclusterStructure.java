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
	
	public TriclusterStructure() {}
	
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

	public void setContiguity(Contiguity cont) {
		this.contiguity = cont;
	}
	
	public Contiguity getContiguity() {
		return this.contiguity;
	}
	
	public Distribution getRowsDistribution() {
		return rowsDist;
	}

	public void setRowsDistribution(Distribution rowsDist) {
		this.rowsDist = rowsDist;
	}

	public Distribution getColumnsDistribution() {
		return columnsDist;
	}

	public void setColumnsDistribution(Distribution columnsDist) {
		this.columnsDist = columnsDist;
	}

	public Distribution getContextsDistribution() {
		return contextsDist;
	}

	public void setContextsDistribution(Distribution contextsDist) {
		this.contextsDist = contextsDist;
	}

	public double getRowsParam1() {
		return rowsParam1;
	}

	public void setRowsSettings(Distribution dist, double param1, double param2) {
		this.rowsDist = dist;
		this.rowsParam1 = param1;
		this.rowsParam2 = param2;
	}
	
	public void setColumnsSettings(Distribution dist, double param1, double param2) {
		this.columnsDist = dist;
		this.columnsParam1 = param1;
		this.columnsParam2 = param2;
	}
	
	public void setContextsSettings(Distribution dist, double param1, double param2) {
		this.contextsDist = dist;
		this.contextsParam1 = param1;
		this.contextsParam2 = param2;
	}
	
	public void setRowsParam1(double rowsParam1) {
		this.rowsParam1 = rowsParam1;
	}

	public double getRowsParam2() {
		return rowsParam2;
	}

	public void setRowsParam2(double rowsParam2) {
		this.rowsParam2 = rowsParam2;
	}

	public double getColumnsParam1() {
		return columnsParam1;
	}

	public void setColumnsParam1(double columnsParam1) {
		this.columnsParam1 = columnsParam1;
	}

	public double getColumnsParam2() {
		return columnsParam2;
	}

	public void setColumnsParam2(double columnsParam2) {
		this.columnsParam2 = columnsParam2;
	}

	public double getContextsParam1() {
		return contextsParam1;
	}

	public void setContextsParam1(double contextsParam1) {
		this.contextsParam1 = contextsParam1;
	}

	public double getContextsParam2() {
		return contextsParam2;
	}

	public void setContextsParam2(double contextsParam2) {
		this.contextsParam2 = contextsParam2;
	}
}
