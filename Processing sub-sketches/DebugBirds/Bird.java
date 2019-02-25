
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

enum State {
  flying, to_land, landed
}

class Bird {

  static boolean debugDrawLandingPoints = true;

  static private float  neighborhoodRadius   = 75;  // radius in which it looks for fellow boids
  static private float  desiredseparation = 25.0f;
  static private float  maxSteerForce = 0.3f;  // 0.1f; //maximum magnitude of the steering vector
  private float maxSpeed = 6;  // 4; //maximum magnitude for the velocity vector

  private float  cohesionMultiplier = 1;
  private float  alignmentMultiplier = 0.5f;
  private float  separationMultiplier = 5;
  private boolean  avoidBirds = true;

  private PVector pos;

  public PVector pos() {
    return pos;
  }

  private PVector vel;
  private PVector acc;

  float hue, sat, bri;
  float size;
  private double flap  = 0;
  private float t = 0;
  State state = State.flying;
  private PApplet sketch;
  PVector landingSite;

  float bottomWallY;
  private int landingTimerMillis;
  private int toLandTimerMillis;

  public Bird(DebugBirds sketch, float baseSize, float baseHue, boolean startLandingTimer) {

    this.sketch = sketch;
    bottomWallY = sketch.height;
    pos = randomPosition();
    vel = velocityForInitialPosition();
    acc = new PVector(0, 0);

    hue = baseHue + sketch.random(-5, 5);
    size = baseSize + sketch.random(-0.5f, 0.5f);

    sat = sketch.random(50, 80);
    bri = sketch.random(50, 80);

    float fastestFlap = 0.5f;
    float slowestFlap = 0.02f;
    flapSpeed = PApplet.map(baseSize, sketch.smallestBirdSize, sketch.biggestBirdSize, fastestFlap, slowestFlap) + sketch.random(-0.05f, 0.05f);

    if (startLandingTimer) {
      startLandingTimer(sketch.millis());
    }
  }

  PVector randomPosition() {
    return new PVector(sketch.random(0, sketch.width), sketch.random(0, sketch.height));
  }

  public void run(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock) {

    updateFlap();

    switch (state) {
    case flying:
      tickLandingTimer();
      break;
    case landed:
      render();
      return;
    case to_land:
      tickToLandTimer();
      break;
    }

    checkAvoidWalls();

    if (landingSite != null) {

      float landingSiteRadius = 15;
      float landingSiteMult = 0.00000005f;
      if (PVector.dist(pos, landingSite) < landingSiteRadius) {
        state = State.landed;
      } else {
        acc.add(PVector.mult(steer(landingSite, true), landingSiteMult * toLandTimerMillis));
      }
    }

    flock(allBirds, myFlock);
    move();
    render();
  }

  private boolean  avoidWalls        = true;
  static float  flyingWallAvoidWeight  = 10;
  static float  landingWallAvoidWeight  = 4;

  void checkAvoidWalls() {

    float wallAvoidWeight = (state == State.to_land) ? landingWallAvoidWeight : flyingWallAvoidWeight;

    PVector avoidGround = avoid(new PVector(pos.x, bottomWallY), true);
    acc.add(PVector.mult(avoidGround, wallAvoidWeight));

    if (!avoidWalls) {
      return;
    }

    PVector avoidCeiling = avoid(new PVector(pos.x, 0), true);
    PVector avoidLeftWall = avoid(new PVector(0, pos.y), true);
    PVector avoidRightWall = avoid(new PVector(sketch.width, pos.y), true);

    acc.add(PVector.mult(avoidLeftWall, wallAvoidWeight));
    acc.add(PVector.mult(avoidCeiling, wallAvoidWeight));
    acc.add(PVector.mult(avoidRightWall, wallAvoidWeight));
  }

  double flapSpeed;

  void updateFlap() {

    if (state == State.landed && sketch.random(0, 100) < 95) {
      return;
    }

    t += flapSpeed;
    flap = Math.sin(t);
  }

  private int lastTickMillis;

