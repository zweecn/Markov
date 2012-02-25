package com.zw.markov;
import java.util.ArrayList;
import java.util.List;

import com.zw.ws.Activity;
import com.zw.ws.AtomService;
import com.zw.ws.ServiceFlow;

public class MarkovState extends ServiceFlow {
	
	public MarkovState() {
		super();
		this.stateId = MarkovInfo.getNextFreeStateID();
		init();
	}
	
	public MarkovState(MarkovState state) {
		super();
		this.stateId = MarkovInfo.getNextFreeStateID();
	}
	
	public MarkovState clone() {
		MarkovState stateNew = new MarkovState(this);
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
		stateNew.nextToDoActivity = (this.nextToDoActivity == null) ? null : this.nextToDoActivity.clone();
		stateNew.nextFreeService = (this.nextFreeService == null) ? null : this.nextFreeService.clone(); 
		stateNew.replaceAction = (this.replaceAction == null) ? null : this.replaceAction;
		
//		System.out.println("\nin clone:");
//		for (Activity at : this.activities) {
//			System.out.print(at.hashCode() + " ");
//		}
//		System.out.println("\nnew:");
//		for (Activity at : stateNew.activities) {
//			System.out.print(at.hashCode() + " ");
//		}
//		System.out.println();
		
		return stateNew;
	}
	
	public List<MarkovState> nextStates(BaseAction baseAction) {
		if (baseAction instanceof BaseAction) {
			List<MarkovState> states = new ArrayList<MarkovState>();
			
			states.add(this.clone());
			states.add(this.clone());
			
			switch (baseAction.getOpNumber()) {
			case MarkovInfo.A_NO_ACTION:
				states = aStepNoAction(states);
//				for (int i = 0; i < states.size(); i++) {
//					states.get(i).init();
//				}
				//System.out.println("5 " + this);
				return states;
			case MarkovInfo.A_RE_DO:
				if (this.isCurrFailed()) {
					states = aStepReDo(states);
//					for (int i = 0; i < states.size(); i++) {
//						states.get(i).init();
//					}
					return states;
				} else {
					break;
				}
			default:
				break;
			}
		}
		return null;
	}
	
	public List<MarkovState> nextStates(ReplaceAction replaceAction) {
		if (replaceAction instanceof ReplaceAction) {
			List<MarkovState> states = new ArrayList<MarkovState>();
			states.add(this.clone());
			states.add(this.clone());
			switch (replaceAction.getOpNumber()) {
			case MarkovInfo.A_REPLACE:
				states = aStepReplace(states);
//				for (int i = 0; i < states.size(); i++) {
//					states.get(i).init();
//				}
				return states;
			default:
				break;
			}
		}
		
		return null;
	}
	
	public Activity getFailedActivity() {
		for (int i = 0; i < nextToDoActivities.size(); i++) {
			if (nextToDoActivities.get(i).getX() < 0) {
				return nextToDoActivities.get(i);
			}
		}
		return nextToDoActivity;
	}

	public String toString() {
		String res = "[State " + String.format("%3d", this.stateId) + ":";
		res += " Global_state=";
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
		res += ", currTimeCost=" + String.format("%7.2f", currTotalTimeCost) + ", nextTimeCost=" 
				+ String.format("%6.2f", nextStepTimeCost) + " (";
		for (Activity at : super.activities) {
			res += "A" + String.format("%1s", at.getNumber()) + ".s=" + at.getBlindService().getNumber() 
					+  " x=" + String.format("%5.2f", at.getX()) + ", ";
		}
		res = res.trim() + ")]";
		return res;
	}

	public int getCurrGlobalState() {
		return currGlobalState;
	}

	public double getCurrTotalTimeCost() {
		return currTotalTimeCost;
	}
	
	public void addCurrTotalTimeCost(double cost) {
		this.currTotalTimeCost += cost;
	}
	
	public double getNextStepTimeCost() {
		return nextStepTimeCost;
	}

	public boolean isCurrFailed() {
		return currFailed;
	}

	public boolean isCurrFinished() {
		return currFinished;
	}

	public double getRedoTimeCost() {
		return redoTimeCost;
	}

	public Activity getNextToDoActivity() {
		return nextToDoActivity;
	}

	public List<Activity> getNextToDoActivities() {
		return nextToDoActivities;
	}

	public ReplaceAction getReplaceAction() {
		return replaceAction;
	}

	public AtomService getNextFreeService() {
		return nextFreeService;
	}

	public void setCurrGlobalState(int currGlobalState) {
		this.currGlobalState = currGlobalState;
	}
	
	public Activity getActivity(int activityNumber) {
		return super.getActivity(activityNumber);
	}

	public void setActivity(Activity activity) {
		for (int i = 0; i < super.activities.size(); i++) {
			if (super.activities.get(i).getNumber() == activity.getNumber()) {
				super.activities.set(i, activity);
			}
		}
	}
	
	private long stateId;
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

