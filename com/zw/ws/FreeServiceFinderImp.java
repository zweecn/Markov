package com.zw.ws;

public class FreeServiceFinderImp implements FreeServiceFinder{
	private Activity oldActivity;
	private AtomService serviceNew;
	
	public AtomService nextFreeService(Activity activity) {
		return ActivityFlow.nextFreeService(activity);
	}


	public void setServiceUsed(AtomService service) {
		ActivityFlow.setServiceUsed(service);
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
				return (oldActivity.getBlindService().getQos().getExecTime() * Math.abs(oldActivity.getX()) 
						+ serviceNew.getQos().getExecTime()
						- oldActivity.getBlindService().getQos().getExecTime());
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
