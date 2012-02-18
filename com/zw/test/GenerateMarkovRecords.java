package com.zw.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
		
		System.out.println("StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		do {
			//System.out.println("do while, state.getNextToDoActivity().getNumber:" + state.getNextToDoActivity().getNumber());
			List<MarkovRecord> records = MarkovInfo.ignore(state);
			if (records != null) {
				//System.out.println(records.size());
				for (MarkovRecord markovRecord : records) {
					queue.offer(markovRecord.getStateAfter());
				}
			} else {
				System.out.println("\nRecord is empty.");
			}
			state = queue.poll(); 
		} while (!queue.isEmpty());
		
	}

}
