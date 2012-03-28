package com.zw;

public final class Configs {
	/*
	 * Web service filename.
	 * */
	public final static String 	CANDIDATE_SERVICE_FILENAME = "markov_output\\wsinfo.txt";
	public final static String 	BILIND_FILENAME = "markov_output\\blind.txt";
	public static String 		GRAPH_FILENAME = "markov_output\\graph.txt";
	public final static String 	LOG_FILE_NAME = "markov_output\\markov_log.txt";

	public static final String[] GRAPH_FILENAME_S = {
		"markov_output\\graph.txt",
		"markov_output\\graph1.txt",
		"markov_output\\graph2.txt",
		"markov_output\\graph3.txt",
		"markov_output\\graph4.txt",
		"markov_output\\graph5.txt",
		"markov_output\\graph6.txt"
	};

	/*
	 * Host and port. No it is unused.
	 * */
	public final static int 	TCP_PORT = 8888;
	public final static String 	TCP_HOST = "127.0.0.1";

	/*
	 * Weaken and random fault.
	 * */
	public static final double 	WEAKEN = 1;
	public static final double 	RANDOM_FAULT = 0.5;
	public static final long 	SLEEP_SECONDS = 1000;
	/*
	 * Random of find service
	 * */
	public static final double 	RANDOM_FIND_FREE_SERVICE = 1.1;
	public static final int 	MAX_ACTIVITY_REPLACE_COUNT = 3;
	public static final boolean IS_RECOMPOSITER_RANDOM = true;

	/*
	 * Reward configure.
	 * */
	public static double 	PUNISHMENT_FAILED = 50;
	public static final double 	PUNISHMENT_PER_SECOND = 1;
	public static final double 	AWARD_SUCCEED = 10;

	/*
	 * Plan of reward
	 * */
	public static final int 	PLAN_CHOUSE = 1;
	public static final int 	PLAN_ONE = 1;
	public static final int 	PLAN_TWO = 2;
	public static final int 	PLAN_THREE = 3;

	/* 
	 * Extend? or ReduceLayer?
	 * */
	public static final boolean IS_EXTEND_TREE = false;
	public static int 			REDUCE_LAYER_SIZE = -1; 


	/* 0 will show normail exec.
	 * 1 will do test 1
	 * 2 will do test 2
	 * 3 will do test 3. Layer size.
	 * 4 will do test 4
	 * */
	public static final int DO_TEST = 3;
}
