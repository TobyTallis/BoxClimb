package com.boxclimb.boxclimb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Settings implements ApplicationListener, InputProcessor, Screen {

	private final Game game;

	private SpriteBatch screenSpriteBatch;
	private OrthographicCamera camera;
	private Texture backgroundTexture;

	private boolean invertxPressed = false;
	private boolean collisionparticleeffectPressed = false;

	Settings(Game gam) {
		game = gam;
		Gdx.input.setInputProcessor(this);
		screenSpriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(game.camW, game.camH);
		backgroundTexture = game.assetManager.get("Background0.png", Texture.class);
	}

	@Override
	public void create() {

	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(16640);
		Gdx.gl.glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		screenSpriteBatch.begin();
		game.defaultFont.setColor(1, 1, 1, 1);
		game.menuFont.draw(screenSpriteBatch, "Invert X axis?  " + game.settingsprefs.getBoolean("invertx", false), game.scrW / 3, game.scrH * 5 / 10);
		game.menuFont.draw(screenSpriteBatch, "Collision Particle Effects?  " + game.settingsprefs.getBoolean("collisionparticleeffect", false), game.scrW / 10, game.scrH * 7 / 10);
		screenSpriteBatch.end();
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			game.setScreen(new Menu(game));
		}
	}

	@Override
	public void resize(int width, int height) {

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
	public void hide() {

	}

	@Override
	public void dispose() {

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
		if (screenY > game.scrH * 4 / 10 && screenY <= game.scrH * 6 / 10) {
			invertxPressed = true;
		}
		if (screenY > game.scrH * 2 / 10 && screenY <= game.scrH * 4 / 10) {
			invertxPressed = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (screenY > game.scrH * 4 / 10 && screenY <= game.scrH * 6 / 10 && invertxPressed) {
			game.settingsprefs.putBoolean("invertx", !game.settingsprefs.getBoolean("invertx", false));
			game.settingsprefs.flush();
		}
		if (screenY > game.scrH * 2 / 10 && screenY <= game.scrH * 4 / 10 && invertxPressed) {
			game.settingsprefs.putBoolean("collisionparticleeffect", !game.settingsprefs.getBoolean("collisionparticleeffect", false));
			game.settingsprefs.flush();
		}
		invertxPressed = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
