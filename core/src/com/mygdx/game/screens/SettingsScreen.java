package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.game.ArenaShooterGame;

public class SettingsScreen extends BaseScreen {

	private Skin skin;
	private Stage stage;
	
	public SettingsScreen(final ArenaShooterGame game) {
		super(game);
		game.settingsScreen = this;
		
		skin = new Skin(Gdx.files.internal("star-soldier-ui/star-soldier-ui.json"));
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		Label soundLabel = new Label("Sounds", skin);
		root.add(soundLabel).pad(10);
		Slider soundSlider = new Slider(0, 100, 1, false, skin);
		soundSlider.setValue(game.soundMultiplier * 100);
		root.add(soundSlider);
		
		root.row();
		
		Label musicLabel = new Label("Musics", skin);
		root.add(musicLabel).pad(10);
		Slider musicSlider = new Slider(0, 100, 1, false, skin);
		musicSlider.setValue(game.musicMultiplier * 100);
		root.add(musicSlider);
		
		root.row();
		
		Label difficultyLabel = new Label("Difficulty (1-5)", skin);
		root.add(difficultyLabel).pad(10);
		Slider difficultySlider = new Slider(1, 5, 1, false, skin);
		difficultySlider.setValue(1);
		root.add(difficultySlider);
		
		root.row();
		
		TextButton mainMenuBtn = new TextButton("Back", skin);
		stage.addActor(mainMenuBtn);
		
		mainMenuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		soundSlider.addListener(new ChangeListener() {
		    @Override
		    public void changed(ChangeEvent event, Actor actor) {
		        Slider slider = (Slider) actor;
		        game.soundMultiplier = slider.getValue() / 100;
		        //game.setScreen(new GameScreen(game));
		    }   
		});
		
		musicSlider.addListener(new ChangeListener() {
		    @Override
		    public void changed(ChangeEvent event, Actor actor) {
		        Slider slider = (Slider) actor;
		        game.musicMultiplier = slider.getValue() / 100;
		        //game.setScreen(new GameScreen(game));
		    }   
		});
		
		difficultySlider.addListener(new ChangeListener() {
		    @Override
		    public void changed(ChangeEvent event, Actor actor) {
		        Slider slider = (Slider) actor;
		        game.difficulty = slider.getValue();
		        //game.setScreen(new GameScreen(game));
		    }   
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
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
		System.out.println("Disposed SettingScreen");
	}
}
