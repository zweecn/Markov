package com.server;

import java.util.Random;

import com.zw.Configs;
import com.zw.markov.BaseAction;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.ActivityFlow;

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
//		bd.printRecords();
//		bd.printStep();
//		bd.printUtility();
		return bd.getStateNew();
	}

	private MarkovState formatRecovery(MarkovState state, double punishment) {
		if (state == null) {
			return null;
		}
		state.init();
		state.setId(0);
		MarkovRecord.clear();
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		System.out.printf("%4.0f  %s\n", punishment, bd.getAction().toFormatString());
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

	private void test1() {
		System.out.println("Start...Probability of fault=" + Configs.RANDOM_FAULT);
		MarkovState state = new MarkovState();
		for (int i = 0; ; i++) {
			if (state != null) {
				state.init();
			}
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
			} else if (state.isFinished()) {
				System.out.println("Finished.");
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
			if (state != null) {
				state.init();
			}
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
				Thread.sleep(Configs.SLEEP_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void test2() {
		System.out.println("Start...Probability of fault=" + Configs.RANDOM_FAULT + "\n");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();
		Configs.REDUCE_LAYER_SIZE = 0;
		for (int i = 0; i <= MarkovRecord.getMaxLayerSize() ; i++) {
			Configs.REDUCE_LAYER_SIZE = i;
			if (Configs.REDUCE_LAYER_SIZE == 0) {
				System.out.print("Layer size:" + "MAX_LAYER");
			} else {
				System.out.print("Layer size:" + Configs.REDUCE_LAYER_SIZE);
			}
			ActivityFlow.clearStaticActivityFlow();
			ActivityFlow.initStaticActivityFlow();
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			recovery(stateTemp);
			try {
				Thread.sleep(Configs.SLEEP_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void test3() {
		System.out.println("Start...Probability of fault=" + Configs.RANDOM_FAULT + "\n");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();
		Configs.PUNISHMENT_FAILED = 0;
		for (;Configs.PUNISHMENT_FAILED <= 1000;) {
			Configs.PUNISHMENT_FAILED += 5;
			ActivityFlow.clearStaticActivityFlow();
			ActivityFlow.initStaticActivityFlow();
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			formatRecovery(stateTemp, Configs.PUNISHMENT_FAILED);
//			try {
//				Thread.sleep(Configs.SLEEP_SECONDS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	public static void main(String[] args) {
		ExecMarkov execMarkov = new ExecMarkov();
		switch (Configs.DO_TEST) {
		case 0:
			execMarkov.normalExec();
			break;
		case 1:
			execMarkov.test1();
			break;
		case 2:
			execMarkov.test2();
			break;
		case 3:
			execMarkov.test3();
			break;
		default:
			break;
		}
	}
}
