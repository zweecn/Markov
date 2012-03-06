package com.zw.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.zw.Configs;

public class ActivityFlow {
	private int activitySize;
	
	protected List<Activity> activities;
	protected List<AtomService> services;
	protected int[][] graph;
	
	private Map<Integer, List<Integer>> prefixMap;
	private Map<Integer, List<Integer>> suffixMap;
	
	public ActivityFlow() {
		//System.out.println("Constructor service flow");
		readCandidateServices();
		//readActivityInfo();
		readGraphInfo();
		initPrefixSuffix();
		readBlindService();
	}
	
	public Activity getActivity(int number) {
		if (number < activitySize && activities.get(number).getNumber() == number) {
			return activities.get(number);
		}
		
		for (int i = 0; i < activities.size(); i++) {
			if (activities.get(i).getNumber() == number) {
				return activities.get(i);
			}
		}
		return null;
	}
	
	private void readCandidateServices() {
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

	private void readGraphInfo() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(Configs.graphFileName));
			String line = bf.readLine();
			activitySize = new Integer(line.trim());
			graph = new int[activitySize][activitySize];
			activities = new ArrayList<Activity>(activitySize);
			for (int i = 0; i < activitySize; i++) {
				Activity activity = new Activity(i);
				activities.add(activity);
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
	
	private void initPrefixSuffix() {
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
	
	private void readBlindService() {
		try {
			Scanner scanner = new Scanner(new File(Configs.blindFileName));
			for (int i = 0; i < activitySize && scanner.hasNext(); i++) {
				int activityNumber = scanner.nextInt();
				int serviceNumber = scanner.nextInt();
				services.get(serviceNumber).setFree(false);
				activities.get(activityNumber).setBlindService(services.get(serviceNumber));
				activities.get(activityNumber).setPredictTimeCost(services.get(serviceNumber).getQos().getExecTime());
				activities.get(activityNumber).setPredictPriceCost(services.get(serviceNumber).getQos().getPrice());
			}
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			System.out.println("Activity " + activities.get(i).getNumber() 
					+ " blind service " + activities.get(i).getBlindService().getNumber());
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
	
	public int getActivitySize() {
		return activities.size();
	}
	
	public boolean hasEdge(int i, int j) {
		try {
			return (graph[i][j] == 1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Integer> getPrefixActivityNumbers(int i){
		return prefixMap.get(i);
	}
	
	public List<Integer> getSuffixActivityNumbers(int i){		
		return suffixMap.get(i);
	}
	
	public AtomService getService(int serviceNumber) {
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

	
}
