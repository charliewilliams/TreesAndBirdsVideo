package Display;

import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphics2D;

public class Glow {

	private static DwPixelFlow	context;
	private static DwFilter		filter;
	private static PGraphics2D	pg_render, pg_luminance, pg_bloom;

	public static void setupGlow(PApplet parent) {

		context = new DwPixelFlow(parent);
//				context.print();
//				context.printGL();

		filter = new DwFilter(context);

		pg_render = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_render.smooth(8);

		pg_luminance = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_luminance.smooth(8);

		pg_bloom = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_bloom.smooth(8);
	}

	public static void render(PGraphics2D dest) {

		// luminance pass
		filter.luminance_threshold.param.threshold = 0.1f; // when 0, all colors are used
		filter.luminance_threshold.param.exponent = 5;
//		filter.luminance_threshold.apply(pg_render, pg_luminance);
		filter.luminance_threshold.apply(dest, pg_luminance);

		// bloom pass
		// if the original image is used as source, the previous luminance pass 
		// can just be skipped
//		filter.bloom.setBlurLayers(10);
		filter.bloom.param.mult = 4; // 1 to 10
		filter.bloom.param.radius = 1f; // 0 to 1
//		filter.bloom.apply(pg_luminance, pg_bloom, pg_render);
		filter.bloom.apply(pg_luminance, pg_bloom, dest);

//		dest.blendMode(PConstants.ADD);
//		dest.blendMode(PConstants.DILATE);
//		dest.image(pg_render, 0, 0, dest.width, dest.height);
	}
}
