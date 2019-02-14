package Display;

import java.util.ArrayList;
import java.util.Collections;

import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Stars {

	private static PGraphics2D		pg_stars;
	private static ArrayList<Star>	stars	= new ArrayList<Star>();

	private static DwPixelFlow	context;
	private static DwFilter		filter;
	private static PGraphics2D	pg_render, pg_luminance, pg_bloom;
	private static PVector		stage;
//	private static float		rotation	= -(float)Math.PI / 4;

	static float lowestPitch = 36;
	static float highestPitch = 75;
	
	public static void addStar(Note n) {

		float nominalPitch = n.pitch;
		if (n.pitch - lowestPitch < 20) {
			nominalPitch += 24;
		}
		float x = PApplet.map(nominalPitch, highestPitch - 12, highestPitch, 0, stage.x) + Util.randomf(-150, 150);
		
		float horizon = stage.y * 0.64f;
		float y = PApplet.map(n.velocity, 0.2f, 0.6f, horizon, 0) + Util.randomf(-50, 50);
		
		if (y > horizon) {
			y %= horizon;
		}
		PVector pos = new PVector(x, y);
		float size = PApplet.map(n.velocity + n.duration, 0, 5, 0.25f, 1.5f);
		stars.add(new Star(pos, size));
		//		PApplet.println("New star", pos, size);
	}

	public static void renderStars(int millis, PApplet parent) {

//		rotation -= 0.0001;

		pg_stars.beginDraw();
		pg_stars.clear();
//		pg_stars.translate(0, stage.y);
//		pg_stars.rotate(rotation);
		for (Star s : stars) {
			s.draw(pg_stars);
		}

				renderGlow(pg_stars);
		pg_stars.endDraw();

		//		pg_stars.save("tmp/stars-" + millis + ".jpg");

		parent.blendMode(PConstants.ADD);
		parent.image(pg_stars, 0, 0);
	}

	public static void goOutTick() {

		Collections.shuffle(stars);

		int removedCount = 0;
		int starsToRemovePerTick = 10;
		while (removedCount < starsToRemovePerTick) {
			
			if (stars.isEmpty()) {
				return;
			}
			if (stars.get(0).goOut()) {
				removedCount++;
			}
		}
	}

	public static void setupGlow(PApplet parent) {

		stage = new PVector(parent.width, parent.height);
		context = new DwPixelFlow(parent);
		context.print();
		context.printGL();

		filter = new DwFilter(context);

		pg_stars = (PGraphics2D) parent.createGraphics(parent.width, parent.height * 2, PConstants.P2D);
		pg_stars.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_stars.smooth(8);

		pg_render = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_render.smooth(8);

		pg_luminance = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_luminance.smooth(8);

		pg_bloom = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_bloom.smooth(8);
	}

	private static void renderGlow(PGraphics2D dest) {

		// luminance pass
		filter.luminance_threshold.param.threshold = 0f; // when 0, all colors are used
		filter.luminance_threshold.param.exponent = 5;
		//		filter.luminance_threshold.apply(pg_render, pg_luminance);
		filter.luminance_threshold.apply(dest, pg_luminance);

		// bloom pass
		// if the original image is used as source, the previous luminance pass 
		// can just be skipped
		//		filter.bloom.setBlurLayers(10);
		filter.bloom.param.mult = 8; // 1 to 10
		filter.bloom.param.radius = 1f; // 0 to 1
		//		filter.bloom.apply(pg_luminance, pg_bloom, pg_render);
		filter.bloom.apply(pg_luminance, pg_bloom, dest);
	}
}
