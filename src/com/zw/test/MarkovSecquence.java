package com.zw.test;

public class MarkovSecquence implements Comparable<Object> {

	public MarkovSecquence(int t, ActionSequence actionSequence) {
		super();
		this.t = t;
		this.actionSequence = actionSequence;
	}

	private int t;
	private ActionSequence actionSequence;
	
	@Override
	public int compareTo(Object o) {
		MarkovSecquence s = (MarkovSecquence) o;
		Integer tThis = this.t;
		Integer tOther = s.t;
		return tThis.compareTo(tOther);
	}

	public int getT() {
		return t;
	}

	public ActionSequence getActionSequence() {
		return actionSequence;
	}
	
	public String toMatlabString() {
		String res = "";
		res += t + " " + actionSequence.toMatlabString();
		return res;
	}
}
