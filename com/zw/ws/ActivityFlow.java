package com.zw.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import com.zw.Configs;
import com.zw.markov.Markov;
import com.zw.markov.MarkovAction;
import com.zw.markov.MarkovState;
import com.zw.markov.ReCompositeAction;

public class ActivityFlow {
	public ActivityFlow() {
//		//System.out.println("Constructor service flow");
//		readCandidateServices();
//		//readActivityInfo();
//		readGraphInfo();
//		readBlindService();
//		initPrefixSuffix();
		
		initActivities();

	}
	
	
/*
 * 
 * This is non static method below.
 * 
 * */
	
	protected List<Activity> activities;
	
	private void initActivities() {
//		System.out.println("ActivityFlow.activitySize=" + ActivityFlow.activitySize);
//		this.activities = new ArrayList<Activity>(ActivityFlow.activitySize);
//		for (int i = 0; i < ActivityFlow.activitySize; i++) {
//			Activity activity = new Activity(i);
//			activities.add(activity);
//		}
		this.activities = new ArrayList<Activity>();
		for (int i = 0; i < ActivityFlow.activitySize; i++) {
			this.activities.add(ActivityFlow.staticActivities.get(i).clone());
		}
	}
	
	public Activity getActivity(int number) {
		if (number < activitySize && this.activities.get(number).getNumber() == number) {
			return this.activities.get(number);
		}

		for (int i = 0; i < this.activities.size(); i++) {
			if (this.activities.get(i).getNumber() == number) {
				return this.activities.get(i);
			}
		}
		return null;
	}
	
	public void setActivity(Activity activity) {
		if (activity == null) {
			return;
		}
		for (int i = 0; i < this.activities.size(); i++) {
			if (this.activities.get(i).getId() == activity.getId()) {
				this.activities.set(i, activity);
			}
		}
	}
	
	public void printFlow() {
		System.out.println("The candidate services are:");
		for (int i = 0; i < services.size(); i++) {
			AtomService service = services.get(i);
			ServiceQoS qos = service.getQos();
			System.out.println("" + service.getNumber() + "\t" + qos.getPrice() 
					+ "\t" + qos.getReliability() + "\t" + qos.getExecTime());
		}
		System.out.println();

		for (int i = 0; i < activitySize; i++) {
			System.out.println("Activity " + this.activities.get(i).getNumber() 
					+ " blind service " + this.activities.get(i).getBlindService().getNumber());
		}
		System.out.println();

		for (int i = 0; i < activitySize; i++) {
			for (int j = 0; j < activitySize; j++) {
				if (graph[i][j] == 1) {
					System.out.println("" + i + " -> " + j);
				}
			}
		}
	}

/*
 * 
 * This is static method below.
 * 
 * */
	
	private static List<Activity> staticActivities;
	private static List<AtomService> services;
	private static int activitySize;
	private static int[][] graph;
	private static Map<Integer, List<Integer>> prefixMap;
	private static Map<Integer, List<Integer>> suffixMap;

	static {
		readCandidateServices();
		readGraphInfo();
		readBlindService();
		initPrefixSuffix();
	}
	
