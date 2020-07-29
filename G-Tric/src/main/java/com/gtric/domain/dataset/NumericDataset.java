package com.gtric.domain.dataset;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONObject;

import com.gtric.domain.tricluster.NumericTricluster;
import com.gtric.domain.tricluster.Tricluster;
import com.gtric.types.Background;
import com.gtric.types.BackgroundType;
import com.gtric.utils.IOUtils;

public class NumericDataset<T extends Number> extends Dataset {

	private Random r = new Random();
	private Map<String, T> realMatrixMap;
	private T maxM;
	private T minM;

	private List<NumericTricluster<Double>> plantedTrics;

	/**
	 * Real valued dataset with uniform background or with missing values
	 * @param numRows
	 * @param numCols
	 * @param numBics
	 * @param background if 'random', uniform dist. is used to generated background. If 'missing'
	 * background is composed by missing values
	 * @param symmetries
	 * @param minM
	 * @param maxM
	 * @param strength
	 */
	public NumericDataset(int numRows, int numCols, int numContexts, Background background, T minM, T maxM) {

		super(numRows, numCols, numContexts, background);

		plantedTrics = new ArrayList<>();
		this.minM = minM;
		this.maxM = maxM;
		this.realMatrixMap = new HashMap<>();
	}

	public void addTricluster(NumericTricluster<Double> tric) {
		this.plantedTrics.add(tric);
	}

