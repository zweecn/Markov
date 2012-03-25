package com.zw.markov;

import com.zw.ws.*;
import java.util.*;

public class MarkovState extends ActivityFlow {
	public MarkovState() {
		super();
		init();
	}
	
	private Activity faultActivity;
	private int faultActivityState;
	private int globalState;
	
	private int id;
	private boolean finished;
	private MarkovState[] nextStateArray;
	private List<Activity> nextToDoActivityList;
	private Activity nextToDoActivity;
	
	public void init() {
		finished = true;
		boolean failed = false;
		nextToDoActivityList = new ArrayList<Activity>();
		nextToDoActivity = null;
//		System.out.println();
//		System.out.println("Here this=" + this);
		for (Activity ac : this.activities) {
			//System.out.println("ac=" + ac.getNumber() + " ac.x=" + ac.getX());
			if (ac.getX() < 0) {
				faultActivity = ac;
				failed = true;
			}
			if (ac.getX() < 1 && isPrefixActivitiesFinished(ac.getNumber())) {
				//System.out.println("ac=" + ac.getNumber() + " x=" + ac.getX());
				nextToDoActivityList.add(ac);
				finished = false;
			}
		}
		//System.out.println();
		for (Activity ac : nextToDoActivityList) {
			if (nextToDoActivity == null || ac.getX() * nextToDoActivity.getBlindService().getQos().getExecTime() 
					< nextToDoActivity.getX() * nextToDoActivity.getBlindService().getQos().getExecTime()) {
				nextToDoActivity = ac;
			}
		}
		
//		if (faultActivityState == 0) {
//			if (faultActivity == null) {
//				this.faultActivityState = Markov.S_NORMAL;
//			} else if (faultActivity.getX() < 0) {
//				this.faultActivityState = Markov.S_FAILED;
//			}
//		} else {
//			this.globalState = faultActivityState;
//		}
		
		if (failed) {
			this.globalState = Markov.S_FAILED;
		}
		if (finished && !failed) {
			this.globalState = Markov.S_SUCCEED;
		}
		if (!finished && !failed) {
			this.globalState = Markov.S_NORMAL;
		}
//		if (faultActivityState != Markov.S_NORMAL && globalState == Markov.S_NORMAL) {
//			this.globalState = faultActivityState;
//		}
		if (faultActivityState != Markov.S_NORMAL) {
			this.globalState = faultActivityState;
		}
		//System.out.println("finished=" + finished + " failed=" + failed);
	}
	
	public MarkovState nextSecond() {
		if (this.isFinished()) {
			return null;
		}
		if (this.isFailed()) {
			return null;
		} 
		if (nextToDoActivityList.size() == 1) { //Seq
			AtomService sTemp = this.getActivity(nextToDoActivity.getNumber()).getBlindService();
			double xAdd = 1.0 / sTemp.getQos().getExecTime();
			this.getActivity(nextToDoActivity.getNumber()).addX(xAdd);
		} else { //Ban
			for (Activity ac : this.nextToDoActivityList) {
				double xAdd = 1.0 / ac.getBlindService().getQos().getExecTime();
				this.getActivity(ac.getNumber()).addX(xAdd);
			}
		}
		this.init();
		return this;
	}
	
	public MarkovState clone() {
		MarkovState state = new MarkovState();
		state.id = this.id;
//		state.faultActivity =  this.faultActivity;
		state.faultActivityState = Markov.S_NORMAL;
		state.faultActivity = (this.faultActivity == null ? null : this.faultActivity.clone());
		state.activities.clear();
		for (Activity ac : this.activities) {
			state.activities.add(ac.clone());
		}
		state.init();
		return state;
	}
	
