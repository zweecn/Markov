package com.zw.markov;

public interface MarkovAction {
	int getId();
	void setId(int id);
	int getOpNumber();
	MarkovAction clone();
	String toString();
	int hashCode();
	boolean equals(Object obj);
	
	/*double getPriceCost();
	double getTimeCost();
	double getPosibility();*/
}