	public List<NumericTricluster<Double>> getPlantedBics() {
		return plantedTrics;
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
	
	public void setMatrixItem(int context, int row, int column, T newItem) {
		this.realMatrixMap.put(context + ":" + row + ":" + column, newItem);
	}
	
	public T getMatrixItem(int context, int row, int column) {
		return this.realMatrixMap.get(context + ":" + row + ":" + column);
	}

	public boolean existsMatrixItem(int context, int row, int column) {
		return this.realMatrixMap.containsKey(context + ":" + row + ":" + column);
	}
	
	public T getMaxM() {
		return maxM;
	}

	public T getMinM() {
		return minM;
	}

	public T generateBackgroundValue() {
		
		T element = null;
		
		if(super.getBackground().getType().equals(BackgroundType.UNIFORM))
			element = generateBackgroundValue(null);
		else if (super.getBackground().getType().equals(BackgroundType.DISCRETE))
			element = generateBackgroundValue(super.getBackground().getParam3());
		else if (super.getBackground().getType().equals(BackgroundType.NORMAL))
			element = generateBackgroundValue(super.getBackground().getParam1(), super.getBackground().getParam2());
		else
			element = (T) new Integer(Integer.MIN_VALUE);
		
		return element;
	}
	
	private T generateBackgroundValue(double[] probs) {
		
		T backgroundValue = null;
		
		if(probs == null) {
			if (minM instanceof Integer) 
				backgroundValue = (T) new Integer(r.nextInt((maxM.intValue() - minM.intValue()) + 1) + minM.intValue());
			else 
				backgroundValue = (T) new Double(r.nextDouble() * (maxM.doubleValue() - minM.doubleValue()) + minM.doubleValue());
		}
		else {
			
			double p = r.nextDouble();
			double sum = 0.0;
			Integer i = 0;
			while(sum < p){
				sum += probs[i];
				i++;
			}
			backgroundValue = (T) i;
		}
		
		return backgroundValue;
	}
	
	private T generateBackgroundValue(double mean, double sd) {
		
		T backgroundValue = null;
		
		NormalDistribution n = new NormalDistribution(mean, sd);

		Double vals = n.sample(1)[0];
		
		if (Double.compare(vals, minM.doubleValue()) < 0)
			vals = minM.doubleValue();
		else if (Double.compare(vals, maxM.doubleValue()) > 0)
			vals = maxM.doubleValue();
		
		if (minM instanceof Integer)
			backgroundValue = (T) new Integer(vals.intValue());
		else
			backgroundValue = (T) vals;	
		
		return backgroundValue;
	}
	
	public NumericTricluster<? extends Number> getTricluster(int id) {

		NumericTricluster<?> res = null;

		for(int i = 0; i < this.plantedTrics.size() && res == null; i++) {
			NumericTricluster<?> t = this.plantedTrics.get(i);
			if(t.getId() == id) 
				res = t;
		}

		return res;
	}
	
	@Override
	public String getTricsInfo() {
		StringBuilder res = new StringBuilder("Number of planted triclusters: " + plantedTrics.size()+"\r\n");
		res.append("Tricluster coverage: " + ((double) (this.getSize() - this.getBackgroundSize())) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Missing values on dataset: " + ((double) this.getNumberOfMissings()) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Noise values on dataset: " + ((double) this.getNumberOfNoisy()) / ((double) this.getSize()) * 100 + "%\n");
		res.append("Errors on dataset: " + ((double) this.getNumberOfErrors()) / ((double) this.getSize()) * 100 + "%\n\n\n");
		
		for(NumericTricluster<? extends Number> tric : plantedTrics) {
			res.append(tric.toString() + "\r\n\n");
			for(Integer context : tric.getContexts()) {
				res.append("Context: " + context + "\n");
				res.append(IOUtils.printNumericTricluster(this.realMatrixMap, context, tric.getRows(), tric.getColumns()) + "\n");
			}
		}
		
		return res.toString().replace(",]","]");
	}
	
	public JSONObject getTricsInfoJSON(Dataset generatedDataset) {
		JSONObject dataset = new JSONObject();
		
		dataset.put("#DatasetRows", this.getNumRows());
		dataset.put("#DatasetColumns", this.getNumCols());
		dataset.put("#DatasetContexts", this.getNumContexts());
		dataset.put("#DatasetMaxValue", this.getMaxM());
		dataset.put("#DatasetMinValue", this.getMinM());
		
		JSONObject triclusters = new JSONObject();
		
		for(Tricluster tric : plantedTrics) 
			triclusters.putOpt(String.valueOf(tric.getId()), tric.toStringJSON(generatedDataset));
		
		dataset.put("Triclusters", triclusters);
		System.out.println("\n\n" + dataset.toString());
		
		return dataset;
	}

	@Override
	public void plantMissingElements(double percBackground, double percTricluster) {

		int nrMissingsBackground = (int) (this.getBackgroundSize() * percBackground);
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
		}

		for(NumericTricluster<? extends Number> t : this.plantedTrics) {

			double random = rand.nextDouble();
			int nrMissingsTric = (int) (t.getSize() * percTricluster * random);
			
			double ratio = ((double)nrMissingsTric) / ((double)t.getSize());
			System.out.println("Tric " + t.getId() + " - Number of missings: " + nrMissingsTric + "(" + ratio + ")\n");

			List<String> elems = this.getTriclusterElements(t.getId());
			String e;
			for(int k = t.getNumberOfMissings(); k < nrMissingsTric; k++) {
				do {
					e = elems.get(rand.nextInt(elems.size()));
				} while (this.isMissing(e) || !respectsOverlapConstraint(e, "Missings", percTricluster));

				this.addMissingElement(e);
				
				for(Integer i : this.getTricsByElem(e))
					this.getTricluster(i).addMissing();
			}
		}
		
		for(String e : this.getMissingElements()) {
			
			String[] coord = e.split(":");
			ctx = Integer.parseInt(coord[0]);
			row = Integer.parseInt(coord[1]);
			col = Integer.parseInt(coord[2]);
	
			if(this.maxM instanceof Double)
				setMatrixItem(ctx, row, col, (T) new Double(Integer.MIN_VALUE));
			else
				setMatrixItem(ctx, row, col, (T) new Integer(Integer.MIN_VALUE));
		}
		
	}

	public void plantNoisyElements(double percBackground, double percTricluster, double maxDeviation) {

		int nrNoiseBackground = (int) (this.getBackgroundSize() * percBackground);
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
		}

		for(NumericTricluster<? extends Number> t : this.plantedTrics) {

			double random = rand.nextDouble();
			int nrNoisyTric = (int) (t.getSize() * percTricluster * random);

			double ratio = ((double)nrNoisyTric) / ((double)t.getSize());
			System.out.println("Tric size: " + t.getSize());
			System.out.println("Tric max perc: " + percTricluster);
			System.out.println("Tric random: " + random);
			System.out.println("Tric " + t.getId() + " - Number of noise: " + nrNoisyTric + "(" + ratio + ")\n");
			
			List<String> elems = this.getTriclusterElements(t.getId());
			String e;
			for(int k = t.getNumberOfNoisy(); k < nrNoisyTric; k++) {
				do {
					e = elems.get(rand.nextInt(elems.size()));
				} while (this.isMissing(e) || this.isNoisy(e) || !respectsOverlapConstraint(e, "Noisy", percTricluster));

				this.addNoisyElement(e);
				
				for(Integer i : this.getTricsByElem(e))
					this.getTricluster(i).addNoisy();

			}
		}
		
