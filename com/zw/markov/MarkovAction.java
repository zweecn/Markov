package com.zw.markov;

public class MarkovAction {
	public MarkovAction(int activityNumber, int serviceNumber, int action) {
		super();
		this.activityNumber = activityNumber;
		this.serviceNumber = serviceNumber;
		this.action = action;
	}
	
	private int activityNumber;
	private int serviceNumber;
	private int action;
	
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
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
}
