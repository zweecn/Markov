package com.zw.markov;

public class MarkovAction {
	public MarkovAction(int activityNumber, int serviceNumber, int action) {
		super();
		this.activityNumber = activityNumber;
		this.serviceNumber = serviceNumber;
		this.actionNumber = action;
	}
	
	private int activityNumber;
	private int serviceNumber;
	private int actionNumber;
	
	public int getActivityNumber() {
		return activityNumber;
	}
	public void setActivityNumber(int activityNumber) {
		this.activityNumber = activityNumber;
	}
	public int getServiceNumber() {
		return serviceNumber;
	}
	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}
	public int getAction() {
		return actionNumber;
	}
	public void setAction(int action) {
		this.actionNumber = action;
	}
	
	public String toString() {
		String res = "";
		String actionText = "";
		switch (actionNumber) {
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
		
		res += "[Action: Activity " + activityNumber + " with service " + serviceNumber + " do " + actionText +"]"; 
		return res;
	}
}
