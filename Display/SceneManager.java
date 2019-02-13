package Display;

import Display.Trees.Grass;
import Model.Section;
import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphics2D;

public class SceneManager {

	private static SceneManager	m;
	private PApplet				parent;
	int[]						backgroundColors	= new int[10];
	PImage						bg;
	private PGraphics			sky_pg1, sky_pg2, ground_pg;
	private PGraphics2D			sky_pg, grass_pg;
	float						cameraZ				= 600;
	int							w, h;
	int							lightColor, darkColor;
	float						backgroundXOffset	= 0;

	public SceneManager(PApplet parent, int debugOffsetMillis) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}

		m = this;
		this.parent = parent;
		w = parent.width;
		h = parent.height;

		bg = parent.loadImage("paper.jpg");

		sky_pg = (PGraphics2D) parent.createGraphics(w * 2, h, PConstants.P2D);
		sky_pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		sky_pg1 = parent.createGraphics(w * 2, h);
		sky_pg1.colorMode(PConstants.HSB, 360, 100, 100, 100);
		sky_pg2 = parent.createGraphics(w * 2, h);
		sky_pg2.colorMode(PConstants.HSB, 360, 100, 100, 100);

		ground_pg = parent.createGraphics(w, h);
		grass_pg = (PGraphics2D) parent.createGraphics(w, h, PConstants.P2D);

		// preroll, start, melodyStart, risingMel, repeatedNotes, bigReturn, highMel, outro, end;
		backgroundColors[Section.preroll.ordinal()]       = parent.color(39, 5, 100); // paper beige
		backgroundColors[Section.start.ordinal()]         = parent.color(39, 5, 100); // paper beige
		backgroundColors[Section.melodyStart.ordinal()]   = parent.color(39, 5, 100); // paper beige
		backgroundColors[Section.risingMel.ordinal()]     = parent.color(204, 100, 80); // night blue
		backgroundColors[Section.repeatedNotes.ordinal()] = parent.color(306, 100, 25); // maroon
		backgroundColors[Section.bigReturn.ordinal()]     = parent.color(39, 5, 100); // paper beige
		backgroundColors[Section.highMel.ordinal()]       = parent.color(204, 100, 25); // night blue
		backgroundColors[Section.outro.ordinal()]         = parent.color(204, 100, 25); // night blue
		backgroundColors[Section.end.ordinal()]           = parent.color(0, 0, 0);
		backgroundColors[Section.end.ordinal() + 1]       = parent.color(0, 0, 0);
		//		int bgColor = color(255, 178, 187);

		lightColor = parent.color(37, 42, 100);
		darkColor = parent.color(223, 40, 46);

		int idx = Section.forMillis(debugOffsetMillis).ordinal();
		generateSky(sky_pg1, debugOffsetMillis, idx);
		generateSky(sky_pg2, debugOffsetMillis, idx + 1);
		generateGround(ground_pg);
	}

	public static SceneManager instance() {
		return m;
	}

	public void update(int millis) {

		float skySpeed = 0.2f;
		backgroundXOffset -= skySpeed;

		Section section = Section.forMillis(millis);
		float pct = section.pctDone(millis);
		float pg1Alpha = Util.logMapf(pct, 0, 1, 100, 0);
		float pg2Alpha = Util.logMapf(pct, 0, 1, 0, 100);

		parent.blendMode(PConstants.REPLACE);
		parent.image(bg, 0, 0, w, h);
		
		sky_pg1.tint(255, pg1Alpha);
		sky_pg2.tint(255, pg2Alpha);

		sky_pg.beginDraw();
		sky_pg.blendMode(PConstants.BLEND);
		sky_pg.image(sky_pg1, 0, 0);
		sky_pg.image(sky_pg2, 0, 0);
		sky_pg.endDraw();
		
//		sky_pg.save("tmp/sky_pg-combined" + millis + ".jpg");

		parent.blendMode(PConstants.MULTIPLY);
		parent.image(sky_pg, backgroundXOffset, 0);

		if (backgroundXOffset < -w) {

			backgroundXOffset = 0;

			int idx = section.ordinal();
			generateSky(sky_pg1, millis, idx);
			generateSky(sky_pg2, millis, idx + 1);
		}

		parent.image(ground_pg, 0, 0);
	}

	static float skyNodeSize = 6;

	/*
	 * Sky idea by Bárbara Almeida / CC-A-SA /
	 * https://www.openprocessing.org/sketch/184276
	 */

	void generateSky(PGraphics pg, int millis, int idx) {

		int color = backgroundColors[idx];
		
		pg.beginDraw();

		pg.background(darkColor);

		float horizonY = 2 * h / 3;

		for (int y = 0; y < h; y += 2) {

			pg.noStroke();

			for (int x = 0; x < w * 2; x += 2) {
				//draw clouds
				float xOff = millis / 100.0f;
				float yOff = millis / 1000.0f;
				float n = parent.noise((x + xOff) / 200.0f, (y + yOff) / 50.0f);

				pg.fill(color, n * PApplet.map(y, 0, 2 * h / 3.0f, 255, 0));
//				pg.fill(darkColor, n * PApplet.map(y, 0, 2 * h / 3.0f, 255, 0));
				pg.ellipse(x, y, skyNodeSize, skyNodeSize);
			}

			//draw the light on the bottom
			pg.strokeWeight(3);
			// Map the alpha so it fades in from 0 at 2/3 of the way down to 1.0 at all-the-way-down
			float alpha = PApplet.map(y, horizonY, h, 0, 255);
			pg.stroke(lightColor, alpha);
			pg.line(0, y, w * 2, y);
		}
		
		pg.endDraw();
		
		Section section = Section.forMillis(millis);
		pg.save("tmp/sky-sec-" + idx + "-" + section + ".jpg");
	}

	void generateGround(PGraphics pg) {

		// draw a non-changing horizon
		pg.beginDraw();
		pg.background(255);

		generateRadialBlur(pg);

		float n = 0; // noise offset

		for (int i = 0; i < 256; i++) {

			float bumpiness = parent.random(0.1f, 0.5f);
			pg.stroke(0, 0, parent.random(5f, 20f), PApplet.map(i, 0, 256, 40, 80));

			pg.beginShape();
			pg.vertex(-1, h);

			float baseY = h * 0.33f - 25;
			for (int x = -1; x <= w + 50; x += 50) {
				float relativeY = PApplet.map(parent.noise(n), 0, 1, 0, 50);
				pg.vertex(x, h - baseY - relativeY);
				n += bumpiness;
			}
			pg.vertex(w, h);
			pg.endShape();
		}

		pg.endDraw();
	}

	public void createGrass() {
		Grass.generate(grass_pg);
	}

	public void renderGrass() {
		parent.image(grass_pg, 0, 0);
	}

	void generateRadialBlur(PGraphics pg) {

		float lightest = 255;
		float darkest = 100;
		pg.noStroke();
		for (float diam = 1.5f * w; diam > 0.5 * w; diam -= 2) {
			pg.fill(PApplet.map(diam, 0.5f * w, 1.5f * w, lightest, darkest));
			pg.ellipse(w / 2, h / 2, diam, diam);
		}
	}
}
