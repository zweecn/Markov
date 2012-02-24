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
	private double redoTimeCost; 
	
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
		
		stateTemp.nextStep();
		//stateTemp.redoTimeCost = this.redoTimeCost;
		
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
		//System.out.println("\nBegin nextStep()-------------------------------");
		
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
				this.globalState = MarkovInfo.S_FAILED;
				nextToDoActivity = super.activities.get(i);
				nextTimeCost = super.activities.get(i).getBlindService().getQos().getExecTime();
				//System.out.println("In if, nextTimeCost=" + nextTimeCost);
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
		//System.out.println(super.getActivitySize());
		for (int i = 0; i < super.getActivitySize(); i++) {
			//System.out.println("Service " + i + " x=" + super.activities.get(i).getX());
			if ((super.activities.get(i).getX() < 1)// && super.activities.get(i).getX() >=0) 
					&& isPrefixActivitiesFinished(super.activities.get(i).getNumber())) {
				nextToDoActivities.add(super.activities.get(i));
				double timeCostTemp = 0;
				if (super.activities.get(i).getX() >=0) {
					timeCostTemp = (1 - super.activities.get(i).getX())
						* super.activities.get(i).getBlindService().getQos().getExecTime();
				} else {
					timeCostTemp = super.activities.get(i).getBlindService().getQos().getExecTime();
				}
				if (nextTimeCost > timeCostTemp) {
					nextTimeCost = timeCostTemp;
					nextToDoActivity = super.activities.get(i);
					//System.out.println("In if 2, nextTimeCost=" + nextTimeCost);
				}
			}
		}
		
		if (nextTimeCost > MarkovInfo.TIME_STEP) {
			nextTimeCost = MarkovInfo.TIME_STEP;
			//System.out.println("In if 3, nextTimeCost=" + nextTimeCost);
		}
		if (nextTimeCost == Double.MAX_VALUE) {
			nextTimeCost = 0;
		}
		//System.out.println("NextToDoActivity=" + nextToDoActivity);
		//System.out.println("In nextStep(), nextTimeCost=" + nextTimeCost + " nextToDoActivity=" + nextToDoActivity.getNumber());
		//System.out.println("End nextStep()-------------------------------\n");
		
	}

	//以下nextTimeCost有问题, setX没有检查是否大于1
	public MarkovState nextUnknownState() {
		//System.out.println("In nextUnknownState");
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

	public MarkovState nextFailedState() {
		//System.out.println("In nextFailedState");
		MarkovState stateAfter = this.clone();
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			nextActivityTemp.setX(stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
		}
		stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_FAILED);
		//System.out.println("nextTimeCost=" + nextTimeCost + " currentTimeCost=" + currentTimeCost);
		stateAfter.setCurrentTimeCost(nextTimeCost + this.currentTimeCost);
		stateAfter.nextStep();
		
		return stateAfter;
	}
	
	public MarkovState nextReDoUnknownState() {
		//System.out.println("In nextReDoUnknownState");
		MarkovState stateAfter = this.clone();
		//System.out.println("nextTimeCost:" + nextTimeCost);
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			if (nextActivityTemp.getX() >= 0 &&nextActivityTemp.getX() < 1) {
				if ((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()) >= 1) {
					nextActivityTemp.setX(1);
				} else {
					nextActivityTemp.setX((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()));
				}
			} else if (nextActivityTemp.getX() < 0){
				//System.out.println("nextActivityTemp.getX():" + nextActivityTemp.getX());
				redoTimeCost = Math.abs(nextActivityTemp.getX()*nextActivityTemp.getBlindService().getQos().getExecTime());
				nextActivityTemp.setX(nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
				//System.out.println("redoTimeCost:" + redoTimeCost);
				//System.out.println("After, nextActivityTemp.getX():" + nextActivityTemp.getX());
			}
		}
		//stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextTimeCost + this.currentTimeCost);
		stateAfter.nextStep();
		
		return stateAfter;
	}
	
	public MarkovState nextReDoFailedState() {
		//System.out.println("In nextReDoFailedState");
		MarkovState stateAfter = this.clone();
		
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			if (nextActivityTemp.getX() >= 0 &&nextActivityTemp.getX() < 1) {
				if ((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()) >= 1) {
					nextActivityTemp.setX(1);
				} else {
					nextActivityTemp.setX((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()));
				}
			} else if (nextActivityTemp.getX() < 0) {
				redoTimeCost = Math.abs(nextActivityTemp.getX()*nextActivityTemp.getBlindService().getQos().getExecTime());
				nextActivityTemp.setX(nextTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
			}
		}
		//stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_FAILED);
		stateAfter.setCurrentTimeCost(nextTimeCost + this.currentTimeCost);
		stateAfter.nextStep();
		
		return stateAfter;
	}
	
	
	public double getReDoTimeCost() {
		return redoTimeCost;
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
		for (int i = 0; i < nextToDoActivities.size(); i++) {
			if (nextToDoActivities.get(i).getX() < 0) {
				return nextToDoActivities.get(i);
			}
		}
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
		result = prime * result + (finished ? 1231 : 1237);
		result = prime * result + globalState;
		temp = Double.doubleToLongBits(nextTimeCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((nextToDoActivities == null) ? 0 : nextToDoActivities
						.hashCode());
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
		if (finished != other.finished) {
			return false;
		}
		if (globalState != other.globalState) {
			return false;
		}
		if (Double.doubleToLongBits(nextTimeCost) != Double
				.doubleToLongBits(other.nextTimeCost)) {
			return false;
		}
		if (nextToDoActivities == null) {
			if (other.nextToDoActivities != null) {
				return false;
			}
		} else if (!nextToDoActivities.equals(other.nextToDoActivities)) {
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
	
	public String toString() {
		String res = "";
		res += "[State: Global_state=";
		switch (globalState) {
		case MarkovInfo.S_UNKNOWN:
			res += "UNKNOW";
			break;
		case MarkovInfo.S_FAILED:
			res += "FAILED";
			break;
		case MarkovInfo.S_SUCCEED:
			res += "SUCCEED";
			break;
		case MarkovInfo.S_DELAYED:
			res += "DELAYED";
			break;
		case MarkovInfo.S_PRICE_UP:
			res += "PRICE_UP";
			break;
		default:
			break;
		}
		res += ", currTimeCost=" + String.format("%7.2f", currentTimeCost) + ", nextTimeCost=" + String.format("%6.2f", nextTimeCost) + " (";
		for (Activity at : super.activities) {
			res += "A" + String.format("%1s", at.getNumber()) + ".s=" + at.getBlindService().getNumber() +  " x=" + String.format("%5.2f", at.getX()) + ", ";
		}
		res = res.trim() + ")]";
		return res;
	}
}
