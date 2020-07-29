package com.gtric.domain.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gtric.domain.tricluster.SymbolicTricluster;
import com.gtric.domain.tricluster.Tricluster;
import com.gtric.types.Background;
import com.gtric.types.BackgroundType;
import com.gtric.utils.IOUtils;

public class SymbolicDataset extends Dataset {

	private Random r = new Random();

	private String[] alphabet;
	private Map<String, String> symbolicMatrixMap;
	private boolean symmetries;

	private List<SymbolicTricluster> plantedTrics;

	/**
	 * Symbolic dataset with random background 
	 * @param numRows
	 * @param numCols
	 * @param numBics
	 * @param probs array with probability associated to which symbol. If null, uniform dist is used
	 * @param symmetries
	 * @param alphabetL
	 */
	public SymbolicDataset(int numRows, int numCols, int numCont, Background background, boolean symmetries,
			int alphabetL) {

		super(numRows, numCols, numCont, background);

		this.plantedTrics = new ArrayList<>();
		this.symmetries = symmetries;

		this.alphabet = new String[alphabetL];
		int val = symmetries ? -(alphabetL / 2) : 0;
		for (int i = 0; i < alphabetL; i++, val++)
			alphabet[i] = Integer.toString(val);
		if (symmetries && alphabetL % 2 == 0)
			for (int i = alphabetL / 2; i < alphabetL; i++)
				alphabet[i] = Integer.toString(Integer.parseInt(alphabet[i]) + 1);

		this.symbolicMatrixMap = new HashMap<>();
	}

	public SymbolicDataset(int numRows, int numCols, int numCont, Background background, boolean symmetries,
			String[] alphabet) {

		super(numRows, numCols, numCont, background);

		this.plantedTrics = new ArrayList<>();
		this.symmetries = symmetries;
		this.alphabet = alphabet;

		this.symbolicMatrixMap = new HashMap<>();
	}

	public int getSymbolIndex(String s) {

		int index = -1;

		for(int i = 0; i < this.alphabet.length && index == -1; i++) {
			if(s.equals(this.alphabet[i]))
				index = i;
		}

		return index;
	}

	public SymbolicTricluster getTricluster(int id) {

		SymbolicTricluster res = null;

		for(int i = 0; i < this.plantedTrics.size() && res == null; i++) {
			SymbolicTricluster t = this.plantedTrics.get(i);
			if(t.getId() == id) 
				res = t;
		}

		return res;
	}

	public void addTricluster(SymbolicTricluster tric) {
		this.plantedTrics.add(tric);
	}

	public List<SymbolicTricluster> getPlantedTrics() {
		return plantedTrics;
	}

	public void setAlphabet(String[] alphabet) {
		this.alphabet = alphabet;
	}

	public String[] getAlphabet() {
		return this.alphabet;
	}

	public boolean hasSymmetries() {
		return symmetries;
	}

	public void setMatrixItem(int context, int row, int column, String newItem) {
		this.symbolicMatrixMap.put(context + ":" + row + ":" + column, newItem);
	}

	public String getMatrixItem(int context, int row, int column) {
		return this.symbolicMatrixMap.get(context + ":" + row + ":" + column);
	}

	public boolean existsMatrixItem(int context, int row, int column) {
		
		return this.symbolicMatrixMap.containsKey(context + ":" + row + ":" + column);
	}

	@Override
	public Tricluster getTriclusterById(int id) {
		
		Tricluster t = null;
		
		for(int i = 0; i < this.plantedTrics.size() && t == null; i++) {
			if(this.plantedTrics.get(i).getId() == id)
				t = this.plantedTrics.get(i);
		}
		
		return t;
	}
	
