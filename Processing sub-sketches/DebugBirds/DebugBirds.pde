
ArrayList<Flock> flocks = new ArrayList<Flock>();
ArrayList<Bird> allBirds = new ArrayList<Bird>();

int numFlocks = 3;
int birdsPerFlock = 5;
int variance = 4;

float smallestBirdSize = 2;
float biggestBirdSize = 10;

void setup() {

  size(1692, 720, P2D);
  colorMode(HSB, 360, 100, 100, 100);

  for (int i = 0; i < numFlocks; i++) {
    Flock f = new Flock(this, birdsPerFlock + random(0, variance));
    flocks.add(f);
    allBirds.addAll(f.birds);
  }
}

void draw() {

  background(0);

  for (Flock f : flocks) {
    f.update(allBirds);
  }

  //saveFrame("output/#####.png");

  //String txt_fps = String.format("%2.0f fps", frameRate);
  //surface.setTitle(txt_fps);
}

void mousePressed() {

  for (Flock f : flocks) {
    f.forceLand();
  }
}
