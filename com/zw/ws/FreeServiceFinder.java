package com.zw.ws;

public interface FreeServiceFinder {
	AtomService nextFreeService();
	void setServiceUsed(int number);
}
