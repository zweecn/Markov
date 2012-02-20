package com.zw.markov;

import java.util.ArrayList;
import java.util.List;
import com.zw.ws.Activity;

public final class MarkovInfo extends Object{
	public static final double EXP = 0.00001;
	public static final int S_UNKNOWN = 1;
	public static final int S_FAILED = 2;
	public static final int S_SUCCEED = 3;
	public static final int S_PRICE_UP = 4;
	public static final int S_DELAYED = 5;

	public static final int A_IGNORE = 70;
	public static final int A_RE_DO = 80;
	public static final int A_REPLACE = 90;
	public static final int A_TERMINATE = 100;
	public static final int A_RE_COMPOSITE = 110;

	public static List<MarkovRecord> ignore(MarkovState state) {
		if (state.getNextToDoActivity() == null) {
			System.out.println("Ignore, Next to do activity is null.");
			return null;
		}
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
//		if (state.isFinished()) {
//			//System.out.println("Ignore, state machine is finished. The state is: " + state.toString());
//			return null;
//		}
//		
		if (state.isFinished()) {
			MarkovState stateAfter = state.clone();
//			stateAfter.setGlobalState(S_FAILED);
			Activity failedActivity = state.getFailedActivity();
			MarkovAction action = new MarkovAction(failedActivity.getNumber(), 
					failedActivity.getBlindService().getNumber(), A_IGNORE);

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
					state.getNextToDoActivity().getBlindService().getNumber(), A_IGNORE);
			
			MarkovRecord record = new MarkovRecord();
			record.setStateBefore(state);
			record.setAction(action);
			record.setStateAfter(stateAfter);
			record.setPosibility(state.getNextToDoActivity().getBlindService().getQos().getReliability());
			record.setPriceCost(0); // ?
			record.setTimeCost(0);  // ?
			
//			System.out.println("stateBefore:" + record.getStateBefore().getNextToDoActivity().getNumber() 
//					+ " stateAfter:" + record.getStateAfter().getNextToDoActivity().getNumber());
			records.add(record);

			stateAfter = state.nextFailedState();
//			stateAfter.getNextToDoActivity().setX(-1); //Maybe some errors
//			stateAfter.setActivity(stateAfter.getNextToDoActivity());
//			stateAfter.setGlobalState(S_FAILED);
//			stateAfter.setCurrentTimeCost(nextTimeCost+state.getCurrentTimeCost());
			
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
	
	public static List<MarkovRecord> terminal(MarkovState state) {
		if (state.getNextToDoActivity() == null) {
			System.out.println("Terminal, Next to do activity is null.");
			return null;
		}
		
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		
		MarkovState stateAfter = state.clone();
		stateAfter.setGlobalState(S_FAILED);
		
		MarkovAction action = new MarkovAction(state.getNextToDoActivity().getNumber(), 
				state.getNextToDoActivity().getBlindService().getNumber(), A_IGNORE);

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
}
