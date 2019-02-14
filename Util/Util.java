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
	
	public static int setAlpha(int color, float alp) {
		
		color &= 0x00FFFFFF;
//		int alpha = ((int)(alp * 2.55)) << 24;
		int alpha = ((int)alp) << 24;
		return alpha | color;
	}
	
	public static int colorFrom360(float hue, float sat, float bri, float alp) {
		
		// Convenience function to convert from 360° hue + 100% sat/bri/alpha
		// to a 0-255 packed 32-bit int like Processing wants: 
		int alpha = ((int)(alp * 2.55)) << 24;
		int brightness = ((int) (bri * 2.55) << 16);
		int saturation = (int) (sat * 2.55) << 8;
		int hueAngle = ((int) (hue * 0.7083333333));
		
		return alpha | brightness | saturation | hueAngle;
	}
	
	public static int colorFrom255(float hue, float sat, float bri, float alp) {
		
		// Convenience function to convert from basic 0-255 hue/sat/bri/alpha
		// to a packed 32-bit int like Processing wants: 
		int alpha = (int)alp << 24;
		int brightness = (int)bri << 16;
		int saturation = (int)sat << 8;
		int hueAngle = (int)hue;
		
		return alpha | brightness | saturation | hueAngle;
	}
	
	public static int bgraFrom255(float blu, float gre, float red, float alp) {
		
		// Convenience function to convert from basic 0-255 hue/sat/bri/alpha
		// to a packed 32-bit int like Processing wants: 
		int blue = (int)blu << 24;
		int green = (int)gre << 16;
		int redd = (int)red << 8;
		int alpha = (int)alp;
		
		return blue | green | redd | alpha;
	}
	
	
}
