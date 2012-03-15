package com.zw.markov;

public abstract class AbstractMarkovAction implements MarkovAction{
	public abstract int getId();
	public abstract int getOpNumber();
	public abstract MarkovAction clone();
	public abstract String toString();
	public abstract String toSimpleString();
	public abstract void setId(int id);
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
