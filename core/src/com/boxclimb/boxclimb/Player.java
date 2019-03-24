package com.boxclimb.boxclimb;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

class Player {

	float x;
	float y;
	int size = 20;
	Body body;
	boolean toJoin = false;
	RopeJointDef jointDef;
	Joint joint;
	Body lastAttachedBody;
	float boost = 0;
	boolean boostActive = false;
	float impulse;
	float lastImpulse = 0f;

	Player() {
	}
}
