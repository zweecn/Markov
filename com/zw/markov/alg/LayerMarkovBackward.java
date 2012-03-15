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
		//initTree();
		long t3 = System.currentTimeMillis();
		initMarkovInfo();
		long t4 = System.currentTimeMillis();
		
		System.out.println("Run time, Gen Record :" + (t2-t1));
		System.out.println("Run time, Init Tree  :" + (t3-t2));
		System.out.println("Run time, Init Markov:" + (t4-t3));
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
	private TreeNode[] treeNodeArray;
	private int treeNodeSize;
	
	
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
				treeNodeSize++;
				actionSetTemp.add(rd.getAction());
			}
			treeNodeSize += actionSetTemp.size();
		}
	}

	private void addToMap(int t, List<MarkovRecord> records) {
		for (MarkovRecord rd : records) {
			TAndAction ta = new TAndAction(t, rd.getAction());
			TAndState ts = new TAndState(t, rd.getStateAfter());
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
	
	// Here bugs
/*	private void initTree() {
		treeNodeArray = new TreeNode[treeNodeSize];
		int actionStartPos[] = new int[getTsize()];
		int actionEndPos[] = new int[getTsize()];
		int stateStartPos[] = new int[getTsize()];
		int stateEndPos[] = new int[getTsize()];
		
		treeNodeArray[0] = new TreeNode(MarkovRecord.getState(0).getId());
		int pos = 0;
		for (int i = 0; i < allLayerRecords.size(); i++) {
 			Set<MarkovAction> actionSetTemp = new HashSet<MarkovAction>();
			for (int j = 0; j < allLayerRecords.get(i).size(); j++) {
				MarkovRecord rd = allLayerRecords.get(i).get(j);
				actionSetTemp.add(rd.getAction());
			}
			actionStartPos[i] = pos + 1;
			actionEndPos[i] = pos + actionSetTemp.size();
			pos += actionSetTemp.size();
			
			stateStartPos[i] = pos + 1;
			stateEndPos[i] = pos + allLayerRecords.get(i).size();
			pos += allLayerRecords.get(i).size();
			
			
			Iterator<MarkovAction> ita = actionSetTemp.iterator();
			for (int k = actionStartPos[i]; k <= actionEndPos[i]; k++) {
				if (ita.hasNext()) {
					treeNodeArray[k] = new TreeNode(k);
					treeNodeArray[k].setAction(ita.next());
				} else {
					System.err.println("Error in initTree(). Code 0x01");
				}
			}
			Iterator<MarkovRecord> itr = allLayerRecords.get(i).iterator();
			for (int k = stateStartPos[i]; k <= stateEndPos[i]; k++) {
				if (itr.hasNext()) {
					treeNodeArray[k] = new TreeNode(k);
					treeNodeArray[k].setState(itr.next().getStateAfter());
				} else {
					System.err.println("Error in initTree(). Code 0x02");
				}
			}
		}
		
		for (int i = 0; i < allLayerRecords.size(); i++) {
			for (int j = 0; j < allLayerRecords.get(i).size(); j++) {
				
			}
		}
		
		
//		for (List<MarkovRecord> rds : allLayerRecords) { 
//			for (MarkovRecord rd : rds) {
//				if (stateNodeArray[rd.getStateAfter().getId()] == null) {
//					stateNodeArray[rd.getStateAfter().getId()] = new TreeNode(rd.getStateAfter().getId());
//				}
//				if (stateNodeArray[rd.getStateBefore().getId()] == null) {
//					stateNodeArray[rd.getStateBefore().getId()] = new TreeNode(rd.getStateBefore().getId());
//				}
//				if (actionNodeArray[rd.getAction().getId()] == null) {
//					actionNodeArray[rd.getAction().getId()] = new TreeNode(rd.getAction().getId());
//				}
//				actionNodeArray[rd.getAction().getId()].setParent(rd.getStateBefore().getId());
//				if (!actionNodeArray[rd.getAction().getId()].getChildren().contains(rd.getStateAfter().getId())) {
//					actionNodeArray[rd.getAction().getId()].addChild(rd.getStateAfter().getId());
//				}
//				stateNodeArray[rd.getStateAfter().getId()].setParent(rd.getAction().getId());
//				if (!stateNodeArray[rd.getStateBefore().getId()].getChildren().contains(rd.getAction().getId())) {
//					stateNodeArray[rd.getStateBefore().getId()].addChild(rd.getAction().getId());
//				}
//				
//			}
//		}	
	}*/
	
	private int getTsize() {
		return allLayerRecords.size();
	}
	
	private double getReward(MarkovState state) {
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
	
	/*private boolean hasChildren(int t, MarkovAction action) {
		TAndAction ta = new TAndAction(t, action);
		return (tAction2ChildStateInfoMap.get(ta) != null);
	}*/
	
	private boolean hasChildren(int t, MarkovState state) {
		TAndState ts = new TAndState(t, state);
		return (tState2ChildActionMap.get(ts) != null);
	}
	
	private void initMarkovInfo() {
		utility = new double[this.getTsize()][MarkovRecord.getStateSize()];
		for (int t = getTsize()-1; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (!hasChildren(t, i)) {
					utility[t][i.getId()] = getReward(i);
				} else {
					utility[t][i.getId()] = -Double.MAX_VALUE;
				}
			}
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
	
	private double maxUtility(int t, MarkovState i) {
		TAndState ts = new TAndState(t, i);
		double res = - Double.MAX_VALUE;
		MarkovAction resAction = null;
		for (MarkovAction a : tState2ChildActionMap.get(ts)) {
			StateTAndAction sta = new StateTAndAction(i, t, a);
			double reward = stateTAction2ChildStateInfoMap.get(sta).get(0).getPrice();
			//System.out.println("ACTION=" + a);
			for (ToStateInfo tsi : stateTAction2ChildStateInfoMap.get(sta)) {
				reward +=  Configs.WEAKEN * tsi.getPosibility() * utility[t+1][tsi.getState().getId()];
			}
			if (res < reward) {
				res = reward;
				resAction = a;
			}
		}
		
		System.out.println("At t=" + t + " State=" + i.getId() + " Action=" + resAction);
		return res;
	}
	
	public double getMarkovBestUtility() {
		for (int t = getTsize()-2; t >= 0; t--) {
			for (MarkovState i : t2StateMap.get(t)) {
				if (hasChildren(t, i)) {
					utility[t][i.getId()] = maxUtility(t, i);
				}
			}
		}
		
		return utility[0][0];
	}
	
}
/*
	private void initMarkovInfo() {
		resultActions = new ArrayList<String>();
//		stateArray = new MarkovState[stateSize];
//		actionArray = new MarkovAction[actionSize];
		
//		for (int i = 0; i < allLayerRecords.size(); i++) {
//			for (MarkovRecord rd : allLayerRecords.get(i)) {
//				stateArray[(int) rd.getStateBefore().getId()] = rd.getStateBefore();
//				stateArray[(int) rd.getStateAfter().getId()] = rd.getStateAfter();
//				actionArray[(int) rd.getAction().getId()] = rd.getAction();
//		 	}
//		}
		
		utility = new double[stateSize];
//		reward_n = new double[stateSize];
//		reward_t = new double[stateSize][actionSize];
//		sas = new StateActionState();
//		posibility = new double[stateSize][actionSize][stateSize];
//		timeCost = new double[stateSize][actionSize][stateSize];
//		priceCost = new double[stateSize][actionSize][stateSize];
		
		
		for (int i = 0; i < stateSize; i++) {
			//System.out.println(stateNodeArray);
			if (!stateNodeArray[i].hasChild()) {
				//utility[i] = reward_n[i] = getReward(stateArray[i]);
				//utility[i] = getReward(stateArray[i]);
				utility[i] = getReward(MarkovRecord.getState(i));
			} else {
				//utility[i] = reward_n[i] = - Double.MAX_VALUE;
				utility[i] = - Double.MAX_VALUE;
			}
		}
		// Fix the bug: the last layer's stateAfter is the leaf, should be getReward
		for (MarkovRecord rd : allLayerRecords.get(allLayerRecords.size()-1)) {
			utility[rd.getStateAfter().getId()] = getReward(rd.getStateAfter());
		}
		
		for (List<MarkovRecord> rds : allLayerRecords) { 
			for (MarkovRecord rd : rds) {
//				reward_t[rd.getStateBefore().getId()][rd.getAction().getId()] = - rd.getPriceCost();
				MarkovRecord.setReward_t(rd.getStateBefore().getId(), rd.getAction().getId(), - rd.getPriceCost());
//				posibility[rd.getStateBefore().getId()][rd.getAction().getId()][rd.getStateAfter().getId()] = rd.getPosibility();
// 1				MarkovRecord.setPosibility(rd.getStateBefore().getId(), rd.getAction().getId(), rd.getStateAfter().getId(), rd.getPosibility());
//				timeCost[rd.getStateBefore().getId()][rd.getAction().getId()][rd.getStateAfter().getId()] = rd.getTimeCost();
//				priceCost[rd.getStateBefore().getId()][rd.getAction().getId()][rd.getStateAfter().getId()] = rd.getPriceCost();
			}
		}
	}
	
	public void printUtility() {
		for (int i = 0; i < utility.length; i++) {
			System.out.println("utility[" + i + "]=" + utility[i]);
		}
	}
	
//	public void printStateFlow() {
//		for (int i = 0; i < stateArray.length; i++) {
//			if (stateArray[i] != null) {
//				System.out.println("------------------------------------------------------------------------");
//				System.out.println("state " + i);
//				stateArray[i].printFlow();
//			}
//		}
//	}
	
	public double getBestChose() {
		//printUtility();
		long t1 = System.currentTimeMillis();
		for (int i = stateSize-1; i >= 0; i--) {
			if (stateNodeArray[i].hasChild()) { //Fix the bug: rewrite utility[i]
				//System.out.println("In state has child, i=" + i);
				utility[i] = max(i);
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Run time, Get Best :" + (t2-t1));
		Collections.reverse(resultActions);
		return utility[0];
	}
	
	public List<String> getResultActions() {
		return this.resultActions;
	}
	
	private double max(int i) {
		double resDouble = - Double.MAX_VALUE;
		int actionId = -1;
		for (int a : stateNodeArray[i].getChildren()) {
//			double temp = reward_t[i][a];
			double temp = MarkovRecord.getReward_t(i, a);
			//System.out.println("Reward " + i+ " " + a +  " " + temp);
			for (int j : actionNodeArray[a].getChildren()) {
//				System.out.println("IN FOR " + MarkovRecord.getPosibility(i, a, j));
//				temp += utility[j] * posibility[i][a][j] * Configs.WEAKEN;
				temp += utility[j] * MarkovRecord.getPosibility(i, a, j) * Configs.WEAKEN;
				//System.out.println("IN FOR, temp=" + temp);
			}
			//System.out.println("out FOR, temp=" + temp + "\n");
			if (resDouble < temp) {
				resDouble = temp;
				actionId = a;
			}
		}
		//String currStep = "At state=" + String.format("%2d", i) + "  do action=" + actionArray[actionId] + "  current utility=" + String.format("%.2f", resDouble);
		String currStep = "At state=" + String.format("%2d", i) + "  do action=" + MarkovRecord.getAction(actionId) + "  current utility=" + String.format("%.2f", resDouble);
		resultActions.add(currStep);
		return resDouble;
	}
	
	private void initTree() {
		stateNodeArray = new StateNode[stateSize];
		actionNodeArray = new ActionNode[actionSize];
		for (List<MarkovRecord> rds : allLayerRecords) { 
			for (MarkovRecord rd : rds) {
				if (stateNodeArray[rd.getStateAfter().getId()] == null) {
					stateNodeArray[rd.getStateAfter().getId()] = new StateNode(rd.getStateAfter().getId());
				}
				if (stateNodeArray[rd.getStateBefore().getId()] == null) {
					stateNodeArray[rd.getStateBefore().getId()] = new StateNode(rd.getStateBefore().getId());
				}
				if (actionNodeArray[rd.getAction().getId()] == null) {
					actionNodeArray[rd.getAction().getId()] = new ActionNode(rd.getAction().getId());
				}
				actionNodeArray[rd.getAction().getId()].setParent(rd.getStateBefore().getId());
				if (!actionNodeArray[rd.getAction().getId()].getChildren().contains(rd.getStateAfter().getId())) {
					actionNodeArray[rd.getAction().getId()].addChild(rd.getStateAfter().getId());
				}
				stateNodeArray[rd.getStateAfter().getId()].setParent(rd.getAction().getId());
				if (!stateNodeArray[rd.getStateBefore().getId()].getChildren().contains(rd.getAction().getId())) {
					stateNodeArray[rd.getStateBefore().getId()].addChild(rd.getAction().getId());
				}
				
			}
		}
		
	}
	
	public void printTree() {	
		for (int i = 0; i < stateNodeArray.length; i++) {
			if (stateNodeArray[i] != null) {
				System.out.println(stateNodeArray[i]);
			}
		}
		for (int i = 0; i < actionNodeArray.length; i++) {
			if (actionNodeArray[i] != null) {
				System.out.println(actionNodeArray[i]);
			}
		}
	}
*/

