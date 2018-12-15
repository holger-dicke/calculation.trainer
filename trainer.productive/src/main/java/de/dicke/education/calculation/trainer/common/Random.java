package de.dicke.education.calculation.trainer.common;

import java.util.concurrent.ThreadLocalRandom;

public class Random {

	private Random() {
	}

	public static int getRandom(int num1Min, int num1Max) {
		if (num1Min > num1Max) {
			int tmp = num1Min;
			num1Min = num1Max;
			num1Max = tmp;
		}
		System.out.println("....");
		int aa = ThreadLocalRandom.current().nextInt(num1Min, num1Max + 1);
		return aa;
	}
}
