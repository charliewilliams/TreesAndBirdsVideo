
int numBlades = 10;
PVector origin = new PVector(0, 0);
float originXSpace = 3;
float originYSpace = 1;
float endYRange = 10;
float endXRange = 5;
float xSpacing = 6;
float bladeHeight = 40;
float bladeWidth = 4;
float r = 8;

int tuftCount = 25;

void setup() {
  size(1024, 512);
  smooth(8);
  //noLoop();
  frameRate(0.5);
  colorMode(HSB, 360, 100, 100, 100);
}

void draw() {

  background(0, 0, 100);

  float tuftSpacing = width / (tuftCount + 2);

  noFill();
  for (int i = 0; i < tuftCount; i++) {
    drawGrass(new PVector((i + 1) * tuftSpacing, random(height * 0.8, height * 0.6)));
  }
}

void drawGrass(PVector origin) {

  PVector end = new PVector(-xSpacing * numBlades / 2, -bladeHeight);

  PVector cp1 = new PVector(20, 0);
  PVector cp2 = new PVector(20, -80);

  pushMatrix();
  translate(origin.x, origin.y);

  for (int i = 0; i < numBlades; i++) {

    cp1.add(new PVector(random(-r, r), random(-r, r)));
    cp2.add(new PVector(random(-r, r), random(-r, r)));

    //origin.x += random(-originXSpace, originXSpace);
    //origin.y += random(-originYSpace / 2, originYSpace * 2);

    end.x += xSpacing + random(-endXRange, endXRange);
    end.y += random(-endYRange, endYRange);
    
    if (end.y > 0) {
      end.y = -endYRange;
    }
    
    float baseHue = random(100, 140);

    for (int j = 0; j < bladeWidth; j++) {
      stroke(baseHue + random(-20, 20), random(70, 100), random(40, 80));
      curveTightness(random(0, 0.5));
      curve(cp1.x, cp1.y, j, 0, end.x, end.y, cp2.x, cp2.y);
    }

    //fill(255, 0, 0);
    //ellipse(cp1.x, cp1.y, 5, 5);
    //fill(0, 0, 255);
    //ellipse(cp2.x, cp2.y, 5, 5);
    //noFill();
  }

  popMatrix();
}
