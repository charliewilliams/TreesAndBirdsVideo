package Display.Birds;
import processing.core.*;
import processing.opengl.*;
import java.util.*;

public class Bird {

	static private float neighborhoodRadius = 100; //radius in which it looks for fellow boids
	static private float desiredseparation = 25.0f;
	static private float maxSpeed = 2; //4; //maximum magnitude for the velocity vector
	static private float maxSteerForce = 0.03f; //0.1f; //maximum magnitude of the steering vector
	
	private PVector pos, vel, acc; //pos, velocity, and acceleration in a vector datatype
	float hue;
	private float flap = 0;
	static private float t = 0;
	private boolean avoidWalls = false;
	private boolean perching = false;
	private int perchTimerMillis;
	private PVector stage;

	Bird(PVector stage, PVector initialPos) {

		this.stage = stage;
		pos = initialPos;
		vel = PVector.random2D();
		acc = new PVector(0, 0);
	}

	void run(ArrayList<Bird> bl, PGraphics2D pg) {

		perchOrFlap();

		//		acc.add(steer(new PVector(mouseX,mouseY,300),true));
		//		acc.add(new PVector(0,.05,0));

		checkAvoidWalls();

		// TODO add Avoid for other trees

		// TODO if landing add Steer toward the home tree + landing = true

		flock(bl);
		move();
//		checkBounds();
		render(pg);
		
//		PApplet.println(pos);
	}

	static float wallAvoidWeight = 5;
	void checkAvoidWalls() {

		if (!avoidWalls) {
			return;
		}
		PVector avoidGround = avoid(new PVector(pos.x, stage.y * 0.6667f), true);
//		PApplet.println(avoidGround);
		acc.add(PVector.mult(avoidGround, wallAvoidWeight));
		PVector avoidLeftWall = avoid(new PVector(pos.x, 0), true);
		PApplet.println(avoidLeftWall);
		acc.add(PVector.mult(avoidLeftWall, wallAvoidWeight));
		acc.add(PVector.mult(avoid(new PVector(stage.x, pos.y), true), wallAvoidWeight));
		acc.add(PVector.mult(avoid(new PVector(0, pos.y), true), wallAvoidWeight));
		acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y), true), wallAvoidWeight));
		acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y), true), wallAvoidWeight));
	}

	void perchOrFlap() {

		if (perching) {
			perchTimerMillis--; // TEMP, should be actual millis and not ticks
		} else {
			t += .1;
			flap = (float) (10 * Math.sin(t));
		}
	}

	/////-----------behaviors---------------
	void flock(ArrayList<Bird> bl) {

		PVector ali = alignment(bl);
		PVector coh = cohesion(bl);
		PVector sep = separation(bl);

		acc.add(PVector.mult(ali, 1));
		acc.add(PVector.mult(coh, 3));
		acc.add(PVector.mult(sep, 1));
	}

	void scatter() {
	}
	////------------------------------------

	void move() {

		vel.add(acc); //add acceleration to velocity
		vel.limit(maxSpeed); //make sure the velocity vector magnitude does not exceed maxSpeed
		pos.add(vel); //add velocity to position
		acc.mult(0); //reset acceleration
	}

	void checkBounds() {

		if (pos.x > stage.x)  	pos.x = 0;
		if (pos.x < 0)      	pos.x = stage.x;
		if (pos.y > stage.y) 	pos.y = 0;
		if (pos.y < 0)      	pos.y = stage.y;
	}

	void render(PGraphics2D ps) {

		// Draw a triangle rotated in the direction of velocity
		float theta = (float) (vel.heading() + Math.toRadians(90));
		// heading2D() above is now heading() but leaving old syntax until Processing.js catches up

		float r = 2;

		ps.fill(200, 100);
		ps.stroke(255);
		ps.pushMatrix();
		ps.translate(pos.x, pos.y);
		ps.rotate(theta);
		ps.beginShape(PConstants.TRIANGLES);
		ps.vertex(0, -r*2);
		ps.vertex(-r, r*2);
		ps.vertex(r, r*2);
		ps.endShape();
		ps.popMatrix();
	}

	//steering. If arrival==true, the boid slows to meet the target. Credit to Craig Reynolds
	PVector steer(PVector target, boolean arrival) {

		PVector steer = new PVector(); //creates vector for steering
		if (!arrival) {
			steer = PVector.sub(target, pos); //steering vector points towards target (switch target and pos for avoiding)
			steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
		} else {
			PVector targetOffset = PVector.sub(target, pos);
			float distance = targetOffset.mag();
			float rampedSpeed = maxSpeed * (distance / 100);
			float clippedSpeed = Math.min(rampedSpeed, maxSpeed);
			PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed/distance));
			steer.set(PVector.sub(desiredVelocity, vel));
		}
		return steer;
	}

	//avoid. If weight == true avoidance vector is larger the closer the boid is to the target
	PVector avoid(PVector target, boolean weight) {

		PVector steer = new PVector(); //creates vector for steering
		steer.set(PVector.sub(pos, target)); //steering vector points away from target
		if (weight) {
			steer.mult((float) (1/Math.sqrt(PVector.dist(pos, target))));
		}
		steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
		return steer;
	}

	PVector separation(ArrayList<Bird> boids) {

		//		PVector posSum = new PVector(0, 0);
		//		PVector repulse;
		//		
		//		for (Bird b: boids) {
		//			
		//			float d = PVector.dist(pos, b.pos);
		//			if (d > 0 && d <= neighborhoodRadius) {
		//				repulse = PVector.sub(pos, b.pos);
		//				repulse.normalize();
		//				repulse.div(d);
		//				posSum.add(repulse);
		//			}
		//		}
		//		return posSum;

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
				diff.div(d);        // Weight by distance
				steer.add(diff);
				count++;            // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float)count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// First two lines of code below could be condensed with new PVector setMag() method
			// Not using this method until Processing.js catches up
			// steer.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxSpeed);
			steer.sub(vel);
			steer.limit(maxSteerForce);
		}
		return steer;
	}

	PVector alignment(ArrayList<Bird> boids) {

		//		PVector velSum = new PVector(0, 0);
		//		int count = 0;
		//		for (Bird b: boids) {
		//
		//			float d = PVector.dist(pos, b.pos);
		//			if (d > 0 && d <= neighborhoodRadius) {
		//				velSum.add(b.vel);
		//				count++;
		//			}
		//		}
		//		if (count > 0) {
		//			velSum.div((float)count);
		//			velSum.limit(maxSteerForce);
		//		}
		//		return velSum;

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
		if (count > 0) {
			sum.div((float)count);
			sum.setMag(maxSpeed);

			// Implement Reynolds: Steering = Desired - Velocity
			PVector steer = PVector.sub(sum, vel);
			steer.limit(maxSteerForce);
			return steer;
		} 
		else {
			return new PVector(0, 0);
		}
	}

	PVector cohesion(ArrayList<Bird> boids) {

//		PVector posSum = new PVector(0, 0);
//		PVector steer = new PVector(0, 0);
//		int count = 0;
//
//		for (Bird b: boids) {
//			float d = PVector.dist(pos, b.pos);
//			if (d > 0 && d <= neighborhoodRadius) {
//				posSum.add(b.pos);
//				count++;
//			}
//		}
//		if (count > 0) {
//			posSum.div((float)count);
//		}
//		steer = PVector.sub(posSum, pos);
//		steer.limit(maxSteerForce); 
//		return steer;
		
		/**/
		

		PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all positions
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
			return steer(sum, perching);  // Steer towards the position
		} 
		else {
			return new PVector(0, 0);
		}
	}
}

