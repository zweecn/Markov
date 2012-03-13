package com.zw.markov;

public class BaseAction extends AbstractMarkovAction implements MarkovAction{
	public BaseAction(int activityNumber, int opNumber, int oldServiceNumber) {
		this.currActivityNumber = activityNumber;
		this.opNumber = opNumber;
		this.oldServiceNumber = oldServiceNumber;
//		id = BaseAction.getNextFreeId();
	}
	
	private int id;
//	private static int freeid = 0;
	
	protected int currActivityNumber;
	protected int opNumber;
	protected int oldServiceNumber;

//	public static int getNextFreeId() {
//		return freeid++;
//	}
	
	public int getCurrActivityNumber() {
		return currActivityNumber;
	}

	public int getOpNumber() {
		return opNumber;
	}
	
	public BaseAction clone() {
		BaseAction action = new BaseAction(this.currActivityNumber, this.opNumber, this.oldServiceNumber);
		return action;
	}
	
	public String toString() {
		String res = "";
		String actionText = "";
		switch (opNumber) {
		case Markov.A_NO_ACTION:
			actionText = "NO_ACTION";
			break;
		case Markov.A_TERMINATE:
			actionText = "TERMINATE";
			break;
		case Markov.A_RE_DO:
			actionText = "RE_DO";
			break;
		case Markov.A_REPLACE:
			actionText = "REPLACE";
			break;
		case Markov.A_RE_COMPOSITE:
			actionText = "RE_COMPOSITE";
			break;
		default:
			break;
		}
		String oldNew = " (old=" + oldServiceNumber + " new=" + oldServiceNumber +")"; 
		res += "[Action: " +  String.format("%3d", getId()) + " " + String.format("%-12s", actionText) 
				+ " Activity " + String.format("%1d", currActivityNumber)
				+ String.format("%-23s", oldNew)
				+ "]";
		return res;
	}



	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currActivityNumber;
		result = prime * result + oldServiceNumber;
		result = prime * result + opNumber;
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
		if (!(obj instanceof BaseAction)) {
			return false;
		}
		BaseAction other = (BaseAction) obj;
		if (currActivityNumber != other.currActivityNumber) {
			return false;
		}
		if (oldServiceNumber != other.oldServiceNumber) {
			return false;
		}
		if (opNumber != other.opNumber) {
			return false;
		}
		return true;
	}

	/*
	@Override
	public double getPriceCost() {
		return ActivityFlow.getService(oldServiceNumber).getQos().getPrice();
	}

	@Override
	public double getTimeCost() {
		return ActivityFlow.getService(oldServiceNumber).getQos().getExecTime();
	}

	@Override
	public double getPosibility() {
		return ActivityFlow.getService(oldServiceNumber).getQos().getReliability();
	}*/
}
