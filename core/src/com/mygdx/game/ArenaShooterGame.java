package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LeaderboardScreen;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.screens.SettingsScreen;

public class ArenaShooterGame extends Game {
	
	public SpriteBatch batch;

	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	public LeaderboardScreen leaderboardScreen;
	public SettingsScreen settingsScreen;
	
	public float soundMultiplier = 0.5f;
	public float musicMultiplier = 0.5f;
	public float difficulty = 1;
	
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
	public void dispose () {
		batch.dispose();
		if(mainMenuScreen != null) mainMenuScreen.dispose();
		if(gameScreen != null) gameScreen.dispose();
		if(leaderboardScreen != null) leaderboardScreen.dispose();
		if(settingsScreen != null) settingsScreen.dispose();
	}
}
