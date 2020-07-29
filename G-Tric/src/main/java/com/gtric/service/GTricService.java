package com.gtric.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.gtric.domain.dataset.Dataset;
import com.gtric.domain.dataset.NumericDataset;
import com.gtric.domain.dataset.SymbolicDataset;
import com.gtric.generator.NumericDatasetGenerator;
import com.gtric.generator.SymbolicDatasetGenerator;
import com.gtric.generator.TriclusterDatasetGenerator;
import com.gtric.tests.OutputWriterThread;
import com.gtric.types.Background;
import com.gtric.types.BackgroundType;
import com.gtric.types.Contiguity;
import com.gtric.types.Distribution;
import com.gtric.types.PatternType;
import com.gtric.types.PlaidCoherency;
import com.gtric.utils.IOUtils;
import com.gtric.utils.NoiseSettings;
import com.gtric.utils.OverlappingSettings;
import com.gtric.utils.TriclusterPattern;
import com.gtric.utils.TriclusterStructure;

public class GTricService extends Observable implements Observer {

	private String path = "data/";
	private String filename = "";
	private boolean singleFile;
	
	public class TriclusterPatternWrapper{
		
		String rowPattern;
		String columnPattern;
		String contextPattern;
		String imagePath;
		
		public TriclusterPatternWrapper(String rowPattern, String columnPattern, String contextPattern,
				String imagePath) {
			this.rowPattern = rowPattern;
			this.columnPattern = columnPattern;
			this.contextPattern = contextPattern;
			this.imagePath = imagePath;
		}

		public String getRowPattern() {
			return rowPattern;
		}

		public String getColumnPattern() {
			return columnPattern;
		}

		public String getContextPattern() {
			return contextPattern;
		}

		public String getImagePath() {
			return imagePath;
		}
		
		public String toString() {
			return rowPattern + "|" + columnPattern + "|" + contextPattern;
		}
		
	}
	
	private JSONObject triclustersJSON;
	private Dataset generatedDataset;
	
	private BiConsumer<Integer, Integer> progressUpdate;
	private Consumer<String> messageUpdate;
	private int currentProgress;
	
	private String state;
	//Paths to files with symbolic and numerics patterns
	private static final String SYMBOLIC_PATTERNS_PATH = "src/main/java/com/gtric/app/service/symbolicPatterns.csv";
	private static final String NUMERIC_PATTERNS_PATH = "src/main/java/com/gtric/app/service/numericPatterns.csv";
	private List<String> numericDatasetDataTypes;
	private List<String> datasetBackground;
	private List<String> distributions;
	private List<String> contiguity;
	private List<String> plaidCoherency;
	private List<TriclusterPatternWrapper> symbolicPatterns;
	private List<TriclusterPatternWrapper> numericPatterns;
	private List<String> symbolType;
	
	//Dataset Properties
	//Numeric or Sumbolic
	private String datasetType;
	private int numRows;
	private int numCols;
	private int numCtxs;
	
	//Numeric dataset
	private boolean realValued;
	private double minM;
	private double maxM;
	
	//symbolic dataset
	private boolean defaultSymbols;
	private int numberOfSymbols;
	private List<String> listOfSymbols;
	
	private Background background;
	
	//TriclusterProperties
	private int numTrics;
	private TriclusterStructure tricStructure;
	
	//Tricluster's Patters
	List<TriclusterPattern> tricPatterns;
	
	//Overlapping
	private OverlappingSettings overlappingSettings;
	
	//Extras
	private NoiseSettings noiseSettings;

	public GTricService() {
		
		this.currentProgress = 0;
		
		numericDatasetDataTypes = new ArrayList<>();
		fillNumericDatasetDataTypes();
		
		datasetBackground = new ArrayList<>();
		fillBackgound();
		
		distributions = new ArrayList<>();
		fillDistributions();
		
		contiguity = new ArrayList<>();
		fillContiguity();
		
		plaidCoherency = new ArrayList<>();
		fillPlaidCoherency();
		
		symbolicPatterns = new ArrayList<>();
		fillSymbolicPatterns();
		
		numericPatterns = new ArrayList<>();
		fillNumericPatterns();
		
		symbolType = new ArrayList<>();
		fillSymbolType();
	}
	
