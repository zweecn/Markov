package com.zw.markov.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class BackwardIteration {
	List<MarkovRecord> records;
	Set<MarkovState> states;
//	Set<MarkovAction> actions;
//	Set<Double> times;
	Map<MarkovAction, Integer> actiomToIntMap;
	Map<Double, Integer> timeToIntMap;
	Map<MarkovState, Set<MarkovAction>> stateToActionSetMap;
	Map<Integer, Set<MarkovState>> timeintToStateSetMap;
	MarkovAction[][] a;
	double[][] u;
	double[][][] r;
	int n;
	
	public BackwardIteration (List<MarkovRecord> records) {
		this.records = records;
		init();
	}
	
	public List<MarkovAction> getBestChose() {
		//int timeSize = (int) records.get(records.size()-1).getStateAfter().getCurrTotalTimeCost();
		
		for (int t = n - 1; t >= 0; t--) {
//			for (MarkovState it : states) {
//				if ((int) it.getCurrTotalTimeCost() == t) {
//					u[timeMap.get(it.getCurrTotalTimeCost())][(int) it.getId()] = maxU(it);
//				}
//			}
			//System.out.println("t=" + t +  " s=" + timeintToStateSetMap.get(t));
			for (MarkovState it : timeintToStateSetMap.get(t)) {
				u[timeToIntMap.get(it.getCurrTotalTimeCost())][(int) it.getId()] = maxU(it);
			}
		}
		System.out.println("maxu:" + u[0][0]);
		
		return null;
	}
	
	private double maxU(MarkovState it) {
		double temp = 0, res = - Double.MAX_VALUE;
		MarkovAction resAction = null;
		//System.out.println("it.id=" + it.getId() + " actions="+stateToActionSetMap.get(it));
		if (stateToActionSetMap.get(it) == null) {
			System.out.println(it);
			return 0;
		} 
		for (MarkovAction ac : stateToActionSetMap.get(it)) {
			temp = r[timeToIntMap.get( it.getCurrTotalTimeCost())][(int)it.getId()][actiomToIntMap.get(ac)];
			for (MarkovRecord rd : records) {
				if (rd.getStateBefore().equals(it)) {
					temp += rd.getPosibility() 
							* u[timeToIntMap.get(rd.getStateAfter().getCurrTotalTimeCost())]
									[(int)rd.getStateAfter().getId()];
				}
			}
			//System.out.println("temp=" + temp + " res=" + res);
			if (res < temp) {
				res = temp;
				resAction = ac;
				//System.out.println("IN IF");
			}
			
		}
		//System.out.println(it + " " + resAction + " res=" + res);
		return res;
	}
	
	private void step1() {
		for (MarkovState s : states) {
			if (stateToActionSetMap.get(s) == null) {
				u[timeToIntMap.get(s.getCurrTotalTimeCost())][(int) s.getId()] = 0; 
						//= r[timeToIntMap.get(s.getCurrTotalTimeCost())][(int) s.getId()] [0]; //ERROR
			}
		}
	}
	
	
	private void init() {
		states = new HashSet<MarkovState>();
//		actions = new HashSet<MarkovAction>();
//		times = new HashSet<Double>();
		actiomToIntMap = new HashMap<MarkovAction, Integer>();
		timeToIntMap = new HashMap<Double, Integer>();
		stateToActionSetMap = new HashMap<MarkovState, Set<MarkovAction>>();
		timeintToStateSetMap = new HashMap<Integer, Set<MarkovState>>();
		
		int tCount = 0, aCount = 0;
		for (int i = 0; i < records.size(); i++) {
			if (!states.contains(records.get(i).getStateBefore())) {
				states.add(records.get(i).getStateBefore());
//				times.add(records.get(i).getStateBefore().getCurrTotalTimeCost());
			}
			
			if (!states.contains(records.get(i).getStateAfter())) {
				states.add(records.get(i).getStateAfter());
//				times.add(records.get(i).getStateAfter().getCurrTotalTimeCost());
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
				//timeStateMap.get(timeMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())).add(records.get(i).getStateBefore());
			} 
			timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())).add(records.get(i).getStateBefore());
			if (timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())) == null) {
				timeintToStateSetMap.put(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()), new HashSet<MarkovState>());
			//	timeStateMap.get(timeMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())).add(records.get(i).getStateAfter());
			} 
			timeintToStateSetMap.get(timeToIntMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())).add(records.get(i).getStateAfter());
			
			
			if (actiomToIntMap.get(records.get(i).getAction()) == null) {
				actiomToIntMap.put(records.get(i).getAction(), aCount++);
			}
		}

//		System.out.println("timeMap.size=" + timeMap.size());
//		System.out.println("state.size=" + states.size());
//		System.out.println("actionMap.size=" + actionMap.size());
		
		n = timeToIntMap.size();
		//u = new double[n + 1][(int) records.get(records.size()-1).getStateAfter().getId() + 1]; 
		u = new double[n + 1][states.size() + 1]; 
		a = new MarkovAction[n + 1][states.size() + 1];
		r = new double[n + 1][states.size() + 1][actiomToIntMap.size() + 1];
		for (MarkovRecord rd : records) {
			int t = timeToIntMap.get(rd.getStateBefore().getCurrTotalTimeCost());
			int it = (int) rd.getStateBefore().getId();
			int a = actiomToIntMap.get(rd.getAction());
			r[t][it][a] = this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
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
		return - cost * time / 1000;
	}
}
