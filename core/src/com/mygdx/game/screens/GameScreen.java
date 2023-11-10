package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.ArenaShooterGame;

public class GameScreen extends BaseScreen{

	Texture playerImage;
	Texture enemyImage;
	Rectangle player;
	Array<Rectangle> enemies;
	Music gameMusic;
	Sound playerShotSound;
	
	public GameScreen(final ArenaShooterGame game) {
		super(game);
		game.gameScreen = this;
		
		playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/9mm-pop-shot.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/hurt-me-plenty.wav"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(game.musicMultiplier);
		
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
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
		//playerImage.dispose();
		//enemyImage.dispose();
		System.out.println("Disposed GameScreen");
	}

}
