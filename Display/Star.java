package Display;

import Util.Util;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Star {

	PVector			pos;
	float			nominalSize;
	private float	currentSize;
	private float	t				= Util.randomf(0, 1);
	private float	twinkleSpeed	= Util.randomf(0.01f, 0.06f);
	private float	maxSize			= 1.5f;

	private enum StarState {
		appearing, steady, disappearing
	};

	private StarState state = StarState.appearing;

	Star(PVector pos, float size) {

		size = Math.min(size, maxSize);
		this.pos = pos;
		this.nominalSize = size;
		this.currentSize = 7;
	}

	public void draw(PGraphics2D pg) {

		switch (state) {

		case appearing:
			if (currentSize > nominalSize) {
				currentSize *= 0.99f;
			} else {
				state = StarState.steady;
			}
			break;

		case steady:
			t += twinkleSpeed;
			currentSize += Math.sin(t) / 75;
			break;

		case disappearing:
			if (currentSize > 0) {
				currentSize *= 0.99f;
			}
			break;
		}

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.noStroke();
		pg.fill(255);
		pg.ellipse(0, 0, currentSize, currentSize);
		pg.popMatrix();
	}

	public boolean goOut() {

		if (state == StarState.disappearing) {
			return false;
		}
		state = StarState.disappearing;
		return true;
	}
}
