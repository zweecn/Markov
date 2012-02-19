package com.zw.markov;
import java.util.ArrayList;
import java.util.List;

import com.zw.ws.Activity;
import com.zw.ws.ServiceFlow;

public class MarkovState extends ServiceFlow {
	private int globalState;
	private double currentTimeCost; 

	private double nextTimeCost;
	private Activity nextToDoActivity;
	private List<Activity> nextToDoActivities;
	private boolean failed;
	private boolean finished;

	public MarkovState() {
		super();
		//System.out.println("MarkovState constructor.");
		nextStep();
	}

	public int getGlobalState() {
		return globalState;
	}

	public void setGlobalState(int globalState) {
		this.globalState = globalState;
	}

	public int[][] getGraph() {
		return super.graph;
	}

	public void setGraph(int[][] graph) {
		super.graph = graph;
	}

	public List<Activity> getActivities() {
		return super.activities;
	}

	public void setActivities(List<Activity> activities) {
		super.activities = activities;
	}

	public Activity getActivity(int activityNumber) {
		return super.activities.get(activityNumber);
	}

	public void setActivity(Activity activity) {
		for (int i = 0; i < super.activities.size(); i++) {
			if (super.activities.get(i).getNumber() == activity.getNumber()) {
				super.activities.set(i, activity);
			}
		}
	}

	public MarkovState clone() {
		MarkovState stateTemp = new MarkovState();

		int[][] graphTemp = new int[graph.length][graph.length];
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				graphTemp[i][j] = graph[i][j];
			}
		}

		List<Activity> activitiesTemp = new ArrayList<Activity>();
		for (int i = 0; i < super.activities.size(); i++) {
			activitiesTemp.add(super.activities.get(i).clone());
		}

		stateTemp.setGraph(graphTemp);
		stateTemp.setGlobalState(this.globalState);
		stateTemp.setActivities(activitiesTemp);
		stateTemp.setCurrentTimeCost(currentTimeCost);
		return stateTemp;
	}

	public double getCurrentTimeCost() {
		return currentTimeCost;
	}

	public void setCurrentTimeCost(double currentTimeCost) {
		this.currentTimeCost = currentTimeCost;
	}
	
	private boolean isPrefixActivitiesFinished(int currActivityNumber) {
		List<Integer> prefixActivityNumbers = super.getPrefixActivityNumbers(currActivityNumber);
		if (prefixActivityNumbers != null && !prefixActivityNumbers.isEmpty()) {
			for (Integer it : prefixActivityNumbers) {
				if (super.getActivity(it).getX() != 1) {
					return false;
				}
			}
		}
		return true;
	}

	// BUGS
	private void nextStep() {
		failed = false;
		finished = true;
		
		nextTimeCost = Double.MAX_VALUE;
		
		List<Integer> activityNumbers = new ArrayList<Integer>();
		for (Activity at : super.activities) {
			activityNumbers.add(at.getNumber());
		}
		
		for (int i = 0; i < super.getActivitySize(); i++) {
			if (super.activities.get(i).getX() < 0) {
				failed = true;
				nextToDoActivity = super.activities.get(i);
				nextTimeCost = 0;
				return;
			}
		}
		
		for (int i = 0; i < super.getActivitySize(); i++) {
			//System.out.println("MarkovState, nextStep: " + super.activities.get(i).getX() + " i=" + i);
			if (super.activities.get(i).getX() < 1) {
				finished = false;
				break;
			}
		}
		
		nextToDoActivities = new ArrayList<Activity>();
		for (int i = 0; i < super.getActivitySize(); i++) {
			if (super.activities.get(i).getX() < 1 && super.activities.get(i).getX() >=0 
					&& isPrefixActivitiesFinished(super.activities.get(i).getNumber())) {
				nextToDoActivities.add(super.activities.get(i));
				double timeCostTemp = (1 - super.activities.get(i).getX()) 
						* super.activities.get(i).getBlindService().getQos().getExecTime();
				if (nextTimeCost > timeCostTemp) {
					nextTimeCost = timeCostTemp;
					nextToDoActivity = super.activities.get(i);
				}
			}
		}
	}

	public MarkovState normalNextState() {
		MarkovState stateAfter = this.clone();
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			nextActivityTemp.setX(stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
		}
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextTimeCost + this.currentTimeCost);
		stateAfter.nextStep();
		
		return stateAfter;
	}


	public double getNextTimeCost() {
		return nextTimeCost;
	}

	public Activity getNextToDoActivity() {
		return nextToDoActivity;
	}
	
	public List<Activity> getNextToDoActivities () {
		return nextToDoActivities;
	}

	public boolean isFailed() {
		return failed;
	}
	
	public boolean isFinished() {
		return finished;
	}
	public Activity getFailedActivity() {
		return nextToDoActivity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentTimeCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (failed ? 1231 : 1237);
		result = prime * result + globalState;
		temp = Double.doubleToLongBits(nextTimeCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((nextToDoActivity == null) ? 0 : nextToDoActivity.hashCode());
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
		if (Double.doubleToLongBits(currentTimeCost) != Double
				.doubleToLongBits(other.currentTimeCost)) {
			return false;
		}
		if (failed != other.failed) {
			return false;
		}
		if (globalState != other.globalState) {
			return false;
		}
		if (Double.doubleToLongBits(nextTimeCost) != Double
				.doubleToLongBits(other.nextTimeCost)) {
			return false;
		}
		if (nextToDoActivity == null) {
			if (other.nextToDoActivity != null) {
				return false;
			}
		} else if (!nextToDoActivity.equals(other.nextToDoActivity)) {
			return false;
		}
		return true;
	}

}
