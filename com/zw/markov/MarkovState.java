package com.zw.markov;
import java.util.ArrayList;
import java.util.List;

import com.zw.ws.Activity;
import com.zw.ws.AtomService;
import com.zw.ws.ServiceFlow;

public class MarkovState extends ServiceFlow {
	
	public MarkovState() {
		super();
		init();
	}
	
	public MarkovState clone() {
		MarkovState stateNew = new MarkovState();
		stateNew.graph = new int[graph.length][graph.length]; //super graph
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				stateNew.graph[i][j] = graph[i][j];
			}
		}
		stateNew.activities = new ArrayList<Activity>(); //super activities
		for (Activity at : super.activities) {
			stateNew.activities.add(at.clone());
		}
		stateNew.services = this.services;
		stateNew.currGlobalState = this.currGlobalState;
		stateNew.currTotalTimeCost = this.currTotalTimeCost;
		stateNew.nextStepTimeCost = this.nextStepTimeCost;
		stateNew.currFailed = this.currFailed;
		stateNew.currFinished = this.currFinished;
		stateNew.redoTimeCost = this.redoTimeCost;
		stateNew.nextToDoActivities = new ArrayList<Activity>();
		for (Activity at : this.nextToDoActivities) {
			stateNew.nextToDoActivities.add(at.clone());
		}
		stateNew.nextToDoActivity = (this.nextToDoActivity == null) ? null : this.nextToDoActivity;
