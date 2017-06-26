package Display.Birds;
import processing.core.*;
import processing.opengl.*;

import java.util.*;

public class Bird {

	PVector pos, vel, acc, ali, coh, sep; //pos, velocity, and acceleration in a vector datatype
	float neighborhoodRadius = 100; //radius in which it looks for fellow boids
	float maxSpeed = 4; //maximum magnitude for the velocity vector
	float maxSteerForce = 0.1f; //maximum magnitude of the steering vector
	float hue;
	float sc = 3; //scale factor for the render of the boid
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
		vel = new PVector(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1);
		acc = new PVector(0, 0, 0);
	}
	
	void run(ArrayList<Bird> bl, PGraphics3D pg) {
		
		if (perching) {
			perchTimerMillis--; // TEMP, should be actual millis and not ticks
		} else {
			t += .1;
			flap = (float) (10 * Math.sin(t));
		}

		//acc.add(steer(new PVector(mouseX,mouseY,300),true));
		//acc.add(new PVector(0,.05,0));
		if (avoidWalls) {
			acc.add(PVector.mult(avoid(new PVector(pos.x, stage.x, pos.z), true), 5));
			acc.add(PVector.mult(avoid(new PVector(pos.x, 0, pos.z), true), 5));
			acc.add(PVector.mult(avoid(new PVector(stage.x, pos.y, pos.z), true), 5));
			acc.add(PVector.mult(avoid(new PVector(0, pos.y, pos.z), true), 5));
			acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 300), true), 5));
			acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 900), true), 5));
		}
		
		// TODO add Avoid for other trees
		
		// TODO if landing add Steer toward the home tree + landing = true
		
		flock(bl);
		move();
		checkBounds();
		render(pg);
	}

	/////-----------behaviors---------------
	void flock(ArrayList<Bird> bl) {
		
		ali = alignment(bl);
		coh = cohesion(bl);
		sep = separation(bl);
		
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

	void render(PGraphics3D ps) {

		ps.pushMatrix();
		ps.translate(pos.x, pos.y, pos.z);
		ps.rotateY((float) Math.atan2(-vel.z, vel.x));
		ps.rotateZ((float) Math.asin(vel.y / vel.mag()));
		ps.stroke(hue);
		ps.fill(hue, 100, 100);

		//draw bird
		ps.beginShape(PConstants.TRIANGLES);
		ps.vertex(3 * sc, 0, 0);
		ps.vertex(-3 * sc, 2 * sc, 0);
		ps.vertex(-3 * sc, -2 * sc, 0);

		ps.vertex(3 * sc, 0, 0);
		ps.vertex(-3 * sc, 2 * sc, 0);
		ps.vertex(-3 * sc, 0, 2 * sc);

		ps.vertex(3 * sc, 0, 0);
		ps.vertex(-3 * sc, 0, 2 * sc);
		ps.vertex(-3 * sc, -2 * sc, 0);

		// wings
		ps.vertex(2 * sc, 0, 0);
		ps.vertex(-1 * sc, 0, 0);
		ps.vertex(-1 * sc, -8 * sc, flap);

		ps.vertex(2 * sc, 0, 0);
		ps.vertex(-1 * sc, 0, 0);
		ps.vertex(-1 * sc, 8 * sc, flap);
		//

		ps.vertex(-3 * sc, 0, 2 * sc);
		ps.vertex(-3 * sc, 2 * sc, 0);
		ps.vertex(-3 * sc, -2 * sc, 0);
		ps.endShape();
//		box(10);
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

