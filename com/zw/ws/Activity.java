package com.zw.ws;

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
	private int redoCount;
	private int replaceCount;
	private int id;
	
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
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void addX(double x) {
		if (this.x >= 0) {
			this.x = (this.x + x >= 1 ? 1 : this.x + x);
		} else {
			this.x = x;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Activity clone() {
		Activity activityTemp = new Activity();
		//System.out.println("Before activityTemp:" + activityTemp + "\t this:" + this);
		activityTemp.x = this.x;
		activityTemp.blindService = this.blindService.clone();
		activityTemp.number = this.number;
		activityTemp.redoCount = this.redoCount;
		activityTemp.replaceCount = this.replaceCount;
				
		//System.out.println("After activityTemp:" + activityTemp + "\t this:" + this);
		//System.out.println();
		
		return activityTemp;
	}
	
	
	public int getRedoCount() {
		return redoCount;
	}

	public void setRedoCount(int redoCount) {
		this.redoCount = redoCount;
	}
	
	public void addRedoCount() {
		this.redoCount++;
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
		result = prime * result
				+ ((blindService == null) ? 0 : blindService.hashCode());
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
		if (blindService == null) {
			if (other.blindService != null) {
				return false;
			}
		} else if (!blindService.equals(other.blindService)) {
			return false;
		}
		if (number != other.number) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		return true;
	}
	
}