	private MarkovState init() {
		nextToDoActivity = null;
		//System.out.println("before init, nextToDoActivity:" + nextToDoActivity);
		currFailed = false;
		this.currGlobalState = MarkovInfo.S_UNKNOWN;
		currFinished = true;
		//currTotalTimeCost = 0;
		nextStepTimeCost = Double.MAX_VALUE;
		nextToDoActivities = new ArrayList<Activity>();
		for (int i = 0; i < super.getActivitySize(); i++) {
			if (super.activities.get(i).getX() < 1) {
				currFinished = false;
			}
			if (super.activities.get(i).getX() < 0) {
				currFailed = true;
				this.currGlobalState = MarkovInfo.S_FAILED;
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
		//System.out.println("after init, nextToDoActivity:" + nextToDoActivity.hashCode());
		return this;
	}
	
	private List<MarkovState> aStepNoAction(List<MarkovState> states) {
		
		for (Activity at : this.nextToDoActivities) {
			for (int i = 0; i < states.size(); i++) {
				Activity runActivity = states.get(i).getActivity(at.getNumber());
				runActivity.addX(nextStepTimeCost / runActivity.getBlindService().getQos().getExecTime());
			}
		}
		//System.out.println("6 " + this);
		//System.out.println("this:" + this.getNextToDoActivity().hashCode());
		//System.out.println("states:" + states.get(1).getNextToDoActivity().hashCode());
		//states.get(0).setCurrGlobalState(MarkovInfo.S_UNKNOWN);
		states.get(0).addCurrTotalTimeCost(nextStepTimeCost);
		
		//states.get(1).setCurrGlobalState(MarkovInfo.S_FAILED);
		states.get(1).addCurrTotalTimeCost(nextStepTimeCost);
		
		//states.get(1).getNextToDoActivity().setX(-1);  //mark
		states.get(1).getActivity(this.nextToDoActivity.getNumber()).setX(-1);
		//System.out.println("7 " + this);
		
		for (int i = 0; i < states.size(); i++) {
			states.get(i).init();
		}
//		System.out.println(states.get(1).getNextToDoActivity().getX());
//		System.out.println("Before aStepNoAction:" + states.get(1).getNextToDoActivity());
//		//System.out.println("In aStepNoAction:");
//		System.out.println(states.get(0).getNextToDoActivity());
//		System.out.println(states.get(1).getNextToDoActivity());
//		for (Activity at : states.get(0).activities) {
//			System.out.print(at + " ");
//		}
//		System.out.println();
//		for (Activity at : states.get(1).activities) {
//			System.out.print(at + " ");
//		}
//		System.out.println();
		//System.out.println("After aStepNoAction:" + states.get(1).getNextToDoActivity());
		return states;
	}
	
	private List<MarkovState> aStepReDo(List<MarkovState> states) {
		for (Activity at : this.nextToDoActivities) {
			for (int i = 0; i < states.size(); i++) {
				Activity runActivity = states.get(i).getActivity(at.getNumber());
				if (runActivity.getX() < 0) {
					redoTimeCost = Math.abs(runActivity.getX() * runActivity.getBlindService()
							.getQos().getExecTime());
				}
				runActivity.addX(nextStepTimeCost / runActivity.getBlindService().getQos().getExecTime());
			}
		}
		states.get(0).addCurrTotalTimeCost(nextStepTimeCost);
		
		states.get(1).addCurrTotalTimeCost(nextStepTimeCost);
		states.get(1).getNextToDoActivity().setX(-1);  //mark
		
		return states;
	}
	
	private List<MarkovState> aStepReplace(List<MarkovState> states) {
		nextFreeService = this.getFreeService();
		for (Activity at : this.nextToDoActivities) {
			for (int i = 0; i < states.size(); i++) {
				Activity runActivity = states.get(i).getActivity(at.getNumber());
				if (runActivity.getX() < 0) {
					runActivity.setBlindService(nextFreeService);
				}
				runActivity.addX(nextStepTimeCost / runActivity.getBlindService().getQos().getExecTime());
			}
		}
		states.get(0).addCurrTotalTimeCost(nextStepTimeCost);
		
		states.get(1).addCurrTotalTimeCost(nextStepTimeCost);
		states.get(1).getNextToDoActivity().setX(-1);  //mark
		
		return states;
	}
	
	
//	private void updateNextActivities(MarkovState stateAfter) {
//		for (Activity at : this.nextToDoActivities) {
//			Activity nextActivityTemp = stateAfter.getActivity(at.getNumber());
//			if (nextActivityTemp.getX() >= 0 &&nextActivityTemp.getX() < 1) {
//				if ((stateAfter.getActivity(at.getNumber()).getX() 
//					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()) >= 1) {
//					nextActivityTemp.setX(1);
//				} else {
//					nextActivityTemp.setX((stateAfter.getActivity(at.getNumber()).getX() 
//					+ nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime()));
//				}
//			} else if (nextActivityTemp.getX() < 0){
//				redoTimeCost = Math.abs(nextActivityTemp.getX()*nextActivityTemp.getBlindService().getQos().getExecTime());
//				nextActivityTemp.setX(nextStepTimeCost/nextActivityTemp.getBlindService().getQos().getExecTime());
//			}
//		}
//	}
	
	//保留接口
	private AtomService getFreeService() {
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
}
