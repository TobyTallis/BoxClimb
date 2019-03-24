package com.boxclimb.boxclimb;

import com.badlogic.gdx.physics.box2d.Body;

class CircleObject {

	float x;
	float y;
	int radius;
	boolean todelete = false;
	Body body;
	int effect = 0; // 0 = coin, 1 = double coin value for 1500 ticks, 2 = increase air control for 400 ticks, 3 = 5 extra boxes spawn, 4 = 40 extra boost

	CircleObject(float x, float y, int radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	CircleObject(float x, float y, int radius, int effect) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.effect = effect;
	}
}
