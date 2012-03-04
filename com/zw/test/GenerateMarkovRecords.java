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

public class GenerateMarkovRecords {
	static Queue<MarkovState> queue = new LinkedList<MarkovState>();
	static Queue<MarkovState> queueTemp = new LinkedList<MarkovState>();
	static Set<MarkovState> stateSet = new HashSet<MarkovState>();
	static List<List<MarkovRecord>> totalLayerRecords = new ArrayList<List<MarkovRecord>>();
	//static List<MarkovRecord> records;
	static List<MarkovRecord> oneLayerRecords;
	static int count;
	static FileWriter writer;
	
	private final static String LOG_FILE_NAME = "markov_output\\markov_log.txt";
	
	public static void main(String[] args) throws IOException {
		writer = new FileWriter(LOG_FILE_NAME);
		MarkovState state = new MarkovState();
		
		//state.printFlow();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		stateSet.add(state);
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		count = 0;
		//queue.offer(state);
		
		queueTemp.offer(state);
		for (int i = 0; i < 3; i++) {
			//System.out.println("Layer:" + i);
			queue.addAll(queueTemp);
			queueTemp.clear();
			oneLayerRecords = new ArrayList<MarkovRecord>();
			while (!queue.isEmpty()) {
				state = queue.poll();
				List<MarkovRecord> records = Markov.noAction(state);
				addToRecords(records);
				records = Markov.terminate(state);
				addToRecords(records);
				records = Markov.redo(state);
				addToRecords(records);
				records = Markov.replace(state);  
				addToRecords(records);
				records = Markov.reComposite(state);
				addToRecords(records);
			}
			totalLayerRecords.add(oneLayerRecords);
		}
		writer.close();
		
		printRecords();
	}

	public static void addToRecords(List<MarkovRecord> records) throws IOException {
		//System.out.println(flag);
		if (records != null && !records.isEmpty()) {
			oneLayerRecords.addAll(records);
			for (MarkovRecord rd : records) {
				queueTemp.offer(rd.getStateAfter());
				writer.append(String.format("%4s", (count++)) + " " + rd.toString() + "\n");
				/*
				//System.out.println(String.format("%4s", (++count)) + " " + rd.toString());
				if (!stateSet.contains(rd.getStateAfter())) {
					queueTemp.offer(rd.getStateAfter());
					stateSet.add(rd.getStateAfter());
				}*/
			}
		}
	}
	
	public static void printRecords() {
		System.out.println("records size=" + totalLayerRecords.size());
		for (int i = 0; i < totalLayerRecords.size(); i++) {
			System.out.println("Layer " + i);
			for (MarkovRecord rd : totalLayerRecords.get(i)) {
				System.out.println(rd.toString());
			}
		}
	}
}
