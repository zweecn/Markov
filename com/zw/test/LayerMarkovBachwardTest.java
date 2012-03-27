package com.zw.test;

import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;

public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		
		/*********************************************************************************
		 * Gen the fault state.
		 *********************************************************************************/
		MarkovState state = new MarkovState();
		state.getActivity(5).setX(-1);
		state.init();
		
		/*********************************************************************************
		 * Begin Markov test.
		 *********************************************************************************/
		long startTime=System.currentTimeMillis(); 
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
//		bd.printRecords();
//		bd.printSimpleRecords();
		double maxUtility =  bd.getMarkovBestUtility();
		System.out.print("Markov:" + bd.getAction());
		System.out.printf("Cost: %.2f ", bd.getCurrActionCost());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.println("\n");
//		bd.printUtility();
//		bd.printMap();
		/*********************************************************************************
		 * End Markov test. Begin Greedy.
		 *********************************************************************************/
		bd.runGreedy();
		System.out.print("Greedy:" + bd.getGreedyAction());
		System.out.printf("Cost: %.2f\n", bd.getGreedyCost());
		/*********************************************************************************
		 * End greedy.
		 *********************************************************************************/
		
		long endTime=System.currentTimeMillis();
		System.out.println("\nTotal RunTime: " 
				+ (endTime - startTime) + " ms. (include the runtime of print the record and print steps)");

	}
}
