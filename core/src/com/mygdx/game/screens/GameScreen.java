package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.ArenaShooterGame;

public class GameScreen extends BaseScreen{

	OrthographicCamera camera;
	
	Texture playerImage;
	Rectangle player;
	
	Texture enemyImage;
	Array<Rectangle> enemies;
	
	Texture crosshairImg;
	
	long lastSpawnTime;
	long spawnInterval = 1_000_000_000;
	
	Music gameMusic;
	Sound playerShotSound;
	
	public GameScreen(final ArenaShooterGame game) {
		super(game);
		game.gameScreen = this;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		playerImage = new Texture("temp/bucket.png");
		enemyImage = new Texture("temp/droplet.png");
		crosshairImg = new Texture("images/crosshair183.png");
		
		player = new Rectangle();
		player.x = 800 / 2 - 64 / 2; // center the player horizontally
		player.y = 480 / 2 - 64 / 2;
		player.width = 64;
		player.height = 64;
		
		// create enemy array and spawn the first one
		enemies = new Array<Rectangle>();
		spawnEnemy();
		
		playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/9mm-pop-shot.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/hurt-me-plenty.wav"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(game.musicMultiplier);	
	}
	
	public void spawnEnemy() {
		Rectangle enemy= new Rectangle();
		enemy.x = MathUtils.random(0, 800 - 64);
		enemy.y = 240;
		enemy.width = 64;
		enemy.height = 64;
		enemies.add(enemy);
		lastSpawnTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.6f, 1);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		// begin a new batch and draw the player and all enemies
		game.batch.begin();
		game.batch.draw(playerImage, player.x, player.y, player.width, player.height);
		game.batch.draw(crosshairImg, Gdx.input.getX() - 32, -Gdx.input.getY() + Gdx.graphics.getHeight() - 32, 64, 64);
		for(Rectangle enemy : enemies) {
			game.batch.draw(enemyImage, enemy.x, enemy.y);
		}
		game.batch.end();
		
		// draw crosshair on mouse location
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)) playerShotSound.play(game.soundMultiplier);
	}
	
	@Override
	public void show() {
		gameMusic.play();
	}
	
	@Override
	public void dispose() {
		gameMusic.dispose();
		playerShotSound.dispose();
		playerImage.dispose();
		enemyImage.dispose();
		crosshairImg.dispose();
		System.out.println("Disposed GameScreen");
	}

}
