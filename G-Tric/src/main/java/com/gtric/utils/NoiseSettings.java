package com.gtric.utils;

public class NoiseSettings {

	private double percMissingsOnBackground;
	private double percMissingsOnTrics;
	private double percNoiseOnBackground;
	private double percNoiseOnTrics;
	private double noiseDeviation;
	private double percErrorsOnBackground;
	private double percErrorsOnTrics;
	
	public NoiseSettings() {}
	
	/**
	 * @param percMissingsOnBackground
	 * @param percMissingsOnTrics
	 * @param percNoiseOnBackground
	 * @param percNoiseOnTrics
	 * @param noiseDeviation
	 * @param percErrorsOnBackground
	 * @param percErrorsOnTrics
	 */
	public NoiseSettings(double percMissingsOnBackground, double percMissingsOnTrics, double percNoiseOnBackground,
			double percNoiseOnTrics, double noiseDeviation, double percErrorsOnBackground, double percErrorsOnTrics) {
		this.percMissingsOnBackground = percMissingsOnBackground;
		this.percMissingsOnTrics = percMissingsOnTrics;
		this.percNoiseOnBackground = percNoiseOnBackground;
		this.percNoiseOnTrics = percNoiseOnTrics;
		this.noiseDeviation = noiseDeviation;
		this.percErrorsOnBackground = percErrorsOnBackground;
		this.percErrorsOnTrics = percErrorsOnTrics;
	}

	/**
	 * @return the percMissingsOnBackground
	 */
	public double getPercMissingsOnBackground() {
		return percMissingsOnBackground;
	}

	/**
	 * @param percMissingsOnBackground the percMissingsOnBackground to set
	 */
	public void setPercMissingsOnBackground(double percMissingsOnBackground) {
		this.percMissingsOnBackground = percMissingsOnBackground;
	}

	/**
	 * @return the percMissingsOnTrics
	 */
	public double getPercMissingsOnTrics() {
		return percMissingsOnTrics;
	}

	/**
	 * @param percMissingsOnTrics the percMissingsOnTrics to set
	 */
	public void setPercMissingsOnTrics(double percMissingsOnTrics) {
		this.percMissingsOnTrics = percMissingsOnTrics;
	}

	/**
	 * @return the percNoiseOnBackground
	 */
	public double getPercNoiseOnBackground() {
		return percNoiseOnBackground;
	}

	/**
	 * @param percNoiseOnBackground the percNoiseOnBackground to set
	 */
	public void setPercNoiseOnBackground(double percNoiseOnBackground) {
		this.percNoiseOnBackground = percNoiseOnBackground;
	}

	/**
	 * @return the percNoiseOnTrics
	 */
	public double getPercNoiseOnTrics() {
		return percNoiseOnTrics;
	}

	/**
	 * @param percNoiseOnTrics the percNoiseOnTrics to set
	 */
	public void setPercNoiseOnTrics(double percNoiseOnTrics) {
		this.percNoiseOnTrics = percNoiseOnTrics;
	}

	/**
	 * @return the noiseDeviation
	 */
	public double getNoiseDeviation() {
		return noiseDeviation;
	}

	/**
	 * @param noiseDeviation the noiseDeviation to set
	 */
	public void setNoiseDeviation(double noiseDeviation) {
		this.noiseDeviation = noiseDeviation;
	}

	/**
	 * @return the percErrorsOnBackground
	 */
	public double getPercErrorsOnBackground() {
		return percErrorsOnBackground;
	}

	/**
	 * @param percErrorsOnBackground the percErrorsOnBackground to set
	 */
	public void setPercErrorsOnBackground(double percErrorsOnBackground) {
		this.percErrorsOnBackground = percErrorsOnBackground;
	}

	/**
	 * @return the percErrorsOnTrics
	 */
	public double getPercErrorsOnTrics() {
		return percErrorsOnTrics;
	}

	/**
	 * @param percErrorsOnTrics the percErrorsOnTrics to set
	 */
	public void setPercErrorsOnTrics(double percErrorsOnTrics) {
		this.percErrorsOnTrics = percErrorsOnTrics;
	}
	
	
	
}
