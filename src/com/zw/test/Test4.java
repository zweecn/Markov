package com.zw.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.zw.Configs;
import com.zw.markov.MarkovRecord;
import com.zw.markov.MarkovState;
import com.zw.ws.ActivityFlow;

public class Test4 {
	
	public Test4() {
		reInitConfigs();
	}
	
	private void reInitConfigs() {
		Configs.CANDIDATE_SERVICE_FILENAME = Configs.CANDIDATE_SERVICE_FILENAME.replace("service", "s4");
		Configs.BILIND_FILENAME = Configs.BILIND_FILENAME.replace("service", "s4");
		Configs.LOG_FILE_NAME = Configs.LOG_FILE_NAME.replace("service", "s4");
		Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("service", "s4");
		Configs.WEAKEN = 1;
		Configs.RANDOM_FAULT =  0.5;  //0.5 is good for test1.;
		Configs.PUNISHMENT_FAILED = 600; //600 is good for test1
		Configs.PUNISHMENT_PER_SECOND = 2; //2 is good for test1
		if (IS_GENERATE_NEW_SERVICE) {
			GenerateWebService.generate(100, 1, 50, 1, 10, 0.1, 0.9);
		}
	}
	
	private static final boolean IS_GENERATE_NEW_SERVICE = false;
	
	private static final int 	GRAPH_COUNT = 95;
	private static final String OUT_FILE = "markov_output\\test4\\seq.txt";
	private static final String PARALLEL_GRAPH_FILE = "graph_bal_";
	private static final String SERIAL_GRAPH_FILE = "graph_seq_";
	private static final String PARALLEL_SERIAL_GRAPH_FILE = "graph_bal_seq_";
	private static final int	X_STEP = 1;
	
	private double[] runTimeSeq = new double[GRAPH_COUNT+1];
	private double[] runTimeBal = new double[GRAPH_COUNT+1];
	private double[] runTimeBalSeq = new double[GRAPH_COUNT+1];
	
	private void run() {
		for (int i = 1; i <= GRAPH_COUNT; i += X_STEP) {
			System.out.println("Do  " + i);
			//SEQ
			initGraphSeq(i);
			MarkovState state = new MarkovState();
			state.getActivity(0).setX(-1);
			state.init();
			runTimeSeq[i] = Test.markovRecovery(state);
			
			//BAL
			initGraphBal(i);
			state = new MarkovState();
			state.getActivity(0).setX(-1);
			state.init();
			runTimeBal[i] = Test.markovRecovery(state);
			
			//BAL_SEQ
			initGraphBalSeq(i);
			state = new MarkovState();
			state.getActivity(0).setX(-1);
			state.init();
			runTimeBalSeq[i] = Test.markovRecovery(state);
		}
	}
	/* LINE 1: SEQ NODE SIZE
	 * LINE 2: SEQ RUNTIME
	 * LINE 3: BAL NODE SIZE
	 * LINE 4: BAL RUNTIME
	 * */
	private String toMatlabString() {
		String res = "";
		// SEQ
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += (i+1) + " ";
		}
		res = res.trim() + "\n";
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += String.format("%d", (int)runTimeSeq[i]) + " ";
		}
		res = res.trim() + "\n";
		
		// BAL
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += (i+1) + " ";
		}
		res = res.trim() + "\n";
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += String.format("%d", (int)runTimeBal[i]) + " ";
		}
		System.out.println(res);
		
		// BAL_SEQ
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += (i+1) + " ";
		}
		res = res.trim() + "\n";
		for (int i = 0; i <= GRAPH_COUNT; i += X_STEP) {
			res += String.format("%d", (int)runTimeBalSeq[i]) + " ";
		}
		System.out.println(res);
		
		return res.trim();
	}
	
	private void writeMatlabFile() {
		try {
			FileOutputStream outputStream = new FileOutputStream(OUT_FILE);
			outputStream.write(toMatlabString().getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void makeGraph(int size) {
		System.out.println("Making graph...");
		String title = "[Graph: First line is NodeCount, Then PrefixNode -> SuffixNode]";
		String blindRes = "[Blind Service: (ActivityNo, BlindServiceNumber]\n";
		Configs.BILIND_FILENAME = "markov_output\\s4\\blind.txt";
		for (int i = 0; i <= size; i += X_STEP) {
			blindRes += i + " " + i + "\n";
			try {
				//SEQ
//				Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_seq_" + (i+1) + ".txt";
				Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.SERIAL_GRAPH_FILE + (i+1));
				FileOutputStream outputStream = new FileOutputStream(Configs.GRAPH_FILENAME);
				String res = title + "\n" + (i+1) + "\n";
				for (int j = 0; j < i; j++) {
					res += j + " -> " + (j+1) + "\n";
				}
				outputStream.write(res.getBytes());
				outputStream.close();		
				
				//BAL
//				Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_bal_" + (i+1) + ".txt";
				Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.PARALLEL_GRAPH_FILE + (i+1));
				outputStream = new FileOutputStream(Configs.GRAPH_FILENAME);
				res = title + "\n" + (i+1) + "\n";
				for (int j = 0; j < i-1; j++) {
					res += "0 -> " + (j+1) + "\n";
				}
				for (int j = 0; j < i-1; j++) {
					res += (j+1) + " -> " + i +  "\n";
				}
				outputStream.write(res.getBytes());
				outputStream.close();		
				
				//SEQ_BAL
//				Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_bal_seq_" + (i+1) + ".txt";
				Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.PARALLEL_SERIAL_GRAPH_FILE + (i+1));
				outputStream = new FileOutputStream(Configs.GRAPH_FILENAME);
				res = title + "\n" + (i+1) + "\n";
				if (i == 1) {
					res += "0 -> 1\n";
				} else if (i == 2) {
					res += "0 -> 1\n" + "1 -> 2\n";
				}
				for (int j = 0; j < i/2; j++) {
					res += j + " -> " + (j+1) + "\n";
				}
				for (int j = i/2; j < i-1; j++) {
					res += (i/2) + " -> " + (j+1) + "\n";
				}
				for (int j = i/2; j < i-1; j++) {
					res += (j+1) + " -> " + i +  "\n";
				}
				outputStream.write(res.getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(Configs.BILIND_FILENAME);
			outputStream.write(blindRes.getBytes());
			outputStream.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("End make.");
	}
	
	public static void initGraphSeq(int i) {
//		Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_seq_" + (i) + ".txt";
		Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.SERIAL_GRAPH_FILE + i);
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovRecord.clear();
	}
	
	public static void initGraphBal(int i) {
//		Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_bal_" + (i) + ".txt";
		Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.PARALLEL_GRAPH_FILE + i);
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovRecord.clear();
	}
	
	public static void initGraphBalSeq(int i) {
//		Configs.GRAPH_FILENAME = "markov_output\\s4\\graph_bal_seq_" + (i) + ".txt";
		Configs.GRAPH_FILENAME = Configs.GRAPH_FILENAME.replace("graph", Test4.PARALLEL_SERIAL_GRAPH_FILE + i);
		ActivityFlow.clearStaticActivityFlow();
		ActivityFlow.initStaticActivityFlow();
		MarkovRecord.clear();
	}
	
	public static void runTest4() {
		makeGraph(Test4.GRAPH_COUNT);
		Test4 test4 = new Test4();
		test4.run();
		test4.writeMatlabFile();
	}
	
	public static void main(String[] args) {
		runTest4();
	}
	
}
