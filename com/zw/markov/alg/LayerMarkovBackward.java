package com.zw.markov.alg;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.zw.Configs;
import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class LayerMarkovBackward {
	public LayerMarkovBackward(MarkovState state) {
		this.state = state;
		generateLayerRecords();
		initTree();
		initMarkovInfo();
	}
	
	private Queue<MarkovState> queue1;
	private Queue<MarkovState> queue2;
	private List<List<MarkovRecord>> allLayerRecords;
//	private List<MarkovRecord> oneLayerRecords;
	private MarkovState state;
	private List<String> resultActions;
//	private StateActionState sas; 
	
	private int stateSize;
	private int actionSize;
	private double[] utility;
//	private double[][] reward_t;
//	private double[] reward_n;
//	private double[][][] posibility;
//	private double[][][] timeCost;
//	private double[][][] priceCost;
	
//	private MarkovState[] stateArray;
//	private MarkovAction[] actionArray;
	private ActionNode[] actionNodeArray;
	private StateNode[] stateNodeArray;
	
//	private class StateActionState {
//		private Map<String, Double> pMap;
//		public StateActionState() {
//			pMap = new HashMap<String, Double>();
//		}
//		public double getPosibility(int i, int a, int j) {
//			return pMap.get(i + "-" + a + "-" + j);
//		}
//		public void setPosibility(int i, int a, int j, double p) {
//			pMap.put(i + "-" + a + "-" + j, p);
//		}
//	}
	
	private void generateLayerRecords() {
		allLayerRecords = new ArrayList<List<MarkovRecord>>();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		
		queue2 = new LinkedList<MarkovState>();
		queue2.offer(state);
		Set<MarkovState> stateSet = new HashSet<MarkovState>();
		for (int i = 0; i < Configs.LAYER_SIZE; i++) {
			queue1 = queue2;
			queue2 = new LinkedList<MarkovState>();
			List<MarkovRecord> oneLayerRecords = new ArrayList<MarkovRecord>();
			while (!queue1.isEmpty()) {
				state = queue1.poll();
				if (!stateSet.contains(state)) {
					stateSet.add(state);
//					MarkovState sTemp1 = state.store();
					List<MarkovRecord> records = Markov.noAction(state);
					addToRecords(oneLayerRecords, records);
					records = Markov.terminate(state);
					addToRecords(oneLayerRecords, records);
					records = Markov.redo(state);
					addToRecords(oneLayerRecords, records);
					records = Markov.replace(state);  
					addToRecords(oneLayerRecords, records);
					MarkovState sTemp = state.store();
					sTemp.init();
					records = Markov.reComposite(state);
					//System.out.println("In Layer:" + records);
					addToRecords(oneLayerRecords, records);
				}
			}
//			System.out.println("One Layer Records:");
//			for (MarkovRecord rd : oneLayerRecords) {
//				System.out.println(rd);
//			}
			allLayerRecords.add(oneLayerRecords);
			//oneLayerRecords.clear();
		}
		stateSize++;
		actionSize++;
		//System.out.println("state size=" + stateSize);
	}

	
	private void addToRecords(List<MarkovRecord>destRecords, List<MarkovRecord> sourceRecord){
//		System.out.println("\nsource:");
//		for (MarkovRecord rd : sourceRecord) {
//			System.out.println(rd.toString());
//		}
//		System.out.println("dest:");
//		for (MarkovRecord rd : destRecords) {
//			System.out.println(rd.toString());
//		}
		if (sourceRecord != null && !sourceRecord.isEmpty()) {
			for (MarkovRecord rd : sourceRecord) {
				destRecords.add(rd.clone());
			}
			//oneLayerRecords.addAll(records);
			for (MarkovRecord rd : sourceRecord) {
				queue2.offer(rd.getStateAfter());
				if (rd.getStateBefore().getId() > stateSize) {
					stateSize = (int) rd.getStateBefore().getId();
				}
				if (rd.getStateAfter().getId() > stateSize) {
					stateSize = (int) rd.getStateAfter().getId();
				}
				if (rd.getAction().getId() > actionSize) {
					actionSize = (int) rd.getAction().getId();
				}
			}
		}
//		System.out.println("OneLayerRecords:");
//		for (MarkovRecord rd : destRecords) {
//			System.out.println(rd.toString());
//		}
//		System.out.println("--------");
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
		for (int i = stateSize-1; i >= 0; i--) {
			if (stateNodeArray[i].hasChild()) { //Fix the bug: rewrite utility[i]
				//System.out.println("In state has child, i=" + i);
				utility[i] = max(i);
			}
		}
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
	
	private double getReward(MarkovState state) {
		switch (state.getCurrGlobalState()) {
		case Markov.S_SUCCEED:
			return ( 10 );
		case Markov.S_DELAYED:
			return ( 10 );
		case Markov.S_PRICE_UP:
			return ( 10 );
		case Markov.S_UNKNOWN:
			return ( 10 );
			
		case Markov.S_FAILED:
			return (-12);
		default:
			return 0;
		}
	}
}
