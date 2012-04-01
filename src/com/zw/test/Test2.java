package com.zw.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.ws.ActivityFlow;
import com.zw.markov.alg.*;

public class Test2 {
	public Test2() {
		init();
	}
	
	public static final String[] OUTPUT_FILE = {
		"markov_output\\test2\\0markov.txt",
		"markov_output\\test2\\1.txt",
		"markov_output\\test2\\2.txt",
		"markov_output\\test2\\3.txt",
		"markov_output\\test2\\4.txt"
	};
	
	public static final int reduceLayerTestCount = 5;
	private int[] time = new int[reduceLayerTestCount];
	@SuppressWarnings("unchecked")
	private Map<Integer, ActionSequence>[] i2seqMaps = new Map[reduceLayerTestCount];
	private List<MarkovSecquence>[] markovSecquenceArray = new List[reduceLayerTestCount];
	
	private void init() {
		for (int i = 0; i < reduceLayerTestCount; i++) {
			time[i] = 0;
			i2seqMaps[i] = new HashMap<Integer, ActionSequence>();
			markovSecquenceArray[i] = new ArrayList<MarkovSecquence>();
		}
	}
	
	private MarkovState[] mutiRecovery(int t, MarkovState state) {
		Test.clearMarkovRecordAndActivityFlow();
		state.setId(0);
		state.init();
		
		MarkovState[] stateTemp = new MarkovState[reduceLayerTestCount];		
		LayerMarkovBackward bd = new LayerMarkovBackward(state);
		bd.runMarkov();
		
		stateTemp[0] = state.clone();
		stateTemp[0].init();
		if (bd.getAction().getOpNumber() != Markov.A_TERMINATE) {
			stateTemp[0].getNextToDoActivity().setX(- stateTemp[0].getNextToDoActivity().getX() 
					+ 1.0 / stateTemp[0].getNextToDoActivity().getBlindService().getQos().getExecTime());
			stateTemp[0].init();
		}

		ActionSequence seq = new ActionSequence(bd.getAction().getOpNumber(), bd.getAction().toFormatString(), 
				bd.getCurrActionCost(), bd.getCurrActionTimeCost(), bd.getCurrActionReward());
		i2seqMaps[0].put(t, seq);
		time[0]++;
		
		for (int i = 1; i < stateTemp.length; i++) {
			stateTemp[i] = state.clone();
			stateTemp[i].init();
			bd.init(i);
			bd.runMarkov();
			
			if (bd.getAction().getOpNumber() != Markov.A_TERMINATE) {
				stateTemp[i].getNextToDoActivity().setX(- stateTemp[i].getNextToDoActivity().getX() 
						+ 1.0 / stateTemp[i].getNextToDoActivity().getBlindService().getQos().getExecTime());
				stateTemp[i].init();
			}

			ActionSequence seqi = new ActionSequence(bd.getAction().getOpNumber(), bd.getAction().toFormatString(), 
					bd.getCurrActionCost(), bd.getCurrActionTimeCost(), bd.getCurrActionReward());
			i2seqMaps[i].put(t, seqi);
			time[i]++;
		}
		
		return stateTemp;
	}
	
	private void runReduceLayerTest2(MarkovState state, int reduceLayerSize) {
		MarkovRecord.clear();
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovState markovState = state.clone();
		markovState.init();
		int i=1;
		do {
//			System.out.printf("mt=%3d %s\n", i, markovState);
			if (markovState.isFailed()) {
				markovState = Test.reduceLayerRecovery(i, markovState, 
						i2seqMaps[reduceLayerSize], reduceLayerSize);
			}
			if (markovState == null) {
				System.out.println("Code 0x01");
				break;
			}
			if (markovState.isFailed()) {
				System.out.println("Code 0x02");
				break;
			}
			if (markovState.isFinished()) {
//				System.out.printf("mt=%3d %s\n", i+1, markovState);
				System.out.println("Finished. Code 0x03");
				break;
			}
			markovState = markovState.nextSecond();
			i++;
			if (markovState == null) {
				System.out.println("Code 0x04");
				break;
			}
			markovState.init();
			if (!markovState.isFailed() && !markovState.isFinished()
					&& Test.isFault(markovState.getNextToDoActivity())) {
					//&& new Random().nextDouble() > markovState.getNextToDoActivity().getBlindService().getQos().getReliability()) {
//				markovState.getNextToDoActivity().setX(-1);
				markovState = Test.makeFault(markovState);
				markovState.init();
			}
		} while (true);
		time[reduceLayerSize] = i + 1;
	}
	
	private void printMap() {
		for (int i = 0; i < reduceLayerTestCount; i++) {
			System.out.println("Reduce Layer = " + i);
			Test.printMarkovActionSequence(i2seqMaps[i]);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeMatlabFileTT() {
		for (int i = 0; i < reduceLayerTestCount; i++) {
			markovSecquenceArray[i].clear();
			Iterator it = i2seqMaps[i].entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, ActionSequence> entry = (Map.Entry<Integer, ActionSequence>) it.next();
				int t = entry.getKey();
				ActionSequence value = entry.getValue();
				markovSecquenceArray[i].add(new MarkovSecquence(t, value));
			}
			Collections.sort(markovSecquenceArray[i]);
			
			try {
				String res = "";
				for (int j = 0; j < markovSecquenceArray[i].size(); j++) {
					res += markovSecquenceArray[i].get(j).toMatlabString() + "\n";
				}
				FileOutputStream outputStream = new FileOutputStream(OUTPUT_FILE[i]);
				outputStream.write(res.getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void runReduceLayerTest() {
		Test2 test2 = new Test2();
		MarkovState state = new MarkovState();
		state.getNextToDoActivity().setX(- 1.0 / state.getNextToDoActivity().getBlindService().getQos().getExecTime());
		MarkovState[] stateArray = test2.mutiRecovery(0, state);
		System.out.println("\nBegin markov test2........................................\n");
		
		for (int i = 0; i < stateArray.length; i++) {
			if (!stateArray[i].isFailed()) {
				test2.runReduceLayerTest2(stateArray[i], i);
			}
		}
		test2.printMap();
		
		System.out.println("\nNext is to Matlab");
		test2.writeMatlabFileTT();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runReduceLayerTest();
	}
}
