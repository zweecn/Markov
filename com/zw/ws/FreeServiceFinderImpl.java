package com.zw.ws;

public class FreeServiceFinderImpl extends ActivityFlow implements FreeServiceFinder{

	private Activity oldActivity;
	private AtomService serviceNew;
	
	@Override
	public AtomService nextFreeService(int activityNumber) {
		oldActivity = super.getActivity(activityNumber).clone();
		for (int i = 0; i < this.services.size(); i++) {
			if (this.services.get(i).isFree()) { 
				//this.services.get(i).setFree(false);  //ERROR
				return serviceNew = this.services.get(i);
			}
		}
		return null;
	}

	@Override
	public void setServiceUsed(int number) {
		if (serviceNew != null && serviceNew.getNumber() == number) {
			serviceNew.setFree(false);
			return;
		}
		for (int i = 0; i < this.services.size(); i++) {
			if (services.get(i).getNumber() == number) {
				services.get(i).setFree(false);
			}
		}
	}

	@Override
	public double getPosibility() {
		if (serviceNew != null) {
			return serviceNew.getQos().getReliability();
		}
		return 0;
	}

	@Override
	public double getTimeCost() {
		if (serviceNew != null) {
			if (Math.abs(oldActivity.getX()) <= 1) {
				return serviceNew.getQos().getExecTime()
						+ (Math.abs(oldActivity.getX()) - 1) 
						* oldActivity.getBlindService().getQos().getExecTime();
			} else {
				return serviceNew.getQos().getExecTime();
			}
		}
		return 0;
	}

	@Override
	public double getPriceCost() {
		if (serviceNew != null) {
			return serviceNew.getQos().getPrice();
		}
		return 0;
	}

}
