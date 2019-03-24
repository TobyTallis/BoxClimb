package com.boxclimb.boxclimb;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

class RectObject {

	float x;
	float y;
	int width;
	int height;
	boolean doubleTouched = false;
	boolean todelete = false;
	Body body;
	int effect = 0; // 0 = box, 1 = boost rate increase box (triple rate of boost gain while in contact)
	int boostGiven = 0;
	Texture texture;

	RectObject(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	RectObject(float x, float y, int width, int height, int effect) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.effect = effect;
	}

	RectObject(float x, float y, int width, int height, Texture texture) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
}
