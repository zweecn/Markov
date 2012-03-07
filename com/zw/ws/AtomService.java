package com.zw.ws;

public class AtomService{
	
	public AtomService(int number, ServiceQoS qos) {
		super();
		this.number = number;
		this.qos = qos;
		this.redoCount = 0;
	}

	private int number;
	private ServiceQoS qos;
	private int redoCount;
	private boolean free;

	public AtomService clone() {
		AtomService serviceTemp = new AtomService(number, qos.clone());
		serviceTemp.redoCount = this.redoCount;
		serviceTemp.free = this.free;
		return serviceTemp;
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public ServiceQoS getQos() {
		return qos;
	}

	public void setQos(ServiceQoS qos) {
		this.qos = qos;
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

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		result = prime * result + ((qos == null) ? 0 : qos.hashCode());
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
		if (!(obj instanceof AtomService)) {
			return false;
		}
		AtomService other = (AtomService) obj;
		if (number != other.number) {
			return false;
		}
		if (qos == null) {
			if (other.qos != null) {
				return false;
			}
		} else if (!qos.equals(other.qos)) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return "" + this.number;
	}
}
