package com.zw.ws;

import com.zw.markov.Markov;

public class Activity {
	public Activity() {
		super();
		this.redoCount = 0;
		this.replaceCount = 0;
	}
	public Activity(int activityNumber) {
		this.number = activityNumber;
		this.redoCount = 0;
		this.replaceCount = 0;
	}

	public Activity(AtomService blindService) {
		this.blindService = blindService;
		this.redoCount = 0;
		this.replaceCount = 0;
	}

	private AtomService blindService;
	private int number;
	private double x;
	private int state;
	private int redoCount;
	private int replaceCount;
	//private int id;

	private double predictTimeCost;
	private double predictPriceCost;

	public AtomService getBlindService() {
		return blindService;
	}
	public void setBlindService(AtomService blindService) {
		this.blindService = blindService;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int activityNumber) {
		this.number = activityNumber;
	}

	public double getX() {
		return x;
	}

	private void initActivityState() {
		if (x > 1) {
			this.state = Markov.S_DELAYED;
		} else if (x == 1) {
			this.state = Markov.S_SUCCEED;
		}else if (x >= 0 && x < 1) {
			this.state = Markov.S_NORMAL;
		} else if (x < 0) {
			this.state = Markov.S_FAILED;
		}
	}
	
	public void setX(double x) {
		this.x = x;
		initActivityState();
	}

	public void addX(double x) {
		if (this.x + x < 1 && this.x + x >= 0) {
			this.x = this.x + x;
		} else if (this.x + x >= 1) {
			this.x = 1;
		} else {
			System.err.println("Error in Activity.java Code 0x11");
		}
		initActivityState();
		/*
		if (this.x >= 0 || x > 1) {
			this.x = ((this.x + x) >= 1 ? 1 : this.x + x);
		} else {
			this.x = x;
		}*/
	}

	//	public int getId() {
	//		return id;
	//	}

	public Activity clone() {
		Activity activityTemp = new Activity();
		activityTemp.x = this.x;
		activityTemp.state = this.state;
		activityTemp.blindService = this.blindService.clone();
		activityTemp.number = this.number;
		activityTemp.redoCount = this.redoCount;
		activityTemp.replaceCount = this.replaceCount;
		return activityTemp;
	}


	public int getReplaceCount() {
		return replaceCount;
	}
	public void setReplaceCount(int replaceCount) {
		this.replaceCount = replaceCount;
	}
	public void addReplaceCount() {
		this.replaceCount++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!(obj instanceof Activity)) {
			return false;
		}
		Activity other = (Activity) obj;
		if (number != other.number) {
			return false;
		}
		if (blindService.getNumber() != other.blindService.getNumber()) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		return true;
	}

	public String toString() {
		String res = "";
		res += "[Activity " + String.format("%2d", this.getNumber());
		res += " x=" + String.format("%.2f", x) + "]";

		return res;
	}

	public double getPredictTimeCost() {
		return predictTimeCost;
	}
	public void setPredictTimeCost(double predictTimeCost) {
		this.predictTimeCost = predictTimeCost;
	}
	public double getPredictPriceCost() {
		return predictPriceCost;
	}
	public void setPredictPriceCost(double predictPriceCost) {
		this.predictPriceCost = predictPriceCost;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
