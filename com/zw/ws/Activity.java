package com.zw.ws;

public class Activity {
	public Activity() {
		super();
	}
	public Activity(int activityNumber) {
		this.activityNumber = activityNumber;
	}
	
	public Activity(AtomService blindService) {
		super();
		this.blindService = blindService;
	}
	
	private AtomService blindService;
	private ServiceQoS expectQoS;
	private ServiceQoS currentQoS;
	private int activityNumber;
	
	public AtomService getBlindService() {
		return blindService;
	}
	public void setBlindService(AtomService blindService) {
		this.blindService = blindService;
		this.currentQoS = this.blindService.getQos();
	}
	public ServiceQoS getExpectQoS() {
		return expectQoS;
	}
	public void setExpectQoS(ServiceQoS expectQoS) {
		this.expectQoS = expectQoS;
	}
	public ServiceQoS getCurrentQoS() {
		return currentQoS;
	}
	public void setCurrentQoS(ServiceQoS currentQoS) {
		this.currentQoS = currentQoS;
	}
	public int getActivityNumber() {
		return activityNumber;
	}
	public void setActivityNumber(int activityNumber) {
		this.activityNumber = activityNumber;
	}
}
