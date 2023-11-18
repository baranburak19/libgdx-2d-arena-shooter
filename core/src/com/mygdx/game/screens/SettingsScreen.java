package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.mygdx.game.ArenaShooterGame;

public class SettingsScreen extends BaseScreen {

	private Skin skin;
	private Stage stage;
	
	private Texture bgImage;
	private int backgroundOffset;
	
	public SettingsScreen(final ArenaShooterGame game) {
		super(game);
		game.settingsScreen = this;
		
		bgImage = new Texture(Gdx.files.internal("bg-layers/blue_nebula_02.png")); 
		backgroundOffset = 0;
		
		skin = new Skin(Gdx.files.internal("star-soldier-ui/star-soldier-ui.json"));
		stage = new Stage();
		
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
		difficultySlider.setValue(game.difficulty);
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
		    }   
		});
		
		musicSlider.addListener(new ChangeListener() {
		    @Override
		    public void changed(ChangeEvent event, Actor actor) {
		        Slider slider = (Slider) actor;
		        game.musicMultiplier = slider.getValue() / 100;
		    }   
		});
		
		difficultySlider.addListener(new ChangeListener() {
		    @Override
		    public void changed(ChangeEvent event, Actor actor) {
		        Slider slider = (Slider) actor;
		        game.difficulty = (int)slider.getValue();
		    }   
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		backgroundOffset++;
		if(backgroundOffset % Gdx.graphics.getHeight() == 0)
			backgroundOffset = 0;
		
		game.batch.begin();
		game.batch.draw(bgImage, 0, -backgroundOffset, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.draw(bgImage, 0, -backgroundOffset + Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.end();
		
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
		
	}
	
	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		System.out.println("Disposed SettingsScreen");
	}
}
