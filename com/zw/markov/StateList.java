package com.zw.markov;

import java.util.ArrayList;
import java.util.List;


public class StateList {
	public StateList() {
		list = new ArrayList<MarkovState>();
	}
	
	private List<MarkovState> list;
	
	public int add(MarkovState state) {
		if (state != null) {
			list.add(state);
		}
		
		if (list.isEmpty() || list.size() == 1) {
			return -1;
		}
		
		for (int i = list.size()-1; i >=0; i--) {
			
		}
		
		return -1;
	}
	
}
