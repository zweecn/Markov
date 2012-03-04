package com.zw.markov.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import com.zw.markov.Markov;
import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class FrontToEnd {
	List<MarkovRecord> records;
	Set<MarkovState> states;
	
	Map<MarkovAction, Integer> actionIDMap;
	Map<Double, Integer> timeIDMap;
	Map<MarkovState, Integer> stateIDMap;
	
	Map<MarkovState, Set<MarkovAction>> state2actionSetMap;
	Map<Integer, Set<MarkovState>> time2stateSetMap;
	Map<MarkovState, Set<MarkovState>> state2stateSetMap;
	Map<stateAndAction, Set<MarkovState>> stateAndAction2StateSetMap;
	
	double[] u;
	double[][] rt;
	double[] rn;
	//int n;
	
	private class stateAndAction {
		private MarkovState state;
		private MarkovAction action;
		public stateAndAction(MarkovState state, MarkovAction action) {
			this.state = state;
			this.action = action;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof stateAndAction)) {
				return false;
			}
			stateAndAction other = (stateAndAction) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (action == null) {
				if (other.action != null) {
					return false;
				}
			} else if (!action.equals(other.action)) {
				return false;
			}
			if (state == null) {
				if (other.state != null) {
					return false;
				}
			} else if (!state.equals(other.state)) {
				return false;
			}
			return true;
		}
		private FrontToEnd getOuterType() {
			return FrontToEnd.this;
		}
		
	}
	
	public FrontToEnd (List<MarkovRecord> records) {
		this.records = records;
		init();
	}
	
	public List<MarkovAction> getBestChose() {
		System.out.println("\nBegin markov:\n");
		if (records != null && !records.isEmpty()) {
			//double maxu = func(records.get(0).getStateBefore());
			double maxu = mfun(records.get(0).getStateBefore());
			System.out.println("At last, the maxu=" + maxu);
		}
		
		//System.out.println("\nAt last, u[0]=" + u[0]);
		
		return null;
	}
	//static int count = 0;
	static double temp;
	private double func(MarkovState it) {
		//System.out.println("in count=" + (count++));
		if (state2actionSetMap.get(it) == null) {
			//System.out.println("sid=" + it.getId() + " rn[stateIDMap.get(it)]=" + rn[stateIDMap.get(it)]);
			return rn[stateIDMap.get(it)];
		}
		double res = - Markov.MIN_VALUE;
		MarkovAction action = null;
		//System.out.println("state2actionSetMap.get(it)=" + state2actionSetMap.get(it));
		for (MarkovAction a : state2actionSetMap.get(it)) {
			temp = rt[stateIDMap.get(it)][actionIDMap.get(a)];
			System.out.println("begin, temp=" + temp);
			for (MarkovRecord rd : records) {
				if (rd.getStateBefore().equals(it) && rd.getAction().equals(a) 
						&& !rd.getStateBefore().equals(rd.getStateAfter())) {
					//System.out.println("IN FUNC, it=" + it.getId() + "  after=" + rd.getStateAfter().getId());
					temp += Markov.WEAKEN * rd.getPosibility() * func(rd.getStateAfter());
					System.out.println("OUT, temp=" + temp);
				}
			}
			System.out.println("end, res=" + res + " temp=" + temp);
			if (res > temp) {
				res = temp;
				action = a;
				System.out.println("IN IF, res=" + res);
			}
		}
		//System.out.println("res=" + res + "\n");
		System.out.println("Action:" + action + " " + " res=" + res);
		System.out.println();
		return res;
	}
	
	public double mfun(MarkovState s) {
		int n = 0;
		for (int i = 0; i < states.size(); i++) {
			u[i] = rn[i];
			//n = (n < stateIDMap.get(i) ? stateIDMap.get(i) : n);
			System.out.println("u[i]=" + u[i]);
			
		}
		n = states.size() - 1;
		for (int t = n; t >=0 ; t--) {
			u[t] = max(t);
		}
		
		return u[0];
	}
	
	private double max(int t) {
		Set<MarkovRecord> recordsTemp = new HashSet<MarkovRecord>();
		Set<MarkovAction> actionsTemp = new HashSet<MarkovAction>();
		for (MarkovRecord rd : records) {
			if (stateIDMap.get(rd.getStateBefore()) == t) {
				recordsTemp.add(rd);
				actionsTemp.add(rd.getAction());
			}
		}
		
		double res = Markov.MIN_VALUE;
		MarkovAction ac = null;
		int test = 0;
		for (MarkovAction a : actionsTemp) {
			double temp = rt[t][actionIDMap.get(a)];
			for (MarkovRecord rd : recordsTemp) {
				if (stateIDMap.get(rd.getStateBefore()) == t && rd.getAction().equals(a)) {
					temp += u[stateIDMap.get(rd.getStateAfter())] * rd.getPosibility();
					test++;
				}
			}
			if (res < temp) {
				res = temp;
				ac = a;
			}
		}
		System.out.println("test="+test);
		System.out.println("sid=" + t + " do " + ac + " res=" + res);
		if (actionsTemp.isEmpty()) {
			System.out.println("EMPTY");
		}
		return  res;
	}
 	
	private void init() {
		states = new HashSet<MarkovState>();
		actionIDMap = new HashMap<MarkovAction, Integer>();
		timeIDMap = new HashMap<Double, Integer>();
		state2actionSetMap = new HashMap<MarkovState, Set<MarkovAction>>();
		time2stateSetMap = new HashMap<Integer, Set<MarkovState>>();
		stateIDMap = new HashMap<MarkovState, Integer>();
		state2stateSetMap = new HashMap<MarkovState, Set<MarkovState>>();
		stateAndAction2StateSetMap = new HashMap<FrontToEnd.stateAndAction, Set<MarkovState>>();
		
		int tCount = 0, aCount = 0, sCount = 0;
		//System.out.println("records=" + records);
		
		for (int i = 0; i < records.size(); i++) {
			if (!states.contains(records.get(i).getStateBefore())) {
				states.add(records.get(i).getStateBefore());
			}
			
			if (!states.contains(records.get(i).getStateAfter())) {
				states.add(records.get(i).getStateAfter());
			} 
			if (state2stateSetMap.get(records.get(i).getStateBefore()) == null) {
				state2stateSetMap.put(records.get(i).getStateBefore(), new HashSet<MarkovState>());
			}
			//System.out.println("state2stateSetMap.get(records.get(i))="+state2stateSetMap.get(records.get(i)));
			state2stateSetMap.get(records.get(i).getStateBefore()).add(records.get(i).getStateAfter());
			
			
			stateAndAction sa = new stateAndAction(records.get(i).getStateBefore(), records.get(i).getAction());
			if (stateAndAction2StateSetMap.get(sa) == null) {
				stateAndAction2StateSetMap.put(sa, new HashSet<MarkovState>());
			}
			stateAndAction2StateSetMap.get(sa).add(records.get(i).getStateAfter());
			
			if (stateIDMap.get(records.get(i).getStateBefore()) == null) {
				stateIDMap.put(records.get(i).getStateBefore(), sCount++);
			}
			if (stateIDMap.get(records.get(i).getStateAfter()) == null) {
				stateIDMap.put(records.get(i).getStateAfter(), sCount++);
			}
			
			if (state2actionSetMap.get(records.get(i).getStateBefore()) == null) {
				state2actionSetMap.put(records.get(i).getStateBefore(), new HashSet<MarkovAction>());
			}
			state2actionSetMap.get(records.get(i).getStateBefore()).add(records.get(i).getAction());
			
			if (timeIDMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost()) == null) {
				timeIDMap.put(records.get(i).getStateBefore().getCurrTotalTimeCost(), tCount++);
			}
			if (timeIDMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()) == null) {
				timeIDMap.put(records.get(i).getStateAfter().getCurrTotalTimeCost(), tCount++);
			}
			
			if (time2stateSetMap.get(timeIDMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())) == null) {
				time2stateSetMap.put(timeIDMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost()), new HashSet<MarkovState>());
			} 
			time2stateSetMap.get(timeIDMap.get(records.get(i).getStateBefore().getCurrTotalTimeCost())).add(records.get(i).getStateBefore());
			if (time2stateSetMap.get(timeIDMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())) == null) {
				time2stateSetMap.put(timeIDMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost()), new HashSet<MarkovState>());
			} 
			time2stateSetMap.get(timeIDMap.get(records.get(i).getStateAfter().getCurrTotalTimeCost())).add(records.get(i).getStateAfter());
			
			
			if (actionIDMap.get(records.get(i).getAction()) == null) {
				actionIDMap.put(records.get(i).getAction(), aCount++);
			}
		}

		//n = timeIDMap.size();
		//System.out.println(states.size()  + " scount=" + sCount);
		u = new double[sCount]; 
		rt = new double[sCount][aCount];
		rn = new double[sCount];
		
		for (MarkovRecord rd : records) {
			int i = stateIDMap.get(rd.getStateBefore());
			int a = actionIDMap.get(rd.getAction());
			rt[i][a] = this.mergeTimeAndCost(rd.getTimeCost(), rd.getPriceCost());
		}
		for (MarkovState s : states) {
			rn[stateIDMap.get(s)] = this.finishStateReward(s);
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
	
//	public double rewardOfSucceed(double time) {
//		return 1000 - time;
//	}
	
	public double finishStateReward(MarkovState state) {
		switch (state.getCurrGlobalState()) {
		case Markov.S_SUCCEED:
			return ( 1000 - state.getCurrTotalTimeCost() + 10);
		case Markov.S_DELAYED:
			return (- state.getCurrTotalTimeCost() / 100 + 10);
		case Markov.S_PRICE_UP:
			return (- state.getCurrTotalTimeCost() / 1000 + 10);
		
		case Markov.S_FAILED:
			return (- state.getCurrTotalTimeCost() / 10 - 1000);
		case Markov.S_UNKNOWN:
			return (- state.getCurrTotalTimeCost() / 10 - 1000);
		default:
			return 0;
		}
	}
}
