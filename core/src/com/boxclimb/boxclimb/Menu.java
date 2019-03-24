package com.boxclimb.boxclimb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Menu implements ApplicationListener, InputProcessor, Screen {

	private final Game game;

	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private Texture backgroundTexture;
	private Texture playButtonTexture;
	private Texture zenButtonTexture;
	private Texture settingsButtonTexture;
	private Texture achievementsButtonTexture;

	private boolean playButtonPressed = false;
	private boolean zenButtonPressed = false;
	private boolean settingsButtonPressed = false;
	private boolean achievementsButtonPressed = false;

	Menu(Game gam) {
		game = gam;
		Gdx.input.setInputProcessor(this);
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(game.camW, game.camH);
		backgroundTexture = game.assetManager.get("Background0.png", Texture.class);
		playButtonTexture = game.assetManager.get("PlayButton.png", Texture.class);
		zenButtonTexture = game.assetManager.get("PlayButton.png", Texture.class);
		settingsButtonTexture = game.assetManager.get("SettingsButton.png", Texture.class);
		achievementsButtonTexture = game.assetManager.get("AchievementsButton.png", Texture.class);
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
		Gdx.gl.glClearColor(51 / 255f, 47 / 255f, 40 / 255f, 1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(camera.combined);
		game.defaultFont.setColor(1, 1, 1, 1);
		spriteBatch.draw(backgroundTexture, -game.camW / 2, -game.camH / 2, game.camW, game.camH);
		spriteBatch.draw(playButtonTexture, -game.camW * 3 / 10, game.camH * 2 / 10, game.camW * 3 / 5, game.camH / 5);
		spriteBatch.draw(zenButtonTexture, -game.camW / 6, 0, game.camW / 3, game.camH / 7);
		spriteBatch.draw(settingsButtonTexture, -game.camW * 3 / 10, -game.camH / 10, game.camW * 3 / 5, game.camH / 7);
		spriteBatch.draw(achievementsButtonTexture, -game.camW * 3 / 10, -game.camH / 10 - game.camH / 7, game.camW * 3 / 5, game.camH / 8);
		spriteBatch.end();
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
		if (screenY > game.scrH * 1 / 10 && screenY <= game.scrH * 5 / 10) {
			playButtonPressed = true;
		} else if (screenY > game.scrH * 5 / 10 && screenY <= game.scrH * 7 / 10) {
			settingsButtonPressed = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (screenY > game.scrH * 1 / 10 && screenY <= game.scrH * 5 / 10 && playButtonPressed) {
			game.zen = false;
			game.setScreen(new Main(game));
		} else if (screenY <= game.scrH * 1 / 10 && playButtonPressed) {
			game.zen = true;
			game.setScreen(new Main(game));
		} else if (screenY > game.scrH * 5 / 10 && screenY <= game.scrH * 7 / 10 && settingsButtonPressed) {
			game.setScreen(new Settings(game));
		}
		playButtonPressed = false;
		achievementsButtonPressed = false;
		settingsButtonPressed = false;
		zenButtonPressed = false;
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
