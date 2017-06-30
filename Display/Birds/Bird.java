package Display.Birds;
import processing.core.*;
import processing.opengl.*;

import java.util.*;

import Util.Util;

public class Bird {

	PVector pos, vel, acc; //pos, velocity, and acceleration in a vector datatype
	float neighborhoodRadius = 100; //radius in which it looks for fellow boids
	float maxSpeed = 2; //4; //maximum magnitude for the velocity vector
	float maxSteerForce = 0.03f; //0.1f; //maximum magnitude of the steering vector
	float hue;
	float sc = 2; //scale factor for the render of the boid
	float flap = 0;
	float t = 0;
	boolean avoidWalls = false;
	boolean perching = false;
	int perchTimerMillis;
	PVector stage;
	Random r = new Random(0);

	Bird(PVector stage, PVector initialPos) {

		this.stage = stage;
		pos = initialPos;
		
	    float angle = Util.random(0, (float)(Math.PI * 2.0));
	    vel = new PVector((float)Math.cos(angle), (float)Math.sin(angle));
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
		checkBounds();
		render(pg);
	}

	void checkAvoidWalls() {
		
		if (!avoidWalls) {
			return;
		}
		acc.add(PVector.mult(avoid(new PVector(pos.x, stage.y), true), 5));
		acc.add(PVector.mult(avoid(new PVector(pos.x, 0), true), 5));
		acc.add(PVector.mult(avoid(new PVector(stage.x, pos.y), true), 5));
		acc.add(PVector.mult(avoid(new PVector(0, pos.y), true), 5));
		acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y), true), 5));
		acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y), true), 5));
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
		if (pos.z > 900)    	pos.z = 300;
		if (pos.z < 300)    	pos.z = 900;
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
			steer.set(PVector.sub(target, pos)); //steering vector points towards target (switch target and pos for avoiding)
			steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
		} else {
			PVector targetOffset = PVector.sub(target, pos);
			float distance=targetOffset.mag();
			float rampedSpeed = maxSpeed*(distance/100);
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
		if (weight)
			steer.mult((float) (1/Math.sqrt(PVector.dist(pos, target))));
		//steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
		return steer;
	}

	PVector separation(ArrayList<Bird> boids) {

		PVector posSum = new PVector(0, 0, 0);
		PVector repulse;
		
		for (int i=0; i<boids.size(); i++) {
			
			Bird b = boids.get(i);
			float d = PVector.dist(pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				repulse = PVector.sub(pos, b.pos);
				repulse.normalize();
				repulse.div(d);
				posSum.add(repulse);
			}
		}
		return posSum;
	}

	PVector alignment(ArrayList<Bird> boids) {

		PVector velSum = new PVector(0, 0, 0);
		int count = 0;
		for (int i = 0; i < boids.size(); i++) {

			Bird b = boids.get(i);
			float d = PVector.dist(pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				velSum.add(b.vel);
				count++;
			}
		}
		if (count > 0) {
			velSum.div((float)count);
			velSum.limit(maxSteerForce);
		}
		return velSum;
	}

	PVector cohesion(ArrayList<Bird> boids) {

		PVector posSum = new PVector(0, 0, 0);
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		for (int i = 0; i < boids.size(); i++) {
			Bird b = boids.get(i);
			float d = PVector.dist(pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				posSum.add(b.pos);
				count++;
			}
		}
		if (count > 0) {
			posSum.div((float)count);
		}
		steer = PVector.sub(posSum, pos);
		steer.limit(maxSteerForce); 
		return steer;
	}
}

