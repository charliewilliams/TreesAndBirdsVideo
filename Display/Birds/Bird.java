package Display.Birds;

import processing.core.*;
import processing.opengl.*;
import java.util.*;

import Util.Util;

public class Bird {

	public enum State {
		flying, to_land, landed
	}

	static private float neighborhoodRadius = 500; // radius in which it looks
													// for fellow boids
	static private float desiredseparation = 75.0f;
	static private float maxSpeed = 3; // 4; //maximum magnitude for the
										// velocity vector
	static private float maxSteerForce = 0.03f; // 0.1f; //maximum magnitude of
												// the steering vector

	private PVector pos; // pos, velocity, and acceleration in a vector datatype
	private PVector vel;
	private PVector acc;

	float hue;
	private float flap = 0;
	static private float t = 0;
	State state = State.flying;
	private PVector stage;
	PVector landingSite;
	
	float bottomWallY;

	Bird(PVector stage, PVector initialPos) {

		this.stage = stage;
		bottomWallY = stage.y * 0.666667f;
		pos = initialPos;
		vel = velocityForInitialPosition(initialPos, stage);
		acc = new PVector(0, 0);

		PApplet.println("New bird", pos);
	}

	public void run(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock, PGraphics2D pg) {

		perchOrFlap();

		checkAvoidWalls();

		// TODO add Avoid for other trees

		// TODO if landing add Steer toward the home tree + landing = true

		flock(allBirds, myFlock);
		move();
		render(pg);
	}

	private boolean avoidWalls = true;
	static float wallAvoidWeight = 4;

	void checkAvoidWalls() {

		if (!avoidWalls) {
			return;
		}
		 PVector avoidGround    = avoid(new PVector(pos.x,   bottomWallY), true);
		 PVector avoidCeiling   = avoid(new PVector(pos.x,   0), true);
		 PVector avoidLeftWall  = avoid(new PVector(0,       pos.y), true);
		 PVector avoidRightWall = avoid(new PVector(stage.x, pos.y), true);
		 
		 acc.add(PVector.mult(avoidGround, wallAvoidWeight));
		 acc.add(PVector.mult(avoidLeftWall, wallAvoidWeight));
		 acc.add(PVector.mult(avoidCeiling, wallAvoidWeight));
		 acc.add(PVector.mult(avoidRightWall, wallAvoidWeight));
	}

	void perchOrFlap() {

		if (state != State.landed) {
			t += .1;
			flap = (float) (10 * Math.sin(t));
		}
	}

	///// -----------behaviors---------------
	void flock(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock) {

		PVector ali = alignment(myFlock);
		PVector coh = cohesion(myFlock);
		PVector sep = separation(allBirds);

		acc.add(PVector.mult(ali, 1));
		acc.add(PVector.mult(coh, 3));
		acc.add(PVector.mult(sep, 1));
	}

	void scatter() {
	}
	//// ------------------------------------

	void move() {

		vel.add(acc); // add acceleration to velocity
		vel.limit(maxSpeed); // make sure the velocity vector magnitude does not
								// exceed maxSpeed
		pos.add(vel); // add velocity to position
		acc.mult(0); // reset acceleration
	}

	void checkBounds() {

		if (pos.x > stage.x)
			pos.x = 0;
		if (pos.x < 0)
			pos.x = stage.x;
		if (pos.y > stage.y)
			pos.y = 0;
		if (pos.y < 0)
			pos.y = stage.y;
	}

	void render(PGraphics2D ps) {

		// Draw a triangle rotated in the direction of velocity
		float theta = (float) (vel.heading() + Math.toRadians(90));

		float r = 2;

		ps.fill(200, 100);
		ps.stroke(255);

		// Draw walls for debug
//		drawWalls(ps);

		ps.pushMatrix();
		ps.translate(pos.x, pos.y);
		ps.rotate(theta);
		ps.beginShape(PConstants.TRIANGLES);
		ps.vertex(0, -r * 2);
		ps.vertex(-r, r * 2);
		ps.vertex(r, r * 2);
		ps.endShape();
		ps.popMatrix();
	}
	
