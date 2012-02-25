package com.zw.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MarkovInfo extends Object{
	public static final double EXP = 0.00001;
	public static final double TIME_STEP = Double.MAX_VALUE;
	public static final int MAX_REDO_COUNT = 1;
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
	
	private static long stateid = 0;
	private static Map<BaseAction, Integer> baseActionMap = new HashMap<BaseAction, Integer>();
	
	public static long getNextFreeStateID() {
		return (stateid++); 
	}
	
	private static boolean isActionCanReDo(BaseAction action) {
		if (baseActionMap.get(action) == null) {
			baseActionMap.put(action, new Integer(0));
			return true;
		}
		return (baseActionMap.get(action) < MarkovInfo.MAX_REDO_COUNT);
	}
	
	private static void addReDoActionUsedCount(BaseAction action) {
		baseActionMap.put(action, baseActionMap.get(action)+1);
	}
	
	
	public static List<MarkovRecord> noAction(MarkovState state) {
		if (state == null || state.getCurrGlobalState() == MarkovInfo.S_SUCCEED
				|| state.isCurrFinished()) {
			return null;
		}
		
		BaseAction noAction = new BaseAction(state.getNextToDoActivity().getNumber(), 
				MarkovInfo.A_NO_ACTION, 
				state.getNextToDoActivity().getBlindService().getNumber());
//		if (!isActionCanReDo(noAction)) {
//			return null;
//		}
//		addReDoActionUsedCount(noAction);
		if (state.getCurrGlobalState() == MarkovInfo.S_FAILED) {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(state);
			record.setAction(noAction);
			record.setPosibility(1);
			record.setTimeCost(0);
			record.setPriceCost(0);
			records.add(record);
			//System.out.println("In failed:" + record.toString());
			return records;
		} else {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(MarkovInfo.A_NO_ACTION);
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(0));
			record.setAction(noAction);
			record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
			record.setTimeCost(0);
			record.setPriceCost(0);
			records.add(record);

			record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(1));
			record.setAction(noAction);
			record.setPosibility(1 - state.getNextToDoActivity().getBlindService().getQos().getReliability());
			record.setTimeCost(0);
			record.setPriceCost(0);
			records.add(record);

			return records;
		}
	}
	
	public static List<MarkovRecord> redo(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		
		BaseAction redoAction = new BaseAction(state.getFailedActivity().getNumber(), 
				MarkovInfo.A_RE_DO, state.getFailedActivity().getBlindService().getNumber());
		if (!isActionCanReDo(redoAction)) {
			return null;
		}
		//System.out.println("IS CURRENT FAILED? " +state.isCurrFailed() + " " + state);
		if (state.isCurrFailed()) {
			addReDoActionUsedCount(redoAction);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(MarkovInfo.A_RE_DO);
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(0));
			record.setAction(redoAction);
			record.setPosibility(state.getFailedActivity().getBlindService().getQos().getReliability());
			record.setTimeCost(Math.abs(state.getFailedActivity().getBlindService().getQos().getExecTime()
					*state.getFailedActivity().getX()));
			record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice());
			records.add(record);

			record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(1));
			record.setAction(redoAction);
			record.setPosibility(1 - state.getFailedActivity().getBlindService().getQos().getReliability());
			record.setTimeCost(Math.abs(state.getFailedActivity().getBlindService().getQos().getExecTime()
					*state.getFailedActivity().getX()));
			record.setPriceCost(state.getFailedActivity().getBlindService().getQos().getPrice());
			records.add(record);

			return records;
			
		} else {
			return null;
		}
	}
	
	public static List<MarkovRecord> terminate(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		BaseAction terminateAction = new BaseAction(state.getNextToDoActivity().getNumber(), 
				MarkovInfo.A_TERMINATE, 
				state.getNextToDoActivity().getBlindService().getNumber());
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		MarkovRecord record = new MarkovRecord();
		record.setStateBefore(state);
		MarkovState stateAfter = state.clone();
		stateAfter.setCurrGlobalState(S_FAILED);
		record.setStateAfter(stateAfter);
		record.setAction(terminateAction);
		record.setPosibility(1);
		record.setTimeCost(0);
		record.setPriceCost(0);
		records.add(record);
		return records;
	}
}

