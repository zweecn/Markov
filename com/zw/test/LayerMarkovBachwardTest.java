package com.zw.test;


import com.zw.markov.Markov;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;

public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();   

		MarkovState state = new MarkovState();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		
		//state.printFlow();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		//bd.printRecords();
		//bd.printUtility();
		System.out.printf("\nThe best is: %.2f\n", bd.getBestChose());
		System.out.println("The steps are:");
		System.out.println(bd.getResultActions().get(0));
//		for (int i = 0; i < bd.getResultActions().size(); i++) {
//			System.out.println(bd.getResultActions().get(i));
//		}
		
//		System.out.println(((long) 18290) << 30);
//		System.out.println(Long.MAX_VALUE);
//		System.out.println();
		
		//bd.printStateFlow();
		long endTime=System.currentTimeMillis();
		System.out.println("\nRunTime: "+(endTime - startTime) + " ms");

	}
}
