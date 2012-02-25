package com.zw.markov;

import com.zw.ws.AtomService;
import com.zw.ws.ServiceFlow;

public class FreeServiceFinderImpl extends ServiceFlow implements FreeServiceFinder{

	@Override
	public AtomService nextFreeService() {
		for (int i = 0; i < this.services.size(); i++) {
			//System.out.print(services.get(i).isFree() + " ");
			if (this.services.get(i).isFree()) { 
				this.services.get(i).setFree(false);  //ERROR
				return this.services.get(i);
			}
		}
		//System.out.println();
		return null;
	}

}
