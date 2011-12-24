package com.zw.markov;

import java.util.Set;

public class MarkovState {
	private Set<ActivityState> activityStates;

	public Set<ActivityState> getActivityStates() {
		return activityStates;
	}

	public void setActivityStates(Set<ActivityState> activityStates) {
		this.activityStates = activityStates;
	}
	
	public void addActivityState(ActivityState activityState) {
		activityStates.add(activityState);
	}
	
	public void removeActivityState(ActivityState activityState) {
		activityStates.remove(activityState);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityStates == null) ? 0 : activityStates.hashCode());
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
		if (!(obj instanceof MarkovState)) {
			return false;
		}
		MarkovState other = (MarkovState) obj;
		if (activityStates == null) {
			if (other.activityStates != null) {
				return false;
			}
		} else if (!activityStates.equals(other.activityStates)) {
			return false;
		}
		return true;
	}
}
