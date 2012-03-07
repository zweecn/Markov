package com.zw.markov;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zw.ws.AtomService;

public class ReCompositeAction extends BaseAction implements MarkovAction {
	public ReCompositeAction(int activityNumber, int opNumber,
			int oldServiceNumber) {
		super(activityNumber, opNumber, oldServiceNumber);
	}
	
	private Map<AtomService, AtomService> oldNewReplaceServiceMap;
	
	public Map<AtomService, AtomService> getOldNewReplaceServiceMap() {
		return oldNewReplaceServiceMap;
	}

	public void setOldNewReplaceServiceMap(
			Map<AtomService, AtomService> oldNewReplaceServiceMap) {
		this.oldNewReplaceServiceMap = oldNewReplaceServiceMap;
	}
	
	public String toString() {
		String res = "";
		String actionText = "";
		switch (opNumber) {
		case Markov.A_NO_ACTION:
			actionText = "NO_ACTION";
			break;
		case Markov.A_TERMINATE:
			actionText = "TERMINATE";
			break;
		case Markov.A_RE_DO:
			actionText = "RE_DO";
			break;
		case Markov.A_REPLACE:
			actionText = "REPLACE";
			break;
		case Markov.A_RE_COMPOSITE:
			actionText = "RE_COMPOSITE";
			break;
		default:
			break;
		}
		
		Iterator<Entry<AtomService, AtomService>> iter = oldNewReplaceServiceMap.entrySet().iterator();
		String oldString = " (old=";
		String newString = "new=";
		while (iter.hasNext()) {
			Map.Entry<AtomService, AtomService> entry = iter.next();
			int olds = entry.getKey().getNumber();
			int news = entry.getValue().getNumber();
			oldString += olds + "-";
			newString += news + "-";
		}
		String oldNew = oldString.substring(0, oldString.length()-1) + " " + newString.substring(0, newString.length()-1) + ")";
		res += "[Action: " + String.format("%3d", getId()) + " " + String.format("%-12s", actionText) 
				+  " Activity " + String.format("%1d", currActivityNumber) 
				+ String.format("%-23s", oldNew)
				+ "]";
		
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((oldNewReplaceServiceMap == null) ? 0
						: oldNewReplaceServiceMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ReCompositeAction)) {
			return false;
		}
		ReCompositeAction other = (ReCompositeAction) obj;
		if (oldNewReplaceServiceMap == null) {
			if (other.oldNewReplaceServiceMap != null) {
				return false;
			}
		} else if (!oldNewReplaceServiceMap
				.equals(other.oldNewReplaceServiceMap)) {
			return false;
		}
		return true;
	}
}
