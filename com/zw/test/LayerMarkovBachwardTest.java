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
		
		LayerMarkovBackward bd = new LayerMarkovBackward();
		bd.generateLayerRecords(state);
		bd.printRecords();
		
		bd.printTree();
	}
}
