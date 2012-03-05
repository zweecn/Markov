package com.zw.markov.alg;

import java.util.HashSet;
import java.util.Set;

import com.zw.markov.MarkovAction;

public class ActionTree {
	public ActionTree(MarkovAction action) {
		super();
		this.action = action;
		this.children = new HashSet<StateTree>();
	}
	
	MarkovAction action;
	StateTree parent;
	Set<StateTree> children;
	
	public StateTree getParent() {
		return parent;
	}
	public void setParent(StateTree parent) {
		this.parent = parent;
	}
	public MarkovAction getAction() {
		return action;
	}
	public void setAction(MarkovAction action) {
		this.action = action;
	}
	public Set<StateTree> getChildren() {
		return children;
	}
	public void setChildren(Set<StateTree> children) {
		this.children = children;
	}
	public void addChild(StateTree child) {
		this.children.add(child);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
		if (!(obj instanceof ActionTree)) {
			return false;
		}
		ActionTree other = (ActionTree) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
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
		return true;
	}
	
}
