package com.gtric.utils;

import com.gtric.types.PlaidCoherency;

public class OverlappingSettings {

	private PlaidCoherency plaid;
	private double percOfOverlappingTrics;
	private int maxTricsPerOverlappedArea;
	private double percOfOverlappingRows;
	private double percOfOverlappingColumns;
	private double percOfOverlappingContexts;
	private double maxPercOfOverlappingElements;
	
	public OverlappingSettings(PlaidCoherency plaid, double percOfOverlappingTrics, int maxTricsPerOverlappedArea,
			double percOfOverlappingRows, double percOfOverlappingColumns, double percOfOverlappingContexts,
			double maxPercOfOverlappingElements) {
		
		this.plaid = plaid;
		this.percOfOverlappingTrics = percOfOverlappingTrics;
		this.maxTricsPerOverlappedArea = maxTricsPerOverlappedArea;
		this.percOfOverlappingRows = percOfOverlappingRows;
		this.percOfOverlappingColumns = percOfOverlappingColumns;
		this.percOfOverlappingContexts = percOfOverlappingContexts;
		this.maxPercOfOverlappingElements = maxPercOfOverlappingElements;
	}

	public OverlappingSettings() {};
	
	public PlaidCoherency getPlaidCoherency() {
		return plaid;
	}

	public void setPlaidCoherency(PlaidCoherency plaid) {
		this.plaid = plaid;
	}

	public double getPercOfOverlappingTrics() {
		return percOfOverlappingTrics;
	}

	public void setPercOfOverlappingTrics(double percOfOverlappingTrics) {
		this.percOfOverlappingTrics = percOfOverlappingTrics;
	}

	public int getMaxTricsPerOverlappedArea() {
		return maxTricsPerOverlappedArea;
	}

	public void setMaxTricsPerOverlappedArea(int maxTricsPerOverlappedArea) {
		this.maxTricsPerOverlappedArea = maxTricsPerOverlappedArea;
	}

	public double getPercOfOverlappingRows() {
		return percOfOverlappingRows;
	}

	public void setPercOfOverlappingRows(double percOfOverlappingRows) {
		this.percOfOverlappingRows = percOfOverlappingRows;
	}

	public double getPercOfOverlappingColumns() {
		return percOfOverlappingColumns;
	}

	public void setPercOfOverlappingColumns(double percOfOverlappingColumns) {
		this.percOfOverlappingColumns = percOfOverlappingColumns;
	}

	public double getPercOfOverlappingContexts() {
		return percOfOverlappingContexts;
	}

	public void setPercOfOverlappingContexts(double percOfOverlappingContexts) {
		this.percOfOverlappingContexts = percOfOverlappingContexts;
	}
	
	public void setMaxPercOfOverlappingElements(double maxPercOfOverlappingElements) {
		this.maxPercOfOverlappingElements = maxPercOfOverlappingElements;
	}
	
	public double getMaxPercOfOverlappingElements() {
		return this.maxPercOfOverlappingElements;
	}
}
