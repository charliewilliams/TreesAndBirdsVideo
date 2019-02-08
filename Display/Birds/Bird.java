package Display.Birds;

import java.util.ArrayList;

import Display.Trees.TreeManager;
import Model.Note;
import Util.Util;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Bird {

	public enum State {
		flying, to_land, landed
	}

	static private float	neighborhoodRadius	= 100;		// radius in which it looks for fellow boids
	static private float	desiredseparation	= 25.0f;
	static private float	maxSpeed			= 3;		// 4; //maximum magnitude for the velocity vector
	static private float	maxSteerForce		= 0.03f;	// 0.1f; //maximum magnitude of the steering vector

	private PVector pos; // pos, velocity, and acceleration in a vector datatype

	public PVector pos() {
		return pos;
	}

	private PVector	vel;
	private PVector	acc;

	float			hue, sat, bri;
	float			size;
	private double	flap	= 0;
	private float	t		= 0;
	State			state	= State.flying;
	private PVector	stage;
	PVector			landingSite;

	float		bottomWallY;
	private int	landingTimerMillis;
	private int	toLandTimerMillis;

	Note n;

	static private int	birdCount	= 0;
	private int			birdSerialNumber;

	public Bird(Note n, PVector stage, PVector initialPos, double flapSpeed_, int millis) {

		this.n = n;
		this.stage = stage;
		bottomWallY = stage.y * 0.666667f;
		pos = initialPos;
		vel = velocityForInitialPosition(initialPos, stage);
		acc = new PVector(0, 0);

		sat = Util.randomf(50, 100);
		bri = Util.randomf(50, 100);
		flapSpeed = flapSpeed_ + Util.random(-0.05, 0.05);

		birdSerialNumber = birdCount++;
		// PApplet.println("New bird", pos);

		startLandingTimer(millis);
	}

	public void run(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock, PGraphics2D pg, int millis) {

		switch (state) {
		case flying:
			tickLandingTimer(millis);
			break;
		case landed:
			render(pg);
			return;
		case to_land:
			tickToLandTimer(millis);
			break;
		}

		updateFlap();

		checkAvoidWalls();

		// TODO add Avoid for other trees

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
		render(pg);
	}

	private boolean	avoidWalls		= true;
	static float	wallAvoidWeight	= 4;

	void checkAvoidWalls() {

		if (!avoidWalls) {
			return;
		}
		PVector avoidGround = avoid(new PVector(pos.x, bottomWallY), true);
		PVector avoidCeiling = avoid(new PVector(pos.x, 0), true);
		PVector avoidLeftWall = avoid(new PVector(0, pos.y), true);
		PVector avoidRightWall = avoid(new PVector(stage.x, pos.y), true);

		acc.add(PVector.mult(avoidGround, wallAvoidWeight));
		acc.add(PVector.mult(avoidLeftWall, wallAvoidWeight));
		acc.add(PVector.mult(avoidCeiling, wallAvoidWeight));
		acc.add(PVector.mult(avoidRightWall, wallAvoidWeight));
	}

	double flapSpeed;

	void updateFlap() {

		t += flapSpeed;
		flap = Math.sin(t);
	}

	private int lastTickMillis;

	void startLandingTimer(int millis) {
		landingTimerMillis = (int) Util.randomf(5000f, 10000f);
		lastTickMillis = millis;
	}

	// This timer counts how long the bird has been flying and makes it land after `landingTimerMillis`.
	void tickLandingTimer(int millis) {

		int millisSinceLastTick = millis - lastTickMillis;
		lastTickMillis = millis;
		landingTimerMillis -= millisSinceLastTick;
		if (landingTimerMillis <= 0) {
			state = State.to_land;
			landingSite = TreeManager.instance().treeStackFor(n).acquireLandingSite(this);
		}
	}

	// This timer counts how long we've been trying to land for; the landing site's pull gets stronger the longer we've been waiting
	private int lastToLandTickMillis;

	void tickToLandTimer(int millis) {

		int millisSinceLastTick = millis - lastToLandTickMillis;
		toLandTimerMillis += millisSinceLastTick;
	}

	///// -----------behaviors---------------
	void flock(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock) {

		PVector ali = alignment(myFlock);
		PVector coh = cohesion(myFlock);
		PVector sep = separation(allBirds);

		acc.add(PVector.mult(ali, 1));
		acc.add(PVector.mult(coh, 3));
		acc.add(PVector.mult(sep, 5));
	}

	void scatter() {
	}
	//// ------------------------------------

	void move() {

		acc.y += flap / 20.0;
		vel.add(acc); // add acceleration to velocity
		vel.limit(maxSpeed); // make sure the velocity vector magnitude does not exceed maxSpeed
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

		float r = state == State.landed ? size : size * (float) (flap / 3 + 0.5);

		ps.fill(hue, sat, bri);
		ps.stroke(hue, 100, 50);

		// Draw walls for debug
		// drawWalls(ps);

		ps.pushMatrix();
		ps.translate(pos.x, pos.y);
		ps.rotate(theta);
		ps.beginShape(PConstants.TRIANGLES);
		ps.vertex(0, -size * 2);
		ps.vertex(-r * 2, size);
		ps.vertex(r * 2, size);
		ps.endShape();
		ps.popMatrix();

		drawLandingPoint(ps);
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

	void drawLandingPoint(PGraphics2D ps) {

		// serial number label
		ps.fill(255);
		ps.text(birdSerialNumber, pos.x, pos.y);

		if (landingSite == null) {
			return;
		}

		// red
		ps.stroke(255, 0, 0, 255);

		// ellipse
		//		ps.strokeWeight(10);
		//		ps.ellipse(landingSite.x, landingSite.y, 10, 10);

		ps.strokeWeight(1);
		ps.line(pos.x, pos.y, landingSite.x, landingSite.y);

		// landing site pitch class label
		ps.fill(0);
		ps.text(n.pitchClass, landingSite.x, landingSite.y);
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

		PVector steer = new PVector(); // creates vector for steering
		PVector direction = PVector.sub(pos, target);
		steer.set(direction); // steering vector points away from target

		double dist = PVector.dist(pos, target);

		//		if (dist < 5) {
		//			PVector random = new PVector(Util.randomf(-0.5f, 0.5f), Util.randomf(-0.5f, 0.5f), 0.0f);
		//			steer = PVector.add(steer, random);
		//		}
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

	PVector velocityForInitialPosition(PVector initialPos, PVector stage) {

		boolean isLeft = initialPos.x < stage.x / 2.0;
		float yVel = Util.randomf(-0.5f, 0.8f);

		if (isLeft) {
			return new PVector(1, yVel);
		} else {
			return new PVector(-1, yVel);
		}
	}
}
