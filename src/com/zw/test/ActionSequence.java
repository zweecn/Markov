package com.zw.test;

public class ActionSequence {
	public ActionSequence(int action, String actionString, double price, double time, double reward) {
		super();
		this.action = action;
		this.actionString = actionString;
		this.price = price;
		this.time = time;
		this.reward = reward;
	}
	private int action;
	private String actionString;
	private double price;
	private double time;
	private double reward;
	
	public String toMatlabString() {
		String res = "";
		res += action + " " + String.format("%.2f", reward) + " " + String.format("%.2f", price) 
				+ " " + String.format("%.2f", time);
		return res;
	}
	
	public int getAction() {
		return action;
	}
	public double getPrice() {
		return price;
	}
	public double getTime() {
		return time;
	}
	public double getReward() {
		return reward;
	}
	
	public String getActionString() {
		return actionString;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		result = prime * result
				+ ((actionString == null) ? 0 : actionString.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(reward);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(time);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ActionSequence)) {
			return false;
		}
		ActionSequence other = (ActionSequence) obj;
		if (action != other.action) {
			return false;
		}
		if (actionString == null) {
			if (other.actionString != null) {
				return false;
			}
		} else if (!actionString.equals(other.actionString)) {
			return false;
		}
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price)) {
			return false;
		}
		if (Double.doubleToLongBits(reward) != Double
				.doubleToLongBits(other.reward)) {
			return false;
		}
		if (Double.doubleToLongBits(time) != Double
				.doubleToLongBits(other.time)) {
			return false;
		}
		return true;
	}
	
}