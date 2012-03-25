package com.zw;

public final class Configs {
	public final static String CANDIDATE_SERVICE_FILENAME = "markov_output\\wsinfo.txt";
	public final static String BILIND_FILENAME = "markov_output\\blind.txt";
	public final static String GRAPH_FILENAME = "markov_output\\graph.txt";
	public final static String LOG_FILE_NAME = "markov_output\\markov_log.txt";
	
	public final static int TCP_PORT = 8888;
	public final static String TCP_HOST = "127.0.0.1";
	
	public static final double WEAKEN = 0.1;
	public static final double TIME_STEP = Double.MAX_VALUE;
	
	public static final double RANDOM_FIND_FREE_SERVICE = 1;
	public static final int LAYER_SIZE = 10;
	public static final int MAX_ACTIVITY_REPLACE_COUNT = 2;
	public static final double RANDOM_FAULT = 0.2;
	
	public static final double FAILED_COST = 100;
	public static final double TIME_DELAY_COST = 1;
}
