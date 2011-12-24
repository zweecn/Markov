package com.zw.markov;

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
			MarkovAction action) {
		double p = 0;
		
		
		
		return p;
	}
	
	public static double getReward() {
		double reward = 0;
		
		return reward;
	}
}
