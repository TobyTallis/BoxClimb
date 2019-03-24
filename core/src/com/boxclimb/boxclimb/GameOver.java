package com.boxclimb.boxclimb;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class GameOver implements ApplicationListener, InputProcessor, Screen {

	private final Game game;

	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;

	GameOver(Game gam) {
		game = gam;
		Gdx.input.setInputProcessor(this);
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(game.camW, game.camH);
		if (game.score + game.extrascore > game.scoresprefs.getInteger("highscore", 0)) {
			game.scoresprefs.putInteger("highscore", game.score + game.extrascore);
		}
		game.scoresprefs.putInteger("coins", game.scoresprefs.getInteger("coins", 0) + game.coins);
		game.coins = 0;
		game.scoresprefs.flush();
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
		game.defaultFont.draw(spriteBatch, "Game Over", -100, game.camH / 4);
		game.defaultFont.draw(spriteBatch, "Coins: " + game.scoresprefs.getInteger("coins"), 50, game.camH / 2 - 60);
		game.defaultFont.draw(spriteBatch, "Score: " + (game.score + game.extrascore), -90, game.camH / 7);
		game.defaultFont.draw(spriteBatch, "Highscore: " + game.scoresprefs.getInteger("highscore", 0), -120, -game.camH / 7);
		game.defaultFont.draw(spriteBatch, "Restart", -game.camW / 4 - 80, -game.camH * 2 / 7);
		game.defaultFont.draw(spriteBatch, "Menu", game.camW / 4 - 60, -game.camH * 2 / 7);
		game.thinFont.draw(spriteBatch, "Height: " + game.score, -game.camW / 4, 0);
		game.thinFont.draw(spriteBatch, "Boxes: " + game.extrascore, game.camW / 4 - 150, 0);
		spriteBatch.end();
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
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Screen screen;
		if (screenY < game.scrH * 11 / 12 && screenY > game.scrH * 5 / 12) {
			if (screenX < game.scrW / 2) {
				screen = new Main(game);
			} else {
				screen = new Menu(game);
			}
			game.setScreen(screen);
		}
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
