package com.zw.markov;

import java.util.ArrayList;
import java.util.List;

import com.zw.ws.ActivityFlow;

public final class Markov extends Object{

//	public static final int MAX_REDO_COUNT = 1;
//	public static final int MAX_TERMINATE_COUNT = 1;
//	public static final int MAX_REPLACE_COUNT = 1;
//	public static final int MAX_RECOMPOSITE_COUNT = 1;
	
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
	
//	private static Map<BaseAction, Integer> reDoActionMap = new HashMap<BaseAction, Integer>();
//	private static Map<BaseAction, Integer> terminateActionMap = new HashMap<BaseAction, Integer>();
//	private static Map<Integer, Integer> replaceActionMap = new HashMap<Integer, Integer>();
//	private static Map<Integer, Integer> reCompositeActionMap = new HashMap<Integer, Integer>(); 
	
//	private static boolean isReDoActionCanDo(BaseAction action) {
//		if (reDoActionMap.get(action) == null) {
//			reDoActionMap.put(action, new Integer(0));
//			return true;
//		}
//		return (reDoActionMap.get(action) < Markov.MAX_REDO_COUNT);
//	}
//	
//	private static void addReDoActionUsedCount(BaseAction action) {
//		reDoActionMap.put(action, reDoActionMap.get(action)+1);
//	}
//	
//	private static boolean isTerminateActionCanDo(BaseAction action) {
//		if (terminateActionMap.get(action) == null) {
//			terminateActionMap.put(action, new Integer(0));
//			return true;
//		}
//		return (terminateActionMap.get(action) < Markov.MAX_TERMINATE_COUNT);
//	}
//	
//	private static void addTerminateActionUsedCount(BaseAction action) {
//		terminateActionMap.put(action, terminateActionMap.get(action)+1);
//	}
//	
//	private static boolean isReplaceActionCanDo(int replaceActivityNumber) {
//		if (replaceActionMap.get(replaceActivityNumber) == null) {
//			replaceActionMap.put(replaceActivityNumber, 0);
//			return true;
//		}
//		return replaceActionMap.get(replaceActivityNumber) < MAX_REPLACE_COUNT;
//	}
//	
//	private static void addReplaceActionUsedCount(int replaceActivityNumber) {
//		replaceActionMap.put(replaceActivityNumber, 
//				replaceActionMap.get(replaceActivityNumber)+1);
//	}
//	
//	private static boolean isReCompositeActionCanDo(int reCompositeActivityNumber) {
//		if (reCompositeActionMap.get(reCompositeActivityNumber) == null) {
//			reCompositeActionMap.put(reCompositeActivityNumber, 0);
//			return true;
//		}
//		return reCompositeActionMap.get(reCompositeActivityNumber) < MAX_RECOMPOSITE_COUNT;
//	}
//	
//	private static void addRecompositeActionUsedCount(int reCompositeActivityNumber) {
//		reCompositeActionMap.put(reCompositeActivityNumber, 
//				reCompositeActionMap.get(reCompositeActivityNumber)+1);
//	}
	
