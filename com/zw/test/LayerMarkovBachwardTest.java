package com.zw.test;

import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.ActivityFlow;


public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		
//		ActivityFlow.printStaticActivityFlow();
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();
		
		long startTime=System.currentTimeMillis(); 
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.printRecords();
//		bd.printSimpleRecords();
//		bd.printUtility();
		System.out.printf("The max utility is: %.2f\n", bd.getMarkovBestUtility());
		bd.printStep();
		long endTime=System.currentTimeMillis();
		System.out.println("Total RunTime: " 
				+ (endTime - startTime) + " ms. (include the runtime of print the record and print steps)");

	}
}
