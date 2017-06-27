package Display;
import processing.core.*;

public class SceneManager {

	private static SceneManager m;
	private PApplet parent;
	int[] backgroundColors = new int[6];
	PImage bg;
	private PGraphics sky_pg, ground_pg;
	float cameraZ = 600;
	int w, h;
	int lightColor, darkColor;
	float backgroundXOffset = 0;

	public SceneManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}

		m = this;
		this.parent = parent;
		w = parent.width;
		h = parent.height;

		bg = parent.loadImage("paper.jpg");
		sky_pg = parent.createGraphics(w * 2, h);
		sky_pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		ground_pg = parent.createGraphics(w, h);

		backgroundColors[0] = parent.color(39, 5, 100); // paper beige
		backgroundColors[1] = parent.color(204, 100, 25); // night blue //color(306, 100, 25); // maroon
		backgroundColors[2] = parent.color(0, 0, 0); // black //color(204, 100, 25); // night blue
		backgroundColors[3] = parent.color(0, 0, 0); // black
		backgroundColors[4] = parent.color(0, 0, 0);
		backgroundColors[5] = parent.color(0, 0, 0);
		//		int bgColor = color(255, 178, 187);

		lightColor = parent.color(37, 42, 100);
		darkColor = parent.color(223, 40, 46);

		generateSky(sky_pg, 0);
		generateGround(ground_pg);
	}

	public static SceneManager instance() {
		return m;
	}

	public void update(int millis) {
		
		backgroundXOffset -= 0.1;
		
		parent.image(bg, 0, 0, w, h);
		parent.blendMode(PConstants.MULTIPLY);
		parent.image(sky_pg, backgroundXOffset, 0);

		if (backgroundXOffset < -w) {
			
			backgroundXOffset = 0;
			generateSky(sky_pg, millis);
		}
		
		parent.image(ground_pg, 0, 0);
	}
	
	static float skyNodeSize = 6;
	
	/*
	 * Sky idea by Bárbara Almeida / CC-A-SA / https://www.openprocessing.org/sketch/184276
	 * */

	void generateSky(PGraphics pg, int millis) {

		pg.beginDraw();

		pg.background(backgroundColors[0]);
		
		float horizonY = 2 * h / 3;

		for (int y = 0; y < h; y += 2) {

			pg.noStroke();
			
			for (int x = 0; x < w * 2; x += 2) {
				//draw clouds
				float xOff = millis / 100.0f;
				float yOff = millis / 1000.0f;
				float n = parent.noise((x + xOff) / 200.0f, (y + yOff) / 50.0f);     

				pg.fill(darkColor, n * PApplet.map(y, 0, 2 * h / 3.0f, 255, 0));
				pg.ellipse(x, y, skyNodeSize, skyNodeSize);
			}

			//draw the light on the bottom
			pg.strokeWeight(3);
			// Map the alpha so it fades in from 0 at 2/3 of the way down to 1.0 at all-the-way-down
			float alpha = PApplet.map(y, horizonY, h, 0, 255);
			pg.stroke(lightColor, alpha);
			pg.line(0, y, w * 2, y);
		}
	}
	
	void generateGround(PGraphics pg) {
		
		// draw a non-changing horizon
		pg.beginDraw();
		pg.background(255);

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
}
