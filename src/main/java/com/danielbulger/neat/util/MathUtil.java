package com.danielbulger.neat.util;

public class MathUtil {

	public static float sigmoid(float x) {
		return (float) (1 / (1 + Math.exp(-4.9 * x)));
	}

	private MathUtil() {
	}
}
