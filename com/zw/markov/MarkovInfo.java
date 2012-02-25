package com.zw.markov;

import java.util.ArrayList;
import java.util.List;

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
	
	private static long stateid = 0;
	public static long getNextFreeStateID() {
		return (stateid++); 
	}
	
	public static List<MarkovRecord> noAction(MarkovState state) {
		if (state == null || state.getCurrGlobalState() == MarkovInfo.S_SUCCEED
				|| state.isCurrFinished()) {
			return null;
		}

		if (state.getCurrGlobalState() == MarkovInfo.S_FAILED) {
			List<MarkovRecord> records = new ArrayList<MarkovRecord>();
			BaseAction noAction = new BaseAction(state.getNextToDoActivity().getNumber(), MarkovInfo.A_NO_ACTION);
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
			BaseAction noAction = new BaseAction(state.getNextToDoActivity().getNumber(), MarkovInfo.A_NO_ACTION);

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
}
