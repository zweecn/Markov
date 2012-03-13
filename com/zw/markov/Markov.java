package com.zw.markov;

import java.util.*;

import com.zw.ws.Activity;
import com.zw.ws.AtomService;
import com.zw.ws.FreeServiceFinder;
import com.zw.ws.FreeServiceFinderImp;
import com.zw.ws.ReCompositor;
import com.zw.ws.ReCompositorImpl;

public final class Markov extends Object{

	public static final int MAX_REDO_COUNT = 1;
	public static final int MAX_TERMINATE_COUNT = 1;
	public static final int MAX_REPLACE_COUNT = 1;
	public static final int MAX_RECOMPOSITE_COUNT = 1;

	public static final int S_NORMAL = 1;
	public static final int S_FAILED = 2;
	public static final int S_SUCCEED = 3;
	public static final int S_PRICE_UP = 4;
	public static final int S_DELAYED = 5;

	public static final int A_NO_ACTION = 0x11;
	public static final int A_TERMINATE = 0x12;
	public static final int A_RE_DO = 0x13;
	public static final int A_REPLACE = 0x14;
	public static final int A_RE_COMPOSITE = 0x15;

	/*private static Map<BaseAction, Integer> reDoActionMap = new HashMap<BaseAction, Integer>();
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
	}*/

	public static MarkovAction noAction(MarkovState state) {
		if (state.isFinished()) {
			return null;
		}
		BaseAction action = new BaseAction(state.getNextToDoActivity().getNumber(), 
				Markov.A_NO_ACTION, 
				state.getNextToDoActivity().getBlindService().getNumber());
		return action;
	}
	
	public static MarkovAction terminate(MarkovState state) {
		BaseAction action = new BaseAction(state.getNextToDoActivity().getNumber(), 
				Markov.A_TERMINATE, state.getNextToDoActivity().getBlindService().getNumber());
		state.setFailed(true);
		return action;
	}
	
	public static MarkovAction redo(MarkovState state) {
		BaseAction action = new BaseAction(state.getFaultActivity().getNumber(), 
				Markov.A_RE_DO, state.getFaultActivity().getBlindService().getNumber());
		Activity faultActivity = state.getFaultActivity();
		faultActivity.setX(0);
		state.setActivity(faultActivity);
		state.init();
		return action; 
	}
	
	public static MarkovAction replace(MarkovState state) {
		FreeServiceFinder freeServiceFinder = new FreeServiceFinderImp();
		Activity faultActivity = state.getFaultActivity();
		int faultServiceNumber = faultActivity.getBlindService().getNumber();
		AtomService freeService = freeServiceFinder.nextFreeService(faultActivity);
		if (freeService == null) {
			state = null;
			return null;
		}
		faultActivity.setBlindService(freeService);
		freeServiceFinder.setServiceUsed(freeService);
		faultActivity.setX(0);
		state.setActivity(faultActivity);
		state.init();
		ReplaceAction action = new ReplaceAction(state.getFaultActivity().getNumber(), 
				Markov.A_REPLACE, faultServiceNumber,
				freeService.getNumber());
		action.setFreeServiceFinder(freeServiceFinder);
//		System.out.println("action=" + action);
//		System.out.println("acti=" + faultActivity.getBlindService().getNumber());
//		System.out.println("state=" + state);
		return action;
	}
	
	public static MarkovAction reComposite(MarkovState state) {
		ReCompositor reCompositor = new ReCompositorImpl();
		state = reCompositor.recomposite(state);
		if (state == null) {
			return null;
		}
 		//System.out.println("state in recomposite=" + state);
		ReCompositeAction reCompositeAction = (ReCompositeAction) ((ReCompositorImpl) reCompositor).getReComAction();
		reCompositeAction.setReCompositor(reCompositor);
		return reCompositeAction;
	}
	
	
	
