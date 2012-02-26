package com.zw.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Markov extends Object{
	public static final double EXP = 0.00001;
	public static final double TIME_STEP = Double.MAX_VALUE;
	public static final int MAX_REDO_COUNT = 1;
	public static final int MAX_TERMINATE_COUNT = 1;
	public static final int MAX_REPLACE_COUNT = 1;
	public static final int MAX_RECOMPOSITE_COUNT = 1;
	
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
	
	private static Map<BaseAction, Integer> reDoActionMap = new HashMap<BaseAction, Integer>();
	private static Map<BaseAction, Integer> terminateActionMap = new HashMap<BaseAction, Integer>();
	private static Map<Integer, Integer> replaceActionMap = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> reCompositeActionMap = new HashMap<Integer, Integer>(); 
	
	private static boolean isReDoActionCanDo(BaseAction action) {
		if (reDoActionMap.get(action) == null) {
			reDoActionMap.put(action, new Integer(0));
			return true;
		}
		return (reDoActionMap.get(action) < Markov.MAX_REDO_COUNT);
	}
	
	private static void addReDoActionUsedCount(BaseAction action) {
		reDoActionMap.put(action, reDoActionMap.get(action)+1);
	}
	
	private static boolean isTerminateActionCanDo(BaseAction action) {
		if (terminateActionMap.get(action) == null) {
			terminateActionMap.put(action, new Integer(0));
			return true;
		}
		return (terminateActionMap.get(action) < Markov.MAX_TERMINATE_COUNT);
	}
	
	private static void addTerminateActionUsedCount(BaseAction action) {
		terminateActionMap.put(action, terminateActionMap.get(action)+1);
	}
	
	private static boolean isReplaceActionCanDo(int replaceActivityNumber) {
		if (replaceActionMap.get(replaceActivityNumber) == null) {
			replaceActionMap.put(replaceActivityNumber, 0);
			return true;
		}
		return replaceActionMap.get(replaceActivityNumber) < MAX_REPLACE_COUNT;
	}
	
	private static void addReplaceActionUsedCount(int replaceActivityNumber) {
		replaceActionMap.put(replaceActivityNumber, 
				replaceActionMap.get(replaceActivityNumber)+1);
	}
	
	private static boolean isReCompositeActionCanDo(int reCompositeActivityNumber) {
		if (reCompositeActionMap.get(reCompositeActivityNumber) == null) {
			reCompositeActionMap.put(reCompositeActivityNumber, 0);
			return true;
		}
		return reCompositeActionMap.get(reCompositeActivityNumber) < MAX_RECOMPOSITE_COUNT;
	}
	
	private static void addRecompositeActionUsedCount(int reCompositeActivityNumber) {
		reCompositeActionMap.put(reCompositeActivityNumber, 
				reCompositeActionMap.get(reCompositeActivityNumber)+1);
	}
	
	public static List<MarkovRecord> noAction(MarkovState state) {
		if (state == null || state.getCurrGlobalState() == Markov.S_SUCCEED
				|| state.isCurrFinished()) {
			return null;
		}
		
		BaseAction noAction = new BaseAction(state.getNextToDoActivity().getNumber(), 
				Markov.A_NO_ACTION, 
				state.getNextToDoActivity().getBlindService().getNumber());
		if (state.getCurrGlobalState() == Markov.S_FAILED) {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(state);
			record.setAction(noAction);
			record.setPosibility(1);
			record.setTimeCost(0);
			record.setPriceCost(0);
			records.add(record);
			return records;
		} else {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_NO_ACTION);
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
				Markov.A_RE_DO, state.getFailedActivity().getBlindService().getNumber());
		if (!isReDoActionCanDo(redoAction)) {
			return null;
		}
		if (state.isCurrFailed()) {
			addReDoActionUsedCount(redoAction);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_RE_DO);
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
				Markov.A_TERMINATE, 
				state.getNextToDoActivity().getBlindService().getNumber());
		if (!isTerminateActionCanDo(terminateAction)) {
			return null;
		}
		addTerminateActionUsedCount(terminateAction);
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
	
	public static List<MarkovRecord> replace(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		
		if (!isReplaceActionCanDo(state.getFailedActivity().getNumber())) {
			return null;
		}
		if (state.isCurrFailed()) {
			addReplaceActionUsedCount(state.getFailedActivity().getNumber());
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_REPLACE);
			if (states == null) {
				return null;
			}
			ReplaceAction replaceAction = new ReplaceAction(state.getFailedActivity().getNumber(), 
					Markov.A_REPLACE, state.getFailedActivity().getBlindService().getNumber(),
					state.getReplaceNewService().getNumber());
			
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(0));
			record.setAction(replaceAction);
			record.setPosibility(state.getFreeServiceFinder().getPosibility());
			record.setTimeCost(state.getFreeServiceFinder().getTimeCost());
			record.setPriceCost(state.getFreeServiceFinder().getPriceCost());
			records.add(record);

			record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(1));
			record.setAction(replaceAction);
			record.setPosibility(1 - state.getFreeServiceFinder().getPosibility());
			record.setTimeCost(state.getFreeServiceFinder().getTimeCost());
			record.setPriceCost(state.getFreeServiceFinder().getPriceCost());
			records.add(record);

			return records;
			
		} else {
			return null;
		}
	}
	
	public static List<MarkovRecord> reComposite(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		
		if (!isReCompositeActionCanDo(state.getFailedActivity().getNumber())) {
			return null;
		}
		if (state.isCurrFailed()) {
			addRecompositeActionUsedCount(state.getFailedActivity().getNumber());
			
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_RE_COMPOSITE);
			if (states == null) {
				return null;
			}
			ReCompositeAction reCompositeAction = new ReCompositeAction(state.getFailedActivity().getNumber(),
					Markov.A_RE_COMPOSITE, state.getFailedActivity().getBlindService().getNumber());
			
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(0));
			record.setAction(reCompositeAction);
			record.setPosibility(state.getReCompositor().getPosibility());
			record.setTimeCost(state.getReCompositor().getTimeCost());
			record.setPriceCost(state.getReCompositor().getPriceCost());
			records.add(record);

			record = new MarkovRecord();
			record.setStateBefore(state);
			record.setStateAfter(states.get(1));
			record.setAction(reCompositeAction);
			record.setPosibility(1- state.getReCompositor().getPosibility());
			record.setTimeCost(state.getReCompositor().getTimeCost());
			record.setPriceCost(state.getReCompositor().getPriceCost());
			records.add(record);

			return records;
			
		} else {
			return null;
		}
	}
}
