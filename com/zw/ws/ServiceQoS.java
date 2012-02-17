package com.zw.ws;

public class ServiceQoS {
	public ServiceQoS(int price, double reliability, int execTime) {
		super();
		this.price = price;
		this.reliability = reliability;
		this.execTime = execTime;
	}
	
	private int price;
	private double reliability;
	private int execTime;
	
	public ServiceQoS clone() {
		ServiceQoS qoS = new ServiceQoS(price, reliability, execTime);
		return qoS;
	}

	public double getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public double getReliability() {
		return reliability;
	}
	public void setReliability(double reliability) {
		this.reliability = reliability;
	}
	public double getExecTime() {
		return execTime;
	}
	public void setExecTime(int execTime) {
		this.execTime = execTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(execTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(reliability);
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
		if (!(obj instanceof ServiceQoS)) {
			return false;
		}
		ServiceQoS other = (ServiceQoS) obj;
		if (Double.doubleToLongBits(execTime) != Double
				.doubleToLongBits(other.execTime)) {
			return false;
		}
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price)) {
			return false;
		}
		if (Double.doubleToLongBits(reliability) != Double
				.doubleToLongBits(other.reliability)) {
			return false;
		}
		return true;
	}
}
