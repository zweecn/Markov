package com.zw.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.markov.alg.BackwardIteration;

public class GenerateMarkovRecords {
	static Queue<MarkovState> queue = new LinkedList<MarkovState>();
	static Set<MarkovState> stateSet = new HashSet<MarkovState>();
	static List<MarkovRecord> records;
	static List<MarkovRecord> totalRecords;
	static int count;
	static FileWriter writer;
	
	private final static String LOG_FILE_NAME = "E:\\markov_log.txt";
	
	public static void main(String[] args) throws IOException {
		writer = new FileWriter(LOG_FILE_NAME);
		MarkovState state = new MarkovState();
		totalRecords = new ArrayList<MarkovRecord>();
		//state.printFlow();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		stateSet.add(state);
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		count = 0;
		queue.offer(state);
		while (!queue.isEmpty()) {
			state = queue.poll();
			records = Markov.terminate(state);
			printRecord("Terminate");
			records = Markov.noAction(state);
			printRecord("No Action");
			records = Markov.redo(state);
			printRecord("Re do");
			records = Markov.replace(state);  
			printRecord("Replace");
			
			records = Markov.reComposite(state);
			printRecord("Recomposite");
			
		} 
		writer.close();
		BackwardIteration bi = new BackwardIteration(totalRecords);
		bi.getBestChose();
	}

	public static void printRecord(String flag) throws IOException {
		//System.out.println(flag);
		if (records != null && !records.isEmpty()) {
			totalRecords.addAll(records);
			for (MarkovRecord rd : records) {
				System.out.println(String.format("%4s", (++count)) + " " + rd.toString());
				writer.append(String.format("%4s", (count)) + " " + rd.toString() + "\n");
				if (!stateSet.contains(rd.getStateAfter())) {
					queue.offer(rd.getStateAfter());
					stateSet.add(rd.getStateAfter());
				}
			}
		}
	}
}
