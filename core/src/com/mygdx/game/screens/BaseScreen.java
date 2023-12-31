package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.ArenaShooterGame;

public class BaseScreen implements Screen{
	public static ArenaShooterGame game;
	
	public static Music UIMusic;
	
	public BaseScreen(ArenaShooterGame game) {
		BaseScreen.game = game;
		
		if(UIMusic == null) {
			UIMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/hurt-me-plenty.wav"));
			UIMusic.setLooping(true);
		}
		UIMusic.setVolume(game.musicMultiplier);
	}

	@Override
	public void show() {
		if(!UIMusic.isPlaying()) UIMusic.play();
	}

	@Override
	public void render(float delta) {}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}
	
}