	private void fillSymbolType() {
		
		symbolType.add("Default");
		symbolType.add("Custom");
	}

	public List<String> getSymbolType(){
		return this.symbolType;
	}
	
	public void setDefaultSymbolBoolean(boolean b) {
		this.defaultSymbols = b;
	}
	
	public void setSingleFileOutput(boolean b) {
		this.singleFile = b;
	}
	
	public boolean isSingleFileOutput() {
		return this.singleFile;
	}
	
	private void fillNumericPatterns() {
		
		BufferedReader patternReader = null;
		patternReader = new BufferedReader(new InputStreamReader(GTricService.class.getResourceAsStream("numericPatterns.csv")));
		
		String row;
		
		try {
			while ((row = patternReader.readLine()) != null) {
			    String[] data = row.split(",");
			    String img = "";
			    if(data.length == 4)
			    	img = data[3];
			    numericPatterns.add(new TriclusterPatternWrapper(data[0], data[1], data[2], img));
			    //System.out.println("Added: (" + data[0] + ", " + data[1] + ", " + data[2] + ")");
			    
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			patternReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillSymbolicPatterns() {
		
		BufferedReader patternReader = null;
		//System.out.println(GTricService.class.getResource("symbolicPatterns.csv").getPath());
		patternReader = new BufferedReader(new InputStreamReader(GTricService.class.getResourceAsStream("symbolicPatterns.csv")));
		
		String row;
		
		try {
			while ((row = patternReader.readLine()) != null) {
			    String[] data = row.split(",");
			    String img = "";
			    if(data.length == 4)
			    	img = data[3];
			    symbolicPatterns.add(new TriclusterPatternWrapper(data[0], data[1], data[2], img));
			    //System.out.println("Added: (" + data[0] + ", " + data[1] + ", " + data[2] + ")");
			    
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			patternReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillPlaidCoherency() {
		
		plaidCoherency.add("Additive");
		plaidCoherency.add("Multiplicative");
		plaidCoherency.add("Interpoled");
		plaidCoherency.add("None");
		plaidCoherency.add("No Overlapping");
	}

	private void fillContiguity() {
		
		contiguity.add("None");
		contiguity.add("Columns");
		contiguity.add("Contexts");
	}

	private void fillDistributions() {
		
		distributions.add("Uniform");
		distributions.add("Normal");
	}

	private void fillBackgound() {
		
		datasetBackground.add("Uniform");
		datasetBackground.add("Normal");
		datasetBackground.add("Discrete");
		datasetBackground.add("Missing");
	}

	public void setDatasetType(String type) {
		this.datasetType = type;
	}
	
	private void fillNumericDatasetDataTypes() {
		
		numericDatasetDataTypes.add("Integer");
		numericDatasetDataTypes.add("Real Valued");
	}

	public List<String> getDataTypes(){
		return this.numericDatasetDataTypes;
	}
	
	public List<String> getDatasetBackground(){
		return this.datasetBackground;
	}
	
	public List<String> getDistributions(){
		return this.distributions;
	}
	
	public List<String> getContiguity(){
		return this.contiguity;
	}
	
	public List<String> getPlaidCoherency(){
		return this.plaidCoherency;
	}
	
	public List<TriclusterPatternWrapper> getSymbolicPatterns(){
		return this.symbolicPatterns;
	}
	
	public List<TriclusterPatternWrapper> getNumericPatterns(){
		return this.numericPatterns;
	}
	
	public void setDatasetProperties(int numRows, int numCols, int numCtxs, boolean realValued, double minM, double maxM, String background,
			double backgroundParam1, double backgroundParam2, double[] backgroundParam3) {
		
		this.numRows = numRows;
		this.numCols = numCols;
		this.numCtxs = numCtxs;
		this.realValued = realValued;
		this.minM = minM;
		this.maxM = maxM;
		
		BackgroundType backgroundType = null;
		
		if(background.equals("Normal"))
			backgroundType = BackgroundType.NORMAL;

		if(background.equals("Uniform"))
			backgroundType = BackgroundType.UNIFORM;

		if(background.equals("Discrete"))
			backgroundType = BackgroundType.DISCRETE;
		
		if(background.equals("Missing"))
			backgroundType = BackgroundType.MISSING;
		
		
		this.background = new Background(backgroundType);
		this.background.setParam1(backgroundParam1);
		this.background.setParam2(backgroundParam2);
		this.background.setParam3(backgroundParam3);
	}
	
	public void setDatasetProperties(int numRows, int numCols, int numCtxs, boolean defaultSymbols, int alphabetLength, String[] symbols, String background,
			double backgroundParam1, double backgroundParam2, double[] backgroundParam3) {
		
		this.numRows = numRows;
		this.numCols = numCols;
		this.numCtxs = numCtxs;
		this.defaultSymbols = defaultSymbols;
		
		if(symbols == null)
			this.numberOfSymbols = alphabetLength;
		else
			this.listOfSymbols = Arrays.asList(symbols);
		
		BackgroundType backgroundType = null;
		
		if(background.equals("Normal"))
			backgroundType = BackgroundType.NORMAL;
		else if(background.equals("Uniform"))
			backgroundType = BackgroundType.UNIFORM;
		else if(background.equals("Discrete")) 
			backgroundType = BackgroundType.DISCRETE;
		else
			backgroundType = BackgroundType.MISSING;
		
		
		this.background = new Background(backgroundType);
		this.background.setParam1(backgroundParam1);
		this.background.setParam2(backgroundParam2);
		this.background.setParam3(backgroundParam3);
	}
	
	public void setTriclustersProperties(int numTrics, String rowDist, double rowDistParam1, double rowDistParam2, String colDist, 
			double colDistParam1, double colDistParam2, String ctxDist, double ctxDistParam1, double ctxDistParam2, String contiguity) {
		
		this.numTrics = numTrics;
		this.tricStructure = new TriclusterStructure();
		
		if(rowDist.equals("Normal"))
			this.tricStructure.setRowsDistribution(Distribution.NORMAL);
		if(rowDist.equals("Uniform"))
			this.tricStructure.setRowsDistribution(Distribution.UNIFORM);
		
		if(colDist.equals("Normal"))
			this.tricStructure.setColumnsDistribution(Distribution.NORMAL);
		if(colDist.equals("Uniform"))
			this.tricStructure.setColumnsDistribution(Distribution.UNIFORM);
		
		if(ctxDist.equals("Normal"))
			this.tricStructure.setContextsDistribution(Distribution.NORMAL);
		if(ctxDist.equals("Uniform"))
			this.tricStructure.setContextsDistribution(Distribution.UNIFORM);
		
		
		this.tricStructure.setRowsParam1(rowDistParam1);
		this.tricStructure.setRowsParam2(rowDistParam2);
		
		this.tricStructure.setColumnsParam1(colDistParam1);
		this.tricStructure.setColumnsParam2(colDistParam2);
		
		this.tricStructure.setContextsParam1(ctxDistParam1);
		this.tricStructure.setContextsParam2(ctxDistParam2);
		
		if(contiguity.equals("Columns"))
			this.tricStructure.setContiguity(Contiguity.COLUMNS);
		else if(contiguity.equals("Contexts"))
			this.tricStructure.setContiguity(Contiguity.CONTEXTS);
		else
			this.tricStructure.setContiguity(Contiguity.NONE);
	}
	
	
	public void setTriclusterPatterns(List<TriclusterPatternWrapper> patterns) {
		
		this.tricPatterns = new ArrayList<>();
		
		for(TriclusterPatternWrapper p : patterns) {
			TriclusterPattern tp = new TriclusterPattern(getPatternType(p.rowPattern), getPatternType(p.columnPattern),
					getPatternType(p.contextPattern));
			System.out.println(getPatternType(p.rowPattern));
			this.tricPatterns.add(tp);
		}
	}
	
	private PatternType getPatternType(String type) {
		
		PatternType res = null;
		System.out.println(type);
		if(type.contains("Constant"))
			res = PatternType.CONSTANT;
		else if(type.contains("Additive"))
			res = PatternType.ADDITIVE;
		else if(type.contains("Multiplicative"))
			res = PatternType.MULTIPLICATIVE;
		else if(type.contains("Order Preserving"))
			res = PatternType.ORDER_PRESERVING;
		else
			res = PatternType.NONE;
		
		return res;
	}
	
	public void setOverlappingSettings(String plaidCoherency, double percOverlappingTrics, int maxOverlappingTrics, double percOverlappingElements,
			double percOverlappingRows, double percOverlappingColumns, double percOverlappingContexts) {
		
		this.overlappingSettings = new OverlappingSettings();
		
		if(plaidCoherency.equals("Additive"))
			this.overlappingSettings.setPlaidCoherency(PlaidCoherency.ADDITIVE);
		else if (plaidCoherency.equals("Multiplicative"))
			this.overlappingSettings.setPlaidCoherency(PlaidCoherency.MULTIPLICATIVE);
		else if (plaidCoherency.equals("Interpoled"))
			this.overlappingSettings.setPlaidCoherency(PlaidCoherency.INTERPOLED);
		else if (plaidCoherency.equals("NONE"))
			this.overlappingSettings.setPlaidCoherency(PlaidCoherency.NONE);
		else
			this.overlappingSettings.setPlaidCoherency(PlaidCoherency.NO_OVERLAPPING);
		
		
		this.overlappingSettings.setPercOfOverlappingTrics(percOverlappingTrics);
		this.overlappingSettings.setMaxTricsPerOverlappedArea(maxOverlappingTrics);
		this.overlappingSettings.setMaxPercOfOverlappingElements(percOverlappingElements);
		this.overlappingSettings.setPercOfOverlappingRows(percOverlappingRows);
		this.overlappingSettings.setPercOfOverlappingColumns(percOverlappingColumns);
		this.overlappingSettings.setPercOfOverlappingContexts(percOverlappingContexts);
	}
	
	public void setExtras(double percMissingsOnBackground, double percMissingsOnTrics, double percNoiseOnBackground, double percNoiseOnTrics,
			double noiseDeviation, double percErrorsOnBackground, double percErrorsOnTrics) {
		
		this.noiseSettings = new NoiseSettings();
		
		this.noiseSettings.setPercMissingsOnBackground(percMissingsOnBackground);
		this.noiseSettings.setPercMissingsOnTrics(percMissingsOnTrics);
		this.noiseSettings.setPercNoiseOnBackground(percNoiseOnBackground);
		this.noiseSettings.setPercNoiseOnTrics(percNoiseOnTrics);
		this.noiseSettings.setNoiseDeviation(noiseDeviation); 
		this.noiseSettings.setPercErrorsOnBackground(percErrorsOnBackground);
		this.noiseSettings.setPercErrorsOnTrics(percErrorsOnTrics);
	}
	
	public Dataset getGeneratedDataset() {
		return this.generatedDataset;
	}
	
	public void generateNumericDataset() throws Exception {
		
		long startTimeGen;
		long stopTimeGen;
		long startTimeBics;
		long stopTimeBics;
		NumericDatasetGenerator generator;
		
		printDatasetSettings();
		
		this.progressUpdate.accept(5, 100);
		this.messageUpdate.accept("Generating Background...");
		
		String tricDataFileName;
		String datasetFileName;
		
		if(this.filename.isEmpty()) {
			if(tricPatterns.size() == 1) {
			tricDataFileName = "tric_" + tricPatterns.get(0).getRowsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getColumnsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;

			datasetFileName = "data_" + tricPatterns.get(0).getRowsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getColumnsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;
			}
			else {
				tricDataFileName = "tric_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
				datasetFileName = "data_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
			}
		}
		else {
			tricDataFileName = this.filename + "_trics";
			datasetFileName = this.filename + "_data";
		}
		
		startTimeGen = System.currentTimeMillis();
		generator = new NumericDatasetGenerator(realValued, numRows, numCols, numCtxs, numTrics, background, minM, maxM);
		stopTimeGen = System.currentTimeMillis();
		
		generator.addObserver(this);
		
		System.out.println("(TricDatasetGenerator) Execution Time: " + ((double)(stopTimeGen - startTimeGen)) / 1000);
		
		updateProgressStatusAndMessage(20, "Generating Triclusters...");
		
		startTimeBics = System.currentTimeMillis();
		NumericDataset generatedDataset = (NumericDataset) generator.generate(tricPatterns, tricStructure, overlappingSettings);
		stopTimeBics = System.currentTimeMillis();

		System.out.println("(GeneratePlaidRealTrics) Execution Time: " + ((double) (stopTimeBics - startTimeBics)) / 1000);
		
		updateProgressStatusAndMessage(80, "Generating Missings...");
		generatedDataset.plantMissingElements(this.noiseSettings.getPercMissingsOnBackground(), this.noiseSettings.getPercMissingsOnTrics());
		
		updateProgressStatusAndMessage(85, "Generating Noise...");
		generatedDataset.plantNoisyElements(this.noiseSettings.getPercNoiseOnBackground(), this.noiseSettings.getPercNoiseOnTrics(), this.noiseSettings.getNoiseDeviation());
		
		updateProgressStatusAndMessage(90, "Generating Errors...");
		generatedDataset.plantErrors(this.noiseSettings.getPercErrorsOnBackground(), this.noiseSettings.getPercErrorsOnTrics(), this.noiseSettings.getNoiseDeviation());
		
		updateProgressStatusAndMessage(95, "Writing output...");
		
		this.generatedDataset = generatedDataset;
		saveResult(generatedDataset, tricDataFileName, datasetFileName);
		
		//updateProgressStatusAndMessage(100, "Completed!");
	}
	
	public JSONObject getTriclustersJSON() {
		return this.triclustersJSON;
	}
	
	public void generateSymbolicDataset() throws Exception {
	
		long startTimeGen;
		long stopTimeGen;
		long startTimeBics;
		long stopTimeBics;

		printDatasetSettings();
		
		TriclusterDatasetGenerator generator = null;
		
		this.progressUpdate.accept(5, 100);
		this.messageUpdate.accept("Generating Background...");
		
		startTimeGen = System.currentTimeMillis();

		if(!this.defaultSymbols) {
			String[] symbols = this.listOfSymbols.toArray(new String[0]);
			generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, symbols,
					false);
		}
		else
			generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, numberOfSymbols, false);
		stopTimeGen = System.currentTimeMillis();

		generator.addObserver(this);
		
		System.out.println("(BicMatrixGenerator) Execution Time: " + ((double)(stopTimeGen - startTimeGen))/1000 + " secs");

		updateProgressStatusAndMessage(20, "Generating Triclusters...");
		
		startTimeBics = System.currentTimeMillis();
		SymbolicDataset generatedDataset = (SymbolicDataset) generator.generate(this.tricPatterns, tricStructure, this.overlappingSettings);
		stopTimeBics = System.currentTimeMillis();
		
		updateProgressStatusAndMessage(80, "Generating Missings...");
		generatedDataset.plantMissingElements(this.noiseSettings.getPercMissingsOnBackground(), this.noiseSettings.getPercMissingsOnTrics());
		
		updateProgressStatusAndMessage(85, "Generating Noise...");
		generatedDataset.plantNoisyElements(this.noiseSettings.getPercNoiseOnBackground(), this.noiseSettings.getPercNoiseOnTrics(), 
				(int)this.noiseSettings.getNoiseDeviation());
		
		updateProgressStatusAndMessage(90, "Generating Errors...");
		generatedDataset.plantErrors(this.noiseSettings.getPercErrorsOnBackground(), this.noiseSettings.getPercErrorsOnTrics(), 
				(int)this.noiseSettings.getNoiseDeviation());
		
		String tricDataFileName;
		String datasetFileName;
		
		updateProgressStatusAndMessage(95, "Writing output...");
		
		if(filename.isEmpty()) {
			if(tricPatterns.size() == 1) {
			tricDataFileName = "tric_" + tricPatterns.get(0).getRowsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getColumnsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;

			datasetFileName = "data_" + tricPatterns.get(0).getRowsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getColumnsPattern().name().charAt(0) 
					+ tricPatterns.get(0).getContextsPattern().name().charAt(0) 
					+ "_" + numRows + "x" + numCols + "x" + numCtxs;
			}
			else {
				tricDataFileName = "tric_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
				datasetFileName = "data_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
			}
		}
		else {
			tricDataFileName = filename + "_trics";
			datasetFileName = filename + "_data";
		}
		
		this.generatedDataset = generatedDataset;
		saveResult(generatedDataset, tricDataFileName, datasetFileName);
		
		//updateProgressStatusAndMessage(100, "Completed!");
	}
	
	public void saveResult(NumericDataset generatedDataset, String tricDataFileName, String datasetFileName) throws Exception {

		long startWriting = 0;
		long stopWriting = 0;
		
		long startWritingTxt;
		long stopWritingTxt;
		
		long startWritingJson;
		long stopWritingJson;
		
		startWritingTxt = System.currentTimeMillis();
		IOUtils.writeFile(path, tricDataFileName + ".txt",generatedDataset.getTricsInfo());
		System.out.println("txt info file written!");
		stopWritingTxt = System.currentTimeMillis();
		System.out.println("(Writing TXT) Execution Time: " + ((double)(stopWritingTxt - startWritingTxt))/1000 + " secs");
		
		startWritingJson = System.currentTimeMillis();
		this.triclustersJSON = generatedDataset.getTricsInfoJSON(generatedDataset);
		IOUtils.writeFile(path, tricDataFileName + ".json", this.triclustersJSON.toString());
		stopWritingJson = System.currentTimeMillis();
		System.out.println("JSON file written!");
		System.out.println("(Writing JSON1) Execution Time: " + ((double)(stopWritingJson - startWritingJson))/1000 + " secs");
		
		long memory_before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Memory before gc: " + memory_before);
		System.gc();
		long memory_after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Memory after gc: " + memory_after);
		
		this.triclustersJSON = this.triclustersJSON.getJSONObject("Triclusters");
		
		int threshold = generatedDataset.getNumRows() / 10;
		int step = generatedDataset.getNumRows() / threshold;

		ExecutorService es = null;
		
		if(!this.isSingleFileOutput())
			es = Executors.newCachedThreadPool();
		
		long startBuildingFile = 0;
		long endBuildingFile = 0;
		
		
		for(int s = 0; s < step; s++)
			if(this.isSingleFileOutput()) {
				startBuildingFile = System.currentTimeMillis();
				String content = IOUtils.matrixToStringColOriented(generatedDataset, threshold, s, s==0);
				endBuildingFile = System.currentTimeMillis();
				
				startWriting = System.currentTimeMillis();
				IOUtils.writeFile(path, datasetFileName + ".tsv", content);	
				stopWriting = System.currentTimeMillis();
			}
			else {
				Thread t = new Thread(new OutputWriterThread(path, datasetFileName, s, threshold, generatedDataset));
				es.execute(t);
			}
		

		if(!this.isSingleFileOutput()) {
			es.shutdown();
			es.awaitTermination(5, TimeUnit.MINUTES);
		}
		
		System.out.println("(Building File) Execution Time: " + ((double)(endBuildingFile - startBuildingFile))/1000 + " secs");
		System.out.println("(Writing File) Execution Time: " + ((double)(stopWriting - startWriting))/1000 + " secs");

	}
	
	public void saveResult(SymbolicDataset generatedDataset, String tricDataFileName, String datasetFileName) throws Exception {

		long startWriting;
		long stopWriting;
		
		long startWritingTxt;
		long stopWritingTxt;
		
		long startWritingJson;
		long stopWritingJson;
		
		startWritingTxt = System.currentTimeMillis();
		IOUtils.writeFile(path, tricDataFileName + ".txt",generatedDataset.getTricsInfo());
		stopWritingTxt = System.currentTimeMillis();
		System.out.println("(Writing TXT) Execution Time: " + ((double)(stopWritingTxt - startWritingTxt))/1000 + " secs");
		
		this.triclustersJSON = generatedDataset.getTricsInfoJSON(generatedDataset);
		
		startWritingJson = System.currentTimeMillis();
		IOUtils.writeFile(path, tricDataFileName + ".json", this.triclustersJSON.toString());
		stopWritingJson = System.currentTimeMillis();
		System.out.println("(Writing JSON) Execution Time: " + ((double)(stopWritingJson - startWritingJson))/1000 + " secs");
		
		this.triclustersJSON = this.triclustersJSON.getJSONObject("Triclusters");

		int threshold = generatedDataset.getNumRows() / 10;
		int step = generatedDataset.getNumRows() / threshold;

		ExecutorService es = null;
		
		if(!this.isSingleFileOutput())
			es = Executors.newCachedThreadPool();
		
		startWriting = System.currentTimeMillis();
		for(int s = 0; s <= step; s++)
			if(this.isSingleFileOutput()) {
				IOUtils.writeFile(path, datasetFileName + ".tsv", IOUtils.matrixToStringColOriented(generatedDataset, threshold, s, s==0));
			}
			else {
				Thread t = new Thread(new OutputWriterThread(path, datasetFileName, s, threshold, generatedDataset));
				es.execute(t);
			}
		stopWriting = System.currentTimeMillis();

		if(!this.isSingleFileOutput()) {
			es.shutdown();
			es.awaitTermination(5, TimeUnit.MINUTES);
		}
		
		System.out.println("(Writing) Execution Time: " + ((double)(stopWriting - startWriting))/1000 + " secs");

	}

	public void setProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
        this.progressUpdate = progressUpdate ;
    }
	
	public void setMessageUpdate(Consumer<String> messageUpdate) {
        this.messageUpdate = messageUpdate ;
    }
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		
		String[] tokens = ((String) arg).split(",");
		String[] msg = tokens[1].split(":");
		String[] tricInfo = msg[1].split(" ");
		int currentTric = Integer.parseInt(tricInfo[1]) + 1;
		
		double progress = (60 / ((double) this.numTrics));
		updateProgressStatusAndMessage(this.currentProgress + (int)progress, "Generating Triclusters (" + currentTric + "/" + this.numTrics + ")...");
	}
	
	private void updateProgressStatusAndMessage(int prog, String msg) {
		
		this.currentProgress = prog;
		this.progressUpdate.accept(prog, 100);
		this.messageUpdate.accept(msg);
	}
	
	private void printDatasetSettings() {
		
		System.out.println("*** Dataset Properties ***");
		System.out.println("NumRows: " + this.numRows);
		System.out.println("NumCols: " + this.numCols);
		System.out.println("NumCtxs: " + this.numCtxs);
		
		if(this.datasetType.equals("Numeric")) {
			System.out.println("RealValued: " + this.realValued );
			System.out.println("Min: " + this.minM);
			System.out.println("Max: " + this.maxM);
		}
		else {
			System.out.println("Default symbols: " + this.defaultSymbols);
			if(this.defaultSymbols)
				System.out.println("Number of Symbols: " + this.numberOfSymbols);
			else
				System.out.println("List of Symbols: " + Arrays.toString(this.listOfSymbols.toArray()));
		}
		
		System.out.println("Background: " + this.background.getType().toString());
		if(this.background.getType().equals(BackgroundType.NORMAL)){
			System.out.println("Background Mean: " + background.getParam1());
			System.out.println("Background Std: " + background.getParam2());
		}
		else if(this.background.getType().equals(BackgroundType.DISCRETE))
			System.out.println("Probabilities: " + Arrays.toString(this.background.getParam3()));
		
		System.out.println("\n*** Triclusters Properties ***");
		System.out.println("NumTrics: " + this.numTrics);
		System.out.println("RowDist: " + this.tricStructure.getRowsDistribution());
		System.out.println("Row Param1: " + this.tricStructure.getRowsParam1());
		System.out.println("Row Param2: " + this.tricStructure.getRowsParam2());
		System.out.println("ColDist: " + this.tricStructure.getColumnsDistribution());
		System.out.println("Col Param1: " + this.tricStructure.getColumnsParam1());
		System.out.println("Col Param2: " + this.tricStructure.getColumnsParam2());
		System.out.println("CtxDist: " + this.tricStructure.getContextsDistribution());
		System.out.println("Ctx Param1: " + this.tricStructure.getContextsParam1());
		System.out.println("Ctx Param2: " + this.tricStructure.getContextsParam2());
		System.out.println("Contiguity: " + this.tricStructure.getContiguity().toString());
		
		
		System.out.println("\n*** Overlapping Settings ***");
		System.out.println("Plaid Coherency: " + this.overlappingSettings.getPlaidCoherency().toString());
		System.out.println("% of overlapping trics: " + this.overlappingSettings.getPercOfOverlappingTrics());
		System.out.println("Max trics per overlapped area: " + this.overlappingSettings.getMaxTricsPerOverlappedArea());
		System.out.println("Max % of overlapping elements per tric: " + this.overlappingSettings.getMaxPercOfOverlappingElements());
		System.out.println("Max % of overlapping rows: " + this.overlappingSettings.getPercOfOverlappingRows());
		System.out.println("Max % of overlapping cols: " + this.overlappingSettings.getPercOfOverlappingColumns());
		System.out.println("Max % of overlapping ctxs: " + this.overlappingSettings.getPercOfOverlappingContexts());
		
		System.out.println("\n*** Patterns ***");
		for(int p = 0; p < this.tricPatterns.size(); p++) {
			System.out.println("Pattern " + p + ": (" + this.tricPatterns.get(p).getRowsPattern().toString() +
					", " + this.tricPatterns.get(p).getColumnsPattern().toString() +
					", " + this.tricPatterns.get(p).getContextsPattern().toString() + ")");
		}
		
		System.out.println("\n*** Missing/Noise/Error Settings ***");
		System.out.println("% of missings on background: " + this.noiseSettings.getPercMissingsOnBackground());
		System.out.println("Max % of missings on trics: " + this.noiseSettings.getPercMissingsOnTrics());
		System.out.println("% of noise on background: " + this.noiseSettings.getPercNoiseOnBackground());
		System.out.println("Max % of noise on trics: " + this.noiseSettings.getPercNoiseOnTrics());
		System.out.println("% of errors on background: " + this.noiseSettings.getPercErrorsOnBackground());
		System.out.println("Max % of errors on trics: " + this.noiseSettings.getPercErrorsOnTrics());
	}
}
