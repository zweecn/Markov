package com.zw.markov.alg;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.zw.Configs;
import com.zw.markov.Markov;
import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class LayerMarkovBackward {
	public LayerMarkovBackward(MarkovState state) {
		this.state = state;
		init();
	}
	
	private MarkovState state;
	private Queue<MarkovState> queue1;
	private Queue<MarkovState> queue2;
	private List<List<MarkovRecord>> allLayerRecords;
	private Map<Integer, Set<MarkovState>> t2StateMap;
	private Map<TAndState, List<MarkovAction>> tState2ChildActionMap;
	private Map<StateTAndAction, List<ToStateInfo>> stateTAction2ChildStateInfoMap;
	
	private double[][] utility;
	private String[] step;
	private double actionCost;
	private MarkovAction firstAction;
	private MarkovState stateNew; 
	
	private long generateRecordRunTime;
	private long initMarkovInfoRunTime;
	private long runMarkovRunTime;
	
	private class TAndState {
		public TAndState(int t, MarkovState state) {
			super();
			this.t = t;
			this.state = state;
		}
		private int t;
		private MarkovState state;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			result = prime * result + t;
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
			if (!(obj instanceof TAndState)) {
				return false;
			}
			TAndState other = (TAndState) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (state == null) {
				if (other.state != null) {
					return false;
				}
			} else if (!state.equals(other.state)) {
				return false;
			}
			if (t != other.t) {
				return false;
			}
			return true;
		}
		private LayerMarkovBackward getOuterType() {
			return LayerMarkovBackward.this;
		}
		public int getT() {
			return t;
		}
		public MarkovState getState() {
			return state;
		}
		
	}
	
	private class ToStateInfo {
		public ToStateInfo(MarkovState state, double posibility, double price,
				double time) {
			super();
			this.state = state;
			this.posibility = posibility;
			this.price = price;
			this.time = time;
		}
		MarkovState state;
		double posibility;
		double price;
		double time;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			long temp;
			temp = Double.doubleToLongBits(posibility);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(price);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			temp = Double.doubleToLongBits(time);
			result = prime * result + (int) (temp ^ (temp >>> 32));
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
			if (!(obj instanceof ToStateInfo)) {
				return false;
			}
			ToStateInfo other = (ToStateInfo) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (Double.doubleToLongBits(posibility) != Double
					.doubleToLongBits(other.posibility)) {
				return false;
			}
			if (Double.doubleToLongBits(price) != Double
					.doubleToLongBits(other.price)) {
				return false;
			}
			if (state == null) {
				if (other.state != null) {
					return false;
				}
			} else if (!state.equals(other.state)) {
				return false;
			}
			if (Double.doubleToLongBits(time) != Double
					.doubleToLongBits(other.time)) {
				return false;
			}
			return true;
		}
		private LayerMarkovBackward getOuterType() {
			return LayerMarkovBackward.this;
		}
		public MarkovState getState() {
			return state;
		}
		public double getPosibility() {
			return posibility;
		}
		public double getPrice() {
			return price;
		}
		public double getTime() {
			return time;
		}
	}
	
	private class StateTAndAction {
		public StateTAndAction(MarkovState state, int t, MarkovAction action) {
			super();
			this.state = state;
			this.t = t;
			this.action = action;
		}
		MarkovState state;
		int t;
		MarkovAction action;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			result = prime * result + t;
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
			if (!(obj instanceof StateTAndAction)) {
				return false;
			}
			StateTAndAction other = (StateTAndAction) obj;
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
			if (t != other.t) {
				return false;
			}
			return true;
		}
		private LayerMarkovBackward getOuterType() {
			return LayerMarkovBackward.this;
		}
		public MarkovState getState() {
			return state;
		}
		public int getT() {
			return t;
		}
		public MarkovAction getAction() {
			return action;
		}
		
	}
	
 	public void printRecords() {
		//System.out.println("records size=" + allLayerRecords.size());
		System.out.println("\n StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		for (int i = 0; i < allLayerRecords.size(); i++) {
			System.out.println("Layer " + i + " >>>>>>>");
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toString());
			}
		}
		System.out.println();
	}
	

	public void printSimpleRecords() {
		System.out.println("\n StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		for (int i = 0; i < allLayerRecords.size(); i++) {
			System.out.println("Layer " + i + " >>>>>>>");
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toSimpleString());
			}
		}
		System.out.println();
	}
	

	public void writeRecords() {
		System.out.println("records size=" + allLayerRecords.size());
		try {
		FileWriter writer = new FileWriter(Configs.LOG_FILE_NAME);
		for (int i = 0; i < allLayerRecords.size(); i++) {
			writer.append(i + "\n");
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toString());
				writer.append(rd.toString() + "\n");
			}
		}
		writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void printMap() {
		System.out.println("\nstateTAction2ChildStateInfoMap:");
		Iterator it = stateTAction2ChildStateInfoMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry =  (Entry) it.next();
			StateTAndAction sta = (StateTAndAction) entry.getKey();
			List<ToStateInfo> value = (List<LayerMarkovBackward.ToStateInfo>) entry.getValue();
			System.out.println( "t=" + sta.getT() + " StateBefore=" + sta.getState().getId() + " Action=" + sta.getAction().getId()  + "  StateAfter=" + value.get(0).getState().getId());
		}
		
		System.out.println("\nt2StateMap:");
		it = t2StateMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Set<MarkovState>> entry =  (Entry<Integer, Set<MarkovState>>) it.next();
			int key = entry.getKey();
			Set<MarkovState> value = entry.getValue();
			for (MarkovState s : value) {
				System.out.println("At t=" + key + " State=" + s.getId());
			}
		}
		
		
		System.out.println("\ntState2ChildActionMap:");
		it = tState2ChildActionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<TAndState, List<MarkovAction>> entry =  (Entry<TAndState, List<MarkovAction>>) it.next();
			TAndState key = entry.getKey();
			List<MarkovAction> value = entry.getValue();
			for (MarkovAction a : value) {
				System.out.println("At t=" + key.getT() + " StateBefore=" + key.getState().getId() + " Action=" + a.getId());
			}
		}
	}
	
	public void printStep() {
		System.out.println("\nThe steps are:");
		for (String s : step) {
			System.out.println(s);
		}
		System.out.println();
		System.out.println("GenerateRecordRunTime: " + generateRecordRunTime + " ms.");
		System.out.println("InitMarkovInfoRunTime: " + initMarkovInfoRunTime + " ms.");
		System.out.println("RunMarkovProcsRunTime: " + runMarkovRunTime  + " ms.");
		System.out.println();
	}
	
	public void printUtility() {
		System.out.println("The utility is:");
		for (int i = 0; i < utility.length; i++) {
			for (int j = 0; j < utility[i].length; j++) {
				if (utility[i][j] <= -Double.MAX_VALUE) {
					System.out.printf("%s ", "MIN_VALUE");
				} else {
					System.out.printf("%6.1f ", utility[i][j]);
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private void init() {
		long t1 = System.currentTimeMillis();
		generateLayerRecords();
		reduceLayer(Configs.REDUCE_LAYER_SIZE);
		extendTree(Configs.IS_EXTEND_TREE);
		initMap();
		long t2 = System.currentTimeMillis();
		initMarkovInfo();
		long t3 = System.currentTimeMillis();
		generateRecordRunTime = t2 - t1;
		initMarkovInfoRunTime = t3 - t2;
	}
	
	private void initMap() {

		for (int t = 0; t < allLayerRecords.size(); t++) {
			addToMap(t, allLayerRecords.get(t));
		}
		Set<MarkovState> tempSet = new HashSet<MarkovState>();
		tempSet.add((allLayerRecords.get(0).get(0).getStateBefore()));
		t2StateMap.put(0, tempSet);
	}
	
	private void generateLayerRecords() {
		allLayerRecords = new ArrayList<List<MarkovRecord>>();
		tState2ChildActionMap = new HashMap<LayerMarkovBackward.TAndState, List<MarkovAction>>();
		stateTAction2ChildStateInfoMap = new HashMap<LayerMarkovBackward.StateTAndAction, List<ToStateInfo>>();
		queue2 = new LinkedList<MarkovState>();
		queue2.offer(state);
		Set<MarkovState> stateSet = new HashSet<MarkovState>();
		t2StateMap = new HashMap<Integer, Set<MarkovState>>();
		for (;;) {
			Set<MarkovState> tempSet = new HashSet<MarkovState>();
			tempSet.addAll(queue2);
			queue1 = queue2;
			queue2 = new LinkedList<MarkovState>();
			List<MarkovRecord> oneLayerRecords = new ArrayList<MarkovRecord>();
			while (!queue1.isEmpty()) {
				state = queue1.poll();
				if (!stateSet.contains(state)) {
					stateSet.add(state);
					List<MarkovRecord> records = Markov.getRecords(state);
					addToRecords(oneLayerRecords, records);
				}
			}
			if (oneLayerRecords.isEmpty()) {
				break;
			}
			allLayerRecords.add(oneLayerRecords);
		}
		if (allLayerRecords.size() > MarkovRecord.getMaxLayerSize()) {
			MarkovRecord.setMaxLayerSize(allLayerRecords.size());
		}
	}
	
	private void extendTree(boolean isExtend) {
		if (!isExtend) {
			return;
		}
		for (int i = 0; i < allLayerRecords.size()-1; i++) {
			Set<Integer> frontLayerStateAfterSet = new HashSet<Integer>();
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				frontLayerStateAfterSet.add(rd.getStateAfter().getId());
			}
			Set<Integer> nextLayerStateBeforeSet = new HashSet<Integer>();
			for (MarkovRecord rd : allLayerRecords.get(i+1)) {
				nextLayerStateBeforeSet.add(rd.getStateBefore().getId());
			}
			frontLayerStateAfterSet.removeAll(nextLayerStateBeforeSet);
			if (!frontLayerStateAfterSet.isEmpty()) {
				for (MarkovRecord rd : allLayerRecords.get(i)) {
					if (frontLayerStateAfterSet.contains(rd.getStateBefore().getId())) {
						allLayerRecords.get(i+1).add(rd);
					}
				}
			}
		}
	}
	
	private void reduceLayer(int i) {
		if (i > 0 && i < allLayerRecords.size()) {
			while (allLayerRecords.size() > i) {
				allLayerRecords.remove(allLayerRecords.size()-1);
			}
		}
	}
	
	private void addToRecords(List<MarkovRecord>destRecords, List<MarkovRecord> sourceRecord){
		if (sourceRecord != null && !sourceRecord.isEmpty() && destRecords != null) {
			destRecords.addAll(sourceRecord);
			Set<MarkovAction> actionSetTemp = new HashSet<MarkovAction>();
			for (MarkovRecord rd : sourceRecord) {
				queue2.offer(rd.getStateAfter());
				actionSetTemp.add(rd.getAction());
			}
		}
	}

	private void addToMap(int t, List<MarkovRecord> records) {
		Set<MarkovState> tempSet = new HashSet<MarkovState>();
		for (MarkovRecord rd : records) {
			TAndState tsb = new TAndState(t, rd.getStateBefore());
			if (tState2ChildActionMap.get(tsb) == null) {
				tState2ChildActionMap.put(tsb, new ArrayList<MarkovAction>());
			}
			tState2ChildActionMap.get(tsb).add(rd.getAction());
			StateTAndAction sta = new StateTAndAction(rd.getStateBefore(), t, rd.getAction());
			if (stateTAction2ChildStateInfoMap.get(sta) == null) {
				stateTAction2ChildStateInfoMap.put(sta, new ArrayList<LayerMarkovBackward.ToStateInfo>());
			}
			ToStateInfo info = new ToStateInfo(rd.getStateAfter(), rd.getPosibility(), rd.getPriceCost(), rd.getTimeCost());
			stateTAction2ChildStateInfoMap.get(sta).add(info); //Mark
			
			tempSet.add(rd.getStateAfter());
			if (t2StateMap.get(t+1) == null) {
				t2StateMap.put(t+1, tempSet);
			} else {
				t2StateMap.get(t+1).addAll(tempSet);
			}
		}
	}
	
	
	private String makeStepString(int t, MarkovAction action, double u) {
		String res = "At t=" + String.format("%2d", t) + " Action=" + action + " utility=" + String.format("%.2f", u);
		return res;
	}
	
	private int getTsize() {
		return allLayerRecords.size() + 1;
	}
	
	private boolean hasChildren(int t, MarkovState state) {
		TAndState ts = new TAndState(t, state);
		return (tState2ChildActionMap.get(ts) != null);
	}
	
	
	/******************************************************************************************
	 * Below is the main Markov alg.
	 *****************************************************************************************/
	private void initMarkovInfo() {
		utility = new double[this.getTsize()][MarkovRecord.getStateSize()];
		step = new String[this.getTsize() - 1];
		for (int i = 0; i < this.getTsize(); i++) {
			for (int j = 0; j < MarkovRecord.getStateSize(); j++) {
				utility[i][j] = - Double.MAX_VALUE;
			}
		}		
		for (int t = getTsize()-1; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (!hasChildren(t, i)) {
					utility[t][i.getId()] = getNReward(t, i);
				} else {
					utility[t][i.getId()] = -Double.MAX_VALUE;
				}
			}
		}
		
	}
	
	public void runMarkov() {
		long t1 = System.currentTimeMillis();
		for (int t = getTsize()-2; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (hasChildren(t, i)) {
					utility[t][i.getId()] = maxUtility(t, i);
				}
			}
		}
		runMarkovRunTime = System.currentTimeMillis() - t1;
	}
	
	private double maxUtility(int t, MarkovState i) {
		TAndState ts = new TAndState(t, i);
		double u = - Double.MAX_VALUE;
		for (MarkovAction a : tState2ChildActionMap.get(ts)) {
			StateTAndAction sta = new StateTAndAction(i, t, a);
			double reward = this.getTReward(sta, stateTAction2ChildStateInfoMap.get(sta));
			for (ToStateInfo tsi : stateTAction2ChildStateInfoMap.get(sta)) {
				reward +=  Configs.WEAKEN * tsi.getPosibility() * utility[t+1][tsi.getState().getId()];
			}
			if (u < reward) {
				u = reward;
				step[t] = this.makeStepString(t, a, u);
				actionCost = stateTAction2ChildStateInfoMap.get(sta).get(0).getPrice();
				firstAction = a;
				stateNew = stateTAction2ChildStateInfoMap.get(sta).get(0).getState();
			}
		}
		return u;
	}
	
	
	/* 
	 * This is the reward of terminate state (Leaf of the UTG tree).
	 * */
	private double getNReward(int t, MarkovState state) {
		if (state.getGlobalState() == Markov.S_FAILED) {
			return (- Configs.PUNISHMENT_FAILED);
		}
		return 0;
	}
	
	/*
	 * This is the reward after do a action.
	 * */
	private double getTReward(StateTAndAction sta, List<ToStateInfo> tsi) {
		double res = 0;
		
		switch (Configs.PLAN_CHOUSE) {
		case Configs.PLAN_ONE:
			res = - (1-tsi.get(0).getPosibility()) * Configs.PUNISHMENT_FAILED
				  - tsi.get(0).getPrice() 
				  - tsi.get(0).getTime() * Configs.PUNISHMENT_PER_SECOND;
			break;
		case Configs.PLAN_TWO:
			res = tsi.get(0).getPosibility() * Configs.AWARD_SUCCEED
			  	  - tsi.get(0).getPrice() 
			  	  - tsi.get(0).getTime() * Configs.PUNISHMENT_PER_SECOND;
		case Configs.PLAN_THREE:
			res = - tsi.get(0).getPrice() 
				  - tsi.get(0).getTime() * Configs.PUNISHMENT_PER_SECOND;
			break;
		default:
			System.err.println("Configure error. Code 0x10");
			System.exit(-1);
			break;
		}
		
//		System.out.println("res=" + res);
		return  res; //- (1-tsi.get(0).getPosibility()) * Configs.FAILED_PUNISHMENT 
				
	}
	/******************************************************************************************
	 * End Markov.
	 *****************************************************************************************/
	
	public double getMarkovBestUtility() {
		return utility[0][0];
	}	
	
	public double getCurrActionCost() {
		return actionCost;
	}
	
	public MarkovAction getAction() {
		return firstAction;
	}
	
	public MarkovState getStateNew() {
		if (stateNew != null) {
			stateNew.init();
		}
		return stateNew;
	}
	
	private double getGreedyReward(MarkovRecord rd) {
		if (rd.getStateAfter().getGlobalState() == Markov.S_FAILED) {
			return (- Configs.PUNISHMENT_FAILED);
		}
		return -(rd.getPriceCost() + rd.getTimeCost() * Configs.PUNISHMENT_PER_SECOND);
	}
	
	/******************************************************************************************
	 * Next is greedy alg.
	 *****************************************************************************************/
	private MarkovAction greedyAction;
	private double greedyCost;
	public void runGreedy() {
		double res = -Double.MAX_VALUE;
		List<MarkovRecord> records = allLayerRecords.get(0);
		for (MarkovRecord rd : records) {
			double temp = getGreedyReward(rd);
			if (res < temp) {
				res = temp;
				greedyAction = rd.getAction();
			}
		}
		greedyCost = res;
	}
	
	public double getGreedyCost() {
		return greedyCost;
	}
	
	public MarkovAction getGreedyAction() {
		return greedyAction;
	}
}


