package com.event;

import java.util.Arrays;

public class EventData {
	public EventData() {
		
	}
	public EventData(int[] activityNumbers, double[] activityXs) {
		super();
		this.activityNumbers = activityNumbers;
		this.activityXs = activityXs;
	}
	
	private int[] activityNumbers;
	private double[] activityXs;
	private byte[] b;
	public static final int EVENT_BYTE_SIZE = 16;
	
	public boolean encode() {
		String s = "";
		for (int i : activityNumbers) {
			s += i + ":";
		}
		s = s.substring(0, s.length()-1) + ";";
		for (double d : activityXs) {
			s += d + ":";
		}
		s = s.substring(0, s.length()-1) + ";";
		//System.out.println("encode:" + s);
		byte[] tempBytes = s.getBytes();
		if (tempBytes.length <= EVENT_BYTE_SIZE) {
			b = Arrays.copyOf(tempBytes, EVENT_BYTE_SIZE);
			return true;
		} else {
			System.err.println("Encode error. Code 0x06");
			return false;
		}
	}
	
	public boolean decode() {
		String s = new String(b);
		String[] temp = s.split(";");
		if (temp.length < 2) {
			return false;
		}
		String[] temps1 = temp[0].split(":");
		String[] temps2 = temp[1].split(":");
		/*System.out.println("-----------");
		System.out.println(Arrays.toString(temps1));
		System.out.println(Arrays.toString(temps2));
		System.out.println("-----------");*/
		if (temps1.length == temps2.length) {
			activityNumbers = new int[temps1.length];
			activityXs = new double[temps2.length];
			for (int i = 0; i < temps2.length; i++) {
				activityNumbers[i] = Integer.parseInt(temps1[i]);
				activityXs[i] = Double.parseDouble(temps2[i]);
			}
			return true;
		} else {
			System.err.println("Decode error. String is:" + s + " Code 0x05.");
			return false;
		}
	}

	public byte[] getBytes() {
		return b;
	}

	public void setBytes(byte[] b) {
		this.b = b;
	}
	
	
	
	public int[] getActivityNumber() {
		return activityNumbers;
	}
	
	public double[] getActivityX() {
		return activityXs;
	}
	@Override
	
	public String toString() {
		return "EventData [activityNumbers=" + Arrays.toString(activityNumbers)
				+ ", activityXs=" + Arrays.toString(activityXs) + "]";
	}
}