	private static void readCandidateServices() {
		services = new ArrayList<AtomService>();
		try {
			Scanner scanner = new Scanner(new File(Configs.candidateServiceFileName));
			while (scanner.hasNext()) {
				int serviceNumber = scanner.nextInt();
				int servicePrice = scanner.nextInt();
				double serviceReliability = scanner.nextDouble();
				int serviceExecTime = scanner.nextInt();
				AtomService service = new AtomService(serviceNumber, 
						new ServiceQoS(servicePrice, serviceReliability, serviceExecTime));
				service.setFree(true);
				services.add(service);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	private static void readGraphInfo() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(Configs.graphFileName));
			String line = bf.readLine();
			activitySize = new Integer(line.trim());
			graph = new int[activitySize][activitySize];

			// 这里注意加回来
			staticActivities = new ArrayList<Activity>(activitySize);
			for (int i = 0; i < activitySize; i++) {
				Activity activity = new Activity(i);
				staticActivities.add(activity);
			}

			for (int i = 0; i < activitySize; i++) {
				for (int j = 0; j < activitySize; j++) {
					graph[i][j] = 0;
				}
			}

			while ((line = bf.readLine()) != null) {
				String[] temp = line.split("->");
				if (temp.length != 2) {
					continue;
				}
				int prefix = new Integer(temp[0].trim());
				int suffix = new Integer(temp[1].trim());
				graph[prefix][suffix] = 1;

			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readBlindService() {
		try {
			Scanner scanner = new Scanner(new File(Configs.blindFileName));
			for (int i = 0; i < activitySize && scanner.hasNext(); i++) {
				int activityNumber = scanner.nextInt();
				int serviceNumber = scanner.nextInt();
				services.get(serviceNumber).setFree(false);
				staticActivities.get(activityNumber).setBlindService(services.get(serviceNumber));
				staticActivities.get(activityNumber).setPredictTimeCost(services.get(serviceNumber).getQos().getExecTime());
				staticActivities.get(activityNumber).setPredictPriceCost(services.get(serviceNumber).getQos().getPrice());
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void initPrefixSuffix() {
		prefixMap = new HashMap<Integer, List<Integer>>();
		suffixMap = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < graph.length; i++) {
			List<Integer> prefixTempList = new ArrayList<Integer>();
			List<Integer> suffixTempList = new ArrayList<Integer>();
			for (int j = 0; j < graph[i].length; j++) {
				if (graph[i][j] == 1) {
					suffixTempList.add(j);
				}
				if (graph[j][i] == 1) {
					prefixTempList.add(j);
				}
			}
			suffixMap.put(new Integer(i), suffixTempList);
			prefixMap.put(new Integer(i), prefixTempList);
		}
	}

	


	/*	@SuppressWarnings("unused")
	private void readActivityInfo() {
		try {
			Scanner scanner = new Scanner(new File(flowInfoFileName));
			activitySize = scanner.nextInt();
			activities = new ArrayList<Activity>(activitySize);
			for (int i = 0; i < activitySize; i++) {
				int activityNumber = scanner.nextInt();
				Activity activity = new Activity();
				activity.setNumber(activityNumber);
				activities.add(activity);
			}

			graph = new int[activitySize][activitySize];
			for (int i = 0; i < activitySize; i++) {
				scanner.nextInt();
				for (int j = 0; j < activitySize; j++) {
					graph[i][j] = scanner.nextInt();
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/

	public static int getActivitySize() {
		return ActivityFlow.staticActivities.size();
	}

	public static int getServiceSize() {
		return ActivityFlow.services.size();
	}
	
	public static boolean hasEdge(int i, int j) {
		try {
			return (graph[i][j] == 1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static List<Integer> getPrefixActivityNumbers(int i){
		return prefixMap.get(i);
	}

	public static List<Integer> getSuffixActivityNumbers(int i){		
		return suffixMap.get(i);
	}

	// warning...
	public static AtomService getService(int serviceNumber) {
		if (serviceNumber < services.size() 
				&& services.get(serviceNumber).getNumber() == serviceNumber) {
			return services.get(serviceNumber);
		}
		for (int i = 0; i < services.size(); i++) {
			if (services.get(i).getNumber() == serviceNumber) {
				return services.get(i);
			}
		}
		return null;
	}
	
	public static AtomService nextFreeService(Activity activity) {
//		oldActivity = activity.clone();
		for (int i = 0; i < ActivityFlow.services.size(); i++) {
			if (ActivityFlow.services.get(i).isFree()) { 
				return ActivityFlow.services.get(i);
			}
		}
		return null;
	}

	public static void setServiceUsed(int number) {
//		if (serviceNew != null && serviceNew.getNumber() == number) {
//			serviceNew.setFree(false);
//			return;
//		}
		for (int i = 0; i < ActivityFlow.services.size(); i++) {
			if (ActivityFlow.services.get(i).getNumber() == number) {
				ActivityFlow.services.get(i).setFree(false);
			}
		}
	}
	
	public static MarkovAction recomposite(MarkovState state) {
		//System.out.println("Old=" + state);
		Map<AtomService, AtomService> oldNewReplaceServiceMap = new HashMap<AtomService, AtomService>();
		if (state.getFailedActivity() == null) {
			return null;
		}
		Activity failedActivity = state.getFailedActivity().clone();
		MarkovAction reComAction = new ReCompositeAction(failedActivity.getId(), Markov.A_RE_COMPOSITE,
				failedActivity.getBlindService().getNumber());
		
		((ReCompositeAction)reComAction).setOldNewReplaceServiceMap(oldNewReplaceServiceMap);
		
		Queue<Activity> queue = new LinkedList<Activity>();
		queue.offer(failedActivity);
		//MarkovState stateNew = state.clone(); //old
		//MarkovState stateNew = state; // New
		//MarkovState stateNew = state.store();
		while (!queue.isEmpty()) {
			Activity activity = queue.poll();
			AtomService failedService = activity.getBlindService();
			if (isReplacedRandom()) {
				//System.out.println("IN IF");
				//AtomService replaceService = state.getFreeServiceFinder().nextFreeService(activity);
				AtomService replaceService = ActivityFlow.nextFreeService(activity);
				if (replaceService == null) {
					break;
				}
				//state.getFreeServiceFinder().setServiceUsed(replaceService.getNumber());
				ActivityFlow.setServiceUsed(replaceService.getNumber());
				oldNewReplaceServiceMap.put(failedService, replaceService);
				//System.out.println("1---state=" + state + " " + replaceService);
//				Activity tempActivity = state.getActivity(activity.getNumber());
//				tempActivity.setBlindService(replaceService);
//				state.setActivity(tempActivity);
				state.getActivity(activity.getNumber()).setBlindService(replaceService);
//				System.out.println("2---state=" + state);
			} else {
				break;
			}
			
			//for (Integer i : state.getSuffixActivityNumbers(activity.getNumber())) {
			for (Integer i : ActivityFlow.getSuffixActivityNumbers(activity.getNumber())) {
				queue.offer(state.getActivity(i));
			}
		}
		if (oldNewReplaceServiceMap.isEmpty()) {
			return null;
		}
		
		//System.out.println("New=" + state);
//		System.out.println("recomposite map=" + oldNewReplaceServiceMap + "\n");
		return reComAction;
	}
	
	private static boolean isReplacedRandom() {
		Random random = new Random();
		if (random.nextFloat() > 0) {
			return true;
		}
		return false;
	}
}
