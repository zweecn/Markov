package com.zw.ws;

public class Activity {
	public Activity() {
		super();
	}
	public Activity(int activityNumber) {
		this.number = activityNumber;
	}
	
	public Activity(AtomService blindService) {
		super();
		this.blindService = blindService;
	}
	
	private AtomService blindService;
	private int number;
	
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
}
