package com.zw.test;

import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.ActivityFlow;

public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		
//		ActivityFlow.printStaticActivityFlow();
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
//		state.getActivity(2).setX();
//		state.setGlobalState(Markov.S_PRICE_UP);
//		state.setFaultActivity(ActivityFlow.getStaticActivity(1).clone());
		
//		state.setFaultActivityState(Markov.S_DELAYED);
		state.init();
		
		long startTime=System.currentTimeMillis(); 
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
//		bd.printRecords();
		bd.printSimpleRecords();
		System.out.printf("The max utility is: %.2f\n", bd.getMarkovBestUtility());
		System.out.printf("First action cost: %.2f\n", bd.getCurrActionCost());
		bd.printStep();
//		bd.printUtility();
//		bd.printMap();
		System.out.printf("Greedy cost: %.2f |", bd.getGreedyCost());
		System.out.println("Greedy action is: " + bd.getGreedyAction());
		long endTime=System.currentTimeMillis();
		System.out.println("\nTotal RunTime: " 
				+ (endTime - startTime) + " ms. (include the runtime of print the record and print steps)");

	}
}
