package com.zw.markov.alg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class BackwardIteration {
	List<MarkovRecord> records;
	Set<MarkovState> states;
//	Set<MarkovAction> actions;
	Set<Double> times;
	Map<MarkovAction, Integer> actionMap;
	Map<Double, Integer> timeMap;
	Map<MarkovState, List<MarkovAction>> statesActionMap;
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
				if ((int) it.getCurrTotalTimeCost() == t) {
					u[t][(int) it.getId()] = maxU(it);
				}
			}
		}
		System.out.println("maxu:" + u[0][0]);
		
		return null;
	}
	
	private double maxU(MarkovState it) {
		double temp = 0, res = 0;
		MarkovAction resAction = null;
		for (MarkovAction ac : actions) {
			temp = r[(int) it.getCurrTotalTimeCost()][(int)it.getId()][ac.getId()];
			for (MarkovRecord rd : records) {
				if (rd.getStateBefore().equals(it)) {
					temp += rd.getPosibility() 
							* u[(int)rd.getStateAfter().getCurrTotalTimeCost()][(int)rd.getStateAfter().getId()];
				}
			}
			if (res < temp) {
				res = temp;
				resAction = ac;
			}
			
		}
//		System.out.println("State:" + it);
//		System.out.println("Action:" + resAction);
//		System.out.println();
		return res;
	}
	
	
	private void init() {
		states = new HashSet<MarkovState>();
//		actions = new HashSet<MarkovAction>();
		times = new HashSet<Double>();
		actionMap = new HashMap<MarkovAction, Integer>();
		timeMap = new HashMap<Double, Integer>();
		
		int tCount = 0, aCount = 0;
		for (int i = 0; i < records.size(); i++) {
			if (!states.contains(records.get(i).getStateBefore())) {
				states.add(records.get(i).getStateBefore());
				times.add(records.get(i).getStateBefore().getCurrTotalTimeCost());
//				if (n < records.get(i).getStateBefore().getCurrTotalTimeCost()) {
//					n = (int) records.get(i).getStateBefore().getCurrTotalTimeCost();
//				}
				//timeMap.put(records.get(i).getStateBefore().getCurrTotalTimeCost(), tCount++);
			}
			
			if (!states.contains(records.get(i).getStateAfter())) {
				states.add(records.get(i).getStateAfter());
				times.add(records.get(i).getStateAfter().getCurrTotalTimeCost());
//				if (n < records.get(i).getStateAfter().getCurrTotalTimeCost()) {
//					n = (int) records.get(i).getStateAfter().getCurrTotalTimeCost();
//				}
				//timeMap.put(records.get(i).getStateAfter().getCurrTotalTimeCost(), tCount++);
			} 
			
			if (timeMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost()) == null) {
				timeMap.put(records.get(i).getStateBefore().getCurrTotalTimeCost(), tCount++);
			}
			
			if (timeMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()) == null) {
				timeMap.put(records.get(i).getStateAfter().getCurrTotalTimeCost(), tCount++);
			}
			
			
//			if (!actions.contains(records.get(i).getAction())) {
//				actions.add(records.get(i).getAction());
//				actionMap.put(records.get(i).getAction(), aCount++);
//			}
			if (actionMap.get(records.get(i).getAction()) == null) {
				actionMap.put(records.get(i).getAction(), aCount++);
			}
		}
//		int aCount = 0;
//		for (MarkovAction ac : actions) {
//			actionMap.put(ac, aCount++);
//		}
		
		System.out.println("timeMap.size=" + timeMap.size());
		System.out.println("state.size=" + states.size());
		System.out.println("actionMap.size=" + actionMap.size());
		
		n = timeMap.size();
		//u = new double[n + 1][(int) records.get(records.size()-1).getStateAfter().getId() + 1]; 
		u = new double[n + 1][states.size() + 1]; 
		a = new MarkovAction[n + 1][states.size() + 1];
		r = new double[n + 1][states.size() + 1][actionMap.size() + 1];
//		r = new double[n + 1][states.size() + 1][records.get(records.size()-1).getAction().getId() + 1];
//		System.out.println("u.length " + u.length + " u[0].length " + u[0].length);
//		System.out.println("a.length " + a.length + " a[0].length " + a[0].length);
//		System.out.println("r.length " + r.length + " r[0].length " + r[0].length + " r[0][0].length " + r[0][0].length);
		for (MarkovRecord rd : records) {
//			r[(int) rd.getStateBefore().getCurrTotalTimeCost()][(int) rd.getStateBefore().getId()][rd.getAction().getId()]
//				= this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
			System.out.println();
			int t = timeMap.get(rd.getStateBefore().getCurrTotalTimeCost());
			int it = (int) rd.getStateBefore().getId();
			int a = actionMap.get(rd.getAction());
			System.out.println(t);
			System.out.println(it);
			System.out.println(a);
			r[t][it][a] = this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
		}
		
	}
	
	public double mergeTimeAndCost(double time, double cost) {
		return - cost * time / 1000;
	}
}
