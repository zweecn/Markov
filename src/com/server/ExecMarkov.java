package com.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zw.Configs;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.LayerMarkovBackward;
import com.zw.ws.ActivityFlow;

public class ExecMarkov {
	
	List<ActionSeq> test1MarkovActionSeqs;
	List<ActionSeq> test1GreedyActionSeqs;
	private MarkovState recovery(MarkovState state) {
		if (state == null) {
			return null;
		}
		state.init();
		state.setId(0);
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		System.out.println("\nFault:" + state);
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		//		bd.printRecords();
		bd.printSimpleRecords();
		double maxUtility =  bd.getMarkovBestUtility();
		System.out.print("Markov:" + bd.getAction());
		System.out.printf("Cost: %.2f ", bd.getCurrActionCost());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.println("\nNew state=" + bd.getStateNew());
		System.out.println();

		test1MarkovActionSeqs.add(new ActionSeq(bd.getAction().getOpNumber(), bd.getCurrActionCost(), 
				bd.getCurrActionTimeCost(), bd.getCurrActionReward()));

		return bd.getStateNew();
	}

	private MarkovState greedyRecovery(MarkovState state) {
		if (state == null) {
			return null;
		}
		state.init();
		state.setId(0);
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		System.out.println("\nFault:" + state);
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runGreedy();
		bd.printSimpleRecords();
		double maxUtility =  bd.getGreedyActionReward();
		System.out.print("Markov:" + bd.getGreedyAction());
		System.out.printf("Cost: %.2f ", bd.getGreedyPriceCost());
		if (maxUtility <= - Double.MAX_VALUE) {
			System.out.printf(" Max utility: %s ", "MIN_VALUE");
		} else {
			System.out.printf(" Max utility: %.2f ", maxUtility);
		}
		System.out.println("\nNew state=" + bd.getStateNew());
		System.out.println();

		test1GreedyActionSeqs.add(new ActionSeq(bd.getGreedyAction().getOpNumber(), 
				bd.getGreedyPriceCost(), 
				bd.getGreedyTimeCost(), bd.getGreedyActionReward()));

		return bd.getStateNew();
	}
	
