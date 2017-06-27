package Util;

public class Util {

	static public float random(float low, float high) {

		float range = high - low;
		return (float) (Math.random() * range + low);
	}
}