	public MarkovState[] getNextTwoStates() {
		//System.out.println("this=" + this);
		//this.init();
		//System.out.println("nextToDoActivity=" +nextToDoActivityList.get(0).getNumber());
		if (this.isFailed()) {
			nextStateArray = new MarkovState[1];
			nextStateArray[0] = this.clone();
			return nextStateArray;
		} else {
			nextStateArray = new MarkovState[2];
			nextStateArray[0] = this.clone();
			nextStateArray[1] = this.clone();
			
			if (nextToDoActivityList.size() == 1) { //Seq
				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setX(1);
				nextStateArray[1].getActivity(nextToDoActivity.getNumber()).setX(-1);
			} else { //Ban
				double timeCostTemp = (1 - this.nextToDoActivity.getX()) * this
						.nextToDoActivity.getBlindService().getQos().getExecTime();
				for (Activity ac : this.nextToDoActivityList) {
					double xTemp = (timeCostTemp + ac.getX() * ac.getBlindService()
							.getQos().getExecTime()) / ac.getBlindService().getQos().getExecTime();
					if (xTemp >= 1) {
						nextStateArray[0].getActivity(ac.getNumber()).setX(1);
					} else {
						nextStateArray[0].getActivity(ac.getNumber()).setX(xTemp);
					}
				}
				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setX(1);
				nextStateArray[1].getActivity(nextToDoActivity.getNumber()).setX(-1);
			}
			nextStateArray[0].init();
			nextStateArray[1].init();
			
//			System.out.println("nextStateArray[0]=" + nextStateArray[0].getActivities());
//			System.out.println("nextStateArray[1]=" + nextStateArray[1].getActivities());
			return nextStateArray;
		}
	}
	
	
	public boolean isPrefixActivitiesFinished(int currActivityNumber) {
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
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((faultActivity == null) ? 0 : faultActivity.hashCode());
		result = prime * result 
				+ ((super.activities == null) ? 0 : super.activities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof MarkovState)) {
			return false;
		}
		MarkovState other = (MarkovState) obj;
		if (faultActivity == null) {
			if (other.faultActivity != null) {
				return false;
			}
		} else if (!faultActivity.equals(other.faultActivity)) {
			return false;
		}
		if (super.activities == null) {
			if (other.activities != null) {
				return false;
			}
		} else if (!super.activities.equals(other.activities)) {
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		String res = "[State " + String.format("%3d", this.id) + ":";
		
		String stateText = "";
		switch (globalState) {
		case Markov.S_NORMAL:
			stateText += "NORMAL";
			break;
		case Markov.S_FAILED:
			stateText += "FAILED";
			break;
		case Markov.S_SUCCEED:
			stateText += "SUCCEED";
			break;
		case Markov.S_DELAYED:
			stateText += "DELAYED";
			break;
		case Markov.S_PRICE_UP:
			stateText += "PRICE_UP";
			break;
		default:
			break;
		}
		res += " [";
		for (Activity at : super.activities) {
			res += "(A" + String.format("%1s", at.getNumber()) + ".s=" + at.getBlindService().getNumber() 
					+  " x=" + String.format("%5.2f", at.getX()) + ") ";
		}
		res = res.trim() + "] Global_state=";
		stateText = String.format("%-7s", stateText);
		res += stateText;
		res = res + "]";
		return res;
	}

	public String toSimpleString() {
		String res = "[S " + String.format("%3d", this.id) + ":";
		
		String stateText = "";
		switch (globalState) {
		case Markov.S_NORMAL:
			stateText += "NORMAL";
			break;
		case Markov.S_FAILED:
			stateText += "FAILED";
			break;
		case Markov.S_SUCCEED:
			stateText += "SUCCEED";
			break;
		case Markov.S_DELAYED:
			stateText += "DELAYED";
			break;
		case Markov.S_PRICE_UP:
			stateText += "PRICE_UP";
			break;
		default:
			break;
		}
		res = res.trim() + " ";
		stateText = String.format("%-7s", stateText);
		res += stateText;
		res = res + "]";
		return res;
	}
	
	public Activity getFaultActivity() {
		return faultActivity;
	}

	public void setFaultActivity(Activity faultActivity) {
		this.faultActivity = faultActivity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Activity getNextToDoActivity() {
		return nextToDoActivity;
	}

	public void setNextToDoActivity(Activity nextToDoActivity) {
		this.nextToDoActivity = nextToDoActivity;
	}

	public int getFaultActivityState() {
		return faultActivityState;
	}

	public void setFaultActivityState(int faultActivityState) {
		this.faultActivityState = faultActivityState;
	}

	public int getGlobalState() {
		return globalState;
	}

	public void setGlobalState(int globalState) {
		this.globalState = globalState;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isFailed() {
		return this.globalState == Markov.S_FAILED;
	}

	public void setFailed(boolean failed) {
		this.globalState = Markov.S_FAILED;
	}
}
