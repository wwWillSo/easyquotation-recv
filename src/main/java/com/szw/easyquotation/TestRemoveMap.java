package com.szw.easyquotation;

import java.util.HashMap;
import java.util.Map;


public class TestRemoveMap {
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();

		map.put("hehe", "hehe");

		System.out.println(map.size());

		map.remove("hehe");

		System.out.println(map.get("hehe"));

		map.put("hehe", "hehe");

		System.out.println(map.get("hehe"));
	}
}
