package com.zw.markov.alg;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.naming.InitialContext;

import com.zw.markov.Markov;
import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class LayerMarkovBackward {
	private final static String LOG_FILE_NAME = "markov_output\\markov_log.txt";
	Queue<MarkovState> queue;
	Queue<MarkovState> queueTemp;
	List<List<MarkovRecord>> allLayerRecords;
	List<MarkovRecord> oneLayerRecords;
	
	public void generateLayerRecords(MarkovState state) {
		allLayerRecords = new ArrayList<List<MarkovRecord>>();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		
		queueTemp = new LinkedList<MarkovState>();
		queueTemp.offer(state);
		Set<MarkovState> stateSet = new HashSet<MarkovState>();
		for (int i = 0; i < 3; i++) {
			queue = queueTemp;
			queueTemp = new LinkedList<MarkovState>();
			oneLayerRecords = new ArrayList<MarkovRecord>();
			while (!queue.isEmpty()) {
				state = queue.poll();
				if (!stateSet.contains(state)) {
					stateSet.add(state);
					List<MarkovRecord> records = Markov.noAction(state);
					addToRecords(records);
					records = Markov.terminate(state);
					addToRecords(records);
					records = Markov.redo(state);
					addToRecords(records);
					records = Markov.replace(state);  
					addToRecords(records);
					records = Markov.reComposite(state);
					addToRecords(records);
				}
			}
			allLayerRecords.add(oneLayerRecords);
		}
		stateSize++;
		actionSize++;
	}

	private int stateSize = 0;
	private int actionSize = 0;
	private void addToRecords(List<MarkovRecord> records){
		if (records != null && !records.isEmpty()) {
			oneLayerRecords.addAll(records);
			for (MarkovRecord rd : records) {
				queueTemp.offer(rd.getStateAfter());
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
	}

	public void printRecords() {
		System.out.println("records size=" + allLayerRecords.size());
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		for (int i = 0; i < allLayerRecords.size(); i++) {
			System.out.println("Layer " + i);
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toString());
			}
		}
	}
	
	public void writeRecords() {
		System.out.println("records size=" + allLayerRecords.size());
		try {
		FileWriter writer = new FileWriter(LOG_FILE_NAME);
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
	
	private double[] utility;
	private double[][] reward_t;
	private double[] reward_n;
	
	private MarkovState[] states;
	private MarkovAction[] actions;
	
	private Map<MarkovState, List<MarkovAction>> stateChildrenMap;
	private Map<MarkovState, MarkovAction> stateParentMap;
	private Map<MarkovAction, List<MarkovState>> actionChildrenMap;
	private Map<MarkovAction, MarkovState> actionParentMap;
	private void initStatesAndActions() {
		states = new MarkovState[stateSize];
		actions = new MarkovAction[actionSize];
		stateChildrenMap = new HashMap<MarkovState, List<MarkovAction>>();
		stateParentMap = new HashMap<MarkovState, MarkovAction>();
		actionChildrenMap = new HashMap<MarkovAction, List<MarkovState>>();
		actionParentMap = new HashMap<MarkovAction, MarkovState>();
		
		for (int i = 0; i < allLayerRecords.size(); i++) {
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				states[(int) rd.getStateBefore().getId()] = rd.getStateBefore();
				states[(int) rd.getStateAfter().getId()] = rd.getStateAfter();
				actions[(int) rd.getAction().getId()] = rd.getAction();
				
				if (stateChildrenMap.get(rd.getStateBefore()) == null) {
					stateChildrenMap.put(rd.getStateBefore(), new ArrayList<MarkovAction>());
				}
				stateChildrenMap.get(rd.getStateBefore()).add(rd.getAction());
				if (stateParentMap.get(rd.getStateAfter()) == null) {
					stateParentMap.put(rd.getStateAfter(), rd.getAction());
				}
				if (actionChildrenMap.get(rd.getAction()) == null) {
					actionChildrenMap.put(rd.getAction(), new ArrayList<MarkovState>());
				}
				actionChildrenMap.get(rd.getAction()).add(rd.getStateAfter());
				if (actionParentMap.get(rd.getAction()) == null) {
					actionParentMap.put(rd.getAction(), rd.getStateBefore());
				}
		 	}
		}
	}
	
	private ActionNode[] actionNodes;
	private StateNode[] stateNodes;
	
	private void initTree() {
//		int stateSize = allLayerRecords.get(allLayerRecords.size()-1)
//				.get(allLayerRecords.get(allLayerRecords.size()-1).size()-1).getStateAfter().getId();
//		int actionSize = allLayerRecords.get(allLayerRecords.size()-1)
//				.get(allLayerRecords.get(allLayerRecords.size()-1).size()-1).getAction().getId();
		stateNodes = new StateNode[stateSize];
		actionNodes = new ActionNode[actionSize];
		
		for (List<MarkovRecord> rds : allLayerRecords) { 
			for (MarkovRecord rd : rds) {
				if (stateNodes[rd.getStateAfter().getId()] == null) {
					stateNodes[rd.getStateAfter().getId()] = new StateNode(rd.getStateAfter().getId());
				}
				if (stateNodes[rd.getStateBefore().getId()] == null) {
					stateNodes[rd.getStateBefore().getId()] = new StateNode(rd.getStateBefore().getId());
				}
				if (actionNodes[rd.getAction().getId()] == null) {
					actionNodes[rd.getAction().getId()] = new ActionNode(rd.getAction().getId());
				}
				
				
				actionNodes[rd.getAction().getId()].setParent(rd.getStateBefore().getId());
				if (!actionNodes[rd.getAction().getId()].getChildren().contains(rd.getStateAfter().getId())) {
					actionNodes[rd.getAction().getId()].addChild(rd.getStateAfter().getId());
				}
				stateNodes[rd.getStateAfter().getId()].setParent(rd.getAction().getId());
				if (!stateNodes[rd.getStateBefore().getId()].getChildren().contains(rd.getAction().getId())) {
					stateNodes[rd.getStateBefore().getId()].addChild(rd.getAction().getId());
				}
				
			}
		}
		
	}
	
	public void printTree() {
		int sRoot = 0;
		initTree();
		for (int i = 0; i < stateNodes.length; i++) {
			if (stateNodes[i] != null) {
				System.out.println(stateNodes[i]);
			}
		}
		for (int i = 0; i < actionNodes.length; i++) {
			if (actionNodes[i] != null) {
				System.out.println(actionNodes[i]);
			}
		}
	}
	
//	StateTree root;
//	public void initTree() {
//		root = new StateTree(allLayerRecords.get(0).get(0).getStateBefore());
//		StateTree rootTemp = root;
//		List<StateTree> stateBeforeTrees = new ArrayList<StateTree>();
//		stateBeforeTrees.add(rootTemp);
//		for (List<MarkovRecord> rds : allLayerRecords) {
//			
//			for (MarkovRecord rd : rds) {
//				 if (!rd.getStateBefore().equals(rootTemp.getState())) {
//					 StateTree tempStateTree =findStateTree(stateBeforeTrees, rd.getStateBefore()); 
//					 if (tempStateTree == null) {
//						 rootTemp = new StateTree(rd.getStateBefore());
//						 stateBeforeTrees.add(rootTemp);
//					 } else {
//						 rootTemp = tempStateTree;
//						 stateBeforeTrees.add(rootTemp);
//					 }
//				 }
//				 ActionTree child = new ActionTree(rd.getAction());
//				 StateTree stateAfterStateTree = new StateTree(rd.getStateAfter());
//				 child.addChild(stateAfterStateTree);
//				 rootTemp.addChild(child);
//				 stateBeforeTrees.add(stateAfterStateTree);
//			}
//			for (MarkovRecord rd : rds) {
//				StateTree childTemp = new StateTree(rd.getStateAfter());
//				
//			}
//		}
//		
//		
//		int stateSize = allLayerRecords.get(allLayerRecords.size()-1)
//				.get(allLayerRecords.get(allLayerRecords.size()-1).size()-1).getStateAfter().getId();
//		utility = new double[stateSize];
//		reward_n = new double[stateSize];
//		int actionSize = allLayerRecords.get(allLayerRecords.size()-1)
//				.get(allLayerRecords.get(allLayerRecords.size()-1).size()-1).getAction().getId();
//		reward_t = new double[stateSize][actionSize];
//		
//		for (int i = 0; i < reward_n.length; i++) {
//			if (stateChildrenMap.get(states[i]) == null) {
//				utility[i] = reward_n[i] = getReward(states[i]);
//			} else {
//				utility[i] = reward_n[i] = - Double.MAX_VALUE;
//			}
//		}
//		
//		for (int i = allLayerRecords.size()-1; i >= 0; i--) {
//			for (int j = allLayerRecords.get(i).size()-1; j >=0; j--) {
//				MarkovRecord rd = allLayerRecords.get(i).get(j);
//				MarkovAction action = rd.getAction();
//				
//				while (j>=0 && action.equals(rd.getAction())) {
//					
//					
//					j--;
//				}
//			}
//		}
//		
//	}
//	
//	private StateTree findStateTree(List<StateTree> stateTrees, MarkovState state) {
//		for (int i = 0; i < stateTrees.size(); i++) {
//			if (stateTrees.get(i).getState().equals(state)) {
//				return stateTrees.get(i); 
//			}
//		}
//		return null;
//	}
	
	private double getReward(MarkovState state) {
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
