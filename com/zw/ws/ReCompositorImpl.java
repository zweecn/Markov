package com.zw.ws;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import com.zw.Configs;
import com.zw.markov.Markov;
import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovState;
import com.zw.markov.ReCompositeAction;

public class ReCompositorImpl implements ReCompositor{
	
	private Map<AtomService, AtomService> oldNewReplaceServiceMap;
	private Activity failedActivity;
	MarkovAction reComAction;
	
	@Override
	public MarkovState recomposite(MarkovState state) {
		oldNewReplaceServiceMap = new HashMap<AtomService, AtomService>();
		if (state.getFaultActivity() == null) {
			return null;
		}
		failedActivity = state.getFaultActivity().clone();
		reComAction = new ReCompositeAction(failedActivity.getNumber(), Markov.A_RE_COMPOSITE,
				failedActivity.getBlindService().getNumber());
		
		((ReCompositeAction)reComAction).setOldNewReplaceServiceMap(oldNewReplaceServiceMap);
		
		Queue<Activity> queue = new LinkedList<Activity>();
		queue.offer(failedActivity);
		double x = 0.2;
		while (!queue.isEmpty()) {
			//System.out.println("queue=" + queue);
			Activity activity = queue.poll();
			//System.out.println("activity=" + activity);
			AtomService failedService = activity.getBlindService();
			if (isReplacedRandom(x)) {
				x *= 2;
				AtomService replaceService = ActivityFlow.nextFreeService(activity);
				if (replaceService == null) {
					break;
				}
//				replaceService.setFree(false);
				ActivityFlow.setServiceUsed(activity.getNumber(), replaceService);
				//System.out.println("HRERE");
				oldNewReplaceServiceMap.put(failedService, replaceService);
				Activity ac = state.getActivity(activity.getNumber());
				ac.setBlindService(replaceService);
				ac.setX(0);
			} else {
				break;
			}
			
			for (Integer i : ActivityFlow.getSuffixActivityNumbers(activity.getNumber())) {
				Activity ac = state.getActivity(i);
				if (ac != null) {
					queue.offer(ac);
				}
			}
		}
		//System.out.println(oldNewReplaceServiceMap.size());
		if (oldNewReplaceServiceMap.isEmpty()) {
			return null;
		}
		state.init();
		return state;		
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
			java.util.Iterator<Entry<AtomService, AtomService>> iter = oldNewReplaceServiceMap.entrySet().iterator();
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
			java.util.Iterator<Entry<AtomService, AtomService>>  iter = oldNewReplaceServiceMap.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<AtomService, AtomService> entry = iter.next();
			    price += (entry.getValue().getQos().getPrice() - entry.getKey().getQos().getPrice());     
			}
			price += failedActivity.getBlindService().getQos().getPrice();
		}
		return price;
	}


	private static boolean isReplacedRandom(double x) {
		Random random = new Random();
		if (random.nextFloat() < Configs.RANDOM_FIND_FREE_SERVICE - x) {
			return true;
		}
		return false;
	}

	public MarkovAction getReComAction() {
		return reComAction;
	}
}
