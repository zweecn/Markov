package com.zw.markov;
import java.util.ArrayList;
import java.util.List;

import com.zw.ws.Activity;
import com.zw.ws.ServiceFlow;

public class MarkovState extends ServiceFlow {
	private int globalState;
	private double currentTimeCost; 
	
	public MarkovState() {
		super();
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
	
	public double nextStateTimeCost() {
		double timeCost = Double.MAX_VALUE;
		for (int i = 0; i < super.activities.size(); i++) {
			double xTemp = super.activities.get(i).getX();
			if (xTemp < 1 && xTemp > 0) {
				double timeCostTemp = xTemp * super.activities.get(i).getBlindService().getQos().getExecTime();
				if (timeCost > timeCostTemp) {
					timeCost = timeCostTemp;
				}
			}
		}
		return timeCost;
	}
	
	public boolean isFailed() {
		for (int i = 0; i < super.activities.size(); i++) {
			if (super.activities.get(i).getX() < 0) {
				return true;
			}
		}
		return false;
	}
	
	public Activity getFailedActivity() {
		for (int i = 0; i < super.activities.size(); i++) {
			if (super.activities.get(i).getX() < 0) {
				return super.activities.get(i);
			}
		}
		return null;
	}
	
}
