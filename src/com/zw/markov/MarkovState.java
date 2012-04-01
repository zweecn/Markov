package com.zw.markov;

import com.zw.ws.*;
import java.util.*;

public class MarkovState extends ActivityFlow {
	public MarkovState() {
		super();
		init();
	}

	public MarkovState(boolean withinit) {
		super();
	}
	
	private Activity faultActivity;
//	private int faultActivityState;
	private int globalState;

	private int id;
	private boolean finished;
	private MarkovState[] nextStateArray;
	private List<Activity> nextToDoActivityList;
	private Activity nextToDoActivity;

	public void init() {
//		System.out.println("In init before:" + this);
		
		boolean failed = false;
		nextToDoActivityList = new ArrayList<Activity>();
		nextToDoActivity = null;
		faultActivity = null;
		for (int i = 0; i < ActivityFlow.getActivitySize(); i++) {
			Activity ac = this.activities.get(i);
			if (ac.getX() < 0) {
				ac.setState(Markov.S_FAILED);
				failed = true;
			} else if (ac.getX() >= 0 && ac.getX() < 1) {
				ac.setState(Markov.S_NORMAL);
			} else if (ac.getX() == 1) {
				ac.setState(Markov.S_SUCCEED);
			} else if (ac.getX() > 1) {
				ac.setState(Markov.S_DELAYED);
			}
			
			switch (ac.getState()) {
			case Markov.S_FAILED:
			case Markov.S_DELAYED:
				faultActivity = ac;
				if (isPrefixActivitiesFinished(ac.getNumber())) {
					nextToDoActivityList.add(ac);
				}
				break;
			case Markov.S_NORMAL:
				if (isPrefixActivitiesFinished(ac.getNumber())) {
					nextToDoActivityList.add(ac);
				}
				break;
			default:
				break;
			}
			
		}
		if (this.activities.get(this.activities.size()-1).getState() == Markov.S_SUCCEED) {
			finished = true;
		} else {
			finished = false;
		}

//		for (Activity ac : this.activities) {
//			if (ac.getX() < 0) {
//				faultActivity = ac;
//				faultActivity.setState(Markov.S_FAILED);
//				failed = true;
//			}
//			if (ac.getX() > 1) {
//				faultActivity = ac;
//				faultActivity.setState(Markov.S_DELAYED);
//			}
//			if (ac.getState() != Markov.S_NORMAL || ac.getState() != Markov.S_SUCCEED) {
//				faultActivity = ac;
//			}
//			
//			if (ac.getX() < 1 && isPrefixActivitiesFinished(ac.getNumber())) {
//				nextToDoActivityList.add(ac);
//				finished = false;
//			}
//		}

		if (nextToDoActivityList.isEmpty()
				&& isPrefixActivitiesFinished(this.getActivity(ActivityFlow.getActivitySize()-1).getNumber())) {
			this.globalState = Markov.S_SUCCEED;
		} else {
			for (Activity ac : nextToDoActivityList) {
				if (nextToDoActivity == null || ac.getX() * nextToDoActivity.getBlindService().getQos().getExecTime() 
						< nextToDoActivity.getX() * nextToDoActivity.getBlindService().getQos().getExecTime()) {
					nextToDoActivity = ac;
				}
			}
			if (faultActivity == null) {
				if (failed) {
					this.globalState = Markov.S_FAILED;
				}
				if (finished && !failed || (this.activities.get(ActivityFlow.getActivitySize()-1).getX() >= 1)) {
					this.globalState = Markov.S_SUCCEED;
				}
				if (!finished && !failed) {
					this.globalState = Markov.S_NORMAL;
				}
			} else {
				if (this.globalState  != Markov.S_SUCCEED) {
					if (faultActivity.getState() != Markov.S_NORMAL) {
						this.globalState = faultActivity.getState();
					}
				}
			} //else
		} //else 
//		System.out.println("In init. state=" + this);
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
		MarkovState state = new MarkovState(false);
		state.id = this.id;
//		state.faultActivityState = this.faultActivityState;
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
//		System.out.println("nextToDoActivity=" +nextToDoActivityList.get(0).getNumber());
		if (this.isFailed()) {
			nextStateArray = new MarkovState[1];
			nextStateArray[0] = this.clone();
//			System.out.println("in if");
			return nextStateArray;
		} else {
			nextStateArray = new MarkovState[2];
			nextStateArray[0] = this.clone();
			nextStateArray[1] = this.clone();

			if (nextToDoActivityList.size() == 1) { //Seq
				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setX(1);
//				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setState(Markov.S_SUCCEED);
				nextStateArray[1].getActivity(nextToDoActivity.getNumber()).setX(-1);
//				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setState(Markov.S_FAILED);
			} else { //Ban
				double timeCostTemp = (1 - this.nextToDoActivity.getX()) * this
						.nextToDoActivity.getBlindService().getQos().getExecTime();
				for (Activity ac : this.nextToDoActivityList) {
					double xTemp = (timeCostTemp + ac.getX() * ac.getBlindService()
							.getQos().getExecTime()) / ac.getBlindService().getQos().getExecTime();
					if (xTemp >= 1) {
						nextStateArray[0].getActivity(ac.getNumber()).setX(1);
//						nextStateArray[0].getActivity(ac.getNumber()).setState(Markov.S_SUCCEED);
					} else {
						nextStateArray[0].getActivity(ac.getNumber()).setX(xTemp);
//						nextStateArray[0].getActivity(ac.getNumber()).setState(Markov.S_NORMAL);
					}
				}
				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setX(1);
//				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setState(Markov.S_SUCCEED);
				nextStateArray[1].getActivity(nextToDoActivity.getNumber()).setX(-1);
//				nextStateArray[0].getActivity(nextToDoActivity.getNumber()).setState(Markov.S_FAILED);
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
//				if (super.getActivity(it).getX() != 1) {
//				System.out.println("it=" + it + " super.getActivity(it).getState()=" +super.getActivity(it).getState());
				if (super.getActivity(it).getState() != Markov.S_SUCCEED) {
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

//	public int getFaultActivityState() {
//		return faultActivityState;
//	}

//	public void setFaultActivityState(int faultActivityState) {
//		this.faultActivityState = faultActivityState;
//	}

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