	public String generateBackgroundValue() {
		
		String element = null;
		
		if(super.getBackground().getType().equals(BackgroundType.UNIFORM))
			element = generateBackgroundValue(null);
		else if (super.getBackground().getType().equals(BackgroundType.DISCRETE))
			element = generateBackgroundValue(super.getBackground().getParam3());
		else if (super.getBackground().getType().equals(BackgroundType.NORMAL))
			element = generateBackgroundValue(super.getBackground().getParam1(), super.getBackground().getParam2());
		else
			element = "";
		
		return element;
	}

	
	public String generateBackgroundValue(double[] probs) {
		
		String element = null;
		
		if (probs == null)
			element = getAlphabet()[r.nextInt(getAlphabet().length)];
		else {
			double p = r.nextDouble();
			double sum = 0.0;
			int i = 0;
			while(sum < p){
				sum += probs[i];
				i++;
			}
			element = getAlphabet()[i - 1];
		}
		
		return element;
	}
	
	public String generateBackgroundValue(double mean, double sd) {
		
		String element = null;
		
		NormalDistribution n = new NormalDistribution(mean, sd);
		int vals = (int) n.sample(1)[0];
		
		if (vals < 0)
			vals = 0;
		else if (vals >= getAlphabet().length)
			vals = getAlphabet().length - 1;
		
		element = getAlphabet()[vals];
		
		return element;
		
	}

