package com.zw.markov;

public class MarkovRecord {
	private MarkovState stateBefore;
	private MarkovState stateAfter;
	//private BaseAction action;
	private MarkovAction action;
	private double posibility;
	private double priceCost;
	private double timeCost;
	
	public MarkovState getStateBefore() {
		return stateBefore;
	}
	public void setStateBefore(MarkovState stateBefore) {
		this.stateBefore = stateBefore;
	}
	public MarkovState getStateAfter() {
		return stateAfter;
	}
	public void setStateAfter(MarkovState stateAfter) {
		this.stateAfter = stateAfter;
	}
	public MarkovAction getAction() {
		return action;
	}
	public void setAction(MarkovAction action) {
		this.action = action;
	}
	public double getPosibility() {
		return posibility;
	}
	public void setPosibility(double posibility) {
		this.posibility = posibility;
	}
	public double getPriceCost() {
		return priceCost;
	}
	public void setPriceCost(double priceCost) {
		this.priceCost = priceCost;
	}
	public double getTimeCost() {
		return timeCost;
	}
	public void setTimeCost(double timeCost) {
		this.timeCost = timeCost;
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
}
