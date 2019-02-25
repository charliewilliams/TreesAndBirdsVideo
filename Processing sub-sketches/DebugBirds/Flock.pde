
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Flock {

  private int baseHue;
  float baseSize;
  double flapSpeed;

  PApplet sketch;

  ArrayList<Bird> birds = new ArrayList<Bird>();

  Flock(DebugBirds sketch, float count) {

    this.sketch = sketch;
    baseHue = (int)(360 / sketch.random(1, 13));


    baseSize = sketch.random(sketch.smallestBirdSize, sketch.biggestBirdSize);


    for (int i = 0; i < count; i++) {
      birds.add(new Bird(sketch, baseSize, baseHue, true));
    }
  }

  void update(ArrayList<Bird> allBirds) {

    for (Bird b : birds) {
      b.run(allBirds, birds);
    }
  }

  void forceLand() {

    for (Bird b : birds) {

      if (b.landingSite != null) {
        continue;
      }

      b.landingSite = b.randomPosition();
      b.state = State.to_land;
    }
  }
}
