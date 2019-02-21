package Display;

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
	PImage						bg;
	private PGraphics			ground_pg;
	private PGraphics2D			sky_pg;
	float						cameraZ				= 600;
	int							w, h;
	int							groundColor, skyBackgroundColor;
	float						backgroundXOffset	= 0;
	float						skySpeed			= 0.4f;

	public SceneManager(PApplet parent, int totalFrames) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}

		m = this;
		this.parent = parent;
		w = parent.width;
		h = parent.height;

		bg = parent.loadImage("paper.jpg");

		int requiredWidth = (int) (totalFrames * skySpeed + w * 2);
		sky_pg = (PGraphics2D) parent.createGraphics(requiredWidth, h, PConstants.P2D);
		sky_pg.colorMode(PConstants.HSB, 360, 100, 100, 100);

		ground_pg = parent.createGraphics(w, h);

		//		int startColor = parent.color(39, 5, 100); // paper beige
		//		int sunset1 = parent.color(43, 91, 100); // sunset 1 (yellow)
		//		int sunset2 = parent.color(14, 94, 90); // sunset 2 (orange)
		//		int nightBlue = parent.color(204, 100, 25); // night blue

		groundColor = parent.color(37, 42, 100);
		skyBackgroundColor = parent.color(223, 40, 46);

		generateSky(sky_pg, requiredWidth);

		generateGround(ground_pg);
	}

	public static SceneManager instance() {
		return m;
	}

	public void update(int millis) {

		backgroundXOffset -= skySpeed;

		// Background paper
		parent.blendMode(PConstants.REPLACE);
		parent.image(bg, 0, 0, w, h);

		// Colour tint
		parent.blendMode(PConstants.BLEND);
		Section section = Section.forMillis(millis);

		float maxAlpha = 60;

		if (section == Section.bigReturn) {

			float pct = section.pctDone(millis);
			float alpha = Util.logMapf(pct, 0, 1, 0, maxAlpha);
			parent.fill(204, 100, 25, alpha);
			parent.rect(w / 2, h / 2, w, h);

		} else if (section == Section.highMel) {

			parent.fill(204, 100, 25, maxAlpha);
			parent.rect(w / 2, h / 2, w, h);

		} else if (section == Section.outro) {

			float pct = section.pctDone(millis);
			float alpha = Util.logMapf(pct, 0, 1, maxAlpha, 0);
			parent.fill(204, 100, 25, alpha);
			parent.rect(w / 2, h / 2, w, h);
		}

		// Multiply the clouds in
		parent.blendMode(PConstants.MULTIPLY);
		parent.image(sky_pg, backgroundXOffset, 0);

		parent.image(ground_pg, 0, 0);
	}

	static float skyNodeSize = 6;

	/*
	 * Sky idea by Bárbara Almeida / CC-A-SA /
	 * https://www.openprocessing.org/sketch/184276
	 */

	void generateSky(PGraphics pg, int requiredWidth) {

		pg.beginDraw();

		pg.background(skyBackgroundColor);

		float horizonY = 2 * h / 3;

		int color = parent.color(39, 5, 100); // paper beige

		for (int y = 0; y < h; y += 2) {

			pg.noStroke();

			//draw clouds
			for (int x = 0; x < requiredWidth; x += 2) {
				
				float n = parent.noise(x / 200.0f, y / 50.0f);

				pg.fill(color, n * PApplet.map(y, 0, 2 * h / 3.0f, 255, 0));
				pg.ellipse(x, y, skyNodeSize, skyNodeSize);
			}

			//draw the light on the bottom
			pg.strokeWeight(3);
			// Map the alpha so it fades in from 0 at 2/3 of the way down to 1.0 at all-the-way-down
			float alpha = PApplet.map(y, horizonY, h, 0, 255);
			pg.stroke(groundColor, alpha);
			pg.line(0, y, w * 2, y);
		}

		pg.endDraw();
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
