package com.zw.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.zw.markov.MarkovInfo;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class GenerateMarkovRecords {
	static Queue<MarkovState> queue = new LinkedList<MarkovState>();
	static Set<MarkovState> stateSet = new HashSet<MarkovState>();
	static List<MarkovRecord> records = null;
	static int count;

	public static void main(String[] args) {
		MarkovState state = new MarkovState();
		//state.printFlow();
		state.setCurrGlobalState(MarkovInfo.S_UNKNOWN);
		stateSet.add(state);
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		count = 0;
		do {

			//records = MarkovInfo.terminate(state);
			//printRecord();
			records = MarkovInfo.noAction(state);
			printRecord();
//			if (state.isCurrFailed() && state.getFailedActivity().getRedoCount() < MarkovInfo.MAX_REDO_COUNT) {
//				records = MarkovInfo.redo(state);
//				printRecord();
//			}
//			if (state.isCurrFailed() ) {//&& state.getReplaceNewActivity()!=null && state.getReplaceNewActivity().getReplaceCount() < MarkovInfo.MAX_REPLACE_COUNT)  {
//				//System.out.println("In if.");
//				records = MarkovInfo.replace(state);
//				printRecord();
//			}
			state = queue.poll();
			if (count > 1) {
				System.exit(-1);
			}
		} while (!queue.isEmpty());
	}

	public static void printRecord() {
		if (records != null) {
			for (MarkovRecord rd : records) {
				if (!stateSet.contains(rd.getStateAfter())) {
					queue.offer(rd.getStateAfter());
					stateSet.add(rd.getStateAfter());
					System.out.println(String.format("%4s", (++count)) + " " + rd.toString());
				}
			}
		}
	}
}
