package com.zw.markov;

public abstract class AbstractMarkovAction implements MarkovAction{
	public abstract int getId();
	public abstract int getOpNumber();
	public abstract MarkovAction clone();
	public abstract String toString();
}
