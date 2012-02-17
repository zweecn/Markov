package com.zw.markov;

import com.zw.ws.ServiceFlow;

public class MarkovState extends ServiceFlow {
	//private Set<ActivityState> activityStates;
	//private ActivityState[]  activityStates;
	//private ServiceFlow flow;
	private boolean finished;
	
	public MarkovState () {
		//this.flow = flow;
		//activityStates = new ActivityState[flow.getActivitySize()];
		finished = false;
	}
	

	public boolean isFinished() {
		if (finished) {
			return true;
		}
		return false;
	}
	
	public MarkovState clone() {
		MarkovState state = new MarkovState();
		
		
		
		
		return state;
	}
}
