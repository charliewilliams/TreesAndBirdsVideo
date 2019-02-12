package Util;

public class Util {

	static public double random(double d, double e) {

		double range = e - d;
		return Math.random() * range + d;
	}

	static public float randomf(float d, float e) {

		float range = e - d;
		return (float) (Math.random() * range + d);
	}

	public static boolean coinToss() {
		return Math.random() > 0.5f;
	}

	public static float logMapf(float value, float start1, float stop1, float start2, float stop2) {
		return (float) logMap((double) value, (double) start1, (double) stop1, (double) start2, (double) stop2);
	}

	public static double logMap(double value, double start1, double stop1, double start2, double stop2) {

		if (start2 == 0) {
			start2 = 0.01;
		}
		if (stop2 == 0) {
			stop2 = 0.01;
		}

		start2 = Math.log(start2);
		stop2 = Math.log(stop2);

		return Math.exp(start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1)));
	}
}
