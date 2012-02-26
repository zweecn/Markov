package com.zw.ws;

public interface FreeServiceFinder {
	AtomService nextFreeService(int activityNumber);
	void setServiceUsed(int number);
	double getPosibility();
	double getTimeCost();
	double getPriceCost();
}
