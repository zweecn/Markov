package com.zw.test;

import com.zw.markov.Markov;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;

public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		MarkovState state = new MarkovState();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		
		//state.printFlow();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		//bd.printRecords();
		System.out.printf("\nThe best is: %.2f\n", bd.getBestChose());
		System.out.println("The steps are:");
		for (int i = 0; i < 1; i++) {
			System.out.println(bd.getResultActions().get(i));
		}
		
//		System.out.println(((long) 18290) << 30);
//		System.out.println(Long.MAX_VALUE);
//		System.out.println();
		
		//bd.printStateFlow();
	}
}
