package com.zw.markov;

public class ActivityState {
	private int acitivityNumber;
	private int serviceNumber;
	private int serviceState;
	
	public int getAcitivityNumber() {
		return acitivityNumber;
	}
	public void setAcitivityNumber(int acitivityNumber) {
		this.acitivityNumber = acitivityNumber;
	}
	public int getServiceNumber() {
		return serviceNumber;
	}
	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}
	public int getServiceState() {
		return serviceState;
	}
	public void setServiceState(int serviceState) {
		this.serviceState = serviceState;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + acitivityNumber;
		result = prime * result + serviceNumber;
		result = prime * result + serviceState;
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
		if (!(obj instanceof ActivityState)) {
			return false;
		}
		ActivityState other = (ActivityState) obj;
		if (acitivityNumber != other.acitivityNumber) {
			return false;
		}
		if (serviceNumber != other.serviceNumber) {
			return false;
		}
		if (serviceState != other.serviceState) {
			return false;
		}
		return true;
	}
	
}
