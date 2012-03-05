package com.zw.markov.alg;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.naming.InitialContext;

import com.zw.markov.Markov;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;

public class LayerMarkovBackward {
	private final static String LOG_FILE_NAME = "markov_output\\markov_log.txt";
	Queue<MarkovState> queue;
	Queue<MarkovState> queueTemp;
	List<List<MarkovRecord>> allLayerRecords;
	List<MarkovRecord> oneLayerRecords;
	
	public void makeLayerRecords(MarkovState state) throws IOException {
		allLayerRecords = new ArrayList<List<MarkovRecord>>();
		state.setCurrGlobalState(Markov.S_UNKNOWN);
		state.getActivity(0).setX(-1);
		state.init();
		
		queueTemp = new LinkedList<MarkovState>();
		queueTemp.offer(state);
		for (int i = 0; i < 3; i++) {
			//System.out.println("Layer:" + i);
			queue = queueTemp;
			queueTemp = new LinkedList<MarkovState>();
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
			allLayerRecords.add(oneLayerRecords);
		}
	}

	private int stateSize = 0;
	private int actionSize = 0;
	private void addToRecords(List<MarkovRecord> records) throws IOException {
		if (records != null && !records.isEmpty()) {
			oneLayerRecords.addAll(records);
			for (MarkovRecord rd : records) {
				queueTemp.offer(rd.getStateAfter());
				if (rd.getStateBefore().getId() > stateSize) {
					stateSize = (int) rd.getStateBefore().getId();
				}
				if (rd.getStateAfter().getId() > stateSize) {
					stateSize = (int) rd.getStateAfter().getId();
				}
				if (rd.getAction().getId() > actionSize) {
					actionSize = (int) rd.getAction().getId();
				}
			}
		}
	}

	public void printRecords() {
		System.out.println("records size=" + allLayerRecords.size());
		System.out.println(" StateBefore \t Action \t StateAfter \t Posibility \t Time \t Cost");
		for (int i = 0; i < allLayerRecords.size(); i++) {
			System.out.println("Layer " + i);
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toString());
			}
		}
	}
	
	public void writeRecords() throws IOException {
		System.out.println("records size=" + allLayerRecords.size());
		FileWriter writer = new FileWriter(LOG_FILE_NAME);
		for (int i = 0; i < allLayerRecords.size(); i++) {
			writer.append(i + "\n");
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				System.out.println(rd.toString());
				writer.append(rd.toString() + "\n");
			}
		}
		writer.close();
	}
	
	double[] utility;
	double[][] reward_t;
	double[] reward_n;
	
	public void initReward() {
		 
	}
	
	MarkovState[] states;
	
	private void initStates() {
		int stateSize = 0;
		for (int i = 0; i < allLayerRecords.size(); i++) {
			for (MarkovRecord rd : allLayerRecords.get(i)) {
				
			}
		}
	}
}
