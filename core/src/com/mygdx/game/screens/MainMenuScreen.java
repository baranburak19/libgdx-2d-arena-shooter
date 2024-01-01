package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.ArenaShooterGame;

public class MainMenuScreen extends BaseScreen {
	
	private Texture bgImage;
	private int backgroundOffset;
	
	private Skin skin;
	private Stage stage;
	
	public MainMenuScreen(final ArenaShooterGame game) {
		super(game);
		game.mainMenuScreen = this;
		
		bgImage = new Texture(Gdx.files.internal("bg-layers/blue_nebula_02.png")); 
		backgroundOffset = 0;
		
		skin = new Skin(Gdx.files.internal("star-soldier-ui/star-soldier-ui.json"));
		stage = new Stage();
		
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		TextButton titleBtn = new TextButton("SPACE ARENA", skin);
		Table menuBtns = new Table();
		
		TextButton startBtn = new TextButton("Start", skin);
		TextButton leaderboardstBtn = new TextButton("Leaderboards", skin);
		TextButton optionsBtn = new TextButton("Options", skin);
		TextButton quitBtn = new TextButton("Quit", skin);
		
		root.add(titleBtn).size(700, 300).padBottom(50);
		root.row();
		root.add(menuBtns);
		
		menuBtns.add(startBtn).fillX();
		menuBtns.row();
		menuBtns.add(leaderboardstBtn);
		menuBtns.row();
		menuBtns.add(optionsBtn).grow();
		menuBtns.row();
		menuBtns.add(quitBtn).growX();
		
		//root.debugAll();

		startBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event , Actor actor) {
				game.setScreen(new GameScreen(game));
				System.out.println("info: setGameScreen called");
			}
		});
		
		leaderboardstBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new LeaderboardScreen(game));
				System.out.println("info: setLeaderboardScreen called");
			}
		});
		
		optionsBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new SettingsScreen(game));
				System.out.println("info: setSettingsScreen called");
			}
		});
		
		quitBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		backgroundOffset++;
		if(backgroundOffset % Gdx.graphics.getHeight() == 0)
			backgroundOffset = 0;
		
		stage.getBatch().begin();
		stage.getBatch().draw(bgImage, 0, -backgroundOffset, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getBatch().draw(bgImage, 0, -backgroundOffset + Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getBatch().end();
		
		stage.act();
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override 
	public void hide() {
		Gdx.input.setInputProcessor(null);
		this.dispose();
	}
	
	@Override
	public void dispose() {
		bgImage.dispose();
		skin.dispose();
		stage.dispose();
		System.out.println("Disposed MainMenuScreen");
	}
}