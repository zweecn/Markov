package com.zw.markov;

import java.util.Set;

import com.zw.ws.ServiceFlow;

public final class MarkovInfo extends Object{
	public static final int BEFORE = 0;
	public static final int RUNNING = 1;
	public static final int AFTER = 2;

	public static final int UNKNOWN = 3;
	public static final int FAILED = 4;
	public static final int SUCCEED = 5;
	public static final int PRICE_UP = 6;

	public static final int DO_NULL = 7;
	public static final int RE_DO = 8;
	public static final int REPLACE = 9;
	public static final int TERMINATE = 10;
	public static final int RE_COMPOSITE = 11;

	public static double getTransProbability(MarkovState oldState, MarkovState newState, 
			MarkovAction action, ServiceFlow flow) {
		double p = 1;
		Set<ActivityState> oldAcitivitates = oldState.getActivityStates();
		Set<ActivityState> newActivityStates = newState.getActivityStates();
		for (ActivityState old : oldAcitivitates) {
			for (ActivityState news : newActivityStates) {
				if (action.getAction() == MarkovInfo.DO_NULL
						&& old.getAcitivityNumber() == news.getAcitivityNumber()
						&& old.getServiceNumber() == news.getServiceNumber()
						&& old.getServiceState() == MarkovInfo.UNKNOWN 
						&& news.getServiceState() == MarkovInfo.SUCCEED) {
					p *= flow.getActivity(old.getAcitivityNumber()).getBlindService().getQos().getReliability();
				} else if (action.getAction() == MarkovInfo.DO_NULL
						&& old.getAcitivityNumber() == news.getAcitivityNumber()
						&& old.getServiceNumber() == news.getServiceNumber()
						&& old.getServiceState() == MarkovInfo.UNKNOWN 
						&& news.getServiceState() == MarkovInfo.FAILED) {
					p *= (1-flow.getActivity(old.getAcitivityNumber()).getBlindService().getQos().getReliability());
				}
			}
		}
		return p;
	}

	public static double getReward() {
		double reward = 0;

		return reward;
	}
}
