package com.zw.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		if (state.isFailed()) {
			MarkovState stateAfter = state.clone();
			stateAfter.setGlobalState(S_FAILED);
			Activity failedActivity = state.getFailedActivity();
			MarkovAction action = new MarkovAction(failedActivity.getNumber(), 
					failedActivity.getBlindService().getNumber(), A_IGNORE);
			
			MarkovRecord record = new MarkovRecord();
			record.setAction(action);
			record.setPosibility(1);
			record.setPriceCost(0); // ?
			record.setStateAfter(stateAfter);
			record.setStateBefore(state);
			record.setTimeCost(0);  // ?
			
			records.add(record);
			return records;
		}
		
		MarkovState stateAfter = state.clone();
		double nextTimeCost = state.nextStateTimeCost();
		stateAfter.setCurrentTimeCost(nextTimeCost+state.getCurrentTimeCost());
		for (int i = 0; i < stateAfter.getActivitySize(); i++) {
			for (int j = 0; j < stateAfter.getActivitySize(); j++) {
				Activity ai = stateAfter.getActivity(i);
				Activity aj = stateAfter.getActivity(j);				
				if (ai.getX() == 1 && aj.getX() < 1) {
					double xTemp = aj.getX() + (nextTimeCost/aj.getBlindService().getQos().getExecTime());
					if (Math.abs(xTemp - 1) < EXP ) {
						aj.setX(1);
					} else {
						aj.setX(xTemp);
					}
				
				}
			}
		}
		
		
		return records;
	}
	
	
}
