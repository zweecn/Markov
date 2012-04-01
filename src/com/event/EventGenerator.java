package com.event;

import com.zw.ws.ActivityFlow;

public class EventGenerator {
	public static void main(String[] args) {
		TCPClient client = new TCPClient();
		client.connect();
		int[] as = new int[ActivityFlow.getActivitySize()];
		double[] xs = new double[ActivityFlow.getActivitySize()];
//		as[0] = 0;
		xs[0] = -1;
		EventData eventData = new EventData(as, xs);
		eventData.encode();
//		System.out.println(eventData);
//		eventData.decode();
//		System.out.println(eventData);
		client.sent(eventData);
		client.close();
	}
}
