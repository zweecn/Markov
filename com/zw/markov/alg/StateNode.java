package com.zw.markov.alg;

import java.util.ArrayList;
import java.util.List;

public class StateNode {
	public StateNode(int id) {
		super();
		this.id = id;
		this.children = new ArrayList<Integer>();
	}
	private int id;
	private int parent;
	private List<Integer> children;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public List<Integer> getChildren() {
		return children;
	}
	public void setChildren(List<Integer> children) {
		this.children = children;
	}
	public void addChild(int child) {
		this.children.add(child);
	}
	public boolean hasChild() {
		return (this.children != null && !this.children.isEmpty()) ;
	}
	
	public String toString() {
		String res = "parent:" + String.format("%2d", parent) + " " 
				+ "id:" + String.format("%2d", id) + " children:";
		for (Integer i : children) {
			res += String.format("%2d", i) + " ";
		}
		return res;
	}
}
