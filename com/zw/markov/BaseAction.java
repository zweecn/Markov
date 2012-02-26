package com.zw.markov;

public class BaseAction {
	public BaseAction(int activityNumber, int opNumber, int oldServiceNumber) {
		this.currActivityNumber = activityNumber;
		this.opNumber = opNumber;
		this.oldServiceNumber = oldServiceNumber;
	}
	
	protected int currActivityNumber;
	protected int opNumber;
	protected int oldServiceNumber;
	
	public int getCurrActivityNumber() {
		return currActivityNumber;
	}

	public int getOpNumber() {
		return opNumber;
	}
	
	public BaseAction clone() {
		BaseAction action = new BaseAction(this.currActivityNumber, this.opNumber, this.oldServiceNumber);
		return action;
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
				+ " (old=" + String.format("%2s", oldServiceNumber) 
				+ " new=" + String.format("%2s", oldServiceNumber) 
				+ ")]";
		
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currActivityNumber;
		result = prime * result + oldServiceNumber;
		result = prime * result + opNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BaseAction)) {
			return false;
		}
		BaseAction other = (BaseAction) obj;
		if (currActivityNumber != other.currActivityNumber) {
			return false;
		}
		if (oldServiceNumber != other.oldServiceNumber) {
			return false;
		}
		if (opNumber != other.opNumber) {
			return false;
		}
		return true;
	}
}