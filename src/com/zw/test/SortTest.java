package com.zw.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SortTest {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("我是郑伟");
		list.add("我是徐飞");
		Collections.sort(list);
		System.out.println(list);
	}
	
}
