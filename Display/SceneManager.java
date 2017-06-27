package Display;
import processing.core.*;

public class SceneManager {

	private static SceneManager m;
	private PApplet parent;
	int[] backgroundColors = new int[6];
	PImage bg;
	private PGraphics pg;
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
		pg = parent.createGraphics(w * 2, h);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);

		backgroundColors[0] = parent.color(39, 5, 100); // paper beige
		backgroundColors[1] = parent.color(204, 100, 25); // night blue //color(306, 100, 25); // maroon
		backgroundColors[2] = parent.color(0, 0, 0); // black //color(204, 100, 25); // night blue
		backgroundColors[3] = parent.color(0, 0, 0); // black
		backgroundColors[4] = parent.color(0, 0, 0);
		backgroundColors[5] = parent.color(0, 0, 0);
		//		int bgColor = color(255, 178, 187);

		lightColor = parent.color(37, 42, 100);
		darkColor = parent.color(223, 40, 46);

		generateSky(pg, 0);
	}

	public static SceneManager instance() {
		return m;
	}

	public void update(int millis) {
		
		backgroundXOffset -= 0.05;
		
		parent.image(bg, 0, 0, w, h);
		parent.blendMode(PConstants.MULTIPLY);
		parent.image(pg, backgroundXOffset, 0);

		if (backgroundXOffset < -w) {
			
			backgroundXOffset = 0;
			generateSky(pg, millis);
		}
	}
	
	static float skyNodeSize = 6;

	void generateSky(PGraphics pg, int millis) {

		pg.beginDraw();

		pg.background(backgroundColors[0]);
//		pg.background(darkColor);

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
			pg.stroke(lightColor, PApplet.map(y, 2 * h / 3, h, 0, 255));
			pg.line(0, y, w * 2, y);
		}

		pg.endDraw();
	}
}