	public static List<MarkovRecord> noActionRecords(MarkovState state) {
		MarkovState stateAfter = state.clone();
		BaseAction noAction = (BaseAction) Markov.noAction(stateAfter);
		//stateAfter.init();
		if (stateAfter == null || stateAfter.getGlobalState() == Markov.S_SUCCEED
				|| stateAfter.isFinished() || MarkovRecord.hasStateAction(stateAfter, noAction)) {
			return null;
		}

//		BaseAction noAction = new BaseAction(stateAfter.getNextToDoActivity().getNumber(), 
//				Markov.A_NO_ACTION, 
//				stateAfter.getNextToDoActivity().getBlindService().getNumber());
		List<MarkovRecord> records = null;
		if (stateAfter.getGlobalState() == Markov.S_FAILED) {
			records = new ArrayList<MarkovRecord>();
			if (!MarkovRecord.hasState(stateAfter)) {
				MarkovRecord.addState(stateAfter);
			} else {
				stateAfter = MarkovRecord.getState(stateAfter);
			}
			if (!MarkovRecord.hasAction(noAction)) {
				MarkovRecord.addAction(noAction);
			} else {
				noAction= (BaseAction) MarkovRecord.getAction(noAction);
			}
			records.add(new MarkovRecord(stateAfter, stateAfter, noAction, 1, 0, 0));
			return records;
		}  else {  
			records = new ArrayList<MarkovRecord>();
			MarkovState[] states = stateAfter.getNextTwoStates();
			MarkovState stateAfter1 = states[0];
			MarkovState stateAfter2 = states[1];
//			System.out.println("stateAfter=" + stateAfter.getNextToDoActivity());
//			System.out.println("stateAfter1=" + stateAfter1);
//			System.out.println("stateAfter2=" + stateAfter2);
			if (!MarkovRecord.hasState(stateAfter1)) {
				MarkovRecord.addState(stateAfter1);
			} else {
				stateAfter1 = MarkovRecord.getState(stateAfter1);
			}
			if (!MarkovRecord.hasState(stateAfter2)) {
				MarkovRecord.addState(stateAfter2);
			} else {
				stateAfter2 = MarkovRecord.getState(stateAfter2);
			}
			if (!MarkovRecord.hasAction(noAction)) {
				MarkovRecord.addAction(noAction);
			} else {
				noAction= (BaseAction) MarkovRecord.getAction(noAction);
			}
			records.add(new MarkovRecord(state, stateAfter1, noAction, 
					stateAfter.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));
			records.add(new MarkovRecord(state, stateAfter2, noAction, 
					1 - stateAfter.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));

			return records;
		}
	}

	/*	if (state.getGlobalState() == Markov.S_FAILED) {
			//System.out.println("state:" + state);

		} else {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			List<MarkovState> states = state.nextStates(Markov.A_NO_ACTION);

			records.add(new MarkovRecord(state, states.get(0), noAction, 
					state.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));
			records.add(new MarkovRecord(state, states.get(1), noAction, 
					1 - state.getNextToDoActivity().getBlindService().getQos().getReliability(), 0, 0));

			return records;
		}*/

	public static List<MarkovRecord> terminateRecords(MarkovState state) {
		MarkovState stateAfter = state.clone();
		BaseAction terminateAction = (BaseAction) Markov.terminate(stateAfter);
		if (stateAfter == null || stateAfter.isFinished() || MarkovRecord.hasStateAction(stateAfter, terminateAction)) {
			return null;
		}
//		BaseAction terminateAction = new BaseAction(state.getNextToDoActivity().getNumber(), 
//				Markov.A_TERMINATE, state.getNextToDoActivity().getBlindService().getNumber());
/*		if (!isTerminateActionCanDo(terminateAction)) {
			return null;
		}
		addTerminateActionUsedCount(terminateAction);*/
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		if (MarkovRecord.hasState(stateAfter)) {
			stateAfter = MarkovRecord.getState(stateAfter);
		} else {
			MarkovRecord.addState(stateAfter);
		}
		if (!MarkovRecord.hasAction(terminateAction)) {
			MarkovRecord.addAction(terminateAction);
		} else {
			terminateAction = (BaseAction) MarkovRecord.getAction(terminateAction);
		}
		records.add(new MarkovRecord(state, stateAfter, terminateAction, 1, 0, 0));
		return records;
	}
	
