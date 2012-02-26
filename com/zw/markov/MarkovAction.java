package com.zw.markov;

public interface MarkovAction {
	int getOpNumber();
	MarkovAction clone();
	String toString();
}
