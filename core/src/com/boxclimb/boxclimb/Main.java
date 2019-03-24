package com.boxclimb.boxclimb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import java.util.ArrayList;
import java.util.Iterator;

class Main implements ApplicationListener, InputProcessor, Screen {

	private final Game game;
	private final float PIXELS_TO_METRES = 100f;

	private World world;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	private Player player;

	private SpriteBatch screenSpriteBatch;
	private SpriteBatch worldSpriteBatch;
	private Texture backgroundTexture;
	private Texture wallTexture;
	private Texture ballTexture;
	private Texture aimingReticuleTexture;
	private Texture boxTexture;
	private Texture boxBoostRateTexture;
	private Texture boxExtraGravityTexture;
	private Texture coinTexture;
	private Texture powerUpDoubleCoinTexture;
	private Texture powerUpAirControlTexture;
	private Texture powerUpBoxesTexture;
	private Texture powerUpBoostTexture;

	private Vector2 touchPosition;
	private Vector2 screenCentre;
	private Vector2 forceVector;
	private ArrayList<Body> boxBodies;
	private ArrayList<Body> circleBodies;
	private ArrayList<Body> particleBodies;
	//private float accumulator;
	private int minBoxTime, maxBoxTime;
	private int boxTime;
	private int coinTime;
	private int powerUpTime;
	private int powerUpDoubleCoinTime;
	private int powerUpAirControlTime;
	private int contactFrameCount;
	private int freeFrameCount;
	private float lastBoxPlayerPosition;
	private float nextDistanceBoxCreation;
	private float extraGravityBoxChance = 0;
	private boolean touchedBeforeAttached = false;

	private ShapeRenderer shapeRenderer;
	private ShaderProgram shader;

	private Contact smashContact;
	private Contact smashContactPrevious;
	private float smashVelDiff;
	private float smashVelDiffPrevious;

	Main(Game gam) {
		game = gam;
		//accumulator = 0;
		game.extrascore = -10;
		Gdx.input.setInputProcessor(this);
		player = new Player();
		boxBodies = new ArrayList<Body>();
		circleBodies = new ArrayList<Body>();
		particleBodies = new ArrayList<Body>();
		initialiseBox2D();
		shapeRenderer = new ShapeRenderer();
		ShaderProgram.pedantic = false;
		shader = new ShaderProgram(Gdx.files.internal("emboss.vsh"), Gdx.files.internal("emboss.fsh"));
		screenSpriteBatch = new SpriteBatch();
		worldSpriteBatch = new SpriteBatch();
		//worldSpriteBatch.setShader(shader);
		if (game.zen) {
			minBoxTime = 10;
			maxBoxTime = 20;
		} else {
			minBoxTime = 20;
			maxBoxTime = 40;
		}
		boxTime = game.randomInt(minBoxTime, maxBoxTime);
		game.assetManager.finishLoading();
		backgroundTexture = game.assetManager.get("Background0.png", Texture.class);
		wallTexture = game.assetManager.get("WallNeonPink3.png", Texture.class);
		wallTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		ballTexture = game.assetManager.get("BallNeonPink.png", Texture.class);
		aimingReticuleTexture = game.assetManager.get("AimingReticule.png", Texture.class);
		boxTexture = game.assetManager.get("BoxNormalNeonPink.png", Texture.class);
		boxBoostRateTexture = game.assetManager.get("BoxBoostNeonPink.png", Texture.class);
		boxExtraGravityTexture = game.assetManager.get("BoxExtraGravityNeonPink.png", Texture.class);
		coinTexture = game.assetManager.get("Coin.png", Texture.class);
		powerUpDoubleCoinTexture = game.assetManager.get("PowerUpDoubleCoins.png", Texture.class);
		powerUpAirControlTexture = game.assetManager.get("PowerUpDoubleCoins.png", Texture.class);
		powerUpBoxesTexture = game.assetManager.get("PowerUpBox.png", Texture.class);
		powerUpBoostTexture = game.assetManager.get("PowerUpBoost.png", Texture.class);
		coinTime = game.randomInt(500, 900);
		powerUpTime = game.randomInt(1500, 3000);
		powerUpDoubleCoinTime = 0;
		powerUpAirControlTime = 0;
	}

