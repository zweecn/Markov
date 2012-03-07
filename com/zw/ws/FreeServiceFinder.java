package com.zw.ws;

public interface FreeServiceFinder {
	AtomService nextFreeService(Activity activity);
	void setServiceUsed(AtomService service);
	double getPosibility();
	double getTimeCost();
	double getPriceCost();
}
