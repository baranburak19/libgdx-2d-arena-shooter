package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.ArenaShooterGame;

public class GameScreen extends BaseScreen{

	Texture player;
	Texture enemy;
	Music gameMusic;
	Sound playerShotSound;
	
	public GameScreen(final ArenaShooterGame game) {
		super(game);
		game.gameScreen = this;
		
		playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/9mm-pop-shot.wav"));
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		//playerShotSound.play(game.soundMultiplier);
		
	}
	
	@Override
	public void dispose() {
		System.out.println("Disposed MainMenuScreen");
	}

}
