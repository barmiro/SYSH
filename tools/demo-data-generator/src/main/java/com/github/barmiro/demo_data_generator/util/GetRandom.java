package com.github.barmiro.demo_data_generator.util;

import java.util.Random;

public class GetRandom {
	
	public static String alphaNumeric(int length) {
		
		String charPool = 
				( "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz"
				+ "0123456789");
		
		Random rand = new Random();
		
		char[] chars = new char[length];
		
		for (int i = 0; i < length; i++) {
			chars[i] = charPool.charAt(rand.nextInt(charPool.length()));
		}
		
		return new String(chars);
	}
}
