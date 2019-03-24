package com.boxclimb.boxclimb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.Iterator;

class Game extends com.badlogic.gdx.Game {

	World world = new World(new Vector2(0, 0), true);
	AssetManager assetManager;
	Preferences scoresprefs;
	Preferences settingsprefs;
	int score = 0;
	int extrascore = 0;
	int coins = 0;
	int scrW;
	int scrH;
	final int camW = 576; //576
	final int camH = 1024; //1024
	float step = 1 / 200f;
	ShaderProgram shaderOutline;

	boolean debug = false;
	boolean zen = false;

	BitmapFont defaultFont;
	BitmapFont thinFont;
	BitmapFont menuFont;

	@Override
	public void create() {
		scoresprefs = Gdx.app.getPreferences("Scores");
		settingsprefs = Gdx.app.getPreferences("Settings");
		assetManager = new AssetManager();
		loadAssets();
		scrW = Gdx.graphics.getWidth();
		scrH = Gdx.graphics.getHeight();
		this.setScreen(new Menu(this));
		Gdx.input.setCatchBackKey(true);
	}

	public void render() {
		super.render();
		assetManager.update();
	}

	public void dispose() {

	}

	private void loadAssets() {
		assetManager.load("PlayButton.png", Texture.class);
		assetManager.load("SettingsButton.png", Texture.class);
		assetManager.load("AchievementsButton.png", Texture.class);
		assetManager.load("LightBackground.png", Texture.class);
		assetManager.load("Background0.png", Texture.class);
		assetManager.finishLoading();
		assetManager.load("DarkBackground.png", Texture.class);
		assetManager.load("WallNeonPink3.png", Texture.class);
		assetManager.load("BallNeonPink.png", Texture.class);
		assetManager.load("BallPink4.png", Texture.class);
		assetManager.load("BallGlowPink.png", Texture.class);
		assetManager.load("AimingReticule.png", Texture.class);
		assetManager.load("BoxNormalPink.png", Texture.class);
		assetManager.load("BoxNormalPink4.png", Texture.class);
		assetManager.load("BoxNormalNeonPink.png", Texture.class);
		assetManager.load("BoxNormalGlowPink.png", Texture.class);
		assetManager.load("BoxBoostPink4.png", Texture.class);
		assetManager.load("BoxBoostNeonPink.png", Texture.class);
		assetManager.load("BoxBoostGlowPink.png", Texture.class);
		assetManager.load("BoxExtraGravityPink4.png", Texture.class);
		assetManager.load("BoxExtraGravityNeonPink.png", Texture.class);
		assetManager.load("BoxExtraGravityGlowPink.png", Texture.class);
		assetManager.load("Coin.png", Texture.class);
		assetManager.load("PowerUpDoubleCoins.png", Texture.class);
		assetManager.load("PowerUpBox.png", Texture.class);
		assetManager.load("PowerUpBoost.png", Texture.class);
		assetManager.load("DarkShape1.png", Texture.class);
		assetManager.load("DarkShape2.png", Texture.class);
		assetManager.load("DarkShape3.png", Texture.class);
		assetManager.load("DarkShape4.png", Texture.class);
		assetManager.load("DarkShape5.png", Texture.class);
		assetManager.load("DarkShape6.png", Texture.class);
		assetManager.load("DarkShape7.png", Texture.class);
		assetManager.load("DarkShape8.png", Texture.class);
		assetManager.load("DarkShape9.png", Texture.class);
		assetManager.load("DarkShape10.png", Texture.class);
		assetManager.load("DarkShape11.png", Texture.class);
		assetManager.load("DarkShape12.png", Texture.class);
		assetManager.load("DarkShape13.png", Texture.class);
		assetManager.load("MediumShape1.png", Texture.class);
		assetManager.load("MediumShape2.png", Texture.class);
		assetManager.load("MediumShape3.png", Texture.class);
		assetManager.load("MediumShape4.png", Texture.class);
		assetManager.load("MediumShape5.png", Texture.class);
		assetManager.load("MediumShape6.png", Texture.class);
		assetManager.load("MediumShape7.png", Texture.class);
		assetManager.load("MediumShape8.png", Texture.class);
		assetManager.load("MediumShape9.png", Texture.class);
		assetManager.load("MediumShape10.png", Texture.class);
		assetManager.load("MediumShape11.png", Texture.class);
		assetManager.load("MediumShape12.png", Texture.class);
		assetManager.load("MediumShape13.png", Texture.class);
		defaultFont = new BitmapFont(Gdx.files.internal("Roboto-Regular.fnt"), Gdx.files.internal("Roboto-Regular.png"), false);
		thinFont = new BitmapFont(Gdx.files.internal("Roboto-Thin.fnt"), Gdx.files.internal("Roboto-Thin.png"), false);
		menuFont = new BitmapFont(Gdx.files.internal("Roboto-Black.fnt"), Gdx.files.internal("Roboto-Black.png"), false);
	}

	int randomInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	float randomFloat(double min, double max) {
		return (float) (min + (Math.random() * ((max - min) + 1)));
	}

	void resetWorld() {
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		Iterator<Body> it = bodies.iterator();
		while (it.hasNext()) {
			world.destroyBody(it.next());
			it.remove();
		}
		world = new World(new Vector2(0, 0), true);
	}

	public void loadShader() {
		String vertexShader;
		String fragmentShader;
		vertexShader = Gdx.files.internal("vertex.glsl").readString();
		fragmentShader = Gdx.files.internal("shader.glsl").readString();
		shaderOutline = new ShaderProgram(vertexShader, fragmentShader);
		if (!shaderOutline.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderOutline.getLog());
	}
}
