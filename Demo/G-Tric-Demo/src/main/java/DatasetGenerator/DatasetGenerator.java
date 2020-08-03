package DatasetGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import com.gtric.utils.InputValidation;
import com.gtric.utils.OverlappingSettings;
import com.gtric.utils.QualitySettings;
import com.gtric.utils.TriclusterPattern;
import com.gtric.utils.TriclusterStructure;

/**
 * Class to show how G-Tric can be used programmatically and to generate the datasets defined in the paper (config_files folder)
 * @author Joao Lobo - jlobo@lasige.di.fc.ul.pt
 */
public class DatasetGenerator {

	private static final String FILE_PATH = "GeneratedDatasets/"; 
	
	public static void main(String[] args) throws Exception {
		
		File directory = new File(FILE_PATH);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
		
	    //Real World data
		generateDatasetByConfig(load_config_file("config_files/Real World/dataset_real_world_1.xml"));
		generateDatasetByConfig(load_config_file("config_files/Real World/dataset_real_world_2.xml"));
		generateDatasetByConfig(load_config_file("config_files/Real World/dataset_real_world_3.xml"));
		generateDatasetByConfig(load_config_file("config_files/Real World/dataset_real_world_4.xml"));
		generateDatasetByConfig(load_config_file("config_files/Real World/dataset_real_world_5.xml"));
		
		//Base
		generateDatasetByConfig(load_config_file("config_files/Base/dataset_base_R.xml"));
		generateDatasetByConfig(load_config_file("config_files/Base/dataset_base_S.xml"));
		generateDatasetByConfig(load_config_file("config_files/Base/dataset_base_B.xml"));
		generateDatasetByConfig(load_config_file("config_files/Base/dataset_base_C.xml"));
		
		//Overlapping
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_R_overlapping_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_R_overlapping_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_S_overlapping_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_S_overlapping_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_B_overlapping_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_B_overlapping_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_C_overlapping_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Overlapping/dataset_base_C_overlapping_high.xml"));
		
		//Quality
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_R_quality_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_R_quality_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_S_quality_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_S_quality_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_B_quality_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_B_quality_high.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_C_quality_low.xml"));
		generateDatasetByConfig(load_config_file("config_files/Quality/dataset_base_C_quality_high.xml"));
		
		//Rich
		generateDatasetByConfig(load_config_file("config_files/Rich/dataset_rich_R.xml"));
		generateDatasetByConfig(load_config_file("config_files/Rich/dataset_rich_S.xml"));
		generateDatasetByConfig(load_config_file("config_files/Rich/dataset_rich_B.xml"));
		generateDatasetByConfig(load_config_file("config_files/Rich/dataset_rich_C.xml"));
		
		
		//generateNumericDataset();
		//generateSymbolicDataset();

	}


