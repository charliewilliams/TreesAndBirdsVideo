package Util;

public class Util {

	static public double random(double d, double e) {

		double range = e - d;
		return Math.random() * range + d;
	}
	
	static public float randomf(float d, float e) {

		float range = e - d;
		return (float)(Math.random() * range + d);
	}

	public static boolean coinToss() {
		return Math.random() > 0.5f;
	}
}