	//在action里面加上消耗和报酬
	public static List<MarkovRecord> redoRecords(MarkovState state) {
		MarkovState stateAfter = state.clone();
		BaseAction redoAction = (BaseAction) Markov.redo(stateAfter);
		
		if (stateAfter == null || stateAfter.isFinished() || MarkovRecord.hasStateAction(stateAfter, redoAction)) {
			return null;
		}

		//System.out.println("in redo, stateAfter=" + stateAfter);
		
//		BaseAction redoAction = new BaseAction(state.getFaultActivity().getNumber(), 
//				Markov.A_RE_DO, state.getFaultActivity().getBlindService().getNumber());
		//		if (!isReDoActionCanDo(redoAction)) {
		//			return null;
		//		}
//		if (stateAfter.isFailed()) {
			//			addReDoActionUsedCount(redoAction);
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			MarkovState[] states = stateAfter.getNextTwoStates();
			//System.out.println("states.length=" + states.length + stateAfter.isFailed());
			MarkovState stateAfter1 = states[0];
			MarkovState stateAfter2 = states[1];
//			System.out.println("in redo, stateAfter1=" + stateAfter1);
//			System.out.println("in redo, stateAfter2=" + stateAfter2 + "\n");
			if (!MarkovRecord.hasState(stateAfter1)) {
				MarkovRecord.addState(stateAfter1);
			} else {
				stateAfter1 = MarkovRecord.getState(stateAfter1);
			}
			if (!MarkovRecord.hasState(stateAfter2)) {
				MarkovRecord.addState(stateAfter2);
			} else {
				stateAfter2 = MarkovRecord.getState(stateAfter2);
			}
			if (!MarkovRecord.hasAction(redoAction)) {
				MarkovRecord.addAction(redoAction);
			} else {
				redoAction = (BaseAction) MarkovRecord.getAction(redoAction);
			}
			
			records.add(new MarkovRecord(state, stateAfter1, redoAction, 
					state.getFaultActivity().getBlindService().getQos().getReliability(),
					state.getFaultActivity().getBlindService().getQos().getPrice(), 
					Math.abs(state.getFaultActivity().getBlindService().getQos().getExecTime()*state.getFaultActivity().getX())));
			records.add(new MarkovRecord(state, stateAfter2, redoAction,
					1 - state.getFaultActivity().getBlindService().getQos().getReliability(), 
					state.getFaultActivity().getBlindService().getQos().getPrice(), 
					Math.abs(state.getFaultActivity().getBlindService().getQos().getExecTime()*state.getFaultActivity().getX())));
			return records;

//		} else {
//			System.out.println("Flow is not failed. Redo action is forbidden.");
//			return null;
//		}
	}

	

