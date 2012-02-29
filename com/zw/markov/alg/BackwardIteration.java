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
		
		for (int t = (int) timeSize; t >= 0; t--) {
			for (MarkovState it : states) {
				u[(int)it.getCurrTotalTimeCost()][(int) it.getId()] = maxU(it);
			}
		}
		System.out.println("maxu:" + u[0][0]);
		
		return null;
	}
	
	private double maxU(MarkovState it) {
		double temp = 0, res = 0;
		for (MarkovAction ac : actions) {
			temp += r[(int) it.getCurrTotalTimeCost()][(int)it.getId()][ac.getId()];
			for (MarkovRecord rd : records) {
				temp += rd.getPosibility() * u[(int) it.getCurrTotalTimeCost() + 1][(int)rd.getStateAfter().getId()];
			}
			if (res < temp) {
				res = temp;
			}
		}
		
		return res;
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
		u = new double[n + 2][(int) records.get(records.size()-1).getStateAfter().getId() + 1]; 
		a = new MarkovAction[n + 1][(int) records.get(records.size()-1).getStateAfter().getId() + 1];
		r = new double[n + 1][(int) records.get(records.size()-1).getStateAfter().getId() + 1][records.get(records.size()-1).getAction().getId() + 1];
		System.out.println("u.length " + u.length + " u[0].length " + u[0].length);
		System.out.println("a.length " + a.length + " a[0].length " + a[0].length);
		System.out.println("r.length " + r.length + " r[0].length " + r[0].length + " r[0][0].length " + r[0][0].length);
		for (MarkovRecord rd : records) {
			r[(int) rd.getStateBefore().getCurrTotalTimeCost()][(int) rd.getStateBefore().getId()][rd.getAction().getId()]
				= this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
		}
		
	}
	
	public double mergeTimeAndCost(double time, double cost) {
		return cost * time / 1000;
	}
}
