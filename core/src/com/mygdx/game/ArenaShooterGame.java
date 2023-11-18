package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LeaderboardScreen;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.screens.SettingsScreen;

public class ArenaShooterGame extends Game {
	
	public static Random random = new Random();
	public SpriteBatch batch;
	
	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	public LeaderboardScreen leaderboardScreen;
	public SettingsScreen settingsScreen;
	
	public float soundMultiplier = 0.1f;
	public float musicMultiplier = 0.1f; //TODO set back to 0.5f default
	public int difficulty = 1;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		this.screen.resize(width, height);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
//		if(this.getScreen() != null) this.screen.dispose();
//		if(mainMenuScreen != null) mainMenuScreen.dispose();
//		if(gameScreen != null) gameScreen.dispose();
//		if(leaderboardScreen != null) leaderboardScreen.dispose();
//		if(settingsScreen != null) settingsScreen.dispose();
	}
}