	public static List<MarkovRecord> replaceRecords(MarkovState state) {
		MarkovState stateAfter = state.clone();
		ReplaceAction replaceAction = (ReplaceAction) Markov.replace(stateAfter);
		
		if (replaceAction == null || stateAfter == null || stateAfter.isFinished() || MarkovRecord.hasStateAction(stateAfter, replaceAction)) {
			//System.out.println("---");
			return null;
		}

		//		if (!isReplaceActionCanDo(state.getFailedActivity().getNumber())) {
		//			return null;
		//		}
		//if (state.isCurrFailed()) {
			//			addReplaceActionUsedCount(state.getFailedActivity().getNumber());
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//		System.out.println("stateAfter=" + stateAfter.isFailed());
		MarkovState[] states = stateAfter.getNextTwoStates();
		if (states == null) {
			return null;
		}

		MarkovState stateAfter1 = states[0];
		MarkovState stateAfter2 = states[1];
		if (!MarkovRecord.hasState(stateAfter1)) {
			MarkovRecord.addState(stateAfter1);
		} else {
			stateAfter1 = MarkovRecord.getState(stateAfter1);
		}
		if (!MarkovRecord.hasState(stateAfter2)) {
			MarkovRecord.addState(stateAfter2);
		} else {
			stateAfter2 = MarkovRecord.getState(stateAfter2);
		}
		if (!MarkovRecord.hasAction(replaceAction)) {
			MarkovRecord.addAction(replaceAction);
		} else {
			replaceAction = (ReplaceAction) MarkovRecord.getAction(replaceAction);
		}

		//Mark
//		ReplaceAction replaceAction = new ReplaceAction(stateAfter.getFaultActivity().getNumber(), 
//				Markov.A_REPLACE, stateAfter.getFaultActivity().getBlindService().getNumber(),
//				stateAfter.getReplaceNewService().getNumber());

		records.add(new MarkovRecord(state, stateAfter1, replaceAction, replaceAction.getPosibility(),
				replaceAction.getPriceCost(), replaceAction.getTimeCost()));
		records.add(new MarkovRecord(state, stateAfter2, replaceAction, 1 - replaceAction.getPosibility(),
				replaceAction.getPriceCost(), replaceAction.getTimeCost()));

//		records.add(new MarkovRecord(stateAfter, stateAfter1, replaceAction, stateAfter.getFreeServiceFinder().getPosibility(),
//				stateAfter.getFreeServiceFinder().getPriceCost(), stateAfter.getFreeServiceFinder().getTimeCost()));
//		records.add(new MarkovRecord(stateAfter, stateAfter2, replaceAction, 1 - stateAfter.getFreeServiceFinder().getPosibility(),
//				stateAfter.getFreeServiceFinder().getPriceCost(), stateAfter.getFreeServiceFinder().getTimeCost()));
		
		//			records.add(new MarkovRecord(state, states.get(0), replaceAction, state.getReplaceNewService().getQos().getReliability(),
		//					state.getReplaceNewService().getQos().getPrice(), state.getReplaceNewService().getQos().getExecTime()));
		//			records.add(new MarkovRecord(state, states.get(1), replaceAction, 1 - state.getReplaceNewService().getQos().getReliability(),
		//					state.getReplaceNewService().getQos().getPrice(), state.getReplaceNewService().getQos().getExecTime()));

		return records;

//		} else {
//			return null;
//		}
	}

