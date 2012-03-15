package com.zw;

public final class Configs {
	public final static String candidateServiceFileName = "markov_output\\wsinfo.txt";
	public final static String blindFileName = "markov_output\\blind.txt";
	public final static String graphFileName = "markov_output\\graph.txt";
	public final static String LOG_FILE_NAME = "markov_output\\markov_log.txt";
	
	public static final double WEAKEN = 0.8;
	public static final double TIME_STEP = Double.MAX_VALUE;
	
	public static final double RANDOM_FIND_FREE_SERVICE = 0.5;
	public static final int LAYER_SIZE = 10;
	public static final int MAX_ACTIVITY_REPLACE_COUNT = 2;
}