	private static void generateDatasetByConfig(Document doc) {
		
		String dataType = doc.getElementsByTagName("data_type").item(0).getTextContent();
		
		if(dataType.equals("real") || dataType.equals("integer"))
			try {
				generateNumericDatasetByConfig(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				generateSymbolicDatasetByConfig(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}


	private static void generateSymbolicDatasetByConfig(Document doc) throws Exception {
		
		String dataType = doc.getElementsByTagName("data_type").item(0).getTextContent();
		String filename = doc.getElementsByTagName("filename").item(0).getTextContent();
		boolean realValued = dataType.equals("real");
		
		Element datasetProperties = (Element) doc.getElementsByTagName("dataset_properties").item(0);
		Element triclustersProperties = (Element) doc.getElementsByTagName("triclusters_properties").item(0);
		Element overlappingProperties = (Element) doc.getElementsByTagName("overlapping").item(0);
		Element qualityProperties = (Element) doc.getElementsByTagName("quality").item(0);
		
		int numRows = Integer.parseInt(datasetProperties.getElementsByTagName("num_rows").item(0).getTextContent());
		int numCols = Integer.parseInt(datasetProperties.getElementsByTagName("num_cols").item(0).getTextContent());
		int numCtxs = Integer.parseInt(datasetProperties.getElementsByTagName("num_ctxs").item(0).getTextContent());
		
		Background background = null;
		BackgroundType backgroundType = BackgroundType.valueOf(datasetProperties.getElementsByTagName("background").item(0).getTextContent());
		
		if(backgroundType.equals(BackgroundType.NORMAL)) {
			double mean = Double.parseDouble(datasetProperties.getElementsByTagName("alphabet_mean").item(0).getTextContent());
			double std = Double.parseDouble(datasetProperties.getElementsByTagName("alphabet_std").item(0).getTextContent());
			background = new Background(backgroundType, mean, std);
		}
		else if(backgroundType.equals(BackgroundType.DISCRETE)){
			
			double[] probs = extractBackgroundProbabilities((Element) doc.getElementsByTagName("background_probabilities").item(0));
			background = new Background(backgroundType, probs);
		}
		else
			background = new Background(backgroundType);
		
		int numTrics = Integer.parseInt(triclustersProperties.getElementsByTagName("num_trics").item(0).getTextContent());
		
		TriclusterDatasetGenerator generator;
		
		String[] alphabet;
		int alphabet_size = datasetProperties.getElementsByTagName("alphabet_size").getLength();
		
		if(alphabet_size == 0) {
			String alphabetSymbols = datasetProperties.getElementsByTagName("alphabet").item(0).getTextContent();
			alphabet = alphabetSymbols.split(",");
			
			generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, alphabet, false);
		}
		else
			generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, alphabet_size, false);
		
		List<TriclusterPattern> patterns = extractTriclustersPatterns((Element) doc.getElementsByTagName("patterns").item(0));		
		TriclusterStructure tricStructure = extractTriclusterStructure(triclustersProperties);
		OverlappingSettings overlapping = extractOverlappingSettings(overlappingProperties);
		
		SymbolicDataset generatedDataset = null;
		
		try {
			generatedDataset = (SymbolicDataset) generator.generate(patterns, tricStructure, overlapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		QualitySettings quality = extractQualitySettings(qualityProperties);
		
		generatedDataset.plantMissingElements(quality.getPercMissingsOnBackground(), quality.getPercMissingsOnTrics());
		generatedDataset.plantNoisyElements(quality.getPercNoiseOnBackground(), quality.getPercNoiseOnTrics(), (int) quality.getNoiseDeviation());
		generatedDataset.plantErrors(quality.getPercErrorsOnBackground(), quality.getPercErrorsOnTrics(), (int) quality.getNoiseDeviation());
		
		GTricService serv = new GTricService();
		serv.setPath(FILE_PATH);
		serv.setSingleFileOutput(true);
		
		try { 
			serv.saveResult(generatedDataset, filename + "_trics", filename + "_data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void generateNumericDatasetByConfig(Document doc) throws Exception {
		
		String dataType = doc.getElementsByTagName("data_type").item(0).getTextContent();
		String filename = doc.getElementsByTagName("filename").item(0).getTextContent();
		boolean realValued = dataType.equals("real");
		
		Element datasetProperties = (Element) doc.getElementsByTagName("dataset_properties").item(0);
		Element triclustersProperties = (Element) doc.getElementsByTagName("triclusters_properties").item(0);
		Element overlappingProperties = (Element) doc.getElementsByTagName("overlapping").item(0);
		Element qualityProperties = (Element) doc.getElementsByTagName("quality").item(0);
		
		int numRows = Integer.parseInt(datasetProperties.getElementsByTagName("num_rows").item(0).getTextContent());
		int numCols = Integer.parseInt(datasetProperties.getElementsByTagName("num_cols").item(0).getTextContent());
		int numCtxs = Integer.parseInt(datasetProperties.getElementsByTagName("num_ctxs").item(0).getTextContent());
		
		Background background = null;
		BackgroundType backgroundType = BackgroundType.valueOf(datasetProperties.getElementsByTagName("background").item(0).getTextContent());
		
		if(backgroundType.equals(BackgroundType.NORMAL)) {
			double mean = Double.parseDouble(datasetProperties.getElementsByTagName("alphabet_mean").item(0).getTextContent());
			double std = Double.parseDouble(datasetProperties.getElementsByTagName("alphabet_std").item(0).getTextContent());
			background = new Background(backgroundType, mean, std);
		}
		else if(backgroundType.equals(BackgroundType.DISCRETE)){
			
			if(realValued)
				throw new Exception("Discrete background are only valid on symbolic or integer datasets!");
			
			double[] probs = extractBackgroundProbabilities((Element) doc.getElementsByTagName("background_probabilities").item(0));
			background = new Background(backgroundType, probs);
		}
		else
			background = new Background(backgroundType);
		
		int min = Integer.parseInt(datasetProperties.getElementsByTagName("alphabet_min").item(0).getTextContent());
		int max = Integer.parseInt(datasetProperties.getElementsByTagName("alphabet_max").item(0).getTextContent());
		int numTrics = Integer.parseInt(triclustersProperties.getElementsByTagName("num_trics").item(0).getTextContent());
		
		TriclusterDatasetGenerator generator = new NumericDatasetGenerator(realValued, numRows, numCols, numCtxs, numTrics, background, min, max);

		List<TriclusterPattern> patterns = extractTriclustersPatterns((Element) doc.getElementsByTagName("patterns").item(0));		
		TriclusterStructure tricStructure = extractTriclusterStructure(triclustersProperties);
		OverlappingSettings overlapping = extractOverlappingSettings(overlappingProperties);
		
		NumericDataset generatedDataset = null;
		
		try {
			generatedDataset = (NumericDataset) generator.generate(patterns, tricStructure, overlapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		QualitySettings quality = extractQualitySettings(qualityProperties);
		
		generatedDataset.plantMissingElements(quality.getPercMissingsOnBackground(), quality.getPercMissingsOnTrics());
		generatedDataset.plantNoisyElements(quality.getPercNoiseOnBackground(), quality.getPercNoiseOnTrics(), quality.getNoiseDeviation());
		generatedDataset.plantErrors(quality.getPercErrorsOnBackground(), quality.getPercErrorsOnTrics(), quality.getNoiseDeviation());
		
		GTricService serv = new GTricService();
		serv.setPath(FILE_PATH);
		serv.setSingleFileOutput(true);
		
		try { 
			serv.saveResult(generatedDataset, filename + "_trics", filename + "_data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static double[] extractBackgroundProbabilities(Element probabilities) {
		
		NodeList probsList = probabilities.getElementsByTagName("probability");
		
		double[] probs = new double[probsList.getLength()];
		
		for(int i = 0; i < probsList.getLength(); i++) 
			probs[i] = Double.parseDouble(probsList.item(i).getTextContent());
			
		return probs;
	}


	private static QualitySettings extractQualitySettings(Element qualityProperties) {
		
		QualitySettings quality = new QualitySettings();
		
		double backgroundMissings = Double.parseDouble(qualityProperties.getElementsByTagName("background_missings").item(0).getTextContent());
		quality.setPercMissingsOnBackground(backgroundMissings);
		
		double triclusterMissings = Double.parseDouble(qualityProperties.getElementsByTagName("tricluster_missings").item(0).getTextContent());
		quality.setPercMissingsOnBackground(triclusterMissings);
		
		double backgroundNoise = Double.parseDouble(qualityProperties.getElementsByTagName("background_noise").item(0).getTextContent());
		quality.setPercNoiseOnBackground(backgroundNoise);
		
		double triclusterNoise = Double.parseDouble(qualityProperties.getElementsByTagName("tricluster_noise").item(0).getTextContent());
		quality.setPercNoiseOnBackground(triclusterNoise);
		
		double noiseDeviation = Double.parseDouble(qualityProperties.getElementsByTagName("noise_deviation").item(0).getTextContent());
		quality.setNoiseDeviation(noiseDeviation);
		
		double backgroundErrors = Double.parseDouble(qualityProperties.getElementsByTagName("background_errors").item(0).getTextContent());
		quality.setPercMissingsOnBackground(backgroundErrors);
		
		double triclusterErrors = Double.parseDouble(qualityProperties.getElementsByTagName("tricluster_errors").item(0).getTextContent());
		quality.setPercMissingsOnBackground(triclusterErrors);
		
		return quality;
	}


	private static List<TriclusterPattern> extractTriclustersPatterns(Element patternsElem) {
		
		List<TriclusterPattern> patterns = new ArrayList<>();
		
		NodeList patternsList = patternsElem.getElementsByTagName("pattern");
		for(int i = 0; i < patternsList.getLength(); i++) {
			Element patternInfo = (Element) patternsList.item(i);
			TriclusterPattern p = new TriclusterPattern(PatternType.valueOf(patternInfo.getElementsByTagName("rows").item(0).getTextContent()),
					PatternType.valueOf(patternInfo.getElementsByTagName("cols").item(0).getTextContent()),
					PatternType.valueOf(patternInfo.getElementsByTagName("ctxs").item(0).getTextContent()));
			patterns.add(p);	
		}
		return patterns;
	}


	private static OverlappingSettings extractOverlappingSettings(Element overlappingProperties) {
		
		OverlappingSettings overlapping = new OverlappingSettings();

		String plaidCoherency = overlappingProperties.getElementsByTagName("plaid_coherency").item(0).getTextContent();
		
		//Plaid Coherency (ADDITIVE, MULTIPLICATIVE, INTERPOLED, NONE or NO_OVERLAPPING)
		overlapping.setPlaidCoherency(PlaidCoherency.valueOf(plaidCoherency));

		if(!overlapping.getPlaidCoherency().equals(PlaidCoherency.NO_OVERLAPPING)) {
		
			//Percentage of overlapping trics defines how many trics are allowed to overlap:
			//if 0.5 only half of the dataset triclusters will overlap
			double percOverlappingTrics = Double.parseDouble(overlappingProperties.getElementsByTagName("perc_overlapping_trics").item(0).getTextContent());
			overlapping.setPercOfOverlappingTrics(percOverlappingTrics);
	
			//Maximum number of triclusters that can overlap together. if equal to 3, there will be, at max, 3 triclusters
			//that intersect each other(T1 ^ T2 ^ T3)
			int maxTricsOverlappedArea = Integer.parseInt(overlappingProperties.getElementsByTagName("max_trics_overlapped_area").item(0).getTextContent());
			overlapping.setMaxTricsPerOverlappedArea(maxTricsOverlappedArea);
	
			//Maximum percentage of elements shared by overlapped triclusters. If 0.5, T1 ^ T2 will have, at max, 50% of the elements
			//of the smallest tric
			double percOverlappingElems = Double.parseDouble(overlappingProperties.getElementsByTagName("perc_overlapping_elems").item(0).getTextContent());
			overlapping.setMaxPercOfOverlappingElements(percOverlappingElems);
	
			//Percentage of allowed amount of overlaping across triclusters rows, columns and contexts. if rows=0.5, then 
			//T2 will intersect, at max, with half(50%) the rows of T1.
			//if you dont want any restriction on the number of rows/cols/ctxs that can overlapp, use 1.0
			double percOverlappingRows = Double.parseDouble(overlappingProperties.getElementsByTagName("perc_overlapping_rows").item(0).getTextContent());
			double percOverlappingCols = Double.parseDouble(overlappingProperties.getElementsByTagName("perc_overlapping_cols").item(0).getTextContent());
			double percOverlappingCtxs = Double.parseDouble(overlappingProperties.getElementsByTagName("perc_overlapping_ctxs").item(0).getTextContent());
			overlapping.setPercOfOverlappingRows(percOverlappingRows);
			overlapping.setPercOfOverlappingColumns(percOverlappingCols);
			overlapping.setPercOfOverlappingContexts(percOverlappingCtxs);
		}
			
		return overlapping;
	}


	private static TriclusterStructure extractTriclusterStructure(Element triclustersProperties) {
		
		TriclusterStructure tricStructure = new TriclusterStructure();

		String rowDist = triclustersProperties.getElementsByTagName("row_distribution").item(0).getTextContent();
		double rowParam1 = Double.parseDouble(triclustersProperties.getElementsByTagName("row_param_1").item(0).getTextContent());
		double rowParam2 = Double.parseDouble(triclustersProperties.getElementsByTagName("row_param_2").item(0).getTextContent());
		
		String colDist = triclustersProperties.getElementsByTagName("col_distribution").item(0).getTextContent();
		double colParam1 = Double.parseDouble(triclustersProperties.getElementsByTagName("col_param_1").item(0).getTextContent());
		double colParam2 = Double.parseDouble(triclustersProperties.getElementsByTagName("col_param_2").item(0).getTextContent());
		
		String ctxDist = triclustersProperties.getElementsByTagName("ctx_distribution").item(0).getTextContent();
		double ctxParam1 = Double.parseDouble(triclustersProperties.getElementsByTagName("ctx_param_1").item(0).getTextContent());
		double ctxParam2 = Double.parseDouble(triclustersProperties.getElementsByTagName("ctx_param_2").item(0).getTextContent());
		
		String contiguity = triclustersProperties.getElementsByTagName("contiguity").item(0).getTextContent();
		
		//Distribution used to calculate the number of rows/cols/ctxs for a tric (NORMAL or UNIFORM)
		//Dist args: if dist=UNIFORM, then param1 and param2 represents the min and max, respectively
		//			 if dist=NORMAL, then param1 and param2 represents the mean and stdDev, respectively
		tricStructure.setRowsSettings(Distribution.valueOf(rowDist), rowParam1, rowParam2);
		tricStructure.setColumnsSettings(Distribution.valueOf(colDist), colParam1, colParam2);
		tricStructure.setContextsSettings(Distribution.valueOf(ctxDist), ctxParam1, ctxParam2);

		//Contiguity can occour on COLUMNS or CONTEXTS. To avoid contiguity use NONE
		tricStructure.setContiguity(Contiguity.valueOf(contiguity));
		
		return tricStructure;
	}


	private static Document load_config_file(String path) {
		
		Document doc = null;
		
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}


	
	public static void generateSymbolicDataset() throws Exception {

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

		generator = new SymbolicDatasetGenerator(numRows,numCols, numCtxs, numTrics, background, alphabet, symmetries);

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

		SymbolicDataset generatedDataset = (SymbolicDataset) generator.generate(patterns, tricStructure, overlapping);		

		System.out.println("Dataset generated!");
		
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
		generatedDataset.plantNoisyElements(noisePercOnBackground, noisePercOnPlantedTrics, noiseDeviation);
		generatedDataset.plantErrors(errorPercOnBackground, errorPercOnPlantedTrics, noiseDeviation);

		String tricDataFileName = "tric_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;
		String datasetFileName = "data_multiple" + "_" + numRows + "x" + numCols + "x" + numCtxs;

		GTricService serv = new GTricService();
		serv.setPath("/Users/atticus/git/pyTriclustering/G-Tric/");
		serv.setSingleFileOutput(true);
		serv.saveResult(generatedDataset, tricDataFileName, datasetFileName);
	}
	
	public static NumericDataset generateNumericDataset() throws Exception {

		
		//datasets number of rows
		int numRows = 28;
		//datasets number of cols
		int numCols = 20;
		//datasets number of contexts
		int numCtxs = 365;
		//number of trics to plant
		int numTrics = 128;
		
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

		generator = new NumericDatasetGenerator(realValued, numRows, numCols, numCtxs, numTrics, background, min, max);

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
		
		NumericDataset generatedDataset = (NumericDataset) generator.generate(patterns, tricStructure, overlapping);
		
		System.out.println("Dataset generated!");
		
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
		
		
		String filename = "dataset_example";

		GTricService serv = new GTricService();
		serv.setPath(FILE_PATH);
		serv.setSingleFileOutput(true);
		serv.saveResult(generatedDataset, filename + "_trics", filename + "_data");
		
		return generatedDataset;
	}
}
