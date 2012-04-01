package com.zw.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.zw.markov.*;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.ActivityFlow;

public class Test1 {
	public Test1() {
		i2markovActionMap = new HashMap<Integer, ActionSequence>();
		i2greedyActionMap = new HashMap<Integer, ActionSequence>();
	}
	
	public static final String MARKOV_OUTPUT_FILE = "markov_output\\test1\\mout.txt";
	public static final String GREEDY_OUTPUT_FILE = "markov_output\\test1\\gout.txt";
	public static final String MARKOV_ACTION_FILE = "markov_output\\test1\\m_action.txt";
	public static final String GREEDY_ACTION_FILE = "markov_output\\test1\\g_action.txt";
	
	private Map<Integer, ActionSequence> i2markovActionMap;
	private Map<Integer, ActionSequence> i2greedyActionMap;
	private int markovTime;	
	private int greedyTime;
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private String toMatlabFormat() {
		String res = "";
		int temp1 = 0;
		int temp2 = 0;
		Iterator it = i2markovActionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ActionSequence> entry = (Entry<Integer, ActionSequence>) it.next();
			if (temp1 < entry.getKey()) {
				temp1 = entry.getKey();
			}
		}
		it = i2greedyActionMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ActionSequence> entry = (Entry<Integer, ActionSequence>) it.next();
			if (temp2 < entry.getKey()) {
				temp2 = entry.getKey();
			}
		}
		int tSize = Math.max(temp1, temp2);
		res += "t = [";
		for (int i = 0; i < tSize; i++) {
			res += (i+1) + " ";
		}
		res = res.trim() + "]\n";
		
		res += "r_m = [";
		double reward = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2markovActionMap.get(i);
			if (seq != null) {
				reward += seq.getReward();
			}
			res += String.format("%.2f", reward) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "r_g = [";
		reward = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2greedyActionMap.get(i);
			if (seq != null) {
				reward += seq.getReward();
			}
			res += String.format("%.2f", reward) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "p_m = [";
		double price = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2markovActionMap.get(i);
			if (seq != null) {
				price -= seq.getPrice();
			}
			res += String.format("%.2f", price) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "p_g = [";
		price = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2greedyActionMap.get(i);
			if (seq != null) {
				price -= seq.getPrice();
			}
			res += String.format("%.2f", price) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "t_m = [";
		double time = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2markovActionMap.get(i);
			if (seq != null) {
				time -= seq.getTime();
			}
			res += String.format("%.2f", time) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "t_g = [";
		time = 0;
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2greedyActionMap.get(i);
			if (seq != null) {
				time -= seq.getTime();
			}
			res += String.format("%.2f", time) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "a_m = [";
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2markovActionMap.get(i);
			int a = 0;
			if (seq != null) {
				a = seq.getAction();
			}
			res += String.format("%d", a) + " "; 
		}
		res = res.trim() + "]\n";
		
		res += "a_g = [";
		for (int i = 0; i < tSize; i++) {
			ActionSequence seq = i2greedyActionMap.get(i);
			int a = 0;
			if (seq != null) {
				a = seq.getAction();
			}
			res += String.format("%d", a) + " "; 
		}
		res = res.trim() + "]\n";
		
		return res;
	}
	
	@SuppressWarnings("unused")
	private void writeMatlabFile(String s) {
		String[] lines = s.split("\n");
		String res = "";
		for (String line : lines) {
			String t = line.split("=")[1].trim();
			String tt = t.substring(1, t.length()-1);
			res += tt + "\n";
		}
		res = res.trim();
		try {
			FileOutputStream outputStream = new FileOutputStream(MARKOV_OUTPUT_FILE);
			outputStream.write(res.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeMatlabFileTT() {		
		String t_markov = "";
		String t_markov_action = "";
		String t_markov_action_text = "";
		String reward_markov = "";
		String price_markov = "";
		String time_markov = "";
		String action_markov = "";
		String action_markov_text = "";
		double reward_markov_double = 0;
		double price_markov_double = 0;
		double time_markov_double = 0;
		for (int i = 0; i < markovTime; i++) {
//			t_markov += i + " ";
			ActionSequence seq = i2markovActionMap.get(i);
			int action = 0;
			String actionText = "NULL";
			if (seq != null) {
				t_markov += i + " ";
				reward_markov_double += seq.getReward();
				price_markov_double -= seq.getPrice();
				time_markov_double -= seq.getTime();
				action = seq.getAction();
				actionText = seq.getActionString().trim();
				
				t_markov_action += i + " ";
				t_markov_action_text += String.format("%d", action) + " ";
			
			reward_markov += String.format("%.2f", reward_markov_double) + " "; 
			price_markov += String.format("%.2f", price_markov_double) + " ";
			time_markov += String.format("%.2f", time_markov_double) + " "; 
			action_markov += String.format("%d", action) + " ";
			action_markov_text += actionText + " ";
			}
		}
		
		
		String t_greedy = "";
		String t_greedy_action = "";
		String t_greedy_action_text = "";
		String reward_greedy = "";
		String price_greedy = "";
		String time_greedy = "";
		String action_greedy = "";
		String action_greedy_text = "";
		double reward_greedy_double = 0;
		double price_greedy_double = 0;
		double time_greedy_double = 0;
		for (int i = 0; i < greedyTime; i++) {
//			t_greedy += i + " ";
			ActionSequence seq = i2greedyActionMap.get(i);
			int action = 0;
			String actionText = "NULL";
			if (seq != null) {
				t_greedy += i + " ";
				reward_greedy_double += seq.getReward();
				price_greedy_double -= seq.getPrice();
				time_greedy_double -= seq.getTime();
				action = seq.getAction();
				actionText = seq.getActionString().trim();
				
				t_greedy_action += i + " ";
				t_greedy_action_text += String.format("%d", action) + " ";
			
			reward_greedy += String.format("%.2f", reward_greedy_double) + " "; 
			price_greedy += String.format("%.2f", price_greedy_double) + " ";
			time_greedy += String.format("%.2f", time_greedy_double) + " "; 
			action_greedy += String.format("%d", action) + " "; 
			action_greedy_text += actionText + " ";
			}
		}
		
		String markovRes = t_markov.trim() + "\n"
				+ action_markov.trim() + "\n"
				+ reward_markov.trim() + "\n"
				+ price_markov.trim() + "\n"
				+ time_markov.trim() + "\n";
				
		String greedyRes = t_greedy.trim() + "\n"
				+ action_greedy.trim() + "\n"
				+ reward_greedy.trim() + "\n"
				+ price_greedy.trim() + "\n"
				+ time_greedy.trim() + "\n";
		try {
			FileOutputStream outputStream = new FileOutputStream(MARKOV_OUTPUT_FILE);
			outputStream.write(markovRes.getBytes());
			outputStream.close();
			outputStream = new FileOutputStream(GREEDY_OUTPUT_FILE);
			outputStream.write(greedyRes.getBytes());
			outputStream.close();
			
			outputStream = new FileOutputStream(MARKOV_ACTION_FILE);
			outputStream.write((t_markov_action.trim() + "\n" + t_markov_action_text.trim()).getBytes());
			outputStream.close();
			
			outputStream = new FileOutputStream(GREEDY_ACTION_FILE);
			outputStream.write((t_greedy_action.trim() + "\n" + t_greedy_action_text.trim()).getBytes());
			outputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("t_markov = " + t_markov);
		System.out.println("action_markov = " + action_markov);
		System.out.println("action_markov = " + action_markov_text);
		System.out.println("reward_markov = " + reward_markov);
		System.out.println("price_markov = " + price_markov);
		System.out.println("time_markov = " + time_markov);
		System.out.println();
		System.out.println("t_greedy = " + t_greedy);
		System.out.println("action_greedy = " + action_greedy);
		System.out.println("action_greedy = " + action_greedy_text);
		System.out.println("reward_greedy = " + reward_greedy);
		System.out.println("price_greedy = " + price_greedy);
		System.out.println("time_greedy = " + time_greedy);
	}
	
	private MarkovState[] bothRecovery(int t, MarkovState state) {
		Test.clearMarkovRecordAndActivityFlow();
		state.setId(0);
		state.init();
		
		MarkovState[] stateTemp = new MarkovState[2];
		
		stateTemp[0] = state.clone();
		stateTemp[0].init();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		
		if (bd.getAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp[0].getNextToDoActivity().setX(- stateTemp[0].getNextToDoActivity().getX() 
					+ 1.0 / stateTemp[0].getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp[0].init();
		}

		ActionSequence seq = new ActionSequence(bd.getAction().getOpNumber(), bd.getAction().toFormatString(), 
				bd.getCurrActionCost(), bd.getCurrActionTimeCost(), bd.getCurrActionReward());
		i2markovActionMap.put(t, seq);
		
		stateTemp[1] = state.clone();
		stateTemp[1].init();
		bd.runGreedy();
		
		if (bd.getGreedyAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp[1].getNextToDoActivity().setX(- stateTemp[1].getNextToDoActivity().getX() 
					+ 1.0 / stateTemp[1].getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp[1].init();
		}
		
		ActionSequence seq2 = new ActionSequence(bd.getGreedyAction().getOpNumber(), bd.getGreedyAction().toFormatString(), 
				bd.getGreedyPriceCost(), bd.getGreedyTimeCost(), bd.getGreedyActionReward());
		i2greedyActionMap.put(t, seq2);
		
		markovTime++;
		greedyTime++;
		
		return stateTemp;
	}
	
	private void runMarkovTest1(MarkovState state) {
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovState markovState = state.clone();
		markovState.init();
		int i=1;
		do {
//			System.out.printf("mt=%3d %s\n", i, markovState);
			if (markovState.isFailed()) {
				markovState = Test.markovRecovery(i, markovState, i2markovActionMap);
			}
			if (markovState == null) {
				System.out.println("Code 0x01");
				break;
			}
			if (markovState.isFailed()) {
				System.out.println("Code 0x02");
				break;
			}
			if (markovState.isFinished()) {
//				System.out.printf("mt=%3d %s\n", i+1, markovState);
				System.out.println("Finished. Code 0x03");
				break;
			}
			markovState = markovState.nextSecond();
			i++;
			if (markovState == null) {
				System.out.println("Code 0x04");
				break;
			}
			markovState.init();
			if (!markovState.isFailed() && !markovState.isFinished()
					&& Test.isFault(markovState.getNextToDoActivity())) {
					//&& new Random().nextDouble() > markovState.getNextToDoActivity().getBlindService().getQos().getReliability()) {
//				markovState.getNextToDoActivity().setX(-1);
				markovState = Test.makeFault(markovState);
				markovState.init();
			}
		} while (true);
		markovTime = i + 1;
	}
	
	private void runGreedyTest1(MarkovState state) {
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovState markovState = state.clone();
		markovState.init();
		int i=1;
		do {
//			System.out.printf("mt=%3d %s\n", i, markovState);
			if (markovState.isFailed()) {
				markovState = Test.greedyRecovery(i, markovState, i2greedyActionMap);
			}
			if (markovState == null) {
				System.out.println("Code 0x01");
				break;
			}
			if (markovState.isFailed()) {
				System.out.println("Code 0x02");
				break;
			}
			if (markovState.isFinished()) {
//				System.out.printf("mt=%3d %s\n", i+1, markovState);
				System.out.println("Finished. Code 0x03");
				break;
			}
			markovState = markovState.nextSecond();
			i++;
			if (markovState == null) {
				System.out.println("Code 0x04");
				break;
			}
			markovState.init();
			if (!markovState.isFailed() && !markovState.isFinished() 
					&& Test.isFault(markovState.getNextToDoActivity())) {
//					&& new Random().nextDouble() > markovState.getNextToDoActivity().getBlindService().getQos().getReliability()) {
//				markovState.getNextToDoActivity().setX(-1);
				markovState = Test.makeFault(markovState);
				markovState.init();
			}
		} while (true);
		greedyTime = i + 1;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean compare(MarkovState state) {
		runMarkovTest1(state);
		runGreedyTest1(state);
		Iterator it = i2markovActionMap.entrySet().iterator();
		double markovReward = 0;
		while (it.hasNext()) {
			Map.Entry<Integer, ActionSequence> entry = (Entry<Integer, ActionSequence>) it.next();
			double rewardTemp =  entry.getValue().getReward();
			markovReward += rewardTemp;
			
		}
		
		it = i2greedyActionMap.entrySet().iterator();
		double greedyReward = 0;
		while (it.hasNext()) {
			Map.Entry<Integer, ActionSequence> entry = (Entry<Integer, ActionSequence>) it.next();
			double rewardTemp =  entry.getValue().getReward();
			greedyReward += rewardTemp;
		}
		return (markovReward > greedyReward);
	}
	
	@SuppressWarnings("unused")
	private static void printMap() {
//		System.out.println("\nMakov Action Sequence");
//		test1.printMarkovActionSequence(test1.i2markovActionMap);
//		System.out.println("\nGreedy Action Sequence");
//		test1.printMarkovActionSequence(test1.i2greedyActionMap);
	}
	
	private static void runWithMatlab() {
		Test1 test1 = new Test1();
		MarkovState state = new MarkovState();
		state.getNextToDoActivity().setX(- 1.0 / state.getNextToDoActivity().getBlindService().getQos().getExecTime());
		MarkovState[] states = test1.bothRecovery(0, state);
		System.out.println("\nBegin markov test1........................................\n");
		if (!states[0].isFailed()) {
			test1.runMarkovTest1(states[0]);
		}
		System.out.println("\nBegin greedy test1........................................\n");
		if (!states[1].isFailed()) {
			test1.runGreedyTest1(states[1]);
		}
		
		System.out.println("\nNext is to Matlab");
		test1.writeMatlabFileTT();
	}
	
	@SuppressWarnings("unused")
	private static void comparePercent() {
		MarkovState state = new MarkovState();
		state.getNextToDoActivity().setX(- 1.0 / state.getNextToDoActivity().getBlindService().getQos().getExecTime());
		final int RUN_COUNT = 100;
		int markovGoodCount = 0;
		for (int i = 0; i < RUN_COUNT; i++) {
			System.out.println("Test " + i);
			Test1 test = new Test1();
			if (test.compare(state)) {
				markovGoodCount++;
			}
		}
		
		System.out.println("\n\nTotal run times = " + RUN_COUNT);
		System.out.println("MarkovGoodCount = " + markovGoodCount);
		System.out.println("GreedyGoodCount = " + (RUN_COUNT-markovGoodCount) + "\n\n");
	}
	
	public static void main(String[] args) {
		runWithMatlab();
//		compareParcent();
	}
}
