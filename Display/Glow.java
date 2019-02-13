package Display;

import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphics2D;

public class Glow {

	static DwPixelFlow context;
	static DwFilter filter;
	static PGraphics2D pg_render, pg_luminance, pg_bloom;

	public static void setupGlow(PApplet parent) {

		context = new DwPixelFlow(parent);
		//		context.print();
		//		context.printGL();

		filter = new DwFilter(context);

		pg_render = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_render.smooth(8);

		pg_luminance = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_luminance.smooth(8);

		pg_bloom = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_bloom.smooth(8);
	}

	public static void drawGlowOnto(PGraphics2D host, float mult, float radius) {

		pg_render.beginDraw();
		pg_render.clear();	
		pg_render.endDraw();
		
	    // luminance pass
	    filter.luminance_threshold.param.threshold = 0.0f; // when 0, all colors are used
	    filter.luminance_threshold.param.exponent  = 5;
	    filter.luminance_threshold.apply(host, pg_luminance);

	    // bloom pass
	    // if the original image is used as source, the previous luminance pass 
	    // can just be skipped
	    //      filter.bloom.setBlurLayers(10);
	    filter.bloom.param.mult   = mult; // 1 to 10
	    filter.bloom.param.radius = radius; // 0 to 1
	    filter.bloom.apply(pg_luminance, pg_bloom, pg_render);
	    
//	    host.blendMode(PConstants.ADD);
	    host.image(pg_render, 0, 0);
	}
}
