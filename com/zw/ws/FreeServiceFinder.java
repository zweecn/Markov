package com.zw.ws;

public interface FreeServiceFinder {
	AtomService nextFreeService(Activity activity);
	void setServiceUsed(int number);
	double getPosibility();
	double getTimeCost();
	double getPriceCost();
}
