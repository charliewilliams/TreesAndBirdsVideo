package Display.Trees;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphics2D;

public class Snow {

	private static ArrayList<Snowflake>	snow	= new ArrayList<Snowflake>();
	private static PGraphics2D			pg_snow;
	private static PApplet				parent;

	private static int snowPerTick = 1;

	public static void setupSnow(PApplet parent) {

		Snow.parent = parent;
		pg_snow = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
	}

	public static void addSnowTick() {

		for (int i = 0; i < snowPerTick; i++) {
			snow.add(new Snowflake(pg_snow));
		}
	}

	public static void render() {

		// Update position & alpha, removing alpha == 0
		ArrayList<Snowflake> toRemove = new ArrayList<Snowflake>();
		for (Snowflake s : snow) {
			s.tick(parent);

			if (s.alpha == 0) {
				toRemove.add(s);
			}
		}
		snow.removeAll(toRemove);

		// Render onto pg_snow
		pg_snow.beginDraw();
		pg_snow.clear();
		
		for (Snowflake s : snow) {
			s.draw(parent, pg_snow);
		}
		pg_snow.endDraw();

		// Draw onto parent
		parent.blendMode(PConstants.ADD);
//		parent.blendMode(PConstants.NORMAL);
		pg_snow.save("tmp/snow.jpg");
		parent.image(pg_snow, 0, 0);
	}

}
