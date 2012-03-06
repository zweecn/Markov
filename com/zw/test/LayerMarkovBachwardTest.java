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
		bd.printRecords();
		System.out.printf("\nThe best is: %.2f\n", bd.getBestChose());
		System.out.println("The steps are:");
		for (String s : bd.getResultActions()) {
			System.out.println(s);
		}
		
		//bd.printStateFlow();
	}
}
