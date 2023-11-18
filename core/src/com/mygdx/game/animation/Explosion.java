package com.mygdx.game.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {
	
	private Animation<TextureRegion> explosionAnimation;
	private float explosionTimer;
	
	private Rectangle boundingRect;
	
	public Explosion(Texture texture, Rectangle boundingRect, float totalAnimationTime) {
		this.boundingRect = boundingRect;
		
		TextureRegion[][] textureRegion2D = TextureRegion.split(texture, 64, 64);
		
		TextureRegion[] textureRegion1D = new TextureRegion[16];
		int index = 0;
		for(int i = 0; i < 4; i++) {
			for(int j= 0; j < 4; j++) {
				textureRegion1D[index++] = textureRegion2D[i][j];
			}
		}
		
		explosionAnimation = new Animation<TextureRegion>(totalAnimationTime/16, textureRegion1D);
	}
	
	public void update(float deltaTime) {
		explosionTimer += deltaTime;
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(explosionAnimation.getKeyFrame(explosionTimer),
				   boundingRect.x,
				   boundingRect.y,
				   boundingRect.width,
				   boundingRect.height);
	}
	
	public boolean isFinished() {
		return explosionAnimation.isAnimationFinished(explosionTimer);
	}
}
