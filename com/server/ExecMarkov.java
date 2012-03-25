package com.server;

import java.util.Random;

import com.zw.Configs;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;

public class ExecMarkov {
	private MarkovState recovery(MarkovState state) {
		if (state == null) {
			return null;
		}
		state.init();
		state.setId(0);
		MarkovRecord.clear();
		System.out.println("\nFault:" + state);
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		double maxUtility =  bd.getMarkovBestUtility();
		System.out.print("Action:" + bd.getAction());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.printf("Action cost: %.2f \n\n", bd.getCurrActionCost());
		
//		bd.printStep();
//		bd.printUtility();
		return bd.getStateNew();
	}


	private boolean isFault(MarkovState state) {
		Random random = new Random();
		if (random.nextDouble() > Configs.RANDOM_FAULT) {
			return false;
		}
		state.getNextToDoActivity().setX(-1);
		return true;
	}

	private void execFlow() {
		MarkovState state = new MarkovState();
		for (int i = 0; ; i++) {
			System.out.printf("t=%3d %s\n", i, state);
			if (state == null) {
				System.out.println("State is null. Code 0x07");
				break;
			} else if (state.isFinished()) {
				System.out.println("Finished.");
				break;
			}
			state = state.nextSecond();
			if (state == null) {
				System.out.println("State is null. Code 0x09");
				break;
			}
			if (isFault(state)) {
				state = recovery(state);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void normalExec() {
		MarkovState state = new MarkovState();
		for (int i = 0; ; i++) {
			System.out.printf("t=%3d %s\n", i, state);
			if (state == null) {
				System.out.println("State is null. Code 0x07");
				break;
			} else if (state.isFinished()) {
				System.out.println("Finished. Code 0x08");
				break;
			}
			state = state.nextSecond();
			if (state == null) {
				System.out.println("State is null. Code 0x09");
				break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		ExecMarkov execMarkov = new ExecMarkov();
		execMarkov.execFlow();
//		execMarkov.normalExec();
	}
}
