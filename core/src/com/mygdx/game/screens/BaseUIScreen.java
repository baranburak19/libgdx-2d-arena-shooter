package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.ArenaShooterGame;

public class BaseUIScreen extends BaseScreen{

	private Texture bgImage;
	private int backgroundOffset;
	
	public BaseUIScreen(final ArenaShooterGame game) {
		super(game);
		bgImage = new Texture(Gdx.files.internal("bg-layers/blue_nebula_02.png")); 
		backgroundOffset = 0;
	}
	
	@Override
	public void render(float delta){
		backgroundOffset++;
		if(backgroundOffset % Gdx.graphics.getHeight() == 0)
			backgroundOffset = 0;
		
		game.batch.begin();
		game.batch.draw(bgImage, 0, -backgroundOffset, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.draw(bgImage, 0, -backgroundOffset + Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.end();
	}

}
