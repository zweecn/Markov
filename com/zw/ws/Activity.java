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
	private double x;
	
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
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	
	public Activity clone() {
		Activity activityTemp = new Activity();
		activityTemp.setX(x);
		activityTemp.setBlindService(blindService.clone()); /////
		activityTemp.setNumber(number);
		return activityTemp;
	}
}
