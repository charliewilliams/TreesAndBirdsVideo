package Display.Trees;

import Model.Note;
import Util.Util;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Grass {

	static int		minTuftsPerNote	= 3;
	static int		numBlades		= 10;
	static float	originXSpace	= 3;
	static float	originYSpace	= 0.5f;
	static float	endYRange		= 5;
	static float	endXRange		= 2.5f;
	static float	xSpacing		= 6;
	static float	bladeHeight		= 20;
	static float	bladeWidth		= 4;
	static float	r				= 8;

	Grass(PVector pos, Note n) {

	}

	public static void generate(PGraphics2D pg) {

		pg.beginDraw();
		pg.noFill();
		pg.background(0, 0, 0, 0);
		for (int i = 0; i < 12; i++) {
			Note fakeNote = new Note(i);
			PVector basePos = TreeManager.instance().treePositionForNote(fakeNote);

			for (int j = 0; j < minTuftsPerNote + Util.random(0, 4); j++) {
				float xRange = 75;
				float yRange = 40;
				PVector pos = PVector.add(basePos, new PVector(Util.randomf(-xRange, xRange), Util.randomf(0, yRange)));
				Grass.drawGrassTuft(pg, pos);
			}
		}
		pg.endDraw();
	}

	public static void drawGrassTuft(PGraphics2D pg, PVector origin) {

		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);

		PVector end = new PVector(-xSpacing * numBlades / 2, -bladeHeight);

		PVector cp1 = new PVector(10, 0);
		PVector cp2 = new PVector(10, -40);

		pg.pushMatrix();
		pg.translate(origin.x, origin.y);

		for (int i = 0; i < numBlades; i++) {

			cp1.add(new PVector(Util.randomf(-r, r), Util.randomf(-r, r)));
			cp2.add(new PVector(Util.randomf(-r, r), Util.randomf(-r, r)));

			//origin.x += random(-originXSpace, originXSpace);
			//origin.y += random(-originYSpace / 2, originYSpace * 2);

			end.x += xSpacing + Util.random(-endXRange, endXRange);
			end.y += Util.random(-endYRange, endYRange);

			if (end.y > 0) {
				end.y = -endYRange;
			}

			float baseHue = Util.randomf(100, 140);

			for (int j = 0; j < bladeWidth; j++) {
				pg.stroke(baseHue + Util.randomf(-20, 20), Util.randomf(70, 100), Util.randomf(40, 80));
				pg.curveTightness(Util.randomf(0, 0.5f));
				pg.curve(cp1.x, cp1.y, j, 0, end.x, end.y, cp2.x, cp2.y);
			}

			// Debug: draw control points
			//fill(255, 0, 0);
			//ellipse(cp1.x, cp1.y, 5, 5);
			//fill(0, 0, 255);
			//ellipse(cp2.x, cp2.y, 5, 5);
			//noFill();
		}

		pg.popMatrix();
	}
}
