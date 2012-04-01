package com.zw.markov;

import java.util.Comparator;


public class StateComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		MarkovState s1 = (MarkovState) o1;
		MarkovState s2 = (MarkovState) o2;
		for (int i = 0; i < s1.getActivities().size(); i++) {
			double x1 = s1.getActivities().get(i).getX();
			double x2 = s2.getActivities().get(i).getX();
			if (x1 < x2) {
				return -1;
			} else if (x1 > x2) {
				return 1;
			}
		}
		
		return 0;
	}

}
