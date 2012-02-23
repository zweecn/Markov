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
		state.setGlobalState(MarkovInfo.S_UNKNOWN);
		state.setCurrentTimeCost(0);
		stateSet.add(state);
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		count = 0;
		do {

			records = MarkovInfo.terminate(state);
			printRecord();
			records = MarkovInfo.noAction(state);
			printRecord();
			if (state.isFailed()) {
				//System.out.println("In if");
				records = MarkovInfo.redo(state);
				//System.out.println("After redo, record.size=" + records.size());
				printRecord();
			}
			state = queue.poll();
		} while (!queue.isEmpty());
	}

	public static void printRecord() {
		if (records != null) {
			for (MarkovRecord markovRecord : records) {
				if (!stateSet.contains(markovRecord.getStateAfter())) {
					queue.offer(markovRecord.getStateAfter());
					stateSet.add(markovRecord.getStateAfter());
					System.out.println((++count) + " " + markovRecord.toString());
				}
			}
		}
	}
}
