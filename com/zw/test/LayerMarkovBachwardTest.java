package com.zw.test;


import com.zw.markov.Markov;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.AtomService;
import com.zw.ws.ServiceQoS;

public class LayerMarkovBachwardTest {
	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();   

		MarkovState state = new MarkovState();
		//state.setGlobalState(Markov.S_NORMAL);
		state.getActivity(0).setX(-1);
		state.init();
		
//		MarkovState state2 = state.clone();
//		state2.getActivity(0).setBlindService(new AtomService(11, new ServiceQoS(1, 1, 1)));
//		state.getActivity(1).setX(-1.1);
//		System.out.println(state2.equals(state));
		
		//state.printFlow();
		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.printRecords();
//		bd.printUtility();
		System.out.printf("\nThe best is: %.2f\n", bd.getMarkovBestUtility());
//		bd.printMap();
//		System.out.println("The steps are:");
//		System.out.println(bd.getResultActions().get(0));
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
