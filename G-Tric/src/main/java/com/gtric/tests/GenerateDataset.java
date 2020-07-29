package com.gtric.tests;

import java.util.ArrayList;
import java.util.List;

import com.gtric.domain.dataset.NumericDataset;
import com.gtric.domain.dataset.SymbolicDataset;
import com.gtric.generator.NumericDatasetGenerator;
import com.gtric.generator.SymbolicDatasetGenerator;
import com.gtric.generator.TriclusterDatasetGenerator;
import com.gtric.service.GTricService;
import com.gtric.types.Background;
import com.gtric.types.BackgroundType;
import com.gtric.types.Contiguity;
import com.gtric.types.Distribution;
import com.gtric.types.PatternType;
import com.gtric.types.PlaidCoherency;
import com.gtric.utils.IOUtils;
import com.gtric.utils.InputValidation;
import com.gtric.utils.OverlappingSettings;
import com.gtric.utils.TriclusterPattern;
import com.gtric.utils.TriclusterStructure;
public class GenerateDataset{

	public static String path;
	public static String outputFolder = "TriGenData";
	public static int trics;
	public static double percMissings;
	
	public static void main (String[] args) throws Exception {
		/*
		int max = -1;
		NumericDataset best;
		
		String currentDirectory = System.getProperty("user.dir");
	    
	    File directory = new File(outputFolder);

	    if (!directory.exists()) {
	        directory.mkdir();
	    }
		
	    path = currentDirectory + "/" + outputFolder + "/";
	    NumericDataset d = null;
	    int run = 0;
	    boolean cont = true;
		while(run == 0) {
			System.out.println("Run " + run + "\n");
			
			d = generateReal();
			
			if(d.getTriclusters().size() > max) {
				best = d;
				max = best.getTriclusters().size();
				
				File folder = new File(path);
				File[] fList = folder.listFiles();
			
				for (int f = 0; f < fList.length; f++) {
				    String pes = fList[f].getAbsolutePath();
				    if (pes.endsWith(".txt") || pes.endsWith(".json")) {
				        boolean success = (new File(fList[f].getAbsolutePath()).delete());
				    }
				}
				saveResult(best, "dataset_trics_info", "dataset");
			}
			run++;
		}
		
		System.out.println("Number of bics positions: " + d.getElements().size());
		System.out.println("Background Size: " + d.getBackgroundSize());
		System.out.println("Dataset - bics = " + (d.getSize() - d.getElements().size()));
		System.out.println("Number of missings = " + ((double) d.getNumberOfMissings()));
		System.out.println("perc of missings (background) = " + ((double) d.getNumberOfMissings()) / ((double) d.getBackgroundSize()) * 100 + "%");
		System.out.println("perc of missings (whole dataset) % = " + ((double) d.getNumberOfMissings()) / ((double) d.getSize()) * 100 + "%");
		*/
		for(int i = 0; i < 1; i++) {
			System.out.println("Run " + i);
			generateReal();
		}
	}

	//*** USA O REAL ***
	public static void generateSymbolic() throws Exception {

		long startTimeGen;
		long stopTimeGen;
		long startTimeBics;
		long stopTimeBics;
		long startWriting;
		long stopWriting;

		//** 1 - Define dataset properties **//
		//num de linhas do dataset
		int numRows = 100;
		//num de colunas do dataset
		int numCols = 100;
		int numCtxs = 100;
		//num de bics a plantar
		int numTrics = 8;

		//tamanho do alfabeto ou simbolos do alfabeto (escolher um)
		int alphabetL = 5;
		String[] alphabet = {"1","2","3","4","5"};

		//simetrias nos valores do dataset
		boolean symmetries = false;

		Background background = null;
		TriclusterDatasetGenerator generator = null;

		/* Background Normal(2.5, 1)
    	background = new Background(BackgroundType.NORMAL, 2.5, 1);
		 */
		/* Background Uniform
        background = new Background(BackgroundType.UNIFORM);
		 */
		/* Background Missing
        background = new Background(BackgroundType.MISSING);
		 */
		// Background Weighted probabilities
		double[] probs = {0.05, 0.1, 0.3, 0.35, 0.2};
		background = new Background(BackgroundType.DISCRETE, probs);
		// **************** //
		
		InputValidation.validateDatasetSettings(numRows, numCols, numCtxs, numTrics, alphabetL);
		
		startTimeGen = System.currentTimeMillis();

		generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, alphabet, symmetries);
		stopTimeGen = System.currentTimeMillis();

