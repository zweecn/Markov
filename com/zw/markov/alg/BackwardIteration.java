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
	Map<MarkovAction, Integer> actiomToIntMap;
	Map<Double, Integer> timeToIntMap;
	Map<MarkovState, Set<MarkovAction>> stateToActionSetMap;
	Map<Integer, Set<MarkovState>> timeintToStateSetMap;

	double[] u;
	double[][] rt;
	double[] rn;
	int n;
	
	public BackwardIteration (List<MarkovRecord> records) {
		this.records = records;
		init();
	}
	
	public List<MarkovAction> getBestChose() {
		System.out.println("\nBegin markov:\n");
		
		step1();
		step2();
		step3();
		
		System.out.println("\nAt last, u[0]=" + u[0]);
		
		return null;
	}
	
	private void step1() {
		for (MarkovState s : states) {
			if (stateToActionSetMap.get(s) == null) {
				u[(int) s.getId()] = rn[(int) s.getId()];
			}
		}
	}
	
	private void step2() {

	}
	
	private void step3() {
		for (int t = n - 1; t >= 0; t--) {
			for (MarkovState it : timeintToStateSetMap.get(t)) {
				u[(int) it.getId()] = minU(it);
				//u[(int) it.getId()] = maxU(it);
			}
		}
	}
	
	private double maxU(MarkovState it) {
		double temp = 0, res = - Double.MAX_VALUE;
		MarkovAction resAction = null;
		if (stateToActionSetMap.get(it) == null) {
			return u[(int) it.getId()];
		}
		for (MarkovAction ac : stateToActionSetMap.get(it)) {
			temp = rt[(int)it.getId()][actiomToIntMap.get(ac)];
			for (MarkovRecord rd : records) {
				if (rd.getStateBefore().equals(it)) {
					temp += rd.getPosibility() * u[(int)rd.getStateAfter().getId()];
				}
			}
			if (res < temp) {
				res = temp;
				resAction = ac;
			}
			
		}
		System.out.println("At state:" + String.format("%2d", it.getId()) + " do " 
				+ resAction + ", u[" + String.format("%2d", it.getId()) + "]=" + res);
		return res;
	}
	
	private double minU(MarkovState it) {
		double temp = 0, res = Double.MAX_VALUE;
		MarkovAction resAction = null;
		if (stateToActionSetMap.get(it) == null) {
			return u[(int) it.getId()];
		}
		for (MarkovAction ac : stateToActionSetMap.get(it)) {
			temp = rt[(int)it.getId()][actiomToIntMap.get(ac)];
			for (MarkovRecord rd : records) {
				if (rd.getStateBefore().equals(it)) {
					temp += rd.getPosibility() * u[(int)rd.getStateAfter().getId()];
				}
			}
			if (res > temp) {
				res = temp;
				resAction = ac;
			}
			
		}
		System.out.println("At state:" + String.format("%2d", it.getId()) + " do " 
				+ resAction + ", u[" + String.format("%2d", it.getId()) + "]=" + res);
		return res;
	}
	
	
	
	private void init() {
		states = new HashSet<MarkovState>();
		actiomToIntMap = new HashMap<MarkovAction, Integer>();
		timeToIntMap = new HashMap<Double, Integer>();
		stateToActionSetMap = new HashMap<MarkovState, Set<MarkovAction>>();
		timeintToStateSetMap = new HashMap<Integer, Set<MarkovState>>();
		
		int tCount = 0, aCount = 0;
		for (int i = 0; i < records.size(); i++) {
			if (!states.contains(records.get(i).getStateBefore())) {
				states.add(records.get(i).getStateBefore());
			}
			
			if (!states.contains(records.get(i).getStateAfter())) {
				states.add(records.get(i).getStateAfter());
			} 
			
			if (stateToActionSetMap.get(records.get(i).getStateBefore()) == null) {
				stateToActionSetMap.put(records.get(i).getStateBefore(), new HashSet<MarkovAction>());
			}
			stateToActionSetMap.get(records.get(i).getStateBefore()).add(records.get(i).getAction());
			
			if (timeToIntMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost()) == null) {
				timeToIntMap.put(records.get(i).getStateBefore().getCurrTotalTimeCost(), tCount++);
			}
			if (timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()) == null) {
				timeToIntMap.put(records.get(i).getStateAfter().getCurrTotalTimeCost(), tCount++);
			}
			
			if (timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())) == null) {
				timeintToStateSetMap.put(timeToIntMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost()), new HashSet<MarkovState>());
			} 
			timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())).add(records.get(i).getStateBefore());
			if (timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())) == null) {
				timeintToStateSetMap.put(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()), new HashSet<MarkovState>());
			} 
			timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())).add(records.get(i).getStateAfter());
			
			
			if (actiomToIntMap.get(records.get(i).getAction()) == null) {
				actiomToIntMap.put(records.get(i).getAction(), aCount++);
			}
		}

		n = timeToIntMap.size();
 
		u = new double[states.size() + 1]; 
		rt = new double[states.size() + 1][actiomToIntMap.size() + 1];
		rn = new double[states.size() + 1];
		for (MarkovRecord rd : records) {
			int i = (int) rd.getStateBefore().getId();
			int a = actiomToIntMap.get(rd.getAction());
			rt[i][a] = this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
			if (stateToActionSetMap.get(rd.getStateAfter()) == null) {
				rn[(int)rd.getStateAfter().getId()] = this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
			}
		} 
		
		
//		Iterator<Entry<MarkovState, Set<MarkovAction>>> it = statesActionMap.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry<MarkovState, Set<MarkovAction>> entry = it.next();
//			MarkovState state = entry.getKey();
//			Set<MarkovAction> value = entry.getValue();
//			System.out.println(state + "\n" + value + "\n");
//		}
//		
//		System.out.println(statesActionMap);
		
//		Iterator<Entry<Integer, Set<MarkovState>>> it = timeintToStateSetMap.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry<Integer, Set<MarkovState>> entry = it.next();
//			int key = entry.getKey();
//			Set<MarkovState> value = entry.getValue();
//			System.out.println(key + ":");
//			for (MarkovState v : value) {
//				System.out.println(v.getId());
//			}
//			System.out.println();
//		}
//		
//		System.out.println(timeToIntMap.size());
	}
	
	public double mergeTimeAndCost(double time, double cost) {
		return  - cost * time / 1000;
	}
}
