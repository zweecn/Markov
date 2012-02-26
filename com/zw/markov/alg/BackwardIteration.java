package com.zw.markov.alg;

import java.util.Arrays;
import java.util.List;

import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;

public class BackwardIteration {
	List<MarkovRecord> records;
	
	public BackwardIteration (List<MarkovRecord> records) {
		this.records = records;
	}
	
	public List<MarkovAction> getBestChose() {
		double[] u = new double[records.size()];
		Arrays.fill(u, mergeTimeAndCost(records.get(records.size()-1).getTimeCost(), 
				records.get(records.size()-1).getPriceCost()));
		
		for (int t = u.length; t > u.length; t--) {
			for (MarkovRecord rd : records) {
				
			}
		}
		
		return null;
	}
	
	public double mergeTimeAndCost(double time, double cost) {
		return time * cost / 1000;
	}
}
