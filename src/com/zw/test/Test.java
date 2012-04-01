package com.zw.test;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.zw.Configs;
import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.Activity;
import com.zw.ws.ActivityFlow;

public class Test {

	public static void clearMarkovRecordAndActivityFlow() {
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static void printMarkovActionSequence(Map<Integer, ActionSequence> i2ActionMap) {
		Iterator it = i2ActionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ActionSequence> entry = (Entry<Integer, ActionSequence>) it.next();
			int t = entry.getKey();
			ActionSequence seq = entry.getValue();
			System.out.printf("At t=%2d Action=%s Reward=%.2f TimeCost=%.2f PriceCost=%.2f\n", 
					t, seq.getActionString(), seq.getReward(), seq.getTime(), seq.getPrice());
		}
	}
	
	public static void printMarkovRecovery(int t, MarkovState state, LayerMarkovBackward bd) {
		System.out.println("\nFault state=" + state);
		System.out.println("New state=" + bd.getStateNew());
		double maxUtility =  bd.getMarkovBestUtility();
		System.out.print("Markov:" + bd.getAction());
		System.out.printf("Cost: %.2f ", bd.getCurrActionCost());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.println("\n");
	}
	
	public static void printGreedyRecovery(int t, MarkovState state, LayerMarkovBackward bd) {
		System.out.println("\nFault state=" + state);
		System.out.println("New state=" + bd.getGreedyStateNew());
		System.out.print("Greedy:" + bd.getGreedyAction());
		System.out.printf("Cost: %.2f ", bd.getGreedyPriceCost());
		double greedyUtility = bd.getGreedyActionReward();
		if (greedyUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", greedyUtility);
		}
		System.out.println("\n");
	}
	
	public static void printReduceLayerRecovery(int t, MarkovState state, 
			LayerMarkovBackward bd, int reduceLayerSize) {
		System.out.println("\nFault state=" + state);
		System.out.println("New state=" + bd.getStateNew());
		double maxUtility =  bd.getMarkovBestUtility();
		System.out.print("ReduceLayerSize:" + reduceLayerSize + " Markov:" + bd.getAction());
		System.out.printf("Cost: %.2f ", bd.getCurrActionCost());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.println("\n");
	}
	
	public static MarkovState reduceLayerRecovery(int t, MarkovState state,
			Map<Integer, ActionSequence> i2markovActionMap, int reduceLayerSize) {
		Test.clearMarkovRecordAndActivityFlow();
		state.setId(0);
		state.init();
		
		MarkovState stateTemp = state.clone();
		stateTemp.init();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.init(reduceLayerSize);
		bd.runMarkov();
		
//		printReduceLayerRecovery(t, state, bd, reduceLayerSize);
		
		if (bd.getAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp.getNextToDoActivity().setX(- stateTemp.getNextToDoActivity().getX() 
					+ 1.0 / stateTemp.getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp.init();
		}
		ActionSequence seq = new ActionSequence(bd.getAction().getOpNumber(), bd.getAction().toFormatString(), 
				bd.getCurrActionCost(), bd.getCurrActionTimeCost(), bd.getCurrActionReward());
		i2markovActionMap.put(t, seq);
		
		return stateTemp;
	}
	
	public static MarkovState markovRecovery(int t, MarkovState state, Map<Integer, ActionSequence> i2markovActionMap) {
		Test.clearMarkovRecordAndActivityFlow();
		state.setId(0);
		state.init();
		
		MarkovState stateTemp = state.clone();
		stateTemp.init();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		
		if (bd.getAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp.getNextToDoActivity().setX(- stateTemp.getNextToDoActivity().getX() 
					+ 1.0 / stateTemp.getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp.init();
		}
//		printMarkovRecovery(t, state, bd);
		ActionSequence seq = new ActionSequence(bd.getAction().getOpNumber(), bd.getAction().toFormatString(), 
				bd.getCurrActionCost(), bd.getCurrActionTimeCost(), bd.getCurrActionReward());
		i2markovActionMap.put(t, seq);
		
		return stateTemp;
	}
	
	public static MarkovState greedyRecovery(int t, MarkovState state, Map<Integer, ActionSequence> i2greedyActionMap) {
		Test.clearMarkovRecordAndActivityFlow();
		state.setId(0);
		state.init();
		
		MarkovState stateTemp = state.clone();
		stateTemp.init();
//		stateTemp.getNextToDoActivity().setX(- stateTemp.getNextToDoActivity().getX());
//		stateTemp.init();
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runGreedy();

		if (bd.getGreedyAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp.getNextToDoActivity().setX(- stateTemp.getNextToDoActivity().getX() 
					+ 1.0 / stateTemp.getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp.init();
		}
		
//		printGreedyRecovery(t, state, bd);
		
		ActionSequence seq = new ActionSequence(bd.getGreedyAction().getOpNumber(), bd.getGreedyAction().toFormatString(), 
				bd.getGreedyPriceCost(), bd.getGreedyTimeCost(), bd.getGreedyActionReward());
		i2greedyActionMap.put(t, seq);
		return stateTemp;
	}
	
	public static MarkovState makeFault(MarkovState state) {
		if (state == null) {
			System.out.println("State is null in Test.java Code 0x06");
			return null;
		}
		if (state.isFailed()) {
			System.out.println("State is failed in Test.java Code 0x06");
			return null;
		}
		if (state.isFinished()) {
			System.out.println("State is finished in Test.java Code 0x06");
			return null;
		}
		Activity nextToDoActivity = state.getNextToDoActivity();
		if (nextToDoActivity == null) {
			System.out.println("Activity is null in Test.java Code 0x06");
			return null;
		}
		if (nextToDoActivity.getX() > 0 && nextToDoActivity.getX() < 1) {
			double xTemp = 1.0 / nextToDoActivity.getBlindService().getQos().getExecTime();
			nextToDoActivity.addX(xTemp);
			nextToDoActivity.setX(- nextToDoActivity.getX());
		} else if (nextToDoActivity.getX() == 1) {
			double xTemp = 1.0 / nextToDoActivity.getBlindService().getQos().getExecTime();
			nextToDoActivity.setX(-xTemp);
		}
		
		return state;
	}
	
	public static boolean isFault(Activity activity) {
		double b = activity.getBlindService().getQos().getExecTime();
		double a = activity.getBlindService().getQos().getReliability();
		double p = Math.pow(a, 1d/b) * Configs.RANDOM_FAULT;
		if (new Random().nextDouble() > p) {
			return true;
		}
		return false;
	}
}
