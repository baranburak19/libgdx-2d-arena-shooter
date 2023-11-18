package com.mygdx.game.screens;

import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.mygdx.game.DBManager;

public class LeaderboardScreen extends BaseScreen {

	private Texture bgImage;
	private int backgroundOffset;
	
	private Skin skin;
	private Stage stage;
	
	public LeaderboardScreen(final ArenaShooterGame game) {
		super(game);
		game.leaderboardScreen = this;
		
		bgImage = new Texture(Gdx.files.internal("bg-layers/blue_nebula_02.png")); 
		backgroundOffset = 0;
		
		skin = new Skin(Gdx.files.internal("star-soldier-ui/star-soldier-ui.json"));
		stage = new Stage();
		
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		TextButton title = new TextButton ("LEADERBOARDS  (TOP 5)", skin);
		root.add(title).grow().row();
		
		Table scoresTable = new Table();
		root.add(scoresTable).expand().center().row();
		populateScoresTable(scoresTable);
		
		TextButton mainMenuBtn = new TextButton("Back", skin);
		root.add(mainMenuBtn).expandY().bottom().left().padBottom(10).padLeft(10);
		
		//root.setDebugAll(true);
		
		mainMenuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
	}
	
	private void populateScoresTable(Table scoresTable) {
		DBManager dbManager = new DBManager();
		try {
			ResultSet retrievedResult = dbManager.getScores();
			
			while (retrievedResult.next()) {
                int score = retrievedResult.getInt("score");
                int difficulty = retrievedResult.getInt("difficulty");
                String date = retrievedResult.getString("date"); 

                //System.out.println("Score: " + score + "Difficulty: " + difficulty + " Date: " + date);
                         
                scoresTable.add(new TextButton("DIFFICULTY: " + difficulty, skin));
                scoresTable.add(new TextButton("SCORE: " + score, skin));
                scoresTable.add(new TextButton("DATE:" + date , skin)).row();
            }
			
			dbManager.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		System.out.println("Disposed LeaderboardScreen");
	}

}