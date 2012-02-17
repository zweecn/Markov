package com.zw.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zw.ws.Activity;

public final class MarkovInfo extends Object{
	public static final int BEFORE = 0;
	public static final int RUNNING = 1;
	public static final int AFTER = 2;

	public static final int UNKNOWN = 3;
	public static final int FAILED = 4;
	public static final int SUCCEED = 5;
	public static final int PRICE_UP = 6;
	public static final int DELAYED = 7; 
	
	public static final int IGNORE = 70;
	public static final int RE_DO = 80;
	public static final int REPLACE = 90;
	public static final int TERMINATE = 100;
	public static final int RE_COMPOSITE = 110;

	public static List<MarkovRecord> ignore(MarkovState state) {
		List<MarkovRecord> records = new ArrayList<MarkovRecord>();
		for (int i = 0; i < state.getActivitySize(); i++) {
			for (int j = 0; j < state.getActivitySize(); j++) {
				Activity ai = state.getActivity(i);
				Activity aj = state.getActivity(j);
				if (state.hasEdge(i, j) && ai.getBeforeOrAfter() == AFTER 
						&& (aj.getBeforeOrAfter() == BEFORE || aj.getBeforeOrAfter() == RUNNING)) {
					
					MarkovRecord tempRecord = new MarkovRecord();
					tempRecord.setAction(new MarkovAction(j, aj.getBlindService().getNumber(), IGNORE));
					tempRecord.setStateBefore(state);
					
				}
			}
		}
		
		
		return records;
	}
	
	
}
