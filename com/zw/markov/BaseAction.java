package com.zw.markov;

public class BaseAction {
	public BaseAction(int activityNumber, int opNumber) {
		this.currActivityNumber = activityNumber;
		this.opNumber = opNumber;
	}
	
	protected int currActivityNumber;
	protected int opNumber;
	
	public int getCurrActivityNumber() {
		return currActivityNumber;
	}

	public int getOpNumber() {
		return opNumber;
	}
	
	public String toString() {
		String res = "";
		String actionText = "";
		switch (opNumber) {
		case MarkovInfo.A_NO_ACTION:
			actionText = "NO_ACTION";
			break;
		case MarkovInfo.A_TERMINATE:
			actionText = "TERMINATE";
			break;
		case MarkovInfo.A_RE_DO:
			actionText = "RE_DO";
			break;
		case MarkovInfo.A_REPLACE:
			actionText = "REPLACE";
			break;
		case MarkovInfo.A_RE_COMPOSITE:
			actionText = "RE_COMPOSITE";
			break;
		default:
			break;
		}
		
		res += "[Action: " + String.format("%-12s", actionText) 
				+  " Activity " + String.format("%1d", currActivityNumber) 
				+ "]";
		
		return res;
	}
}
