package com.boxclimb.boxclimb;

import com.badlogic.gdx.physics.box2d.Body;

class Particle {

	float x;
	float y;
	int life;
	int colour;
	Body body;

	Particle(float x, float y, int life, int colour) {
		this.x = x;
		this.y = y;
		this.life = life;
		this.colour = colour;
}
}