	@Override
	public String getTricsInfo() {
		StringBuilder res = new StringBuilder("Number of planted triclusters: " + plantedTrics.size()+"\r\n");
		res.append("Tricluster coverage: " + ((double) (this.getSize() - this.getBackgroundSize())) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Missing values on dataset: " + ((double) this.getNumberOfMissings()) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Noise values on dataset: " + ((double) this.getNumberOfNoisy()) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Errors on dataset: " + ((double) this.getNumberOfErrors()) / ((double) this.getSize()) * 100 + "%\n\n\n");
		
		for(SymbolicTricluster tric : plantedTrics) {
			res.append(tric.toString() + "\r\n\n");
			for(int context : tric.getContexts()) {
				res.append("Context: " + context + "\n");
				res.append(IOUtils.printSymbolicTricluster(this.symbolicMatrixMap, context, tric.getRows(), tric.getColumns()) + "\n");
			}
		}
		return res.toString().replace(",]","]");
	}

	@Override
	public void plantMissingElements(double percBackground, double percTricluster) {

		int nrMissingsBackground = (int) (this.getBackgroundSize() * percBackground);
		//System.out.println("Total expected missings: " + nrMissingsBackground);
		Random rand = new Random();

		int row = -1;
		int col = -1;
		int ctx = -1;

		for (int k = 0; k < nrMissingsBackground; k++) {
			String e;
			do {
				row = rand.nextInt(getNumRows());
				col = rand.nextInt(getNumCols());
				ctx = rand.nextInt(getNumContexts());
				e = ctx + ":" + row + ":" + col;
			} while (this.isMissing(e) || this.isPlanted(e));

			this.addMissingElement(e);
			
			//System.out.println("Missing on background " + e);
		}
		
		//System.out.println("Total Missings on back: " + this.getNumberOfMissings() + "(" + (double)this.getNumberOfMissings() / this.getBackgroundSize() +  "%)");

		for(SymbolicTricluster t : this.plantedTrics) {

			System.out.println("Planting missings on tric " + t.getId());
			
			int nrMissingsTric = (int) (t.getSize() * percTricluster * rand.nextDouble());

			List<String> elems = this.getTriclusterElements(t.getId());
			String e;
			for(int k = t.getNumberOfMissings(); k < nrMissingsTric; k++) {
				do {
					e = elems.get(rand.nextInt(elems.size()));
				} while (this.isMissing(e) || !respectsOverlapConstraint(e, "Missings", percTricluster));

				this.addMissingElement(e);

				for(Integer i : this.getTricsByElem(e))
					this.getTricluster(i).addMissing();

				String[] coord = e.split(":");

				ctx = Integer.parseInt(coord[0]);
				row = Integer.parseInt(coord[1]);
				col = Integer.parseInt(coord[2]);

			}
		}

		for(String e : this.getMissingElements()) {
			String[] coord = e.split(":");
			ctx = Integer.parseInt(coord[0]);
			row = Integer.parseInt(coord[1]);
			col = Integer.parseInt(coord[2]);

			this.setMatrixItem(ctx, row, col, "");
		}
	}

	public void plantNoisyElements(double percBackground, double percTricluster, int maxDeviation) {

		int nrNoiseBackground = (int) (this.getBackgroundSize() * percBackground);
		//System.out.println("Total expected noisy: " + nrNoiseBackground);
		Random rand = new Random();

		int row = -1;
		int col = -1;
		int ctx = -1;

		for (int k = 0; k < nrNoiseBackground; k++) {

			boolean stop = false;
			String e;

			do {
				row = rand.nextInt(getNumRows());
				col = rand.nextInt(getNumCols());
				ctx = rand.nextInt(getNumContexts());
				e = ctx + ":" + row + ":" + col;
				if(!this.isNoisy(e) && !this.isPlanted(e) && !this.isMissing(e))
					stop = true;

			} while (!stop);

			this.addNoisyElement(e);
			
			//System.out.println("Noisy on background " + e);
		}

		//System.out.println("Total Noisy on back: " + this.getNumberOfNoisy() + "(" + (double)this.getNumberOfNoisy() / this.getBackgroundSize() +  "%)");
		
		for(SymbolicTricluster t : this.plantedTrics) {

			System.out.println("Planting noise on tric " + t.getId());
			
			int nrNoisyTric = (int) (t.getSize() * percTricluster * rand.nextDouble());

			List<String> elems = this.getTriclusterElements(t.getId());
			String e;
			for(int k = t.getNumberOfNoisy(); k < nrNoisyTric; k++) {
				do {
					e = elems.get(rand.nextInt(elems.size()));
				} while (this.isMissing(e) || this.isNoisy(e) || !respectsOverlapConstraint(e, "Noisy", percTricluster));

				this.addNoisyElement(e);

				//System.out.println("Noisy on tric " + t.getId() + "on " + e);
				
				for(Integer i : this.getTricsByElem(e))
					this.getTricluster(i).addNoisy();
			}
		}

		for(String e : this.getNoisyElements()) {
			
			String[] coord = e.split(":");
			ctx = Integer.parseInt(coord[0]);
			row = Integer.parseInt(coord[1]);
			col = Integer.parseInt(coord[2]);
			
			int symbolIndex = -1;
			
			if(this.existsMatrixItem(ctx, row, col))
				symbolIndex = this.getSymbolIndex(this.getMatrixItem(ctx, row, col));
			else {
				String newSymbol = this.generateBackgroundValue();
				this.setMatrixItem(ctx, row, col, newSymbol);
				symbolIndex = this.getSymbolIndex(newSymbol);
			}

			int deviation = 1 + rand.nextInt(maxDeviation);
			int newIndex = rand.nextBoolean() ? (symbolIndex + deviation) : (symbolIndex - deviation);
			String newValue;
			
			if(newIndex < 0)
				newValue = this.alphabet[0];
			else if(newIndex >= this.alphabet.length)
				newValue = this.alphabet[this.alphabet.length - 1];
			else
				newValue = this.alphabet[newIndex];
			 
			//System.out.println("(Noisy) OldValue: " + this.getMatrixItem(ctx, row, col) + " Deviation: " + deviation + " NewValue: " + newValue);

			this.setMatrixItem(ctx, row, col, newValue);
		}
	}


	public void plantErrors(double percBackground, double percTricluster, int minDeviation) {

		int nrErrorsBackground = (int) (this.getBackgroundSize() * percBackground);
		//System.out.println("Total expected errors: " + nrErrorsBackground);
		Random rand = new Random();

		int row = -1;
		int col = -1;
		int ctx = -1;

		for (int k = 0; k < nrErrorsBackground; k++) {

			boolean stop = false;
			String e;

			do {
				row = rand.nextInt(getNumRows());
				col = rand.nextInt(getNumCols());
				ctx = rand.nextInt(getNumContexts());
				e = ctx + ":" + row + ":" + col;

				if(!this.isError(e) && !this.isPlanted(e) && !this.isMissing(e) && !this.isNoisy(e))
					stop = true;

			} while (!stop);

			this.addErrorElement(e);
			
			//System.out.println("Error on background " + e);
			
			int newIndex = (rand.nextBoolean()) ? this.alphabet.length - 1 : 0;
			
			//System.out.println("(Error back) OldValue: " + this.getMatrixItem(ctx, row, col) + " NewValue: " + this.alphabet[newIndex]);
			
			this.setMatrixItem(ctx, row, col, this.alphabet[newIndex]);
		}

		//System.out.println("Total Errors on back: " + this.getNumberOfErrors() + "(" + (double)this.getNumberOfErrors() / this.getBackgroundSize() +  "%)");
		
		for(SymbolicTricluster t : this.plantedTrics) {

			System.out.println("Planting errors on tric " + t.getId());
			
			int nrErrorsTric = (int) (t.getSize() * percTricluster * rand.nextDouble());

			List<String> elems = this.getTriclusterElements(t.getId());
			String e;
			for(int k = t.getNumberOfErrors(); k < nrErrorsTric; k++) {
				do {
					e = elems.get(rand.nextInt(elems.size()));
				} while (this.isMissing(e) || this.isNoisy(e) || this.isError(e) || !respectsOverlapConstraint(e, "Errors", percTricluster));

				this.addErrorElement(e);

				for(Integer i : this.getTricsByElem(e))
					this.getTricluster(i).addError();
				
				String[] coord = e.split(":");
				ctx = Integer.parseInt(coord[0]);
				row = Integer.parseInt(coord[1]);
				col = Integer.parseInt(coord[2]);
				
				int symbolIndex = -1;
				
				if(this.existsMatrixItem(ctx, row, col))
					symbolIndex = this.getSymbolIndex(this.getMatrixItem(ctx, row, col));
				else {
					String newSymbol = this.generateBackgroundValue();
					this.setMatrixItem(ctx, row, col, newSymbol);
					symbolIndex = this.getSymbolIndex(newSymbol);
				}
				
				int newIndex;
				
				do {
					newIndex = rand.nextInt(this.alphabet.length); 
				}while(Math.abs(symbolIndex - newIndex) <= minDeviation);
				
				//System.out.println("(Error tric) OldValue: " + this.getMatrixItem(ctx, row, col) +" NewValue: " + this.alphabet[newIndex]);
				
				this.setMatrixItem(ctx, row, col, this.alphabet[newIndex]);
			}
		}		
	}

	private boolean respectsOverlapConstraint(String elem, String type, double percTricluster) {

		boolean respects = true;

		List<Integer> trics = this.getTricsByElem(elem);

		for(int i = 0; i < trics.size() && respects; i++) {
			SymbolicTricluster t = this.getTricluster(trics.get(i));
			int maxAllowed = (int) (t.getSize() * percTricluster);

			if(type.equals("Missings") && t.getNumberOfMissings() + 1 > maxAllowed)
				respects = false;

			if(type.equals("Noisy") && t.getNumberOfNoisy() + 1 > maxAllowed)
				respects = false;

			if(type.equals("Errors") && t.getNumberOfErrors() + 1 > maxAllowed)
				respects = false;
		}

		return respects;
	}

	public JSONObject getTricsInfoJSON(SymbolicDataset generatedDataset) {
		JSONObject dataset = new JSONObject();
		
		dataset.put("#DatasetRows", this.getNumRows());
		dataset.put("#DatasetColumns", this.getNumCols());
		dataset.put("#DatasetContexts", this.getNumContexts());
		JSONArray alphabet = new JSONArray();
		for(String s : this.alphabet)
			alphabet.put(s);
		dataset.put("#DatasetAlphabet", alphabet);
		
		JSONArray triclusterList = new JSONArray();
		JSONObject triclusters = new JSONObject();
		
		for(Tricluster tric : plantedTrics) 
			triclusters.put(String.valueOf(tric.getId()), tric.toStringJSON(generatedDataset));
		
		dataset.put("Triclusters", triclusters);
		
		System.out.println("\n\n" + dataset.toString());
		
		return dataset;
	}
}