  void startLandingTimer(int millis) {
    landingTimerMillis = (int) sketch.random(8000f, 15000f);
    lastTickMillis = millis;
  }

  // This timer counts how long the bird has been flying and makes it land after `landingTimerMillis`.
  void tickLandingTimer() {

    int millis = sketch.millis();
    int millisSinceLastTick = millis - lastTickMillis;
    lastTickMillis = millis;
    landingTimerMillis -= millisSinceLastTick;

    if (landingTimerMillis <= 0 && landingSite == null) {
      state = State.to_land;
      landingSite = randomPosition();
    }
  }

  // This timer counts how long we've been trying to land for; the landing site's pull gets stronger the longer we've been waiting
  private int lastToLandTickMillis;

  void tickToLandTimer() {

    int millis = sketch.millis();
    int millisSinceLastTick = millis - lastToLandTickMillis;
    toLandTimerMillis += millisSinceLastTick;
  }

  ///// -----------behaviors---------------
  void flock(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock) {

    PVector ali = alignment(myFlock);
    PVector coh = cohesion(myFlock);
    PVector sep = separation(allBirds);

    acc.add(PVector.mult(ali, alignmentMultiplier));
    acc.add(PVector.mult(coh, cohesionMultiplier));
    acc.add(PVector.mult(sep, separationMultiplier));
  }

  void scatter() {
  }
  //// ------------------------------------

  void move() {

    // flapspeed: PApplet.map(baseSize, 2f, 10f, 0.5f, 0.01f);
    // i.e. bigger birds flap slower
    // However that means that the same multiplier on flap will make
    // smaller birds jerk all over the place; we need to multiply smaller
    // birds by a smaller number

    float slowestFlapSpeed = 0.125f;
    float fastestFlapSpeed = 0.5f;
    acc.y += flap * PApplet.map((float) flapSpeed, 0.5f, 0.01f, fastestFlapSpeed, slowestFlapSpeed);
    vel.add(acc); // add acceleration to velocity
    vel.limit(maxSpeed); // make sure the velocity vector magnitude does not exceed maxSpeed
    pos.add(vel); // add velocity to position
    //    pos.y += flap / 20;
    acc.mult(0f); // reset acceleration
  }

  //float  lastTheta  = -1;
  //float  maxRotation  = (float) Math.toRadians(30);

  void render() {

    // Draw a triangle rotated in the direction of velocity
    float theta = (float) (vel.heading() + Math.toRadians(90));
    //if (theta > maxRotation) {
    //  theta = PApplet.lerp(lastTheta, theta, 0.25f); // lerp to maybe fix judder?
    //}
    //lastTheta = theta;

    float r = size * (float) (flap / 3 + 0.5);

    sketch.fill(hue, sat, bri);
    sketch.stroke(hue, 100, 50);

    // Draw walls for debug
    // drawWalls(ps);

    sketch.pushMatrix();
    sketch.translate(pos.x, pos.y);
    sketch.rotate(theta);
    //    sketcher.beginShape(PConstants.TRIANGLES);
    sketch.beginShape(PConstants.POLYGON);
    //    sketcher.beginShape();

    sketch.vertex(0, -size * 2);
    sketch.vertex(-r * 2, size);
    sketch.vertex(0, -size * 1.1f);
    sketch.vertex(r * 2, size);

    sketch.endShape(PConstants.CLOSE);
    sketch.popMatrix();

    if (debugDrawLandingPoints) {
      drawLandingPoint();
    }
  }

  void drawWalls() {

    sketch.strokeWeight(5);
    sketch.stroke(255, 0, 0, 255); // left - red
    sketch.line(0, 0, 0, bottomWallY);
    sketch.stroke(0, 255, 0, 255); // bottom - green
    sketch.line(0, bottomWallY, sketch.width, bottomWallY);
    sketch.stroke(0, 0, 255, 255); // right - blue
    sketch.line(sketch.width, 0, sketch.width, bottomWallY);
    sketch.stroke(255, 0, 255, 255); // top - magenta
    sketch.line(sketch.width, 0, 0, 0);
    sketch.stroke(255);
  }

