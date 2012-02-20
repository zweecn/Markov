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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MarkovState state = new MarkovState();
		//state.printFlow();
		state.setGlobalState(MarkovInfo.S_UNKNOWN);
		state.setCurrentTimeCost(0);
		
		Queue<MarkovState> queue = new LinkedList<MarkovState>();
		Set<MarkovState> stateSet = new HashSet<MarkovState>();
		stateSet.add(state);
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		int i = 0;
		int whileI = 0;
		do {
			List<MarkovRecord> records = MarkovInfo.ignore(state);
			//System.out.println("whileI:" + (++whileI));
			if (records != null) {
				for (MarkovRecord markovRecord : records) {
					if (!stateSet.contains(markovRecord.getStateAfter())) {
						queue.offer(markovRecord.getStateAfter());
						stateSet.add(markovRecord.getStateAfter());
						System.out.println((++i) + " " + markovRecord.toString());
					}
				}
			} else {
				//System.out.println("\nRecord is empty.");
				//break;
			}
			state = queue.poll(); 
		} while (!queue.isEmpty());
		
	}

}
