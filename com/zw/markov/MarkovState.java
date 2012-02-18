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
	private boolean failed;
	
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
	
	private void nextStep() {
		failed = false;
		for (int i = 0; i < super.activities.size(); i++) {
			//System.out.println("nextStep, activity.getX:" + super.activities.get(i).getX());
			if (super.activities.get(i).getX() < 0) {
				failed = true;
				nextToDoActivity = super.activities.get(i);
				nextTimeCost = 0;
				return;
			}
		}
		
		nextTimeCost = Double.MAX_VALUE;
		for (int i = 0; i < super.activities.size(); i++) {
			double xTemp = super.activities.get(i).getX();
			//System.out.println("MarkovState, xTemp: " + xTemp);
			if (xTemp < 1 && xTemp >= 0) {
				double timeCostTemp = (1-xTemp) * super.activities.get(i).getBlindService().getQos().getExecTime();
				if (nextTimeCost > timeCostTemp) {
					//System.out.println("nextTimeCost:" + nextTimeCost + " timeCostTemp:" + timeCostTemp);
					nextTimeCost = timeCostTemp;
					nextToDoActivity = super.activities.get(i);
					//System.out.println("MarkovState, if, nextToDoActivity:" + nextToDoActivity.getNumber());
				}
			}
		}
	}
	
	public double getNextTimeCost() {
		return nextTimeCost;
	}
	
	public Activity getNextToDoActivity() {
		return nextToDoActivity;
	}
	
	public boolean isFailed() {
		return failed;
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
