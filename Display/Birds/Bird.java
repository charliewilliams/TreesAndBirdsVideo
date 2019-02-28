package Display.Birds;

import java.util.ArrayList;
import java.util.Random;

import org.gicentre.handy.HandyRenderer;

import Display.Trees.TreeManager;
import Model.Note;
import Util.Util;
import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Bird {

	static boolean debugDrawLandingPoints = false;

	public enum State {
		flying, to_land, landed
	}

	static private float	neighborhoodRadius	= 75;	// radius in which it looks for fellow boids
	static private float	desiredseparation	= 25.0f;
	static private float	maxSteerForce		= 0.3f;	// 0.1f; //maximum magnitude of the steering vector
	private float			maxSpeed			= 6;	// 4; //maximum magnitude for the velocity vector

	private float	cohesionMultiplier		= 1; //3;
	private float	alignmentMultiplier		= 0.5f;
	private float	separationMultiplier	= 3;
	private boolean	avoidBirds				= true;

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

	Note note;

	static private int	birdCount	= 0;
	private int			birdSerialNumber;
	private Random		rand;

	public Bird(Note n, PVector stage, PVector initialPos, double flapSpeed_, int millis, Random rand, float maxSpeed,
			boolean startLandingTimer) {

		this.note = n;
		this.stage = stage;
		this.rand = rand;
		bottomWallY = stage.y;
		pos = initialPos;
		vel = velocityForInitialPosition(initialPos, stage);
		acc = new PVector(0, 0);

		if (maxSpeed > 0) {
			this.maxSpeed = maxSpeed;
		}

		sat = Util.randomf(50, 80, rand);
		bri = Util.randomf(50, 80, rand);
		flapSpeed = flapSpeed_ + Util.randomf(-0.05f, 0.05f, rand);

		birdSerialNumber = birdCount++;
		// PApplet.println("New bird", pos);

		if (startLandingTimer) {
			startLandingTimer(millis);
		}
	}

	public void run(ArrayList<Bird> allBirds, ArrayList<Bird> myFlock, PGraphicsJava2D pg, int millis,
			HandyRenderer sketcher) {

		updateFlap();

		switch (state) {
		case flying:
			tickLandingTimer(millis);
			break;
		case landed:
			render(pg, sketcher);
			return;
		case to_land:
			tickToLandTimer(millis);
			break;
		}

		checkAvoidWalls();

		if (landingSite != null) {

			float landingSiteRadius = 15;
			float landingSiteMult = 0.05f;
			if (PVector.dist(pos, landingSite) < landingSiteRadius) {
				state = State.landed;
			} 
			else {
				vel.add(PVector.mult(steer(landingSite, true), landingSiteMult));
//				acc.add(PVector.mult(steer(landingSite, true), landingSiteMult));
			}
		}
		
			flock(allBirds, myFlock);
		move();
		render(pg, sketcher);
	}

	void debugForceLand() {

		if (landingSite != null) {
			pos = landingSite;
		}
	}

	void flyAway(PVector stage, int millis) {

		if (state != State.landed) {
			return;
		}
		// don't ever fly below your current point
		bottomWallY = pos.y;

		// New landing site, offstage
		landingSite = new PVector(Util.coinToss() ? -40 : stage.x + 40, Util.randomf(stage.y / 2, -stage.y / 2));
		state = State.flying;
		startLandingTimer(millis);
		cohesionMultiplier = 0.1f;
		alignmentMultiplier = 0.05f;
		avoidWalls = false;

		// upward momentum
		pos.x += Util.randomf(-10, 10);
		pos.y += Util.randomf(-10, 0);
		acc = new PVector(Util.randomf(-20, 20), 0);
	}

	private boolean	avoidWalls				= true;
	static float	flyingWallAvoidWeight	= 10;
	static float	landingWallAvoidWeight	= 4;

	void checkAvoidWalls() {

		float wallAvoidWeight = (state == State.to_land) ? landingWallAvoidWeight : flyingWallAvoidWeight;

		PVector avoidGround = avoid(new PVector(pos.x, bottomWallY), true);
		acc.add(PVector.mult(avoidGround, wallAvoidWeight));

		if (!avoidWalls || state == State.to_land) {
			return;
		}

		PVector avoidCeiling = avoid(new PVector(pos.x, 0), true);
		PVector avoidLeftWall = avoid(new PVector(0, pos.y), true);
		PVector avoidRightWall = avoid(new PVector(stage.x, pos.y), true);

		acc.add(PVector.mult(avoidLeftWall, wallAvoidWeight));
		acc.add(PVector.mult(avoidCeiling, wallAvoidWeight));
		acc.add(PVector.mult(avoidRightWall, wallAvoidWeight));
	}

	double flapSpeed;

	void updateFlap() {

		if (state == State.landed && Util.random(0, 100) < 95) {
			return;
		}

		t += flapSpeed;
		flap = Math.sin(t);
	}

	private int lastTickMillis;
	private boolean landingTimerStarted = false;

	void startLandingTimer(int millis) {
		landingTimerMillis = (int) Util.randomf(8000f, 15000f, rand);
		lastTickMillis = millis;
		landingTimerStarted = true;
	}

	// This timer counts how long the bird has been flying and makes it land after `landingTimerMillis`.
	void tickLandingTimer(int millis) {
		
		if (!landingTimerStarted) {
			return;
		}

		int millisSinceLastTick = millis - lastTickMillis;
		lastTickMillis = millis;
		landingTimerMillis -= millisSinceLastTick;

		if (landingTimerMillis <= 0 && landingSite == null) {
			state = State.to_land;
			landingSite = TreeManager.instance().acquireLandingSite(this, note);
			landingTimerStarted = true;
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
		//		pos.y += flap / 20;
		acc.mult(0); // reset acceleration
	}

	float	lastTheta	= -1;
	float	maxRotation	= (float) Math.toRadians(30);

	void render(PGraphicsJava2D ps, HandyRenderer sketcher) {

		if (state == State.landed) {
			sketcher.setSeed(0);
		}

		// Draw a triangle rotated in the direction of velocity
		float theta = (float) (vel.heading() + Math.toRadians(90));
		if (theta > maxRotation) {
			theta = PApplet.lerp(lastTheta, theta, 0.25f); // lerp to maybe fix judder?
		}
		lastTheta = theta;

		float r = size * (float) (flap / 3 + 0.5);

		ps.fill(hue, sat, bri);
		ps.stroke(hue, 100, 50);

		// Draw walls for debug
		// drawWalls(ps);

		ps.pushMatrix();
		ps.translate(pos.x, pos.y);
		ps.rotate(theta);
		
		sketcher.beginShape(PConstants.POLYGON);

		sketcher.vertex(0, -size * 2);
		sketcher.vertex(-r * 2, size);
		sketcher.vertex(0, -size * 1.1f);
		sketcher.vertex(r * 2, size);

		sketcher.endShape(PConstants.CLOSE);
		ps.popMatrix();

		if (debugDrawLandingPoints) {
			drawLandingPoint(ps);
		}
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

	void drawLandingPoint(PGraphicsJava2D ps) {

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
		ps.text(note.pitchClass, landingSite.x, landingSite.y);
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

		if (dist < 5) {
			PVector random = new PVector(Util.randomf(-0.5f, 0.5f), Util.randomf(-0.5f, 0.5f), 0.0f);
			steer = PVector.add(steer, random);
		}
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
			return steer(sum, false); // Steer towards the position.
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
