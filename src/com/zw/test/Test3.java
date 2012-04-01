package com.zw.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import com.zw.Configs;
import com.zw.markov.MarkovState;
import com.zw.ws.ActivityFlow;

public class Test3 {

	public static final String OUT_MARKOV = "markov_output\\test3\\markov.txt";
	public static final String OUT_GREEDY = "markov_output\\test3\\greedy.txt";
	public static final int PUNISHMENT_STEP = 60;
	public static final int PUNISHMENT_PER_STEP = 2;
	
	private Map<Integer, ActionSequence> p2markovActionMap = new HashMap <Integer, ActionSequence>();
	private Map<Integer, ActionSequence> p2greedyActionMap = new HashMap <Integer, ActionSequence>();
	private int[] punishment = new int[PUNISHMENT_STEP];
	

	private void writeMatlabFile() {
		String resMarkov = "";
		String resGreedy = "";
		for (int i = 0; i < punishment.length; i++) {
			resMarkov += String.format("%.2f", punishment[i]/ActivityFlow.getTotalPriceCost()) 
					+ " " + p2markovActionMap.get(punishment[i]).toMatlabString() + "\n";
			resGreedy += String.format("%.2f", punishment[i]/ActivityFlow.getTotalPriceCost()) 
					+ " " + p2greedyActionMap.get(punishment[i]).toMatlabString() + "\n";
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(OUT_MARKOV);
			outputStream.write(resMarkov.getBytes());
			outputStream.close();
			outputStream = new FileOutputStream(OUT_GREEDY);
			outputStream.write(resGreedy.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runMarkov() {
		System.out.println("Markov Start...Probability of fault=" + Configs.RANDOM_FAULT + "\n");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();

		for (int i = 1; i < punishment.length; i++) {
			punishment[i] = punishment[i-1] + PUNISHMENT_PER_STEP;
		}
		for (int i = 0; i < punishment.length; i++) {
			Configs.PUNISHMENT_FAILED = punishment[i];
			
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			Test.markovRecovery(punishment[i], stateTemp, p2markovActionMap);
			System.out.println("Begin punishment:" + punishment[i] + " Total:" 
					+ ActivityFlow.getTotalPriceCost() 
					+ " punishment/total=" 
					+ String.format("%.2f", punishment[i]/ActivityFlow.getTotalPriceCost())
					+ " Action=" + p2markovActionMap.get(punishment[i]).getActionString());
		}
		System.out.println("Finished.");
	}

	private void runGreedy() {
		System.out.println("Greedy Start...Probability of fault=" + Configs.RANDOM_FAULT + "\n");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();

		for (int i = 1; i < punishment.length; i++) {
			punishment[i] = punishment[i-1] + PUNISHMENT_PER_STEP;
		}
		for (int i = 0; i < punishment.length; i++) {
			Configs.PUNISHMENT_FAILED = punishment[i];
			
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			Test.greedyRecovery(punishment[i], stateTemp, p2greedyActionMap);
			System.out.println("Begin punishment:" + punishment[i] + " Total:" 
					+ ActivityFlow.getTotalPriceCost() 
					+ " punishment/total=" 
					+ String.format("%.2f", punishment[i]/ActivityFlow.getTotalPriceCost())
					+ " Action=" + p2greedyActionMap.get(punishment[i]).getActionString());
		}
		System.out.println("Finished.");
	}
	
	public static void runTest3() {
		Test3 test3 = new Test3();
		test3.runMarkov();
		test3.runGreedy();
		test3.writeMatlabFile();
	}

	public static void main(String[] args) {
		runTest3();
	}
}
