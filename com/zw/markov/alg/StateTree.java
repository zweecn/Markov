package com.zw.markov.alg;

import java.util.HashSet;
import java.util.Set;

import com.zw.markov.MarkovState;

public class StateTree {
	public StateTree(MarkovState state) {
		super();
		this.state = state;
		this.children = new HashSet<ActionTree>();
	}
	
	MarkovState state;
	ActionTree parent;
	Set<ActionTree> children;
	
	public ActionTree getParent() {
		return parent;
	}
	public void setParent(ActionTree parent) {
		this.parent = parent;
	}
	public MarkovState getState() {
		return state;
	}
	public void setState(MarkovState state) {
		this.state = state;
	}
	public Set<ActionTree> getChildren() {
		return children;
	}
	public void setChildren(HashSet<ActionTree> children) {
		this.children = children;
	}
	public void addChild(ActionTree child) {
		this.children.add(child);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		if (!(obj instanceof StateTree)) {
			return false;
		}
		StateTree other = (StateTree) obj;
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		return true;
	}
}
