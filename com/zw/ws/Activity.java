package com.zw.ws;

public class Activity {
	public Activity() {
		super();
	}
	public Activity(int activityNumber) {
		this.number = activityNumber;
	}
	
	public Activity(AtomService blindService) {
		this.blindService = blindService;
	}
	
	private AtomService blindService;
	private int number;
	private int serviceState;
	private int beforeOrAfter;
	
	public AtomService getBlindService() {
		return blindService;
	}
	public void setBlindService(AtomService blindService) {
		this.blindService = blindService;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int activityNumber) {
		this.number = activityNumber;
	}
	public int getServiceState() {
		return serviceState;
	}
	public void setServiceState(int serviceState) {
		this.serviceState = serviceState;
	}
	public int getBeforeOrAfter() {
		return beforeOrAfter;
	}
	public void setBeforeOrAfter(int beforeOrAfter) {
		this.beforeOrAfter = beforeOrAfter;
	}
}
