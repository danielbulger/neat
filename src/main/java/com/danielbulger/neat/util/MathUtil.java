package com.danielbulger.neat.util;

public class MathUtil {

	public static float sigmoid(float x) {
		return 1f / (1f + (float) Math.pow(Math.E, -x));
	}

	private MathUtil() {
	}
}
