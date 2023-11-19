package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Hud {
	private Stage stage;
	
	private Label livesLabel;
    private Label shieldLabel;
    private Label scoreLabel;
	
	public Hud(SpriteBatch spriteBatch, Viewport gameViewport) {
		stage = new Stage(gameViewport, spriteBatch);
		
		Table hud = new Table();
		hud.setFillParent(true);
		hud.top();
		stage.addActor(hud);
		
		// common label style
		LabelStyle labelStyle = new LabelStyle(); 
		BitmapFont font = new BitmapFont(Gdx.files.internal("hud/hudfont.fnt"));
		font.getData().setScale(0.3f);
		labelStyle.font = font;
		
		// lives
		Image livesIcon = new Image(new Texture("hud/playerLife2_red.png"));
		livesIcon.setSize(10, 10);
		livesLabel = new Label("Lives: ", labelStyle);

        // shield 
        Image shieldIcon = new Image(new Texture("hud/shield.png"));
        shieldIcon.setSize(10, 10);
		shieldLabel = new Label("Shield: ", labelStyle);
		
        // score
        Image scoreIcon = new Image(new Texture("hud/star_gold.png"));
        shieldIcon.setSize(10, 10);
		scoreLabel = new Label("Score: ", labelStyle);
		
        hud.add(livesIcon).size(10, 10).pad(2);
        hud.add(livesLabel).size(10,10);
        hud.add(shieldIcon).size(10, 10).pad(2);
        hud.add(shieldLabel).size(10, 10);
        hud.add(scoreIcon).size(10, 10).pad(2);
        hud.add(scoreLabel).size(10, 10);
        
        //hud.debugAll();
	}
	
	public Stage getStage() { return this.stage; }
	
	public void update(int lives, int shield, int score) {
		livesLabel.setText(lives);
        shieldLabel.setText(shield);
        scoreLabel.setText(score);
	}
	 
	public void dispose(){
        stage.dispose();
	}
}
