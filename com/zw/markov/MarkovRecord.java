package com.zw.markov;

import java.util.HashMap;
import java.util.Map;

public class MarkovRecord {
	private MarkovState stateBefore;
	private MarkovState stateAfter;
	private MarkovAction action;
	private double posibility;
	private double priceCost;
	private double timeCost;
	
	private static Map<Long, Double> posibilityMap = new HashMap<Long, Double>();
	private static Map<Long, Double> priceMap = new HashMap<Long, Double>();
	private static Map<Long, Double> timeMap = new HashMap<Long, Double>();
	private static Map<Long, Double> reward_tMap = new HashMap<Long, Double>();
	private static Map<Long, MarkovState> stateMap = new HashMap<Long, MarkovState>();
	private static Map<Long, MarkovAction> actionMap = new HashMap<Long, MarkovAction>();
	
	public MarkovRecord(MarkovState stateBefore, MarkovState stateAfter, 
			MarkovAction action, double posibility, double priceCost, double timeCost) {
		this.stateBefore = stateBefore;
		this.stateAfter = stateAfter;
		this.action = action;
		this.posibility = posibility;
		this.priceCost = priceCost;
		this.timeCost = timeCost;
	
		init();	
	}
	
	public void init() {
		MarkovRecord.setPosibility(this.stateBefore.getId(), this.action.getId(), this.stateAfter.getId(), this.posibility);
		MarkovRecord.setPriceCost(this.stateBefore.getId(), this.action.getId(), this.priceCost);
		MarkovRecord.setTimeCost(this.stateBefore.getId(), this.action.getId(), this.timeCost);
		MarkovRecord.setState(this.stateBefore.getId(), this.stateBefore);
		MarkovRecord.setState(this.stateAfter.getId(), this.stateAfter);
		MarkovRecord.setAction(this.action.getId(), this.action);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		long temp;
		temp = Double.doubleToLongBits(posibility);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(priceCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((stateAfter == null) ? 0 : stateAfter.hashCode());
		result = prime * result
				+ ((stateBefore == null) ? 0 : stateBefore.hashCode());
		temp = Double.doubleToLongBits(timeCost);
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
		if (!(obj instanceof MarkovRecord)) {
			return false;
		}
		MarkovRecord other = (MarkovRecord) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (Double.doubleToLongBits(posibility) != Double
				.doubleToLongBits(other.posibility)) {
			return false;
		}
		if (Double.doubleToLongBits(priceCost) != Double
				.doubleToLongBits(other.priceCost)) {
			return false;
		}
		if (stateAfter == null) {
			if (other.stateAfter != null) {
				return false;
			}
		} else if (!stateAfter.equals(other.stateAfter)) {
			return false;
		}
		if (stateBefore == null) {
			if (other.stateBefore != null) {
				return false;
			}
		} else if (!stateBefore.equals(other.stateBefore)) {
			return false;
		}
		if (Double.doubleToLongBits(timeCost) != Double
				.doubleToLongBits(other.timeCost)) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		String res = new String();
		res += stateBefore.toString() + " || " + action.toString() + " || " + stateAfter.toString()
				+ " [Posibility: " + String.format("%4.2f", posibility) + "] || [Time_cost: " 
				+ String.format("%6.2f", timeCost)  + "] || [Price_cost:" + String.format("%6.1f", priceCost) + "]";
		return res;
	}
	public MarkovState getStateBefore() {
		return stateBefore;
	}
	public MarkovState getStateAfter() {
		return stateAfter;
	}
	public MarkovAction getAction() {
		return action;
	}
	public double getPosibility() {
		return posibility;
	}
	public double getPriceCost() {
		return priceCost;
	}
	public double getTimeCost() {
		return timeCost;
	}

	public static double getPosibility(int i, int a, int j) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		result = prime * result + j;
		return MarkovRecord.posibilityMap.get(result);
	}

	public static void setPosibility(int i, int a, int j, double p) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		result = prime * result + j;
		MarkovRecord.posibilityMap.put(result, p);
	}
	
	public static double getPriceCost(int i, int a) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		return MarkovRecord.priceMap.get(result);
	}

	public static void setPriceCost(int i, int a, double priceCost) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		MarkovRecord.priceMap.put(result, priceCost);
	}
	
	public static double getTimeCost(int i, int a) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		return MarkovRecord.timeMap.get(result);
	}

	public static void setTimeCost(int i, int a, double timeCost) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		MarkovRecord.timeMap.put(result, timeCost);
	}
	
	public static double getReward_t(int i, int a) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		return MarkovRecord.reward_tMap.get(result);
	}

	public static void setReward_t(int i, int a, double reward) {
		final int prime = 31;
		long result = 1;
		result = prime * result + i;
		result = prime * result + a;
		MarkovRecord.reward_tMap.put(result, reward);
	}
	
	public static MarkovAction getAction(long i) {
		return MarkovRecord.actionMap.get(i);
	}

	public static MarkovAction setAction(long i, MarkovAction action) {
		return MarkovRecord.actionMap.put(i, action);
	}
	
	public static MarkovState getState(long i) {
		return MarkovRecord.stateMap.get(i);
	}

	public static MarkovState setState(long i, MarkovState state) {
		return MarkovRecord.stateMap.put(i, state);
	}
}
