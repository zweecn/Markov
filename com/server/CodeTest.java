package com.server;

import java.util.Arrays;

public class CodeTest {
	public static void main(String[] args) {
		String s = 2 + ":" + 3.5;
		byte[] b = s.getBytes();
//		for (byte c : b) {
//			System.out.println(c);
//		}
		
		byte[] ba = Arrays.copyOf(b, 8);
		
		String sa = new String(ba);
		System.out.println(sa);
		String[] sl = sa.split(":");
		int a1 = Integer.parseInt(sl[0]);
		double a2 = Double.parseDouble(sl[1]);
		System.out.println(a1 + " " + a2);
		
//		for (byte c : ba) {
//			System.out.println(c);
//		}
	}
}