		System.out.println("(BicMatrixGenerator) Execution Time: " + ((double)(stopTimeGen - startTimeGen))/1000 + " secs");

		//** 2 - Set tricluster's patterns **//
		List<TriclusterPattern> patterns = new ArrayList<>();
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.NONE, PatternType.ORDER_PRESERVING));
		//patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.CONSTANT, PatternType.NONE));
		//patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.CONSTANT, PatternType.CONSTANT));
		//patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.NONE, PatternType.CONSTANT));
		//patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.NONE, PatternType.NONE));
		//patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.CONSTANT, PatternType.NONE));
		//patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.NONE, PatternType.CONSTANT));
		// *************** //
		
		InputValidation.validatePatterns(patterns);
		
		//** 3 - Define tricluster's structure **//
		//Object that encapsulates the configurations of the tricluster's structure
		TriclusterStructure tricStructure = new TriclusterStructure();
		
		//Distribution used to calculate the number of rows/cols/ctxs for a tric (NORMAL or UNIFORM)
		//Dist args: if dist=UNIFORM, then param1 and param2 represents the min and max, respectively
		//			 if dist=NORMAL, then param1 and param2 represents the mean and stdDev, respectively
		tricStructure.setRowsSettings(Distribution.NORMAL, 3, 7);
		tricStructure.setColumnsSettings(Distribution.NORMAL, 4, 6);
		tricStructure.setContextsSettings(Distribution.NORMAL, 2, 10);
		
		//Contiguity can occour on COLUMNS or CONTEXTS. To avoid contiguity use NONE
		tricStructure.setContiguity(Contiguity.NONE);
		// ************* /
		
		//** 4- Define overlapping settings ** //
		//Object to encapsulate overlapping parameters
		OverlappingSettings overlapping = new OverlappingSettings();
		
		//Plaid Coherency (ADDITIVE, MULTIPLICATIVE, INTERPOLED, NONE or NO_OVERLAPPING)
		overlapping.setPlaidCoherency(PlaidCoherency.NO_OVERLAPPING);
		
		//Percentage of overlapping trics defines how many trics are allowed to overlap:
		//if 0.5 only half of the dataset triclusters will overlap
		overlapping.setPercOfOverlappingTrics(0.99);
		
		//Maximum number of triclusters that can overlap together. if equal to 3, there will be, at max, 3 triclusters
		//that intersect each other(T1 ^ T2 ^ T3)
		overlapping.setMaxTricsPerOverlappedArea(391);
		
		//Maximum percentage of elements shared by overlapped triclusters. If 0.5, T1 ^ T2 will have, at max, 50% of the elements
		//of the smallest tric
		overlapping.setMaxPercOfOverlappingElements(0.93);
		
		//Percentage of allowed amount of overlaping across triclusters rows, columns and contexts. if rows=0.5, then 
		//T2 will intersect, at max, with half(50%) the rows of T1.
		//if you dont want any restriction on the number of rows/cols/ctxs that can overlapp, use 1.0
		overlapping.setPercOfOverlappingRows(1);
		overlapping.setPercOfOverlappingColumns(1);
		overlapping.setPercOfOverlappingContexts(1);
		// ************* //
		
		startTimeBics = System.currentTimeMillis();
		SymbolicDataset generatedDataset = (SymbolicDataset) generator.generate(patterns, tricStructure, overlapping);
		stopTimeBics = System.currentTimeMillis();

		//Percentage of missing values on the background, that is, values that do not belong to planted trics (Range = [0,1])
		double missingPercOnBackground = 0.0;
		//Maximum percentage of missing values on each tricluster. Range [0,1]. 
		//Ex: 0.1 significa que cada tric tem no maximo 10% de missings. Pode ter menos
		double missingPercOnPlantedTrics = 0.0;

		//Same as above but for noise
		double noisePercOnBackground = 0.0;
		double noisePercOnPlantedTrics = 0.0;
		//Level of symbol deviation, that is, the maximum difference between the current symbol on the matrix and the one that
		//will replaced it to be considered noise.
		//Ex: Let Alphabet = [1,2,3,4,5] and CurrentSymbol = 3, if the noiseDeviation is '1', then CurrentSymbol will be, randomly,
		//replaced by either '2' or '4'. If noiseDeviation is '2', CurrentSymbol can be replaced by either '1','2','4' or '5'.
		int noiseDeviation = 1;

		//Same as above but for errors
		//Similar as noise, a new value is considered an error if the difference between it and the current value in the matrix is
		//greater than noiseDeviation.
		//Ex: Alphabet = [1,2,3,4,5], If currentValue = 2, and errorDeviation = 2, to turn currentValue an error, it's value must be
		//replaced by '5', that is the only possible value that respects abs(currentValue - newValue) > noiseDeviation
		double errorPercOnBackground = 0.0;
		double errorPercOnPlantedTrics = 0.0;
		
		generatedDataset.plantMissingElements(missingPercOnBackground, missingPercOnPlantedTrics);
		//generatedDataset.plantNoisyElements(noisePercOnBackground, noisePercOnPlantedTrics, noiseDeviation);
		//generatedDataset.plantErrors(errorPercOnBackground, errorPercOnPlantedTrics, noiseDeviation);
		
		System.out.println("(GeneratePlaidSymbolicBics) Execution Time: " + ((double)(stopTimeBics - startTimeBics))/1000 + " secs");

		String tricDataFileName;
		String datasetFileName;
		
		if(patterns.size() == 1) {
			tricDataFileName = "tric_" + patterns.get(0).getRowsPattern().name().charAt(0) 
					+ patterns.get(0).getColumnsPattern().name().charAt(0) 
					+ patterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;

			datasetFileName = "data_" + patterns.get(0).getRowsPattern().name().charAt(0) 
					+ patterns.get(0).getColumnsPattern().name().charAt(0) 
					+ patterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;
		}
		else {
			tricDataFileName = "tric_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
			datasetFileName = "data_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
		}

		saveResult(generatedDataset, tricDataFileName, datasetFileName);

		//Tests.testMaxTricsOnOverlappedArea(generatedDataset, overlapping, numTrics);
		//Tests.testPercOfOverlappingTrics(generatedDataset, overlapping, numTrics);
		//Tests.testContiguity(generatedDataset.getPlantedTrics(), tricStructure.getContiguity());
		//Tests.testMissingNoiseError(generatedDataset);
		
		//generateHeatMap(tricDataFileName, datasetFileName);
	}

	
	public static NumericDataset generateReal() throws Exception {

		long startTimeGen;
		long stopTimeGen;
		long startTimeBics;
		long stopTimeBics;

		//num de linhas do dataset
		int numRows = 28;
		//num de colunas do dataset
		int numCols = 20;
		int numCtxs = 365;
		//num de bics a plantar
		int numTrics = 128;

		trics = numTrics;
		
		//TODO: limites dos valores do dataset (usar em caso de dataset real)
		double min = -10;
		double max = 30;

		//use real valued or integer alphabet
		boolean realValued = true;

		Background background = null;
		TriclusterDatasetGenerator generator = null;

		/* Background Normal(2.5, 1)
    	*/
    	background = new Background(BackgroundType.NORMAL, 14, 7);
		 

		/* Background Uniform
        background = new Background(BackgroundType.UNIFORM);
        generator = new BicMatrixGenerator(numRows,numCols,numBics, background, alphabetL, symmetries);
		 */

		/* Background Missing
        background = new Background(BackgroundType.MISSING);
		generator = new BicMatrixGenerator(numRows,numCols,numBics, background, alphabetL, symmetries);
		 */

		/*
		double[] probs = {0.05, 0.1, 0.3, 0.35, 0.2};
		background = new Background(BackgroundType.DISCRETE, probs);
		*/
		//background = new Background(BackgroundType.MISSING);
		
		//background = new Background(BackgroundType.UNIFORM);

		startTimeGen = System.currentTimeMillis();
		generator = new NumericDatasetGenerator(realValued, numRows, numCols, numCtxs, numTrics, background, min, max);
		stopTimeGen = System.currentTimeMillis();
		
		System.out.println("(BackgroundGenerator) Execution Time: " + ((double)(stopTimeGen - startTimeGen)) / 1000);

		//Padrao
		List<TriclusterPattern> patterns = new ArrayList<>();
		patterns.add(new TriclusterPattern(PatternType.ORDER_PRESERVING, PatternType.NONE, PatternType.NONE));
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.ORDER_PRESERVING, PatternType.NONE));
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.NONE, PatternType.ORDER_PRESERVING));
		
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.NONE, PatternType.NONE));
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.CONSTANT, PatternType.NONE));
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.NONE, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.CONSTANT, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.NONE, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.CONSTANT, PatternType.NONE));
		patterns.add(new TriclusterPattern(PatternType.NONE, PatternType.CONSTANT, PatternType.CONSTANT));
		
		patterns.add(new TriclusterPattern(PatternType.ADDITIVE, PatternType.CONSTANT, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.ADDITIVE, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.CONSTANT, PatternType.ADDITIVE));
		patterns.add(new TriclusterPattern(PatternType.ADDITIVE, PatternType.ADDITIVE, PatternType.ADDITIVE));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.ADDITIVE, PatternType.ADDITIVE));
		patterns.add(new TriclusterPattern(PatternType.ADDITIVE, PatternType.CONSTANT, PatternType.ADDITIVE));
		patterns.add(new TriclusterPattern(PatternType.ADDITIVE, PatternType.ADDITIVE, PatternType.CONSTANT));
		
		patterns.add(new TriclusterPattern(PatternType.MULTIPLICATIVE, PatternType.CONSTANT, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.MULTIPLICATIVE, PatternType.CONSTANT));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.CONSTANT, PatternType.MULTIPLICATIVE));
		patterns.add(new TriclusterPattern(PatternType.MULTIPLICATIVE, PatternType.MULTIPLICATIVE, PatternType.MULTIPLICATIVE));
		patterns.add(new TriclusterPattern(PatternType.CONSTANT, PatternType.MULTIPLICATIVE, PatternType.MULTIPLICATIVE));
		patterns.add(new TriclusterPattern(PatternType.MULTIPLICATIVE, PatternType.CONSTANT, PatternType.MULTIPLICATIVE));
		patterns.add(new TriclusterPattern(PatternType.MULTIPLICATIVE, PatternType.MULTIPLICATIVE, PatternType.CONSTANT));
		

		//** 3 - Define tricluster's structure **//
		//Object that encapsulates the configurations of the tricluster's structure
		TriclusterStructure tricStructure = new TriclusterStructure();
		
		//Distribution used to calculate the number of rows/cols/ctxs for a tric (NORMAL or UNIFORM)
		//Dist args: if dist=UNIFORM, then param1 and param2 represents the min and max, respectively
		//			 if dist=NORMAL, then param1 and param2 represents the mean and stdDev, respectively
		tricStructure.setRowsSettings(Distribution.UNIFORM, 4, 4);
		tricStructure.setColumnsSettings(Distribution.UNIFORM, 4, 4);
		tricStructure.setContextsSettings(Distribution.UNIFORM, 8, 8);
		
		//Contiguity can occour on COLUMNS or CONTEXTS. To avoid contiguity use NONE
		tricStructure.setContiguity(Contiguity.NONE);
		// ************* /

		//** Define overlapping settings ** //
		
		//Object to encapsulate overlapping parameters
		OverlappingSettings overlapping = new OverlappingSettings();
		
		//Plaid Coherency (ADDITIVE, MULTIPLICATIVE, INTERPOLED, NONE or NO_OVERLAPPING
		overlapping.setPlaidCoherency(PlaidCoherency.NO_OVERLAPPING);
		
		//Percentage of overlapping trics defines how many trics are allowed to overlap:
		//if 0.5 only half of the dataset triclusters will overlap
		overlapping.setPercOfOverlappingTrics(0.8);
		
		//Maximum number of triclusters that can overlap together. if equal to 3, there will be, at max, 3 triclusters
		//that intersect each other(T1 ^ T2 ^ T3)
		overlapping.setMaxTricsPerOverlappedArea(8);
		
		//Maximum percentage of elements shared by overlapped triclusters. If 0.5, T1 ^ T2 will have, at max, 50% of the elements
		//of the smallest tric
		overlapping.setMaxPercOfOverlappingElements(0.6);
		
		//Percentage of allowed amount of overlaping across triclusters rows, columns and contexts. if rows=0.5, then 
		//T2 will intersect, at max, with half(50%) the rows of T1.
		//if you dont want any restriction on the number of rows/cols/ctxs that can overlapp, use 1.0
		overlapping.setPercOfOverlappingRows(1.0);
		overlapping.setPercOfOverlappingColumns(1.0);
		overlapping.setPercOfOverlappingContexts(1.0);		
		//** end of overlapping settings ** //
		
		startTimeBics = System.currentTimeMillis();
		NumericDataset generatedDataset = (NumericDataset) generator.generate(patterns, tricStructure, overlapping);
		stopTimeBics = System.currentTimeMillis();
		
		System.out.println("(GenerateTrics) Execution Time: " + ((double) (stopTimeBics - startTimeBics)) / 1000);
		System.out.println("Number of planted trics = " + generatedDataset.getTriclusters().size());
		
		/*
		for(int id = 0; id < numTrics; id++) {
			NumericTricluster<?> t = generatedDataset.getTricluster(id);
			System.out.println("ID = " + id + "(" + t.getNumContexts() + "x" + t.getNumRows() + "x" + t.getNumCols() + ")");
			List<String> elements = generatedDataset.getTriclusterElements(id);
			Map<Integer, Integer> overlappedTricsCounter = new HashMap<>();
			for(String elem : elements) {
				List<Integer> overlappedTrics = generatedDataset.getTricsByElem(elem);
				for(Integer tric : overlappedTrics) {
					if(tric != id) {
						if(overlappedTricsCounter.containsKey(tric))
							overlappedTricsCounter.put(tric, overlappedTricsCounter.get(tric) + 1);
						else
							overlappedTricsCounter.put(tric, 1);
					}	
				}
			}
			for(Integer k : overlappedTricsCounter.keySet()) {
				int total = overlappedTricsCounter.get(k);
				double perc = ((double) total) / ((double) t.getSize());
				System.out.println("Overlaps with tric " + k + " in " + total + " (" + perc + ") positions");
				
				if(Double.compare(perc, overlapping.getMaxPercOfOverlappingElements()) > 0)
					throw new OutputErrorException("excedeu o max_overlap_elements (3)");
			}
		}
		*/
		
		
		//Percentage of missing values on the background, that is, values that do not belong to planted trics (Range = [0,1])
		//Maximum percentage of missing values on each tricluster. Range [0,1]. 
		//Ex: 0.1 significa que cada tric tem no maximo 10% de missings. Pode ter menos
		double missingPercOnBackground = 0.15;
		double missingPercOnPlantedTrics = 0.05;
		
		//Same as above but for noise
		double noisePercOnBackground = 0.2;
		double noisePercOnPlantedTrics = 0.1;
		//Level of symbol deviation, that is, the maximum difference between the current symbol on the matrix and the one that
		//will replaced it to be considered noise.
		//Ex: Let Alphabet = [1,2,3,4,5] and CurrentSymbol = 3, if the noiseDeviation is '1', then CurrentSymbol will be, randomly,
		//replaced by either '2' or '4'. If noiseDeviation is '2', CurrentSymbol can be replaced by either '1','2','4' or '5'.
		int noiseDeviation = 1;

		//Same as above but for errors
		//Similar as noise, a new value is considered an error if the difference between it and the current value in the matrix is
		//greater than noiseDeviation.
		//Ex: Alphabet = [1,2,3,4,5], If currentValue = 2, and errorDeviation = 2, to turn currentValue an error, it's value must be
		//replaced by '5', that is the only possible value that respects abs(currentValue - newValue) > noiseDeviation
		double errorPercOnBackground = 0.05;
		double errorPercOnPlantedTrics = 0.02;
		
		generatedDataset.plantMissingElements(missingPercOnBackground, missingPercOnPlantedTrics);
		generatedDataset.plantNoisyElements(noisePercOnBackground, noisePercOnPlantedTrics, noiseDeviation);
		generatedDataset.plantErrors(errorPercOnBackground, errorPercOnPlantedTrics, noiseDeviation);
		
		
		String tricDataFileName;
		String datasetFileName;
		
		if(patterns.size() == 1) {
			tricDataFileName = "tric_" + patterns.get(0).getRowsPattern().name().charAt(0) 
					+ patterns.get(0).getColumnsPattern().name().charAt(0) 
					+ patterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;

			datasetFileName = "data_" + patterns.get(0).getRowsPattern().name().charAt(0) 
					+ patterns.get(0).getColumnsPattern().name().charAt(0) 
					+ patterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;
		}
		else {
			tricDataFileName = "dataset_5_trics";
			datasetFileName = "dataset_5_data";
		}
		 
		//Tests.testMissingNoiseError(generatedDataset);
		//System.out.println("Missings: " + Arrays.toString(generatedDataset.getMissingElements().toArray()));
		//System.out.println("Noise: " + Arrays.toString(generatedDataset.getNoisyElements().toArray()));
		//System.out.println("Errors: " + Arrays.toString(generatedDataset.getErrorElements().toArray()));
		
		GTricService serv = new GTricService();
		serv.setPath("/Users/atticus/git/pyTriclustering/G-Tric/");
		serv.setSingleFileOutput(true);
		serv.saveResult(generatedDataset, tricDataFileName, datasetFileName);

		//IOUtils.generateHeatMap(tricDataFileName, datasetFileName);
		
		return generatedDataset;
	}

	

	

	private static void saveResult(SymbolicDataset generatedDataset, String tricDataFileName, String datasetFileName) throws Exception {

		//BicResult.println("Planted BICS:\n" + trueBics.toString());
		IOUtils.writeFile(path, tricDataFileName + ".txt",generatedDataset.getTricsInfo());
		//BicResult.println("Dataset:\n" + BicPrinting.plot(dataset));
		IOUtils.writeFile(path, tricDataFileName + ".json",generatedDataset.getTricsInfoJSON(generatedDataset).toString());

		long startWriting;
		long stopWriting;

		int threshold = 1000;
		int step = generatedDataset.getNumRows() / threshold;

		startWriting = System.currentTimeMillis();
		for(int s = 0; s <= step; s++)
			IOUtils.writeFile(path, datasetFileName + ".txt", IOUtils.matrixToStringColOriented(generatedDataset, threshold, s, s==0));
		stopWriting = System.currentTimeMillis();

		System.out.println("(Writing) Execution Time: " + ((double)(stopWriting - startWriting))/1000 + " secs");

	}
}
