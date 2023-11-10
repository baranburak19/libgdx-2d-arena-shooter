package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.ArenaShooterGame;

public class MainMenuScreen extends BaseScreen {
	
	private Skin skin;
	private Stage stage;
	
	public MainMenuScreen(final ArenaShooterGame game) {
		super(game);
		game.mainMenuScreen = this;
		
		skin = new Skin(Gdx.files.internal("star-soldier-ui/star-soldier-ui.json"));
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		TextButton titleBtn = new TextButton("TİTLE", skin);
		Table menuBtns = new Table();
		TextButton startBtn = new TextButton("Start", skin);
		TextButton leaderboardstBtn = new TextButton("Leaderboards", skin);
		TextButton optionsBtn = new TextButton("Options", skin);
		
		//Texture wholeIm = new Texture(Gdx.files.internal("badlogic.jpg"));
		//Drawable drawable = new TextureRegionDrawable(new TextureRegion(wholeIm, 0, 0, 40, 40));
		//ImageButton startBtn = new ImageButton(drawable);
		
		
		root.add(titleBtn).grow();
		root.row();
		root.add(menuBtns).center();
		
		menuBtns.add(startBtn).fillX();
		menuBtns.row();
		menuBtns.add(leaderboardstBtn);
		menuBtns.row();
		menuBtns.add(optionsBtn).grow();
		
		//root.debugAll();

		startBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event , Actor actor) {
				game.setScreen(new GameScreen(game));
			}
		});
		
		leaderboardstBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new LeaderboardScreen(game));
			}
		});
		
		optionsBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new SettingsScreen(game));
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.2f, .2f, .2f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		System.out.println("Disposed MainMenuScreen");
	}
}