	void drawWalls(PGraphics2D ps) {
		
		ps.strokeWeight(5);
		ps.stroke(255, 0, 0, 255); // left - red
		ps.line(0, 0, 0, bottomWallY);
		ps.stroke(0, 255, 0, 255); // bottom - green
		ps.line(0, bottomWallY, stage.x, bottomWallY);
		ps.stroke(0, 0, 255, 255); // right - blue
		ps.line(stage.x, 0, stage.x, bottomWallY);
		ps.stroke(255, 0, 255, 255); // top - magenta
		ps.line(stage.x, 0, 0, 0);
		ps.stroke(255);
	}

	// steering. If arrival==true, the boid slows to meet the target. Credit to
	// Craig Reynolds
	PVector steer(PVector target, boolean arrival) {

		PVector steer = new PVector(); // creates vector for steering
		if (!arrival) {
			steer = PVector.sub(target, pos); // steering vector points towards
												// target (switch target and pos
												// for avoiding)
			steer.limit(maxSteerForce); // limits the steering force to
										// maxSteerForce
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

	// avoid. If weight == true avoidance vector is larger the closer the boid
	// is to the target
	PVector avoid(PVector target, boolean weight) {

		PVector steer = new PVector(); // creates vector for steering
		steer.set(PVector.sub(pos, target)); // steering vector points away from target
		
//		double dist = Math.sqrt(PVector.dist(pos, target));
		double dist = PVector.dist(pos, target);
		if (weight) {
			double divisor = dist * dist + 1;
			steer.mult((float) (1 / divisor));
		}
		steer.limit(maxSteerForce); // limits the steering force to
									// maxSteerForce
		return steer;
	}

	PVector separation(ArrayList<Bird> boids) {

		// PVector posSum = new PVector(0, 0);
		// PVector repulse;
		//
		// for (Bird b: boids) {
		//
		// float d = PVector.dist(pos, b.pos);
		// if (d > 0 && d <= neighborhoodRadius) {
		// repulse = PVector.sub(pos, b.pos);
		// repulse.normalize();
		// repulse.div(d);
		// posSum.add(repulse);
		// }
		// }
		// return posSum;

		/* Shiffman */

		PVector steer = new PVector(0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (Bird other : boids) {

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
			// First two lines of code below could be condensed with new PVector
			// setMag() method
			// Not using this method until Processing.js catches up
			steer.setMag(maxSpeed);

			// Implement Reynolds: Steering = Desired - Velocity
			// steer.normalize();
			// steer.mult(maxSpeed);
			steer.sub(vel);
			steer.limit(maxSteerForce);
		}
		return steer;
	}

	PVector alignment(ArrayList<Bird> boids) {

		// PVector velSum = new PVector(0, 0);
		// int count = 0;
		// for (Bird b: boids) {
		//
		// float d = PVector.dist(pos, b.pos);
		// if (d > 0 && d <= neighborhoodRadius) {
		// velSum.add(b.vel);
		// count++;
		// }
		// }
		// if (count > 0) {
		// velSum.div((float)count);
		// velSum.limit(maxSteerForce);
		// }
		// return velSum;

		/**/

		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Bird other : boids) {
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

		// PVector posSum = new PVector(0, 0);
		// PVector steer = new PVector(0, 0);
		// int count = 0;
		//
		// for (Bird b: boids) {
		// float d = PVector.dist(pos, b.pos);
		// if (d > 0 && d <= neighborhoodRadius) {
		// posSum.add(b.pos);
		// count++;
		// }
		// }
		// if (count > 0) {
		// posSum.div((float)count);
		// }
		// steer = PVector.sub(posSum, pos);
		// steer.limit(maxSteerForce);
		// return steer;

		/**/

		PVector sum = new PVector(0, 0); // Start with empty vector to
											// accumulate all positions
		int count = 0;
		for (Bird other : boids) {
			float d = PVector.dist(pos, other.pos);
			if ((d > 0) && (d < neighborhoodRadius)) {
				sum.add(other.pos); // Add position
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return steer(sum, state == State.to_land); // Steer towards the
														// position. TODO
														// instead of slowing
														// when we're going to
														// land, slow when we're
														// close to the landing
														// site
		} else {
			return new PVector(0, 0);
		}
	}

	PVector velocityForInitialPosition(PVector initialPos, PVector stage) {

		boolean isLeft = initialPos.x < stage.x / 2.0;
		float yVel = Util.random(-0.5f, 0.8f);

		if (isLeft) {
			return new PVector(1, yVel);
		} else {
			return new PVector(-1, yVel);
		}
	}
}
