package com.zw.markov.alg;

import java.util.ArrayList;
import java.util.List;

import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovState;

public class TreeNode {
	public TreeNode(int id) {
		super();
		this.id = id;
		this.children = new ArrayList<Integer>();
	}
	private int id;
	private int parent;
	private List<Integer> children;
	private MarkovState state;
	private MarkovAction action;
	
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
	public MarkovState getState() {
		return state;
	}
	public void setState(MarkovState state) {
		this.state = state;
	}
	public MarkovAction getAction() {
		return action;
	}
	public void setAction(MarkovAction action) {
		this.action = action;
	}
}
