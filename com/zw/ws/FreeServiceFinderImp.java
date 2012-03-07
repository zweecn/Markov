package com.zw.ws;

//public class FreeServiceFinderImpl extends ActivityFlow implements FreeServiceFinder{

//
//	private Activity oldActivity;
//	private AtomService serviceNew;
//	
//	
//	public AtomService nextFreeService(Activity activity) {
//		oldActivity = activity.clone();
//		for (int i = 0; i < this.services.size(); i++) {
//			if (this.services.get(i).isFree()) { 
//				return serviceNew = this.services.get(i);
//			}
//		}
//		return null;
//	}
//
//
//	public void setServiceUsed(int number) {
//		if (serviceNew != null && serviceNew.getNumber() == number) {
//			serviceNew.setFree(false);
//			return;
//		}
//		for (int i = 0; i < this.services.size(); i++) {
//			if (services.get(i).getNumber() == number) {
//				services.get(i).setFree(false);
//			}
//		}
//	}

//	@Override
//	public double getPosibility() {
//		if (serviceNew != null) {
//			return serviceNew.getQos().getReliability();
//		}
//		return 0;
//	}
//
//	@Override
//	public double getTimeCost() {
//		if (serviceNew != null) {
//			if (Math.abs(oldActivity.getX()) <= 1) {
//				return (oldActivity.getBlindService().getQos().getExecTime() * Math.abs(oldActivity.getX()) 
//						+ serviceNew.getQos().getExecTime()
//						- oldActivity.getBlindService().getQos().getExecTime());
//			} else {
//				return serviceNew.getQos().getExecTime();
//			}
//		}
//		return 0;
//	}
//
//	@Override
//	public double getPriceCost() {
//		if (serviceNew != null) {
//			return serviceNew.getQos().getPrice();
//		}
//		return 0;
//	}
//}
