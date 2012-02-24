package com.zw.markov;

import java.util.ArrayList;
import java.util.List;
import com.zw.ws.Activity;

public final class MarkovInfo extends Object{
	public static final double EXP = 0.00001;
	public static final double TIME_STEP = Double.MAX_VALUE;
	public static final int MAX_REDO_COUNT = 2;
	
	public static final int S_UNKNOWN = 1;
	public static final int S_FAILED = 2;
	public static final int S_SUCCEED = 3;
	public static final int S_PRICE_UP = 4;
	public static final int S_DELAYED = 5;

	public static final int A_NO_ACTION = 0x11;
	public static final int A_TERMINATE = 0x12;
	public static final int A_RE_DO = 0x13;
	public static final int A_REPLACE = 0x14;
	public static final int A_RE_COMPOSITE = 0x15;
	
	public static List<MarkovRecord> noAction(MarkovState state) {
		if (state.getNextToDoActivity() == null) {
			System.out.println("Ignore, Next to do activity is null.");
			return null;
		}
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();

		if (state.isFinished()) {
			MarkovState stateAfter = state.clone();
			Activity failedActivity = state.getFailedActivity();
			MarkovAction action = new MarkovAction(failedActivity.getNumber(), 
					failedActivity.getBlindService().getNumber(), A_NO_ACTION);

			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setAction(action);
			record.setStateAfter(stateAfter);
			record.setPosibility(1);
			record.setPriceCost(0); // ?
			record.setTimeCost(0);  // ?

			records.add(record);

			return records;
		} else {
			MarkovState stateAfter = state.nextUnknownState();
			MarkovAction action = new MarkovAction(state.getNextToDoActivity().getNumber(), 
					state.getNextToDoActivity().getBlindService().getNumber(), A_NO_ACTION);
			
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setAction(action);
			record.setStateAfter(stateAfter);
			record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
			record.setPriceCost(0); // ?
			record.setTimeCost(0);  // ?
			
			records.add(record);

			stateAfter = state.nextFailedState();
			
			record = new MarkovRecord();
			record.setStateBefore(state);
			record.setAction(action);
			record.setStateAfter(stateAfter);
			record.setPosibility(1 - state.getNextToDoActivity().getBlindService().getQos().getReliability());
			record.setPriceCost(0); // ?
			record.setTimeCost(0);  // ?

			records.add(record);
			return records;
		}
	}
	
	public static List<MarkovRecord> terminate(MarkovState state) {
		if (state.getNextToDoActivity() == null) {
			System.out.println("Terminal, Next to do activity is null.");
			return null;
		}
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		
		MarkovState stateAfter = state.clone();
		stateAfter.setGlobalState(S_FAILED);
		
		MarkovAction action = new MarkovAction(state.getNextToDoActivity().getNumber(), 
				state.getNextToDoActivity().getBlindService().getNumber(), MarkovInfo.A_TERMINATE);

		MarkovRecord record = new MarkovRecord();
		record.setStateBefore(state);
		record.setAction(action);
		record.setStateAfter(stateAfter);
		record.setPosibility(1);
		record.setPriceCost(0); // ?
		record.setTimeCost(0);  // ?

		records.add(record);

		return records;
	}
	
	public static List<MarkovRecord> redo(MarkovState state) {
//		if (state.getFailedActivity().getRedoCount() > MAX_REDO_COUNT) {
//			return null;
//		}
		state.getFailedActivity().addRedoCount();
		state.getFailedActivity().getBlindService().addRedoCount();
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		
		MarkovState stateAfter = state.nextReDoUnknownState();
		MarkovAction action = new MarkovAction(state.getFailedActivity().getNumber(), 
				state.getFailedActivity().getBlindService().getNumber(), A_RE_DO);
		
		MarkovRecord record = new MarkovRecord();
		record.setStateBefore(state);
		record.setAction(action);
		record.setStateAfter(stateAfter);
		record.setPosibility(state.getFailedActivity().getBlindService().getQos().getReliability());
		record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice()); // ?
		record.setTimeCost(state.getReDoTimeCost());  // ?
//		System.out.println("stateAfter.getReDoTimeCost()=" + stateAfter.getReDoTimeCost());
//		System.out.println("state.getReDoTimeCost()=" + state.getReDoTimeCost());
		records.add(record);
		
		stateAfter = state.nextReDoFailedState();
		action = new MarkovAction(state.getFailedActivity().getNumber(), 
				state.getFailedActivity().getBlindService().getNumber(), A_RE_DO);
		
		record = new MarkovRecord();
		record.setStateBefore(state);
		record.setAction(action);
		record.setStateAfter(stateAfter);
		record.setPosibility(1 - state.getFailedActivity().getBlindService().getQos().getReliability());
		record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice()); // ?
		record.setTimeCost(state.getReDoTimeCost());  // ?
		
		records.add(record);
		return records;
	}
}