	private MarkovState test3Recovery(MarkovState state, double punishment) {
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
	
	private MarkovState test4Recovery(MarkovState state) {
		if (state == null) {
			return null;
		}
		state.init();
		state.setId(0);
		MarkovRecord.clear();
		long t1 = System.currentTimeMillis();
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		long t2 = System.currentTimeMillis();
		System.out.printf("%d\t%d\t%s\n", ActivityFlow.getActivitySize(), t2 - t1, bd.getAction().toFormatString());
		return bd.getStateNew();
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
				System.out.println();
				break;
			} else if (state.isFinished()) {
				System.out.println("Finished. Code 0x08");
				System.out.println();
				break;
			}
			state = state.nextSecond();
			if (state == null) {
				System.out.println("State is null. Code 0x09");
				System.out.println();
				break;
			}
			
			try {
				Thread.sleep(Configs.SLEEP_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isFault(MarkovState state) {
//		Random random = new Random();
//		if (random.nextDouble() > Configs.RANDOM_FAULT) {
//			return false;
//		}
		state.getNextToDoActivity().setX(-1);
		return true;
	}

//	private double[] makeEvent() {
//		double[] faults = new double[ActivityFlow.getActivitySize()];
//		t1_event = new String[faults.length];
//		t1_greedyReward = new double[faults.length];
//		t1_markovReward = new double[faults.length];
//		Random random = new Random();
//		for (int i = 0; i < faults.length; i++) {
//			if (random.nextBoolean()) {
//				faults[i] = -1;
//				t1_event[i] = "FAILED";
//			} else {
//				faults[i] = 1.5;
//				t1_event[i] = "DELAY";
//			}
//		}
//		return faults;
//	}
	
	private class ActionSeq {
		public ActionSeq(int action, double price, double time, double reward) {
			super();
			this.action = action;
			this.price = price;
			this.time = time;
			this.reward = reward;
		}
		private int action;
		private double price;
		private double time;
		private double reward;
		public int getAction() {
			return action;
		}
		public double getPrice() {
			return price;
		}
		public double getTime() {
			return time;
		}
		public double getReward() {
			return reward;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + action;
			long temp;
			temp = Double.doubleToLongBits(price);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(reward);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(time);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ActionSeq)) {
				return false;
			}
			ActionSeq other = (ActionSeq) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (action != other.action) {
				return false;
			}
			if (Double.doubleToLongBits(price) != Double
					.doubleToLongBits(other.price)) {
				return false;
			}
			if (Double.doubleToLongBits(reward) != Double
					.doubleToLongBits(other.reward)) {
				return false;
			}
			if (Double.doubleToLongBits(time) != Double
					.doubleToLongBits(other.time)) {
				return false;
			}
			return true;
		}
		private ExecMarkov getOuterType() {
			return ExecMarkov.this;
		}
		
	}
	
	private void printTest1_1(String title, List<ActionSeq> sas, int index) {
		System.out.println(title);
		if (sas.isEmpty()) {
			System.out.println("Empty Code 0x05");
			return;
		}
		System.out.print("action" + index + " = [");
		for (int i = 0; i < sas.size(); i++) {
			ActionSeq as = sas.get(i);
			System.out.printf("%d ", as.getAction());
		}
		System.out.println("]");
		
		System.out.print("tc" + index + " = [");
		System.out.printf("%.2f ", sas.get(0).getTime());
		for (int i = 1; i < sas.size(); i++) {
			ActionSeq as = sas.get(i);
			System.out.printf("%.2f ", as.getTime() + sas.get(i-1).getTime());
		}
		System.out.println("]");
		
		System.out.print("pc" + index + " = [");
		System.out.printf("%.2f ", sas.get(0).getPrice());
		for (int i = 1; i < sas.size(); i++) {
			ActionSeq as = sas.get(i);
			System.out.printf("%.2f ", as.getPrice() + sas.get(i-1).getPrice());
		}
		System.out.println("]");
		
		System.out.print("r" + index + " = [");
		System.out.printf("%.2f ", sas.get(0).getReward());
		for (int i = 1; i < sas.size(); i++) {
			ActionSeq as = sas.get(i);
			System.out.printf("%.2f ", as.getReward() + sas.get(i-1).getReward());
		}
		System.out.println("]");
		System.out.println();
	}
	
	private void runMarkovTest1_1() {
		MarkovState markovState = new MarkovState();
		markovState.getNextToDoActivity().setX(-1);
		markovState.init();
		int i=0;
		do {
			System.out.printf("mt=%3d %s\n", i++, markovState);
			if (markovState.isFailed()) {
				markovState = recovery(markovState);
			}
			if (markovState == null) {
				System.out.println("Code 0x01");
				System.out.println();
				break;
			}
			if (markovState.isFailed()) {
				System.out.println("Code 0x02");
				System.out.println();
				break;
			}
			if (markovState.isFinished()) {
				System.out.println("Finished. Code 0x03");
				System.out.println();
				break;
			}
			markovState = markovState.nextSecond();
			if (markovState == null) {
				System.out.println("Code 0x04");
				System.out.println();
				break;
			}
			markovState.init();
			if (!markovState.isFailed() && !markovState.isFinished() 
					&& new Random().nextDouble() > markovState.getNextToDoActivity().getBlindService().getQos().getReliability()) {
				markovState.getNextToDoActivity().setX(-1);
				markovState.init();
			}
		} while (true);
	}
	
	private void runGreedyTest1_1() {
		MarkovState markovState = new MarkovState();
		markovState.getNextToDoActivity().setX(-1);
		markovState.init();
		int i=0;
		do {
			System.out.printf("mt=%3d %s\n", i++, markovState);
			if (markovState.isFailed()) {
				markovState = greedyRecovery(markovState);
			}
			if (markovState == null) {
				System.out.println("Code 0x01");
				System.out.println();
				break;
			}
			if (markovState.isFailed()) {
				System.out.println("Code 0x02");
				System.out.println();
				break;
			}
			if (markovState.isFinished()) {
				System.out.println("Finished. Code 0x03");
				System.out.println();
				break;
			}
			markovState = markovState.nextSecond();
			if (markovState == null) {
				System.out.println("Code 0x04");
				System.out.println();
				break;
			}
			markovState.init();
			if (!markovState.isFailed() && !markovState.isFinished() 
					&& new Random().nextDouble() > markovState.getNextToDoActivity().getBlindService().getQos().getReliability()) {
				markovState.getNextToDoActivity().setX(-1);
				markovState.init();
			}
		} while (true);
	}
	
	private void test1_1() {
		System.out.println("Start...Probability of fault=" + Configs.RANDOM_FAULT);
		test1MarkovActionSeqs = new ArrayList<ExecMarkov.ActionSeq>();
		test1GreedyActionSeqs = new ArrayList<ExecMarkov.ActionSeq>();
		System.out.println("Begin Greedy.");
		runGreedyTest1_1();
		System.out.println("Begin Markov.");
		runMarkovTest1_1();
		printTest1_1("% Greedy Result", test1GreedyActionSeqs, 1);
		printTest1_1("% Markov Result", test1MarkovActionSeqs, 0);
	}
	
	@SuppressWarnings("unused")
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
		System.out.println("Punishment\tAction");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();
		Configs.PUNISHMENT_FAILED = 0;
		for (;Configs.PUNISHMENT_FAILED <= 500;) {
			Configs.PUNISHMENT_FAILED += 5;
			ActivityFlow.clearStaticActivityFlow();
			ActivityFlow.initStaticActivityFlow();
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			test3Recovery(stateTemp, Configs.PUNISHMENT_FAILED);
		}
	}
	
	private void test4() {
		System.out.println("Start...test 4"  + "\n");
		System.out.println("NSize Runtime(ms)  Action");
		MarkovState state = new MarkovState();
		state.getActivity(0).setX(-1);
		state.init();
		
		for (int i=0; i<Configs.GRAPH_FILENAME_S.length; i++) {
			Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME_S[i];
			ActivityFlow.clearStaticActivityFlow();
			ActivityFlow.initStaticActivityFlow();
			MarkovState stateTemp = state.clone();
			stateTemp.init();
			test4Recovery(stateTemp);
		}
	}
	
	
	public static void main(String[] args) {
		ExecMarkov execMarkov = new ExecMarkov();
		switch (Configs.DO_TEST) {
		case 0:
			execMarkov.normalExec();
			break;
		case 1:
//			execMarkov.test1();
			execMarkov.test1_1();
			break;
		case 2:
			execMarkov.test2();
			break;
		case 3:
			execMarkov.test3();
			break;
		case 4:
			execMarkov.test4();
			break;
		default:
			break;
		}
	}
}
