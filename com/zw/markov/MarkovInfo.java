package com.zw.markov;

import java.util.ArrayList;
import java.util.List;
import com.zw.ws.Activity;

public final class MarkovInfo extends Object{
	public static final double EXP = 0.00001;
	public static final double TIME_STEP = Double.MAX_VALUE;
	public static final int MAX_REDO_COUNT = 2;
	public static final int MAX_REPLACE_COUNT = 1;
	
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
	
//	public static List<MarkovRecord> noAction(MarkovState state) {
//		if (state.getNextToDoActivity() == null) {
//			System.out.println("Ignore, Next to do activity is null.");
//			return null;
//		}
//		
//		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//
//		if (state.isFinished()) {
//			MarkovState stateAfter = state.clone();
//			Activity failedActivity = state.getFailedActivity();
//			BaseAction action = new BaseAction(failedActivity.getNumber(), 
//					failedActivity.getBlindService().getNumber(), A_NO_ACTION);
//
//			MarkovRecord record = new MarkovRecord();
//			record.setStateBefore(state);
//			record.setAction(action);
//			record.setStateAfter(stateAfter);
//			record.setPosibility(1);
//			record.setPriceCost(0); // ?
//			record.setTimeCost(0);  // ?
//
//			records.add(record);
//
//			return records;
//		} else {
//			MarkovState stateAfter = state.nextUnknownState();
//			BaseAction action = new BaseAction(state.getNextToDoActivity().getNumber(), 
//					state.getNextToDoActivity().getBlindService().getNumber(), A_NO_ACTION);
//			
//			MarkovRecord record = new MarkovRecord();
//			record.setStateBefore(state);
//			record.setAction(action);
//			record.setStateAfter(stateAfter);
//			record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
//			record.setPriceCost(0); // ?
//			record.setTimeCost(0);  // ?
//			
//			records.add(record);
//
//			stateAfter = state.nextFailedState();
//			
//			record = new MarkovRecord();
//			record.setStateBefore(state);
//			record.setAction(action);
//			record.setStateAfter(stateAfter);
//			record.setPosibility(1 - state.getNextToDoActivity().getBlindService().getQos().getReliability());
//			record.setPriceCost(0); // ?
//			record.setTimeCost(0);  // ?
//
//			records.add(record);
//			return records;
//		}
//	}
//	
//	public static List<MarkovRecord> terminate(MarkovState state) {
//		if (state.getNextToDoActivity() == null) {
//			System.out.println("Terminal, Next to do activity is null.");
//			return null;
//		}
//		
//		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//		
//		MarkovState stateAfter = state.clone();
//		stateAfter.setGlobalState(S_FAILED);
//		
//		BaseAction action = new BaseAction(state.getNextToDoActivity().getNumber(), 
//				state.getNextToDoActivity().getBlindService().getNumber(), MarkovInfo.A_TERMINATE);
//
//		MarkovRecord record = new MarkovRecord();
//		record.setStateBefore(state);
//		record.setAction(action);
//		record.setStateAfter(stateAfter);
//		record.setPosibility(1);
//		record.setPriceCost(0); // ?
//		record.setTimeCost(0);  // ?
//
//		records.add(record);
//
//		return records;
//	}
//	
//	public static List<MarkovRecord> redo(MarkovState state) {
////		if (state.getFailedActivity().getRedoCount() > MAX_REDO_COUNT) {
////			return null;
////		}
//		state.getFailedActivity().addRedoCount();
//		state.getFailedActivity().getBlindService().addRedoCount();
//		
//		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//		
//		MarkovState stateAfter = state.nextReDoUnknownState();
//		BaseAction action = new BaseAction(state.getFailedActivity().getNumber(), 
//				state.getFailedActivity().getBlindService().getNumber(), A_RE_DO);
//		
//		MarkovRecord record = new MarkovRecord();
//		record.setStateBefore(state);
//		record.setAction(action);
//		record.setStateAfter(stateAfter);
//		record.setPosibility(state.getFailedActivity().getBlindService().getQos().getReliability());
//		record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice()); // ?
//		record.setTimeCost(state.getReDoTimeCost());  // ?
////		System.out.println("stateAfter.getReDoTimeCost()=" + stateAfter.getReDoTimeCost());
////		System.out.println("state.getReDoTimeCost()=" + state.getReDoTimeCost());
//		records.add(record);
//		
//		stateAfter = state.nextReDoFailedState();
//		action = new BaseAction(state.getFailedActivity().getNumber(), 
//				state.getFailedActivity().getBlindService().getNumber(), A_RE_DO);
//		
//		record = new MarkovRecord();
//		record.setStateBefore(state);
//		record.setAction(action);
//		record.setStateAfter(stateAfter);
//		record.setPosibility(1 - state.getFailedActivity().getBlindService().getQos().getReliability());
//		record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice()); // ?
//		record.setTimeCost(state.getReDoTimeCost());  // ?
//		
//		records.add(record);
//		return records;
//	}
//	
//	public static List<MarkovRecord> replace(MarkovState state) {		
//		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//		
//		MarkovState stateAfter = state.nextReplaceUnknownState();
//		//System.out.println(state.getReplaceOldActivity());
//		System.out.println("before " + state.toString() + " " + state.getReplaceNewActivity());
//		System.out.println("after  " + stateAfter.toString()+ " " + stateAfter.getReplaceNewActivity());
//		BaseAction action = new BaseAction(state.getReplaceOldActivity().getNumber(), 
//				state.getReplaceOldActivity().getBlindService().getNumber(),
//				state.getReplaceNewActivity().getBlindService().getNumber(), A_REPLACE);
//		
//		MarkovRecord record = new MarkovRecord();
//		record.setStateBefore(state);
//		record.setAction(action);
//		record.setStateAfter(stateAfter);
//		record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
//		record.setPriceCost(state.getReplaceNewActivity().getBlindService().getQos().getPrice()); // ?
//		record.setTimeCost(state.getReplaceNewActivity().getBlindService().getQos().getExecTime());  // ?
//		records.add(record);
//		
//		stateAfter = state.nextReplaceFailedState();
//		//action
//		record = new MarkovRecord();
//		record.setStateBefore(state);
//		record.setAction(action);
//		record.setStateAfter(stateAfter);
//		record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
//		record.setPriceCost(state.getReplaceNewActivity().getBlindService().getQos().getPrice()); // ?
//		record.setTimeCost(state.getReplaceNewActivity().getBlindService().getQos().getExecTime());   // ?
//		records.add(record);
//		
//		return records;
//	}
	
	public static List<MarkovRecord> noAction(MarkovState state) {
		//System.out.println("1 " + state);
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		BaseAction noAction = new BaseAction(-1, MarkovInfo.A_NO_ACTION);
		//System.out.println("2 " + state);
		List<MarkovState> states = state.nextStates(noAction);
		
		//System.out.println("3 " + state);
		
		MarkovRecord record = new MarkovRecord();
		record.setStateBefore(state);
		record.setStateAfter(states.get(0));
		record.setAction(noAction);
		record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
		record.setTimeCost(0);
		record.setPriceCost(0);
		records.add(record);
		
		//System.out.println(state);
		
		record = new MarkovRecord();
		record.setStateBefore(state);
		record.setStateAfter(states.get(1));
		record.setAction(noAction);
		record.setPosibility(1 - state.getNextToDoActivity().getBlindService().getQos().getReliability());
		record.setTimeCost(0);
		record.setPriceCost(0);
		records.add(record);
		
		//System.out.println(state);
		return records;
	}
	
	private static long stateid = 0;
	public static long getNextFreeStateID() {
		return (stateid++); 
	}
}
