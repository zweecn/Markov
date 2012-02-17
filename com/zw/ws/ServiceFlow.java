package com.zw.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceFlow {
	private final static String flowInfoFileName = "E:\\markov_output\\flowinfo.txt";
	private final static String candidateServiceFileName = "E:\\markov_output\\wsinfo.txt";
	private final static String blindFileName = "E:\\markov_output\\blind.txt";
	private final static String graphFileName = "E:\\markov_output\\graph.txt";
	private int activitySize;
	protected List<Activity> activities;
	protected List<AtomService> services;
	private int[][] graph;
	
	public ServiceFlow() {
		readCandidateServices();
		//readActivityInfo();
		readGraphInfo();
		readBlindService();
	}
	
	public Activity getActivity(int number) {
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
			Scanner scanner = new Scanner(new File(candidateServiceFileName));
			while (scanner.hasNext()) {
				int serviceNumber = scanner.nextInt();
				int servicePrice = scanner.nextInt();
				double serviceReliability = scanner.nextDouble();
				int serviceExecTime = scanner.nextInt();
				AtomService service = new AtomService(serviceNumber, 
						new ServiceQoS(servicePrice, serviceReliability, serviceExecTime));
				services.add(service);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void readGraphInfo() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(graphFileName));
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
				int end = new Integer(temp[1].trim());
				graph[prefix][end] = 1;
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
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
	}
	
	private void readBlindService() {
		try {
			Scanner scanner = new Scanner(new File(blindFileName));
			while (scanner.hasNext()) {
				int activityNumber = scanner.nextInt();
				int serviceNumber = scanner.nextInt();
				activities.get(activityNumber).setBlindService(services.get(serviceNumber));
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
}