//		stateNew.replaceNewActivity = (this.replaceNewActivity == null) ? null : this.replaceNewActivity.clone();
//		stateNew.replaceOldActivity = (this.replaceOldActivity == null) ? null : this.replaceOldActivity.clone();
		stateNew.nextFreeService = (this.nextFreeService == null) ? null : this.nextFreeService.clone(); 
		stateNew.replaceAction = (this.replaceAction == null) ? null : this.replaceAction;
		
		return stateNew;
	}
	
	private int currGlobalState;
	private double currTotalTimeCost; 
	private double nextStepTimeCost;
	private boolean currFailed;
	private boolean currFinished;
	private double redoTimeCost;
	
	private Activity nextToDoActivity;
	private List<Activity> nextToDoActivities;
	private ReplaceAction replaceAction;
	
	private AtomService nextFreeService;

	private void init() {
		currFailed = false;
		this.currGlobalState = MarkovInfo.S_UNKNOWN;
		currFinished = true;
		nextStepTimeCost = Double.MAX_VALUE;
		nextToDoActivities = new ArrayList<Activity>();
		for (int i = 0; i < super.getActivitySize(); i++) {
			if (super.activities.get(i).getX() < 1) {
				currFinished = false;
			}
			if (super.activities.get(i).getX() < 0) {
				currFailed = true;
				this.currGlobalState = MarkovInfo.S_FAILED;
				//nextToDoActivity = super.activities.get(i);
				//nextStepTimeCost = super.activities.get(i).getBlindService().getQos().getExecTime();
			}
			if ((super.activities.get(i).getX() < 1) 
					&& isPrefixActivitiesFinished(super.activities.get(i).getNumber())) {
				nextToDoActivities.add(super.activities.get(i));
				double timeCostTemp = 0;
				if (super.activities.get(i).getX() >=0) {
					timeCostTemp = (1 - super.activities.get(i).getX())
						* super.activities.get(i).getBlindService().getQos().getExecTime();
				} else {
					timeCostTemp = super.activities.get(i).getBlindService().getQos().getExecTime();
				}
				if (nextStepTimeCost > timeCostTemp) {
					nextStepTimeCost = timeCostTemp;
					nextToDoActivity = super.activities.get(i);
				}
			}
		}
		if (currFinished) {
			nextStepTimeCost = 0;
			if (currFailed) {
				this.currGlobalState = MarkovInfo.S_FAILED;
			} else {
				this.currGlobalState = MarkovInfo.S_SUCCEED;
			}
		}
		if (nextStepTimeCost > MarkovInfo.TIME_STEP) {
			nextStepTimeCost = MarkovInfo.TIME_STEP;
		}
	}
	
	public int getGlobalState() {
		return currGlobalState;
	}

	public void setGlobalState(int globalState) {
		this.currGlobalState = globalState;
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

	

	public double getCurrentTimeCost() {
		return currTotalTimeCost;
	}

	public void setCurrentTimeCost(double currentTimeCost) {
		this.currTotalTimeCost = currentTimeCost;
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
		
		currFailed = false;
		currFinished = true;
		
		nextStepTimeCost = Double.MAX_VALUE;
		
		List<Integer> activityNumbers = new ArrayList<Integer>();
		for (Activity at : super.activities) {
			activityNumbers.add(at.getNumber());
		}
		
		for (int i = 0; i < super.getActivitySize(); i++) {
			if (super.activities.get(i).getX() < 0) {
				currFailed = true;
				this.currGlobalState = MarkovInfo.S_FAILED;
				nextToDoActivity = super.activities.get(i);
				nextStepTimeCost = super.activities.get(i).getBlindService().getQos().getExecTime();
				//System.out.println("In if, nextTimeCost=" + nextTimeCost);
				return;
			}
		}
		
		for (int i = 0; i < super.getActivitySize(); i++) {
			//System.out.println("MarkovState, nextStep: " + super.activities.get(i).getX() + " i=" + i);
			if (super.activities.get(i).getX() < 1) {
				currFinished = false;
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
				if (nextStepTimeCost > timeCostTemp) {
					nextStepTimeCost = timeCostTemp;
					nextToDoActivity = super.activities.get(i);
					//System.out.println("In if 2, nextTimeCost=" + nextTimeCost);
				}
			}
		}
		System.out.println("nextTodoActivities:");
		for (Activity at : nextToDoActivities) {
			System.out.print(at.getNumber() + " ");
		}
		System.out.println();
		
		if (nextStepTimeCost > MarkovInfo.TIME_STEP) {
			nextStepTimeCost = MarkovInfo.TIME_STEP;
			//System.out.println("In if 3, nextTimeCost=" + nextTimeCost);
		}
		if (nextStepTimeCost == Double.MAX_VALUE) {
			nextStepTimeCost = 0;
		}
	}
	
	public void updateNextActivities(MarkovState stateAfter) {
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			if (nextActivityTemp.getX() >= 0 &&nextActivityTemp.getX() < 1) {
				if ((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()) >= 1) {
					nextActivityTemp.setX(1);
				} else {
					nextActivityTemp.setX((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()));
				}
			} else if (nextActivityTemp.getX() < 0){
				//System.out.println("nextActivityTemp.getX():" + nextActivityTemp.getX());
				redoTimeCost = Math.abs(nextActivityTemp.getX()*nextActivityTemp.getBlindService().getQos().getExecTime());
				nextActivityTemp.setX(nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
				//System.out.println("redoTimeCost:" + redoTimeCost);
				//System.out.println("After, nextActivityTemp.getX():" + nextActivityTemp.getX());
			}
		}
	}

	//以下nextTimeCost有问题, setX没有检查是否大于1
	public MarkovState nextUnknownState() {
		MarkovState stateAfter = this.clone();
		updateNextActivities(stateAfter);
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		return stateAfter;
	}

	public MarkovState nextFailedState() {
		MarkovState stateAfter = this.clone();
		updateNextActivities(stateAfter);
		stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_FAILED);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		return stateAfter;
	}
	
	public MarkovState nextReDoUnknownState() {
		MarkovState stateAfter = this.clone();
		updateNextActivities(stateAfter);
		//stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		return stateAfter;
	}
	
	public MarkovState nextReDoFailedState() {
		MarkovState stateAfter = this.clone();
		updateNextActivities(stateAfter);
		stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_FAILED);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		return stateAfter;
	}
	
	public MarkovState nextReplaceUnknownState() {
		System.out.println("In  nextReplaceUnknownState:" + this.replaceOldActivity);
		MarkovState stateAfter = this.clone();
		nextFreeService = getFreeService(); //Mark, this have, failed not have
		updateReplaceNextActivities(stateAfter);
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		//updateReplaceNextActivities(stateAfter);
		System.out.println("out nextReplaceUnknownState:" + this.replaceOldActivity);
		return stateAfter;
	}
	
	public MarkovState nextReplaceFailedState() {
		MarkovState stateAfter = this.clone();
		updateReplaceNextActivities(stateAfter);
		stateAfter.getNextToDoActivity().setX(-1);
		stateAfter.setGlobalState(MarkovInfo.S_UNKNOWN);
		stateAfter.setCurrentTimeCost(nextStepTimeCost + this.currTotalTimeCost);
		stateAfter.nextStep();
		//updateReplaceNextActivities(stateAfter);
		return stateAfter;
	}
	
	public void updateReplaceNextActivities(MarkovState stateAfter) {
		System.out.println("In  updateReplaceNextActivities:" + this.replaceOldActivity);
		for (Activity at : nextToDoActivities) {
			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
			System.out.println("nextTodoactivities:" + nextActivityTemp.getNumber());
			if (nextActivityTemp.getX() >= 0 &&nextActivityTemp.getX() < 1) {
				if ((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()) >= 1) {
					nextActivityTemp.setX(1);
				} else {
					nextActivityTemp.setX((stateAfter.getActivity(at.getNumber()).getX() 
					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()));
				}
			} else if (nextActivityTemp.getX() < 0){
				
				//redoTimeCost = Math.abs(nextActivityTemp.getX()*nextActivityTemp.getBlindService().getQos().getExecTime());
				replaceOldActivity = nextActivityTemp.clone();
				nextActivityTemp.setBlindService(nextFreeService);
				nextActivityTemp.addReplaceCount();
				//System.out.println(service.getNumber());
				replaceNewActivity = nextActivityTemp.clone();
				nextActivityTemp.setX(nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
			}
		}
		System.out.println("out updateReplaceNextActivities:" + this.replaceOldActivity);
	}
	
	//保留接口
	public AtomService getFreeService() {
		for (int i = 0; i < super.services.size(); i++) {
			//System.out.print(services.get(i).isFree() + " ");
			if (super.services.get(i).isFree()) {
				super.services.get(i).setFree(false);
				return super.services.get(i);
			}
		}
		//System.out.println();
		return null;
	}
	
	public Activity getReplaceOldActivity() {
		return replaceOldActivity;
	}

	public Activity getReplaceNewActivity() {
		return replaceNewActivity;
	}
	
	public double getReDoTimeCost() {
		return redoTimeCost;
	}

	public double getNextTimeCost() {
		return nextStepTimeCost;
	}

	public Activity getNextToDoActivity() {
		return nextToDoActivity;
	}
	
	public List<Activity> getNextToDoActivities () {
		return nextToDoActivities;
	}

	public boolean isFailed() {
		return currFailed;
	}
	
	public boolean isFinished() {
		return currFinished;
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
		temp = Double.doubleToLongBits(currTotalTimeCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (currFailed ? 1231 : 1237);
		result = prime * result + (currFinished ? 1231 : 1237);
		result = prime * result + currGlobalState;
		temp = Double.doubleToLongBits(nextStepTimeCost);
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
		if (Double.doubleToLongBits(currTotalTimeCost) != Double
				.doubleToLongBits(other.currTotalTimeCost)) {
			return false;
		}
		if (currFailed != other.currFailed) {
			return false;
		}
		if (currFinished != other.currFinished) {
			return false;
		}
		if (currGlobalState != other.currGlobalState) {
			return false;
		}
		if (Double.doubleToLongBits(nextStepTimeCost) != Double
				.doubleToLongBits(other.nextStepTimeCost)) {
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
		switch (currGlobalState) {
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
		res += ", currTimeCost=" + String.format("%7.2f", currTotalTimeCost) + ", nextTimeCost=" + String.format("%6.2f", nextStepTimeCost) + " (";
		for (Activity at : super.activities) {
			res += "A" + String.format("%1s", at.getNumber()) + ".s=" + at.getBlindService().getNumber() +  " x=" + String.format("%5.2f", at.getX()) + ", ";
		}
		res = res.trim() + ")]";
		return res;
	}
}