  void drawLandingPoint() {

    if (landingSite == null) {
      return;
    }

    // red
    sketch.stroke(255, 0, 0, 255);

    // ellipse
    //    sketch.strokeWeight(10);
    //    sketch.ellipse(landingSite.x, landingSite.y, 10, 10);

    sketch.strokeWeight(1);
    sketch.line(pos.x, pos.y, landingSite.x, landingSite.y);
  }

  // steering. If arrival==true, the boid slows to meet the target. Credit to Craig Reynolds
  PVector steer(PVector target, boolean arrival) {

    PVector steer = new PVector(); // creates vector for steering
    if (!arrival) {
      steer = PVector.sub(target, pos); // steering vector points towards target (switch target and pos for avoiding)
      steer.limit(maxSteerForce); // limits the steering force to maxSteerForce
    } else {
      PVector targetOffset = PVector.sub(target, pos);
      float distance = targetOffset.mag();
      float rampedSpeed = maxSpeed * (distance / 100);
      float clippedSpeed = Math.min(rampedSpeed, maxSpeed);
      PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed / distance));
      steer.set(PVector.sub(desiredVelocity, vel));
    }
    return steer;
  }

  // avoid. If weight == true avoidance vector is larger the closer the boid is to the target
  PVector avoid(PVector target, boolean weight) {

    if (!avoidBirds) {
      return new PVector();
    }

    PVector steer = new PVector(); // creates vector for steering
    PVector direction = PVector.sub(pos, target);
    steer.set(direction); // steering vector points away from target

    double dist = PVector.dist(pos, target);

    //    if (dist < 5) {
    //      PVector random = new PVector(random(-0.5f, 0.5f), random(-0.5f, 0.5f), 0.0f);
    //      steer = PVector.add(steer, random);
    //    }
    if (weight) {
      double divisor = dist * dist + 1;
      steer.mult((float) (1 / divisor));
    }
    steer.limit(maxSteerForce); // limits the steering force to maxSteerForce
    return steer;
  }

  PVector separation(ArrayList<Bird> boids) {

    PVector steer = new PVector(0, 0);
    int count = 0;
    // For every boid in the system, check if it's too close
    for (Bird other : boids) {

      if (other.state != State.flying) {
        continue;
      }

      float d = PVector.dist(pos, other.pos);

      if (d > 0 && d < desiredseparation) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(pos, other.pos);
        diff.normalize();
        diff.div(d); // Weight by distance
        steer.add(diff);
        count++; // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float) count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {

      steer.setMag(maxSpeed);
      steer.sub(vel);
      steer.limit(maxSteerForce);
    }
    return steer;
  }

  PVector alignment(ArrayList<Bird> boids) {

    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Bird other : boids) {

      if (this == other || other.state != State.flying) {
        continue;
      }

      float d = PVector.dist(pos, other.pos);
      if ((d > 0) && (d < neighborhoodRadius)) {
        sum.add(other.vel);
        count++;
      }
    }
    if (count > 0 && sum.mag() > 0) {

      sum.div((float) count);
      sum.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      PVector steer = PVector.sub(sum, vel);
      steer.limit(maxSteerForce);
      return steer;
    } else {
      return new PVector(0, 0);
    }
  }

  PVector cohesion(ArrayList<Bird> boids) {

    PVector sum = new PVector(0, 0); // Start with empty vector to accumulate all positions
    int count = 0;
    for (Bird other : boids) {

      if (this == other || other.state != State.flying) {
        continue;
      }

      float d = PVector.dist(pos, other.pos);
      if ((d > 0) && (d < neighborhoodRadius)) {
        sum.add(other.pos); // Add position
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return steer(sum, state == State.to_land); // Steer towards the position.
    } else {
      return new PVector(0, 0);
    }
  }

  PVector velocityForInitialPosition() {

    boolean isLeft = pos.x < sketch.width / 2.0;
    float yVel = sketch.random(-0.5f, 0.8f);

    if (isLeft) {
      return new PVector(1, yVel);
    } else {
      return new PVector(-1, yVel);
    }
  }
}
