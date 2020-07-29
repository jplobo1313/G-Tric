package com.gtric.service;

import javafx.concurrent.Task;

public abstract class GenerateDatasetTask<V> extends Task<V> {
	
	public GTricService gTricService;

	public GenerateDatasetTask(GTricService gTricService) {
	        this.gTricService = gTricService;
	}
	
}