		for(String e : this.getNoisyElements()) {
			
			String[] coord = e.split(":");

			ctx = Integer.parseInt(coord[0]);
			row = Integer.parseInt(coord[1]);
			col = Integer.parseInt(coord[2]);

			T symbolIndex = null;
			
			if(this.existsMatrixItem(ctx, row, col))
				symbolIndex = this.getMatrixItem(ctx, row, col);
			else {
				symbolIndex = this.generateBackgroundValue();
				this.setMatrixItem(ctx, row, col, symbolIndex);
			}

			double deviation;
			T newElem;
			
			if(this.maxM instanceof Double) {
				deviation = rand.nextDouble() * maxDeviation;
				deviation = rand.nextBoolean() ? deviation : -deviation;
				double newItem = symbolIndex.doubleValue() + deviation;
				
				if(Double.compare(newItem, minM.doubleValue()) < 0)
					newElem = (T) new Double(minM.doubleValue());
				else if(Double.compare(newItem, maxM.doubleValue()) > 0)
					newElem = (T) new Double(maxM.doubleValue());
				else
					newElem = (T) new Double(newItem);
				}
			else {
				deviation = 1.0 + rand.nextInt((int)maxDeviation);
				deviation = rand.nextBoolean() ? deviation : -deviation;
				System.out.println(symbolIndex);
				int newItem = symbolIndex.intValue() + (int)deviation;
				
				if(newItem < minM.intValue())
					newElem = (T) new Integer(minM.intValue());
				else if(newItem > maxM.intValue())
					newElem = (T) new Integer(maxM.intValue());
				else
					newElem = (T) new Integer(newItem);
			}
			
			setMatrixItem(ctx, row, col, newElem);
		}

	}

	public void plantErrors(double percBackground, double percTricluster, double minDeviation) {

		int nrErrorsBackground = (int) (this.getBackgroundSize() * percBackground);
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

			T newElem = (rand.nextBoolean()) ? maxM : minM;

			this.setMatrixItem(ctx, row, col, newElem);
		}

		for(NumericTricluster<? extends Number> t : this.plantedTrics) {

			int nrErrorsTric = (int) (t.getSize() * percTricluster * rand.nextDouble());
			double ratio = ((double)nrErrorsTric) / ((double)t.getSize());
			System.out.println("Tric " + t.getId() + " - Number of errors: " + nrErrorsTric + "(" + ratio + ")\n");

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

				double currentElement = 0;
				
				if(this.existsMatrixItem(ctx, row, col))
					currentElement = this.getMatrixItem(ctx, row, col).doubleValue();
				else {
					currentElement = this.generateBackgroundValue().doubleValue();
					this.setMatrixItem(ctx, row, col, (T) new Double(currentElement));
				}
				
				double candidate = 0;
				T newElem;
				
				if(this.maxM instanceof Double) {
					
					do {
						candidate = minM.doubleValue() + (maxM.doubleValue() - minM.doubleValue()) *  rand.nextDouble();
					}while(Math.abs(currentElement - candidate) <= minDeviation);
					
					if(Double.compare(candidate, minM.doubleValue()) < 0)
						newElem = (T) new Double(minM.doubleValue());
					else if(Double.compare(candidate, maxM.doubleValue()) > 0)
						newElem = (T) new Double(maxM.doubleValue());
					else
						newElem = (T) new Double(candidate);
					}
				else {
					
					do {
						candidate = minM.doubleValue() + (maxM.doubleValue() - minM.doubleValue()) *  rand.nextDouble();
					}while(Math.abs(currentElement - candidate) <= minDeviation);
					
					candidate = Math.round(candidate);
					
					if(Double.compare(candidate, minM.doubleValue()) < 0)
						newElem = (T) new Integer(minM.intValue());
					else if(Double.compare(candidate, maxM.doubleValue()) > 0)
						newElem = (T) new Integer(maxM.intValue());
					else
						newElem = (T) new Integer((int)candidate);
				}
				
				setMatrixItem(ctx, row, col, newElem);
				
			}
		}

	}

	private boolean respectsOverlapConstraint(String elem, String type, double percTricluster) {

		boolean respects = true;

		List<Integer> trics = this.getTricsByElem(elem);

		for(int i = 0; i < trics.size() && respects; i++) {
			NumericTricluster<? extends Number> t = this.getTricluster(trics.get(i));
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

}
