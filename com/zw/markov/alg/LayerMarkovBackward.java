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
		long t1 = System.currentTimeMillis();
		generateLayerRecords();
		long t2 = System.currentTimeMillis();
		initMarkovInfo();
		long t3 = System.currentTimeMillis();
		
		generateRecordRunTime = t2 - t1;
		initMarkovInfoRunTime = t3 - t2;
	}
	
	private MarkovState state;
	private Queue<MarkovState> queue1;
	private Queue<MarkovState> queue2;
	private List<List<MarkovRecord>> allLayerRecords;
	private Map<Integer, Set<MarkovState>> t2StateMap;
	//private Map<TAndState, MarkovAction> tState2ParentActionMap;
	//private Map<TAndAction, MarkovState> tAction2ParentStateMap;
	private Map<TAndState, List<MarkovAction>> tState2ChildActionMap;
	private Map<StateTAndAction, List<ToStateInfo>> stateTAction2ChildStateInfoMap;
	
	private double[][] utility;
	private String[] step;
	
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
	/*
	private class TAndAction {
		public TAndAction(int t, MarkovAction action) {
			super();
			this.t = t;
			this.action = action;
		}
		int t;
		MarkovAction action;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
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
			if (!(obj instanceof TAndAction)) {
				return false;
			}
			TAndAction other = (TAndAction) obj;
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
			if (t != other.t) {
				return false;
			}
			return true;
		}
		private LayerMarkovBackward getOuterType() {
			return LayerMarkovBackward.this;
		}
		
	}*/
	
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
	
	private void generateLayerRecords() {
		allLayerRecords = new ArrayList<List<MarkovRecord>>();
		//tAction2ParentStateMap = new HashMap<LayerMarkovBackward.TAndAction, MarkovState>();
		//tState2ParentActionMap = new HashMap<LayerMarkovBackward.TAndState, MarkovAction>();
		tState2ChildActionMap = new HashMap<LayerMarkovBackward.TAndState, List<MarkovAction>>();
		stateTAction2ChildStateInfoMap = new HashMap<LayerMarkovBackward.StateTAndAction, List<ToStateInfo>>();
		queue2 = new LinkedList<MarkovState>();
		queue2.offer(state);
		Set<MarkovState> stateSet = new HashSet<MarkovState>();
		t2StateMap = new HashMap<Integer, Set<MarkovState>>();
		for (;;) {
			Set<MarkovState> tempSet = new HashSet<MarkovState>();
			tempSet.addAll(queue2);
			t2StateMap.put(allLayerRecords.size(), tempSet);
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
			addToMap(allLayerRecords.size(), oneLayerRecords);
			allLayerRecords.add(oneLayerRecords);
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
		for (MarkovRecord rd : records) {
//			TAndAction ta = new TAndAction(t, rd.getAction());
//			TAndState ts = new TAndState(t, rd.getStateAfter());
//			if (tAction2ParentStateMap.get(ta) == null) {
//				tAction2ParentStateMap.put(ta, rd.getStateBefore());
//			}
//			if (tState2ParentActionMap.get(ts) == null) {
//				tState2ParentActionMap.put(ts, rd.getAction());
//			}
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
	
	/*
	 * 
	 * Below is the main Markov alg.
	 * 
	 * */
	private void initMarkovInfo() {
		utility = new double[this.getTsize()][MarkovRecord.getStateSize()];
		step = new String[this.getTsize() - 1];
		for (int t = getTsize()-1; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (!hasChildren(t, i)) {
					utility[t][i.getId()] = getNReward(i);
				} else {
					utility[t][i.getId()] = -Double.MAX_VALUE;
				}
			}
		}
	}
	
	private double maxUtility(int t, MarkovState i) {
		TAndState ts = new TAndState(t, i);
		double u = - Double.MAX_VALUE;
//		MarkovAction resAction = null;
		for (MarkovAction a : tState2ChildActionMap.get(ts)) {
			StateTAndAction sta = new StateTAndAction(i, t, a);
			double reward = this.getTReward(stateTAction2ChildStateInfoMap.get(sta).get(0).getState(),
					stateTAction2ChildStateInfoMap.get(sta).get(0).getTime(), 
					stateTAction2ChildStateInfoMap.get(sta).get(0).getPrice());
			for (ToStateInfo tsi : stateTAction2ChildStateInfoMap.get(sta)) {
				reward +=  Configs.WEAKEN * tsi.getPosibility() * utility[t+1][tsi.getState().getId()];
			}
			if (u < reward) {
				u = reward;
				//resAction = a;
				step[t] = this.makeStepString(t, a, u);
			}
		}
		return u;
	}
	
	public double getMarkovBestUtility() {
		long t1 = System.currentTimeMillis();
		for (int t = getTsize()-2; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (hasChildren(t, i)) {
					utility[t][i.getId()] = maxUtility(t, i);
				}
			}
		}
		runMarkovRunTime = System.currentTimeMillis() - t1;
		return utility[0][0];
	}	
	
	/* 
	 * This is the reward of terminate state (Leaf of the UTG tree).
	 * */
	private double getNReward(MarkovState state) {
		switch (state.getGlobalState()) {
		case Markov.S_SUCCEED:
			return ( 10 );
		case Markov.S_DELAYED:
			return ( 10 );
		case Markov.S_PRICE_UP:
			return ( 10 );
		case Markov.S_NORMAL:
			return ( 10 );
		case Markov.S_FAILED:
			return (-12);
		default:
			return 0;
		}	
	}
	
	/*
	 * This is the reward after do a action.
	 * */
	private double getTReward(MarkovState resultState, double time, double cost) {
		return cost;
	}
	
	/*
	 * End Markov..
	 * 
	 */
}


