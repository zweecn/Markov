package com.zw.markov;

import java.util.*;


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
	private static List<MarkovState> stateList = new ArrayList<MarkovState>();
	private static Set<MarkovState> stateBeforeSet = new HashSet<MarkovState>();
	private static Map<StateAction, Double> stateAction2PriceCostMap = new HashMap<MarkovRecord.StateAction, Double>();
	private static Map<StateAction, Double> stateAction2TimeCostMap = new HashMap<MarkovRecord.StateAction, Double>();
	
	//StateAction sa = new StateAction(null, null);
	
	private static class StateAction {
		public StateAction(MarkovState state, MarkovAction action) {
		//	super();
			this.state = state;
			this.action = action;
		}
		
		private MarkovState state;
		private MarkovAction action;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
		//	result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
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
			if (!(obj instanceof StateAction)) {
				return false;
			}
			StateAction other = (StateAction) obj;
//			if (!getOuterType().equals(other.getOuterType())) {
//				return false;
//			}
			if (action == null) {
				if (other.action != null) {
					return false;
				}
			} else if (!action.equals(other.action)) {
				return false;
			}
			if (state == null) {
				if (other.state != null) {
					return false;
				}
			} else if (!state.equals(other.state)) {
				return false;
			}
			return true;
		}
//		private MarkovRecord getOuterType() {
//			return MarkovRecord.this;
//		}
	}
	
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
		MarkovRecord.addStateAction(this.stateBefore, this.action, this.priceCost, this.timeCost);
		MarkovRecord.stateBeforeSet.add(this.stateBefore);
	}
	
	public MarkovRecord clone() {
		MarkovRecord record = new MarkovRecord(stateBefore, stateAfter, action, posibility, priceCost, timeCost);
		return record;
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
	
	public static boolean hasState(MarkovState state) {
		return stateList.contains(state);
	}
	
	public static void addState(MarkovState state) {
		int i = stateList.size();
		state.setId(i);
		stateList.add(state);
	}
	
	public static MarkovState getState(MarkovState state) {
		if (stateList.contains(state)) {
			for (int i = 0; i < stateList.size(); i++) {
				if (state.equals(stateList.get(i))) {
					return stateList.get(i);
				}
			}
		}
		return null;
	}
	
	public static boolean hasStateBefore(MarkovState state) {
		return stateBeforeSet.contains(state);
	}
	
	public static boolean hasStateAction(MarkovState state, MarkovAction action) {
		StateAction sa = new StateAction(state, action);
		if (stateAction2PriceCostMap.get(sa) == null) {
			return false;
		}
		return true;
	}
	
	public static void addStateAction(MarkovState state, MarkovAction action, double price, double time) {
		StateAction sa = new StateAction(state, action);
		stateAction2PriceCostMap.put(sa, price);
		stateAction2TimeCostMap.put(sa, time);
	}
}
