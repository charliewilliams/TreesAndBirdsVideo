
void setup() {

  //size(1692, 720);
  size(1692, 720, P2D);
}


void draw() {

  generateRadialBlur();

  saveFrame("blur-##.png");

  generateGround();

  saveFrame("ground-##.png");
  
  if (frameCount > 32) {
    exit();
  }
}

void generateGround() {

  // draw a non-changing horizon
  background(255);

  float n = 0; // noise offset

  for (int i = 0; i < 256; i++) {

    float bumpiness = random(0.1f, 0.5f);
    stroke(0, 0, random(5f, 20f), PApplet.map(i, 0, 256, 40, 80));

    beginShape();
    vertex(-1, height);

    float baseY = height * 0.33f - 25;
    for (int x = -1; x <= width + 50; x += 50) {
      float relativeY = PApplet.map(noise(n), 0, 1, 0, 50);
      vertex(x, height - baseY - relativeY);
      n += bumpiness;
    }
    vertex(width, height);
    endShape();
  }
}

void generateRadialBlur() {

  float lightest = 255;
  float darkest = 100;
  noStroke();
  for (float diam = 1.5f * width; diam > 0.5 * width; diam -= 2) {
    fill(PApplet.map(diam, 0.5f * width, 1.5f * width, lightest, darkest));
    ellipse(width / 2, height / 2, diam, diam);
  }
}
