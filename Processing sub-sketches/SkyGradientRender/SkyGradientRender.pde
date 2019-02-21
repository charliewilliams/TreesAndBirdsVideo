
void setup() {

  size(1692, 720, P2D);
  smooth(8);

  background(255);
  colorMode(HSB, 360, 100, 100, 100);

  color c = color(204, 100, 25, 100);  

  noLoop();

  // One way
  //color clear = color(0, 0, 0, 0);
  //for (int y = 0; y <= height * 0.66666; y++) {
  //  float alpha = map(y, height * 0.333333333, height * 0.666667, 0, 1);
  //  color cc = lerpColor(c, clear, alpha);
  //  stroke(cc);
  //  //alpha = logMap(y, height * 0.333333333, height * 0.666667f, 100, 0);
  //  //stroke(c, alpha);
  //  line(0, y, width, y);
  //}


  // Another way
  //for (int y = 0; y <= height; y++) {
  //  float alpha = map(y, height * 0.333333333, height * 0.666667, 100, 0);
  //  stroke(c, alpha);
  //  line(0, y, width, y);
  //}

  // A third way
  background(c);
  for (int y = 0; y <= height; y++) {
    float alpha = logMap(y, height, 0, 100, 0);
    stroke(255, alpha);
    line(0, y, width, y);
  }

  saveFrame("gradient.png");
}

void draw() {
}

float logMap(float value, float start1, float stop1, float start2, float stop2) {

  if (start2 == 0) {
    start2 = 0.01;
  }
  if (stop2 == 0) {
    stop2 = 0.01;
  }

  start2 = log(start2);
  stop2 = log(stop2);

  return exp(start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1)));
}
