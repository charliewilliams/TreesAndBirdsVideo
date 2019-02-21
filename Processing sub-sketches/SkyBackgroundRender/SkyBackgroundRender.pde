
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphics2D;

int totalFrames = 13250;

//float cameraZ = 600;
//float backgroundXOffset  = 0;
float skySpeed      = 0.0025f;

color groundColor;
color skyBackgroundColor;
color paper;

float t = 0;
float startX = 0;
float startY = 10000;
PVector pos = new PVector(startX, startY, 999);
float deltaZ = 0.0001;

float startX2 = 7458;
float startY2 = 23840;
PVector pos2 = new PVector(startX2, startY2, 9999);
float deltaZ2 = 0.000524;

void setup() {

  size(1692, 720, P2D);
  smooth(2);
  colorMode(PConstants.HSB, 360, 100, 100, 100);

  groundColor = color(37, 42, 100);
  skyBackgroundColor = color(223, 40, 46);
  paper = color(39, 5, 100); // paper beige
}

public void draw() {

  generateSky();

  saveFrame("sky/#####.png");

  if (frameCount > totalFrames) {
    exit();
  }

  String txt_fps = String.format("%2.0f fps", frameRate);
  surface.setTitle(txt_fps);
}

static float skyNodeSize = 6;

/*
   * Sky idea by Bárbara Almeida / CC-A-SA /
 * https://www.openprocessing.org/sketch/184276
 */

void generateSky() {

  background(skyBackgroundColor);

  float horizonY = 2 * height / 3;

  for (int y = 0; y < height; y += 2) {

    noStroke();

    //draw clouds
    for (int x = 0; x < width; x += 2) {

      float n = noise(pos.x + x / 200.0, pos.y + y / 50.0, pos.z);
      float n2 = noise(pos2.x + x / 2000.0, pos2.y + y / 500.0, pos.z);
      n += map(n2, 0, 1, -1, 1);

      fill(paper, n * PApplet.map(y, 0, height * 0.666667f, 100, 0));
      ellipse(x, y, skyNodeSize, skyNodeSize);
    }

    //draw the light on the bottom
    strokeWeight(3);
    // Map the alpha so it fades in from 0 at 2/3 of the way down to 1.0 at all-the-way-down
    float alpha = PApplet.map(y, horizonY, height, 0, 255);
    stroke(groundColor, alpha);
    line(0, y, width, y);
  }

  startX += skySpeed;
  startY -= skySpeed / 2;

  pos.x = startX;
  pos.y = startY;
  pos.z += deltaZ;
  
  startX2 += skySpeed * 0.67f;
  startY2 -= skySpeed / 1.4395f;

  pos2.x = startX2;
  pos2.y = startY2;
  pos2.z += deltaZ2;
}
