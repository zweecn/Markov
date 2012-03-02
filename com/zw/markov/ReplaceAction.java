package com.zw.markov;

public class ReplaceAction extends BaseAction implements MarkovAction{
	public ReplaceAction(int activityNumber, int opNumber, int oldServiceNumber, int newServiceNumber) {
		super(activityNumber, opNumber, oldServiceNumber);
		this.oldServiceNumber = oldServiceNumber;
		this.newServiceNumber = newServiceNumber;
	}
	
	private int oldServiceNumber;
	private int newServiceNumber;
	
	public int getOldServiceNumber() {
		return oldServiceNumber;
	}
	
	public int getNewServiceNumber() {
		return newServiceNumber;
	}
	
	public ReplaceAction clone() {
		ReplaceAction action = new ReplaceAction(currActivityNumber, 
				newServiceNumber, oldServiceNumber, newServiceNumber);
		return action;
	}
	
	public String toString() {
		String res = "";
		String actionText = "";
		switch (opNumber) {
		case Markov.A_NO_ACTION:
			actionText = "NO_ACTION";
			break;
		case Markov.A_TERMINATE:
			actionText = "TERMINATE";
			break;
		case Markov.A_RE_DO:
			actionText = "RE_DO";
			break;
		case Markov.A_REPLACE:
			actionText = "REPLACE";
			break;
		case Markov.A_RE_COMPOSITE:
			actionText = "RE_COMPOSITE";
			break;
		default:
			break;
		}
				
		String oldNew = " (old=" + oldServiceNumber + " new=" + newServiceNumber +")"; 
		res += "[Action: " + String.format("%2d", getId()) + " " + String.format("%-12s", actionText) 
				+ " Activity " + String.format("%1d", currActivityNumber)
				+ String.format("%-23s", oldNew)
				+ "]";
		
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + newServiceNumber;
		result = prime * result + oldServiceNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ReplaceAction)) {
			return false;
		}
		ReplaceAction other = (ReplaceAction) obj;
		if (newServiceNumber != other.newServiceNumber) {
			return false;
		}
		if (oldServiceNumber != other.oldServiceNumber) {
			return false;
		}
		return true;
	}
	
	
}
