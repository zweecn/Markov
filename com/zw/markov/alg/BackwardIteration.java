package com.zw.markov.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class BackwardIteration {
	List<MarkovRecord> records;
	Set<MarkovState> states;
	Set<MarkovAction> actions;
	Set<Double> times;
	
	MarkovAction[][] a;
	double[][] u;
	double[][][] r;
	int n;
	
	public BackwardIteration (List<MarkovRecord> records) {
		this.records = records;
		init();
	}
	
	public List<MarkovAction> getBestChose() {
		int timeSize = (int) records.get(records.size()-1).getStateAfter().getCurrTotalTimeCost();
		int stateSize = (int) states.size();
		
		
		for (int t = (int) timeSize; t >= 0; t--) {
			for (MarkovState it : states) {
				if (((int) it.getCurrTotalTimeCost()) == t) {
					//u[(int) it.getId()] = maxU(it);
				}
			}
		}
		
		
		return null;
	}
	
	private double maxU(MarkovState it) {
		double temp = 0;
		for (MarkovAction ac : actions) {
			
		}
		
		return 0;
	}
	
	
	private void init() {
		states = new HashSet<MarkovState>();
		actions = new HashSet<MarkovAction>();
		times = new HashSet<Double>();
		for (int i = 0; i < records.size(); i++) {
			if (!states.contains(records.get(i).getStateBefore())) {
				states.add(records.get(i).getStateBefore());
				times.add(records.get(i).getStateBefore().getCurrTotalTimeCost());
				if (n < records.get(i).getStateBefore().getCurrTotalTimeCost()) {
					n = (int) records.get(i).getStateBefore().getCurrTotalTimeCost();
				}
			}
			if (!states.contains(records.get(i).getStateAfter())) {
				states.add(records.get(i).getStateAfter());
				times.add(records.get(i).getStateAfter().getCurrTotalTimeCost());
				if (n < records.get(i).getStateAfter().getCurrTotalTimeCost()) {
					n = (int) records.get(i).getStateAfter().getCurrTotalTimeCost();
				}
			}
			if (!actions.contains(records.get(i).getAction())) {
				actions.add(records.get(i).getAction());
			}
		}
		u = new double[n][states.size()]; 
		a = new MarkovAction[n][states.size()];
		r = new double[n][states.size()][actions.size()];
		for (MarkovRecord rd : records) {
			u[(int) rd.getStateBefore().getCurrTotalTimeCost()][(int) rd.getStateBefore().getId()] 
				= this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
		}
		
	}
	
	public double mergeTimeAndCost(double time, double cost) {
		return time * cost / 1000;
	}
}