	public static List<MarkovRecord> noAction(MarkovState state) {
	//	System.out.println("In noAction:" + state);
		//MarkovState state = state.store();
		//MarkovState stateStore = state.store();
		if (state == null || state.getCurrGlobalState() == Markov.S_SUCCEED
				|| state.isCurrFinished()) {
			return null;
		}
		
		BaseAction noAction = new BaseAction(state.getNextToDoActivity().getNumber(), 
				Markov.A_NO_ACTION, 
				state.getNextToDoActivity().getBlindService().getNumber());
		if (state.getCurrGlobalState() == Markov.S_FAILED) {
			//System.out.println("state:" + state);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();

			records.add(new MarkovRecord(state, state, noAction, 1, 0, 0));
			//System.out.println("After noAction:" + state);
			return records;
		} else {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_NO_ACTION);

			records.add(new MarkovRecord(state, states.get(0), noAction, 
					state.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));
			records.add(new MarkovRecord(state, states.get(1), noAction, 
					1 - state.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));
			
			return records;
		}
	}
	
	public static List<MarkovRecord> redo(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		
		BaseAction redoAction = new BaseAction(state.getFailedActivity().getNumber(), 
				Markov.A_RE_DO, state.getFailedActivity().getBlindService().getNumber());
//		if (!isReDoActionCanDo(redoAction)) {
//			return null;
//		}
		if (state.isCurrFailed()) {
//			addReDoActionUsedCount(redoAction);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_RE_DO);

			records.add(new MarkovRecord(state, states.get(0), redoAction, 
					state.getFailedActivity().getBlindService().getQos().getReliability(),
					state.getFailedActivity().getBlindService().getQos().getPrice(), 
					Math.abs(state.getFailedActivity().getBlindService().getQos().getExecTime()*state.getFailedActivity().getX())));
			records.add(new MarkovRecord(state, states.get(1), redoAction,
					1 - state.getFailedActivity().getBlindService().getQos().getReliability(), 
					state.getFailedActivity().getBlindService().getQos().getPrice(), 
					Math.abs(state.getFailedActivity().getBlindService().getQos().getExecTime()*state.getFailedActivity().getX())));
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
//		if (!isTerminateActionCanDo(terminateAction)) {
//			return null;
//		}
//		addTerminateActionUsedCount(terminateAction);
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		MarkovState stateAfter = state.clone();
		stateAfter.setCurrGlobalState(S_FAILED);
		records.add(new MarkovRecord(state, stateAfter, terminateAction, 1, 0, 0));
		return records;
	}
	
	public static List<MarkovRecord> replace(MarkovState state) {
		if (state == null || state.isCurrFinished()) {
			return null;
		}
		
//		if (!isReplaceActionCanDo(state.getFailedActivity().getNumber())) {
//			return null;
//		}
		if (state.isCurrFailed()) {
//			addReplaceActionUsedCount(state.getFailedActivity().getNumber());
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_REPLACE);
			if (states == null) {
				return null;
			}
			
			ReplaceAction replaceAction = new ReplaceAction(state.getFailedActivity().getNumber(), 
					Markov.A_REPLACE, state.getFailedActivity().getBlindService().getNumber(),
					state.getReplaceNewService().getNumber());
		
//			records.add(new MarkovRecord(state, states.get(0), replaceAction, state.getFreeServiceFinder().getPosibility(),
//					state.getFreeServiceFinder().getPriceCost(), state.getFreeServiceFinder().getTimeCost()));
//			records.add(new MarkovRecord(state, states.get(1), replaceAction, 1 - state.getFreeServiceFinder().getPosibility(),
//					state.getFreeServiceFinder().getPriceCost(), state.getFreeServiceFinder().getTimeCost()));

			records.add(new MarkovRecord(state, states.get(0), replaceAction, state.getReplaceNewService().getQos().getReliability(),
					state.getReplaceNewService().getQos().getPrice(), state.getReplaceNewService().getQos().getExecTime()));
			records.add(new MarkovRecord(state, states.get(1), replaceAction, 1 - state.getReplaceNewService().getQos().getReliability(),
					state.getReplaceNewService().getQos().getPrice(), state.getReplaceNewService().getQos().getExecTime()));
			
			return records;
			
		} else {
			return null;
		}
	}
	
	public static List<MarkovRecord> reComposite(MarkovState state) {
		MarkovState stateTemp = state.store();
		//System.out.println("1---stateStore=" + stateStore);
		if (stateTemp == null || stateTemp.isCurrFinished()) {
			return null;
		}
		
//		if (!isReCompositeActionCanDo(state.getFailedActivity().getNumber())) {
//			return null;
//		}
		if (stateTemp.isCurrFailed()) {
//			addRecompositeActionUsedCount(state.getFailedActivity().getNumber());
			//System.out.println("Before ReCom:" + stateStore);
//			System.out.println("1---state=" + state);
//			System.out.println("1---stateStore=" + stateStore);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			
//			ReCompositeAction reCompositeAction = // (ReCompositeAction) stateStore.getReCompositeAction();
//					(ReCompositeAction) ActivityFlow.recomposite(stateStore);
//			System.out.println("++++" + stateStore);
			List<MarkovState> states = stateTemp.nextStates(Markov.A_RE_COMPOSITE);
//			System.out.println("2---state=" + state);
//			System.out.println("2---stateStore=" + stateStore);
			if (states == null) {
				//System.out.println("Here, states are=" + states);
				return null;
			}
//			ReCompositeAction reCompositeAction = new ReCompositeAction(state.getFailedActivity().getNumber(),
//					Markov.A_RE_COMPOSITE, state.getFailedActivity().getBlindService().getNumber());
			ReCompositeAction reCompositeAction =  (ReCompositeAction) stateTemp.getReCompositeAction();
//						(ReCompositeAction) ActivityFlow.recomposite(stateStore);
//			reCompositeAction.setOldNewReplaceServiceMap(state.getReCompositor().getOldNewReplaceMap());
	
//			records.add(new MarkovRecord(state, states.get(0), reCompositeAction, state.getReCompositor().getPosibility(),
//					state.getReCompositor().getPriceCost(), state.getReCompositor().getTimeCost()));
//			records.add(new MarkovRecord(state, states.get(1), reCompositeAction, 1- state.getReCompositor().getPosibility(),
//					state.getReCompositor().getPriceCost(), state.getReCompositor().getTimeCost()));

//			System.out.println("After ReCom, stateStore" + stateStore);
//			System.out.println("state=" + state);
			records.add(new MarkovRecord(state, states.get(0), reCompositeAction, 0,
					0, 0));
			records.add(new MarkovRecord(state, states.get(1), reCompositeAction, 0,
					0, 0));
			
			return records;
			
		} else {
			return null;
		}
	}
}

