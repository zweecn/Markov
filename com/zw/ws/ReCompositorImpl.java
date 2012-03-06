package com.zw.ws;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;


import com.zw.markov.MarkovState;

public class ReCompositorImpl extends ActivityFlow implements ReCompositor{

	private Map<AtomService, AtomService> oldNewReplaceServiceMap;
	private Activity failedActivity;
	
	@Override
	public MarkovState recomposite(MarkovState state) {
		//System.out.println("Old=" + state);
		
		oldNewReplaceServiceMap = new HashMap<AtomService, AtomService>();
		if (state.getFailedActivity() == null) {
			return null;
		}
		failedActivity = state.getFailedActivity().clone();
		
		Queue<Activity> queue = new LinkedList<Activity>();
		queue.offer(failedActivity);
		//MarkovState stateNew = state.clone(); //old
		//MarkovState stateNew = state; // New
		while (!queue.isEmpty()) {
			Activity activity = queue.poll();
			AtomService failedService = activity.getBlindService();
			if (isReplacedRandom()) {
				AtomService replaceService = state.getFreeServiceFinder().nextFreeService(activity);
				if (replaceService == null) {
					break;
				}
				state.getFreeServiceFinder().setServiceUsed(replaceService.getNumber());
				
				oldNewReplaceServiceMap.put(failedService, replaceService);
				state.getActivity(activity.getNumber()).setBlindService(replaceService);
			} else {
				break;
			}
			
			for (Integer i : state.getSuffixActivityNumbers(activity.getNumber())) {
				queue.offer(state.getActivity(i));
			}
		}
		if (oldNewReplaceServiceMap.isEmpty()) {
			return null;
		}
		
		//System.out.println("New=" + state);
		return state;
	}

	@Override
	public Map<AtomService, AtomService> getOldNewReplaceMap() {
		return oldNewReplaceServiceMap;
	}
	
	private boolean isReplacedRandom() {
		Random random = new Random();
		if (random.nextFloat() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public double getPosibility() {
		if (failedActivity != null) {
			return failedActivity.getBlindService().getQos().getReliability();
		}
		return 0;
	}

	@Override
	public double getTimeCost() {
		double timeRes = 0;
		if (failedActivity != null) {
			Iterator<Entry<AtomService, AtomService>> iter = oldNewReplaceServiceMap.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<AtomService, AtomService> entry = iter.next();
			    timeRes += (entry.getValue().getQos().getExecTime() - entry.getKey().getQos().getExecTime()); 
			}
			timeRes += failedActivity.getBlindService().getQos().getExecTime() * Math.abs(failedActivity.getX());
		}
		return timeRes;
	}

	@Override
	public double getPriceCost() {
		double price = 0;

		if (failedActivity != null) {
			Iterator<Entry<AtomService, AtomService>> iter = oldNewReplaceServiceMap.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<AtomService, AtomService> entry = iter.next();
			    price += (entry.getValue().getQos().getPrice() - entry.getKey().getQos().getPrice());     
			}
			price += failedActivity.getBlindService().getQos().getPrice();
		}
		return price;
	}
}