	public static List<MarkovRecord> reCompositeRecords(MarkovState state) {
		MarkovState stateAfter = state.clone();
		ReCompositeAction reCompositeAction =  (ReCompositeAction) Markov.reComposite(stateAfter);
		//MarkovState stateTemp = stateAfter.clone();
		if (reCompositeAction == null || stateAfter.isFinished() || MarkovRecord.hasStateAction(stateAfter, reCompositeAction)) {
			return null;
		}

		//		if (!isReCompositeActionCanDo(state.getFailedActivity().getNumber())) {
		//			return null;
		//		}
		//if (stateTemp.isCurrFailed()) {
			//			addRecompositeActionUsedCount(state.getFailedActivity().getNumber());
			//System.out.println("Before ReCom:" + stateStore);
			//			System.out.println("1---state=" + state);
			//			System.out.println("1---stateStore=" + stateStore);
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();

		//			ReCompositeAction reCompositeAction = // (ReCompositeAction) stateStore.getReCompositeAction();
		//					(ReCompositeAction) ActivityFlow.recomposite(stateStore);
		//			System.out.println("++++" + stateStore);
		MarkovState[] states = stateAfter.getNextTwoStates();
		//			System.out.println("2---state=" + state);
		//			System.out.println("2---stateStore=" + stateStore);
		if (states == null) {
			//System.out.println("Here, states are=" + states);
			return null;
		}
		//			ReCompositeAction reCompositeAction = new ReCompositeAction(state.getFailedActivity().getNumber(),
		//					Markov.A_RE_COMPOSITE, state.getFailedActivity().getBlindService().getNumber());
		//			ReCompositeAction reCompositeAction =  (ReCompositeAction) stateTemp.getReCompositeAction();
		ReCompositorImpl reCompositor = ((ReCompositorImpl) reCompositeAction.getReCompositor());			
//		ReCompositeAction reCompositeAction =  (ReCompositeAction) reCompositor.getReComAction();

		MarkovState stateAfter1 = states[0];
		MarkovState stateAfter2 = states[1];
		if (!MarkovRecord.hasState(stateAfter1)) {
			MarkovRecord.addState(stateAfter1);
		} else {
			stateAfter1 = MarkovRecord.getState(stateAfter1);
		}
		if (!MarkovRecord.hasState(stateAfter2)) {
			MarkovRecord.addState(stateAfter2);
		} else {
			stateAfter2 = MarkovRecord.getState(stateAfter2);
		}
		if (!MarkovRecord.hasAction(reCompositeAction)) {
			MarkovRecord.addAction(reCompositeAction);
		} else {
			reCompositeAction = (ReCompositeAction) MarkovRecord.getAction(reCompositeAction);
		}
		
		//						(ReCompositeAction) ActivityFlow.recomposite(stateStore);
		//			reCompositeAction.setOldNewReplaceServiceMap(state.getReCompositor().getOldNewReplaceMap());

		//			records.add(new MarkovRecord(state, states.get(0), reCompositeAction, state.getReCompositor().getPosibility(),
		//					state.getReCompositor().getPriceCost(), state.getReCompositor().getTimeCost()));
		//			records.add(new MarkovRecord(state, states.get(1), reCompositeAction, 1- state.getReCompositor().getPosibility(),
		//					state.getReCompositor().getPriceCost(), state.getReCompositor().getTimeCost()));

		//			System.out.println("After ReCom, stateStore" + stateStore);
		//			System.out.println("state=" + state);
		records.add(new MarkovRecord(state, stateAfter1, reCompositeAction, reCompositor.getPosibility(),
				reCompositor.getPriceCost(), reCompositor.getTimeCost()));
		records.add(new MarkovRecord(state, stateAfter2, reCompositeAction, 1 - reCompositor.getPosibility(),
				reCompositor.getPriceCost(), reCompositor.getTimeCost()));

		return records;

//		} else {
//			return null;
//		}
	}

	public static List<MarkovRecord> getRecords(MarkovState state) {
		//System.out.println("In getRecords, state=" + state);
		if (MarkovRecord.hasStateBefore(state)) {
			return null;
		}
		List<MarkovRecord> resultRecords = new ArrayList<MarkovRecord>();
		
		if (state.getGlobalState() == Markov.S_NORMAL) {
			List<MarkovRecord> tempRecords = Markov.noActionRecords(state);
			
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
		}
		if (state.getGlobalState() == Markov.S_DELAYED) {
			List<MarkovRecord> tempRecords = Markov.noActionRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.terminateRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.replaceRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.reCompositeRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
		}
		if (state.getGlobalState() == Markov.S_PRICE_UP) {
			List<MarkovRecord> tempRecords = Markov.noActionRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.terminateRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.replaceRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.reCompositeRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
		}
		if (state.getGlobalState() == Markov.S_SUCCEED) {
			List<MarkovRecord> tempRecords = Markov.noActionRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
		}
		if (state.getGlobalState() == Markov.S_FAILED) {
			List<MarkovRecord> tempRecords = Markov.terminateRecords(state);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.redoRecords(state);
		//	System.out.println("Redo:" + tempRecords);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			//System.out.println("state=" + state);
			tempRecords = Markov.replaceRecords(state);
		//	System.out.println("Replace:" + tempRecords);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			tempRecords = Markov.reCompositeRecords(state);
		//	System.out.println("reComposite:" + tempRecords);
			if (tempRecords != null && !tempRecords.isEmpty()) {
				resultRecords.addAll(tempRecords);
			}
			//System.out.println("Line 469");
		}

		return resultRecords;
	}
}

