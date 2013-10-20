package com.delciolab.spaceshooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShooterGame implements ApplicationListener {
	public static final int SCREEN_HEIGHT = 480;
	public static final int SCREEN_WIDTH = 800;
	private static final float FONT_SCALE = 3;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture background;
	//private Sprite spaceshipSprite;
	private AnimatedSprite spaceshipAnimated;
	private ShotManager shotManager;
	private Music gameMusic;
	private Enemy enemy;
	private CollisionManager collisionManager;
	private boolean isGameOver = false;
	
	@Override
	public void create() {		// when the game first starts
		
		Texture.setEnforcePotImages(false); // sets to no need to have a POT (power of two) image to draw
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT); // setting camera
		
		batch = new SpriteBatch();
		
		background = new Texture(Gdx.files.internal("data/shooterBackground.png"));		// Gdx.files.internal will get the file
		Texture spaceshipTexture = new Texture(Gdx.files.internal("data/shooterSpaceshipFrames.png"));
		Sprite spaceshipSprite = new Sprite(spaceshipTexture);
		//spaceshipSprite.setPosition(800 / 2 - (spaceshipSprite.getWidth() / 2), 0);
		
		spaceshipAnimated = new AnimatedSprite(spaceshipSprite);
		spaceshipAnimated.setPosition(SCREEN_WIDTH / 2, 0);
		
		Texture shotTexture = new Texture(Gdx.files.internal("data/shotsFrames.png"));
		Texture enemyShotTexture = new Texture(Gdx.files.internal("data/enemy-shots.png"));
		shotManager = new ShotManager(shotTexture, enemyShotTexture);
		
		Texture enemyTexture = new Texture(Gdx.files.internal("data/enemy.png"));
		enemy = new Enemy(enemyTexture, shotManager);
		
		collisionManager = new CollisionManager(spaceshipAnimated, enemy, shotManager);
		
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("data/metal-guitar.mp3"));
		//gameMusic.setVolume(.25f);
		gameMusic.setLooping(true);
		gameMusic.play();
	}

	@Override		// cleans up on exit
	public void dispose() {
		batch.dispose();
	}

	@Override		// most important method, called many times per second
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		// Drawing stuff happens between batch.begin and batch.end
		batch.draw(background, 0, 0);
		
		if (isGameOver) {
			BitmapFont font = new BitmapFont();
			font.setScale(FONT_SCALE);
			String gameOver = "Gotcha!";
			font.draw(batch, gameOver, ((SCREEN_WIDTH / 2) - (font.getXHeight() / FONT_SCALE * gameOver.length())), (SCREEN_HEIGHT - font.getLineHeight()) / 2 + font.getLineHeight());// 340, 250);
		}
		
		spaceshipAnimated.draw(batch);
		enemy.draw(batch);
		shotManager.draw(batch);
		batch.end();
		
		// After drawing everything, inputs can be checked
		handleInput();
		
		if (!isGameOver) {
			spaceshipAnimated.move();
			enemy.update();
			shotManager.update();
			
			collisionManager.handleCollisions();
		}
		
		if (spaceshipAnimated.isDead()) {
			isGameOver = true;
		}
	}

	private void handleInput() {
		if (Gdx.input.isTouched()) {
			
			if (isGameOver) {
				spaceshipAnimated.setDead(false);
				isGameOver = false;
			}
			
			Vector3 touchPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPosition);
			
			//int xTouch = Gdx.input.getX();
			if (touchPosition.x > spaceshipAnimated.getX()) {
				spaceshipAnimated.moveRight();
			}
			else
			{
				spaceshipAnimated.moveLeft();
			}
			shotManager.firePlayerShot(spaceshipAnimated.getX());
		}
	}

	@Override		// called whenever a user changes the size of the game screen
	public void resize(int width, int height) {
	}

	@Override		
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
