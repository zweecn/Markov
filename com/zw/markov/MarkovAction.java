package com.zw.markov;

public interface MarkovAction {
	int getId();
	int getOpNumber();
	MarkovAction clone();
	String toString();
}