	public void create() {
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(16640);
		Gdx.gl.glClearColor(51 / 255f, 47 / 255f, 40 / 255f, 1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//accumulator += delta;
		//while (accumulator >= game.step) {
		world.step(game.step, 6, 2);
		//	accumulator -= game.step;
		//}
		player.x = (player.body.getPosition().x * PIXELS_TO_METRES);
		player.y = (player.body.getPosition().y * PIXELS_TO_METRES);
		if (player.y > camera.position.y) {
			camera.position.set(0, player.y, 0);
			game.score = (int) (camera.position.y / 20);
			extraGravityBoxChance = game.score / 60 < 60 ? game.score / 60 : 60;
			if (!game.zen) {
				if (game.score + game.extrascore > 2000) {
					minBoxTime = 50;
					maxBoxTime = 100;
				} else if (game.score + game.extrascore > 1500) {
					minBoxTime = 30;
					maxBoxTime = 80;
				} else if (game.score + game.extrascore > 800) {
					minBoxTime = 20;
					maxBoxTime = 60;
				} else if (game.score + game.extrascore > 200) {
					minBoxTime = 20;
					maxBoxTime = 40;
				}
			}
			//if (player.y - lastBoxPlayerPosition > nextDistanceBoxCreation) {
			//	boxTime = 0;
			//}
		}
		if (player.toJoin) {
			player.joint = world.createJoint(player.jointDef);
			player.toJoin = false;
			player.boostActive = false;
		}/*
		if (player.impulse != player.lastImpulse) {
			System.out.println(player.impulse);
			if (player.impulse / 30 > 6) {
				for (int i = 0; i < player.impulse / 30; i++) {
					createParticle((player.body.getPosition().cpy().add(player.jointDef.bodyB.getPosition())).scl(0.5f), new Vector2(game.randomFloat(-player.impulse / 100, player.impulse / 100), game.randomFloat(-player.impulse / 100, player.impulse / 100)), 2);
				}
			}
			player.lastImpulse = player.impulse;
		}*/
		if (smashVelDiff != smashVelDiffPrevious) {
			for (int i = 0; i < smashVelDiff / 30; i++) {
				createParticle((smashContact.getFixtureA().getBody().getPosition().cpy().add(smashContact.getFixtureB().getBody().getPosition())).scl(0.5f), new Vector2(game.randomFloat(-smashVelDiff / 100, smashVelDiff / 100), game.randomFloat(-smashVelDiff / 100, smashVelDiff / 100)), 2);
			}
			smashVelDiffPrevious = smashVelDiff;
		}
		if (player.boost < 0) {
			player.boost = 0;
			player.boostActive = false;
		} else if (player.boost > 100) {
			player.boost = 100;
		}
		if (player.boostActive) {
			Vector2 forceVector = screenCentre.cpy().sub(touchPosition).nor();
			int max = 50;
			forceVector.x = forceVector.x < -max ? -max : forceVector.x > max ? max : forceVector.x;
			forceVector.y = forceVector.y < -max ? -max : forceVector.y > max ? max : forceVector.y;
			////////// random number alert!!!! (12)
			if (player.body.getLinearVelocity().dot(forceVector) <= 12) {
				player.body.applyForceToCenter(forceVector.cpy().scl(4 / (PIXELS_TO_METRES * game.step)), true);
				player.boost -= 1;
				createParticle(player.body.getPosition(), forceVector.cpy().scl(-10), 1);
				createParticle(player.body.getPosition(), forceVector.cpy().scl(-10), 1);
				createParticle(player.body.getPosition(), forceVector.cpy().scl(-10), 1);
			} else {
				player.boost -= 0.2;
				createParticle(player.body.getPosition(), forceVector.cpy().scl(-10), 2);
			}
		} else {
			player.body.applyForceToCenter(0, -10 * player.body.getMass() / (300 * game.step), true);
			if (world.getJointCount() > 0) {
				RectObject box = (RectObject) player.lastAttachedBody.getUserData();
				double boost;
				if (box.effect == 1) boost = 0.3;
				else boost = 0;
				player.boost += boost;
				box.boostGiven += boost;
			}
		}
		if (world.getJointCount() > 0) {
			contactFrameCount += 1;
		} else {
			freeFrameCount += 1;
		}
		boxTime -= 1;
		coinTime -= 1;
		powerUpTime -= 1;
		if (boxTime <= 0) {
			int effectChance = game.randomInt(1, 100);
			int effect = effectChance < extraGravityBoxChance ? 2 : effectChance > 98 ? 1 : 0;
			int x_pos;
			switch (effect) {
				case 2:
					x_pos = game.randomInt(Math.max((int) player.x - 50, -game.camW / 2 + 50), Math.min(game.camW / 2 - 50, (int) player.x + 50));
					break;
				case 1:
					x_pos = game.randomInt(-game.camW / 2 + 50, game.camW / 2 - 50);
					break;
				case 0:
					x_pos = game.randomInt(Math.max((int) player.x - 300, -game.camW / 2 + 50), Math.min(game.camW / 2 - 50, (int) player.x + 300));
					break;
				default:
					x_pos = game.randomInt(-game.camW / 2 + 50, game.camW / 2 - 50);
					break;
			}
			createBox(x_pos, game.randomInt(800, 1100) + camera.position.y, game.randomFloat(0, 3), game.randomFloat(-2, 2), effect);
			boxTime = game.randomInt(minBoxTime, maxBoxTime);
			lastBoxPlayerPosition = player.y;
			nextDistanceBoxCreation = game.randomFloat(minBoxTime, maxBoxTime);
		}
		if (coinTime <= 0 && circleBodies.size() < 3) {
			createCircle(game.randomInt(-game.camW / 2 + 50, game.camW / 2 - 50), game.randomInt(800, 2000) + camera.position.y, 15, 0);
			coinTime = game.randomInt(500, 900);
		}
		if (powerUpTime <= 0 && circleBodies.size() < 3) {
			createCircle(game.randomInt(-game.camW / 2 + 50, game.camW / 2 - 50), game.randomInt(800, 2000) + camera.position.y, 30, game.randomInt(1, 4));
			powerUpTime = game.randomInt(1500, 3000);
		}
		if (powerUpDoubleCoinTime >= 0) {
			powerUpDoubleCoinTime -= 1;
		}
		if (powerUpAirControlTime >= 0) {
			powerUpAirControlTime -= 1;
		}
		worldSpriteBatch.enableBlending();
		worldSpriteBatch.begin();
		worldSpriteBatch.setProjectionMatrix(camera.combined);
		worldSpriteBatch.draw(backgroundTexture, -game.camW / 2, game.camH * ((int) (camera.position.y / game.camH)) - game.camH / 2, game.camW, game.camH);
		worldSpriteBatch.draw(backgroundTexture, -game.camW / 2, game.camH * ((int) (camera.position.y / game.camH)) + game.camH / 2, game.camW, game.camH);
		Iterator<Body> boxIt = boxBodies.iterator();
		while (boxIt.hasNext()) {
			Body body = boxIt.next();
			if (body.getPosition().y * PIXELS_TO_METRES < camera.position.y - game.camH / 2 - 20) {
				if (body == player.lastAttachedBody) {
					player.lastAttachedBody = null;
				}
				world.destroyBody(body);
				boxIt.remove();
			} else {
				body.applyForceToCenter(0, -12 * body.getMass() / (300 * game.step), true);
				RectObject box = (RectObject) body.getUserData();
				Texture texture = boxTexture;
				switch (box.effect) {
					case 0:
						texture = boxTexture;
						break;
					case 1:
						texture = boxBoostRateTexture;
						break;
					case 2:
						texture = boxExtraGravityTexture;
						body.applyForceToCenter(0, -40 * body.getMass() / (300 * game.step), true);
						break;
				}
				worldSpriteBatch.draw(texture, body.getPosition().x * PIXELS_TO_METRES - box.width*2, body.getPosition().y * PIXELS_TO_METRES - box.height*2, box.width*2, box.height*2, box.width * 4, box.height * 4, 1, 1, (body.getAngle() * 180) / (float) Math.PI, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
			}
		}
		Iterator<Body> circleIt = circleBodies.iterator();
		while (circleIt.hasNext()) {
			Body body = circleIt.next();
			CircleObject circle = (CircleObject) body.getUserData();
			if (body.getPosition().y * PIXELS_TO_METRES < camera.position.y - game.camH / 2 - 20) {
				world.destroyBody(body);
				circleIt.remove();
			} else if (circle.todelete) {
				world.destroyBody(body);
				circleIt.remove();
				switch (circle.effect) {
					case 0:
						if (powerUpDoubleCoinTime > 0) {
							game.coins += 2;
						} else {
							game.coins += 1;
						}
						break;
					case 1:
						powerUpDoubleCoinTime = 1500;
						break;
					case 2:
						powerUpAirControlTime = 400;
						break;
					case 3:
						for (int i = 0; i < 5; i++)
							createBox(game.randomInt(-game.camW / 2 + 50, game.camW / 2 - 50), game.randomInt(700, 900) + camera.position.y, game.randomFloat(0, 3), game.randomFloat(-2, 2), game.randomInt(1, 100) < 85 ? 0 : 1);
						break;
					case 4:
						player.boost += 50;
						break;
				}
			} else {
				if (circle.effect != 0) {
					body.applyForceToCenter(0, -4 * body.getMass() / (300 * game.step), true);
				}
				int radius = 30;
				Texture texture = coinTexture;
				switch (circle.effect) {
					case 0:
						radius = 15;
						break;
					case 1:
						texture = powerUpDoubleCoinTexture;
						break;
					case 2:
						texture = powerUpAirControlTexture;
						break;
					case 3:
						texture = powerUpBoxesTexture;
						break;
					case 4:
						texture = powerUpBoostTexture;
						break;
				}
				worldSpriteBatch.draw(texture, body.getPosition().x * PIXELS_TO_METRES - radius, body.getPosition().y * PIXELS_TO_METRES - radius, radius * 2, radius * 2);
			}
		}
		Iterator<Body> particleIt = particleBodies.iterator();
		while (particleIt.hasNext()) {
			Body body = particleIt.next();
			Particle particle = (Particle) body.getUserData();
			if (particle.life <= 0) {
				world.destroyBody(body);
				particleIt.remove();
			} else {
				particle.life -= 1;
				Sprite particleSprite = new Sprite(ballTexture);
				switch (particle.colour) {
					case 2:
						particleSprite = new Sprite(boxExtraGravityTexture);
				}
				particleSprite.setAlpha(particle.life / 80);
				worldSpriteBatch.draw(particleSprite, body.getPosition().x * PIXELS_TO_METRES - 2, body.getPosition().y * PIXELS_TO_METRES - 2, 5, 5);
			}
		}
		/*if (touchPosition != null && !touchedBeforeAttached) {
			forceVector = screenCentre.cpy().sub(touchPosition).scl(10 / PIXELS_TO_METRES);
			forceVector.x = forceVector.x < -50 ? -50 : forceVector.x > 50 ? 50 : forceVector.x;
			forceVector.y = forceVector.y < -50 ? -50 : forceVector.y > 50 ? 50 : forceVector.y;
			forceVector = forceVector.cpy().scl(1 / (100 * game.step));
			for (int i = 1; i < 17; i++) {
				Vector2 trajectoryPoint = getTrajectoryPoint(player.body.getPosition(), player.body.getLinearVelocity().cpy().add(forceVector.cpy().scl(game.step / player.body.getMass())), i);
				worldSpriteBatch.draw(ballTexture, (trajectoryPoint.x * PIXELS_TO_METRES) - (player.size / 3), (trajectoryPoint.y * PIXELS_TO_METRES) - (player.size / 3), player.size / 3, player.size / 3, player.size * 2 / 3, player.size * 2 / 3, 1, 1, player.body.getAngle(), 0, 0, ballTexture.getWidth(), ballTexture.getHeight(), false, false);
			}
		}*/
		worldSpriteBatch.draw(ballTexture, player.x - player.size*2, player.y - player.size*2, player.size * 4, player.size * 4);
		worldSpriteBatch.draw(wallTexture, -game.camW / 2 - 10, -game.camH / 2 + camera.position.y, 40, game.camH, 0, (int) -camera.position.y * wallTexture.getHeight() / game.camH, wallTexture.getWidth(), wallTexture.getHeight(), false, false);
		worldSpriteBatch.draw(wallTexture, game.camW / 2 - 30, -game.camH / 2 + camera.position.y, 40, game.camH, 0, (int) -camera.position.y * wallTexture.getHeight() / game.camH, wallTexture.getWidth(), wallTexture.getHeight(), false, false);
		worldSpriteBatch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(camera.combined);
		if (touchPosition != null) {
			shapeRenderer.setColor(255 / 255f, 1 / 255f, 251 / 255f, .8f);
			Vector2 playerPosition = new Vector2(player.x, player.y);
			shapeRenderer.line(playerPosition, playerPosition.cpy().add(screenCentre.cpy().sub(touchPosition)));
		}
		shapeRenderer.end();
		if (game.debug) {
			debugRenderer.render(world, camera.combined.cpy().scale(PIXELS_TO_METRES, PIXELS_TO_METRES, 1));
		}
		screenSpriteBatch.begin();
		game.defaultFont.setColor(1, 1, 1, 1);
		game.menuFont.draw(screenSpriteBatch, "SCORE: " + (game.score + game.extrascore), 30, game.scrH - 40);
		game.menuFont.draw(screenSpriteBatch, "COINS: " + game.coins, 30, game.scrH - 100);
		game.menuFont.draw(screenSpriteBatch, "" + (int) Math.ceil(player.boost), 90, 150);
		game.menuFont.draw(screenSpriteBatch, "" + (int) player.impulse / 30, 480, 150);
		if (game.zen) {
			game.menuFont.draw(screenSpriteBatch, "ZEN", game.scrW - 140, game.scrH - 40);
		}
		if (game.debug) {
			game.defaultFont.draw(screenSpriteBatch, "Y: " + player.y, 30, 30);
			game.defaultFont.draw(screenSpriteBatch, "T: " + boxTime, 300, 30);
			game.defaultFont.draw(screenSpriteBatch, "BC: " + (world.getBodyCount() - 3), 500, 30);
			game.defaultFont.draw(screenSpriteBatch, "min: " + minBoxTime + ", max: " + maxBoxTime, 30, 80);
			game.defaultFont.draw(screenSpriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 30, 130);
			game.defaultFont.draw(screenSpriteBatch, "C x2: " + powerUpDoubleCoinTime, 300, 100);
			game.defaultFont.draw(screenSpriteBatch, "Air: " + powerUpAirControlTime, 300, 150);
			game.defaultFont.draw(screenSpriteBatch, "Power: " + powerUpTime, 300, 200);
			game.defaultFont.draw(screenSpriteBatch, "ExGrav %: " + extraGravityBoxChance, 100, 300);
			game.menuFont.draw(screenSpriteBatch, "" + contactFrameCount, 300, 500);
			game.menuFont.draw(screenSpriteBatch, "" + freeFrameCount, 300, 550);
		}
		screenSpriteBatch.draw(wallTexture, 50, 50, 50, player.boost * 3);
		screenSpriteBatch.draw(aimingReticuleTexture, screenCentre.x - 250, screenCentre.y - 250, 500, 500);
		screenSpriteBatch.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		camera.update();
		if (player.y < camera.position.y - game.camH / 2 - player.size) {
			game.resetWorld();
			game.setScreen(new GameOver(game));
		}
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			game.resetWorld();
			game.setScreen(new GameOver(game));
		}
	}

	@Override
	public void resize(int width, int height) {
		screenCentre = new Vector2(game.scrW / 2, game.scrH / 2);
	}

	@Override
	public void render() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void hide() {

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (game.settingsprefs.getBoolean("invertx")) {
			touchPosition = new Vector2(screenX, game.scrH - screenY);
		} else {
			touchPosition = new Vector2(game.scrW - screenX, game.scrH - screenY);
		}
		if (screenX > game.scrW - 50 && screenY < 50) game.debug = !game.debug;
		if (player.body.getJointList().size == 0 && player.boost > 0) player.boostActive = true;
		if (world.getJointCount() == 0) {
			touchedBeforeAttached = true;
			if (player.boost == 0) game.step = 1 / 100f;
		} else touchedBeforeAttached = false;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		player.boostActive = false;
		Vector2 touchUpPosition;
		if (game.settingsprefs.getBoolean("invertx")) {
			touchUpPosition = new Vector2(screenX, game.scrH - screenY);
		} else {
			touchUpPosition = new Vector2(game.scrW - screenX, game.scrH - screenY);
		}
		forceVector = screenCentre.cpy().sub(touchUpPosition).scl(10 / PIXELS_TO_METRES);
		/*int max = (int) (powerUpAirControlTime > 0 ? game.step*50000 : game.step*20000);
		if (player.body.getJointList().size > 0) {
            world.destroyJoint(player.joint);
            max = (int) (game.step*80000);
        }
        forceVector.x = forceVector.x < -max ? -max : forceVector.x > max ? max : forceVector.x;
        forceVector.y = forceVector.y < -max ? -max : forceVector.y > max ? max : forceVector.y;*/
		if (player.body.getJointList().size > 0 && !touchedBeforeAttached) {
			world.destroyJoint(player.joint);
			RectObject box = (RectObject) player.lastAttachedBody.getUserData();
			//////// SIMPLE COMBO VERSION 0.1
			if (contactFrameCount < 50 && freeFrameCount < 100) {
				player.boost += 10;
			} else {
				player.boost += 5;
			}
			game.step = 1 / 200f;
			freeFrameCount = 0;
			int max = 50;
			forceVector.x = forceVector.x < -max ? -max : forceVector.x > max ? max : forceVector.x;
			forceVector.y = forceVector.y < -max ? -max : forceVector.y > max ? max : forceVector.y;
			player.body.applyForceToCenter(forceVector.cpy().scl(1 / (100 * game.step)), true);
		}
		//player.body.setLinearVelocity(0, 0);k
		//player.body.setAngularVelocity(0);
		touchPosition = null;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (game.settingsprefs.getBoolean("invertx")) {
			touchPosition = new Vector2(screenX, game.scrH - screenY);
		} else {
			touchPosition = new Vector2(game.scrW - screenX, game.scrH - screenY);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	private void initialiseBox2D() {
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(game.camW, game.camH);
		camera.position.set(0, 0, 0);
		world = game.world;
		player.x = 0;
		player.y = 0;

		/*RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);

		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.4f, 0.4f, 0.4f, 1);
		rayHandler.setBlur(true);
		rayHandler.setCulling(false);
		rayHandler.setShadows(true);

		lights.add(new PointLight(rayHandler, 200, Color.WHITE, 2000 / PIXELS_TO_METRES, 400 / PIXELS_TO_METRES, 300 / PIXELS_TO_METRES));
		lights.add(new PointLight(rayHandler, 128, Color.WHITE, 2000 / PIXELS_TO_METRES, 1200 / PIXELS_TO_METRES, 700 / PIXELS_TO_METRES));
		lights.add(new PointLight(rayHandler, 128, Color.WHITE, 1800 / PIXELS_TO_METRES, 800 / PIXELS_TO_METRES, 2700 / PIXELS_TO_METRES));
		lights.add(new DirectionalLight(rayHandler, 200, Color.WHITE, 300));
		lights.get(0).setContactFilter((short) 0x0008, (short) 0x0000, (short) -1);
		lights.get(0).setSoft(false);*/

		final BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyDef.BodyType.DynamicBody;
		playerBodyDef.bullet = true;
		playerBodyDef.fixedRotation = true;
		playerBodyDef.position.set(player.x / PIXELS_TO_METRES, player.y / PIXELS_TO_METRES);
		player.body = world.createBody(playerBodyDef);
		Shape playerShape = new CircleShape();
		playerShape.setRadius(player.size / PIXELS_TO_METRES);
		FixtureDef playerFixtureDef = new FixtureDef();
		playerFixtureDef.shape = playerShape;
		playerFixtureDef.density = 0.5f;
		playerFixtureDef.restitution = 1;
		playerFixtureDef.friction = 0.2f;
		playerFixtureDef.filter.categoryBits = 0x0002;
		player.body.createFixture(playerFixtureDef);
		player.body.setUserData(player);
		player.jointDef = new RopeJointDef();
		player.jointDef.bodyA = player.body;
		player.jointDef.collideConnected = true;
		playerShape.dispose();

		BodyDef platformBodyDef = new BodyDef();
		platformBodyDef.type = BodyDef.BodyType.StaticBody;
		platformBodyDef.bullet = false;
		PolygonShape platformShape = new PolygonShape();
		FixtureDef platformFixtureDef = new FixtureDef();
		platformFixtureDef.density = 1;
		platformFixtureDef.restitution = 1;
		platformFixtureDef.friction = 0.2f;
		for (RectObject plat : new RectObject[]{new RectObject(-game.camW / 2, 0, 20, game.camH * 200), new RectObject(game.camW / 2, 0, 20, game.camH * 200)}) {
			platformBodyDef.position.set(plat.x / PIXELS_TO_METRES, plat.y / PIXELS_TO_METRES);
			plat.body = world.createBody(platformBodyDef);
			platformShape.setAsBox(plat.width / PIXELS_TO_METRES, plat.height / PIXELS_TO_METRES, new Vector2(0, 0), 0);
			platformFixtureDef.shape = platformShape;
			plat.body.createFixture(platformFixtureDef);
		}
		platformShape.dispose();

		createBox(50, 100);
		createBox(-100, 300);
		createBox(-70, 700);
		createBox(0, -40);

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getBody() == player.body || contact.getFixtureB().getBody() == player.body) {
					if (player.body.getJointList().size == 0) {
						for (Body b : boxBodies) {
							if ((contact.getFixtureA().getBody() == b || contact.getFixtureB().getBody() == b) && b != player.lastAttachedBody) {
								game.step = 1 / 200f;
								player.toJoin = true;
								player.jointDef.bodyB = b;
								player.jointDef.localAnchorA.set(player.body.getLocalCenter());
								player.jointDef.localAnchorB.set(b.getLocalPoint(player.body.getPosition()));
								player.lastAttachedBody = b;
								game.extrascore += 10;
								contactFrameCount = 0;
							} else if ((contact.getFixtureA().getBody() == b || contact.getFixtureB().getBody() == b) && b == player.lastAttachedBody && contactFrameCount > 5) {
								game.step = 1 / 200f;
								RectObject box = (RectObject) b.getUserData();
								box.doubleTouched = true;
							}
						}
					}
					for (Body c : circleBodies) {
						if ((contact.getFixtureA().getBody() == c || contact.getFixtureB().getBody() == c)) {
							game.step = 1 / 200f;
							CircleObject circle = (CircleObject) c.getUserData();
							circle.todelete = true;
						}
					}
					if (contact.getFixtureA().getBody() == player.body) {
						player.impulse = player.body.getLinearVelocity().cpy().sub(contact.getFixtureB().getBody().getLinearVelocity()).len2();
					} else {
						player.impulse = player.body.getLinearVelocity().cpy().sub(contact.getFixtureA().getBody().getLinearVelocity()).len2();
					}
				}
				if (game.settingsprefs.getBoolean("collisionparticleeffect")) {
					if (contact.getFixtureA().getFilterData().maskBits != 0x0001 && contact.getFixtureB().getFilterData().maskBits != 0x0001) {
						float velDiff = contact.getFixtureA().getBody().getLinearVelocity().cpy().sub(contact.getFixtureB().getBody().getLinearVelocity()).len2();
						if (velDiff / 30 > 6) {
							smashContact = contact;
							smashVelDiff = velDiff;
						}
					}
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}

	private Vector2 getTrajectoryPoint(Vector2 startingPosition, Vector2 startingVelocity, float n) {
		Vector2 stepVelocity = new Vector2(startingVelocity).scl(1 / 60f, 1 / 60f).scl(n);
		Vector2 stepGravity = new Vector2(0, -10).scl(1 / 3600f, 1 / 3600f).scl(0.5f * ((n * n) + n));
		return startingPosition.add(stepVelocity.x, stepVelocity.y).add(stepGravity.x, stepGravity.y);
	}

	private Vector2 getWorldCoords(Vector2 screenCoords) {
		return new Vector2(camera.position.x + screenCoords.x - game.scrW / 2,
				camera.position.y + screenCoords.y - game.scrH / 2);
	}

	private Vector2 getWorldCoords(float screenX, float screenY) {
		return new Vector2(camera.position.x + screenX - game.scrW / 2,
				camera.position.y + screenY - game.scrH / 2);
	}

	private Vector2 getScreenCoords(Vector2 worldCoords) {
		return new Vector2(worldCoords.x - camera.position.x + game.scrW / 2,
				worldCoords.y - camera.position.y - game.scrH / 2);
	}

	private Vector2 getScreenCoords(float worldX, float worldY) {
		return new Vector2(worldX - camera.position.x + game.scrW / 2,
				worldY - camera.position.y - game.scrH / 2);
	}

	private Body createBox(float x, float y) {
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyDef.BodyType.DynamicBody;
		boxBodyDef.bullet = false;
		PolygonShape boxShape = new PolygonShape();
		FixtureDef boxFixtureDef = new FixtureDef();
		boxFixtureDef.density = 1;
		boxFixtureDef.restitution = 1;
		boxFixtureDef.friction = 0.2f;
		boxBodyDef.position.set(x / PIXELS_TO_METRES, y / PIXELS_TO_METRES);
		Body body = world.createBody(boxBodyDef);
		boxShape.setAsBox(20 / PIXELS_TO_METRES, 20 / PIXELS_TO_METRES, new Vector2(0, 0), 0);
		boxFixtureDef.shape = boxShape;
		body.createFixture(boxFixtureDef);
		body.setUserData(new RectObject(x, y, 20, 20));
		boxBodies.add(body);
		boxShape.dispose();
		return body;
	}

	private Body createBox(float x, float y, float angle, float angularVelocity, int effect) {
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyDef.BodyType.DynamicBody;
		boxBodyDef.bullet = false;
		boxBodyDef.angle = angle;
		PolygonShape boxShape = new PolygonShape();
		FixtureDef boxFixtureDef = new FixtureDef();
		boxFixtureDef.density = 1;
		boxFixtureDef.restitution = 1;
		boxFixtureDef.friction = 1;
		boxBodyDef.position.set(x / PIXELS_TO_METRES, y / PIXELS_TO_METRES);
		Body body = world.createBody(boxBodyDef);
		boxShape.setAsBox(20 / PIXELS_TO_METRES, 20 / PIXELS_TO_METRES, new Vector2(0, 0), 0);
		boxFixtureDef.shape = boxShape;
		body.createFixture(boxFixtureDef);
		body.setUserData(new RectObject(x, y, 20, 20, effect));
		body.setAngularVelocity(angularVelocity);
		boxBodies.add(body);
		boxShape.dispose();
		return body;
	}

	private Body createCircle(float x, float y, int radius, int effect) {
		BodyDef coinBodyDef = new BodyDef();
		coinBodyDef.type = BodyDef.BodyType.DynamicBody;
		coinBodyDef.bullet = false;
		Shape coinShape = new CircleShape();
		FixtureDef coinFixtureDef = new FixtureDef();
		coinFixtureDef.density = 1;
		coinFixtureDef.restitution = 1;
		coinFixtureDef.friction = 1;
		coinBodyDef.position.set(x / PIXELS_TO_METRES, y / PIXELS_TO_METRES);
		Body body = world.createBody(coinBodyDef);
		coinShape.setRadius(radius / PIXELS_TO_METRES);
		coinFixtureDef.shape = coinShape;
		coinFixtureDef.isSensor = true;
		body.createFixture(coinFixtureDef);
		body.setUserData(new CircleObject(x, y, radius, effect));
		circleBodies.add(body);
		coinShape.dispose();
		return body;
	}

	private Body createParticle(Vector2 position, Vector2 velocity, int colour) {
		BodyDef particleBodyDef = new BodyDef();
		particleBodyDef.type = BodyDef.BodyType.DynamicBody;
		particleBodyDef.bullet = true;
		Shape particleShape = new CircleShape();
		FixtureDef particleFixtureDef = new FixtureDef();
		particleFixtureDef.density = 1;
		particleFixtureDef.restitution = 1;
		particleFixtureDef.friction = 1;
		particleBodyDef.position.set(position.x + velocity.x * velocity.x * game.randomFloat(-1, 1) / 10000, position.y + velocity.y * velocity.y * game.randomFloat(-1, 1) / 10000);
		Body body = world.createBody(particleBodyDef);
		particleShape.setRadius(1 / PIXELS_TO_METRES);
		particleFixtureDef.shape = particleShape;
		particleFixtureDef.filter.maskBits = 0x0001;
		body.createFixture(particleFixtureDef);
		body.setUserData(new Particle(position.x, position.y, game.randomInt(50, 80), colour));
		particleBodies.add(body);
		body.setLinearVelocity(velocity.cpy().scl(game.randomFloat(3, 7) / 5));
		body.setLinearDamping(0.1f);
		particleShape.dispose();
		return body;
	}
}
