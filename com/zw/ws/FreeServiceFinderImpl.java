package com.zw.ws;


public class FreeServiceFinderImpl extends ServiceFlow implements FreeServiceFinder{

	@Override
	public AtomService nextFreeService() {
		for (int i = 0; i < this.services.size(); i++) {
			if (this.services.get(i).isFree()) { 
				//this.services.get(i).setFree(false);  //ERROR
				return this.services.get(i);
			}
		}
		return null;
	}

	@Override
	public void setServiceUsed(int number) {
		for (int i = 0; i < this.services.size(); i++) {
			if (services.get(i).getNumber() == number) {
				services.get(i).setFree(false);
			}
		}
	}
}
