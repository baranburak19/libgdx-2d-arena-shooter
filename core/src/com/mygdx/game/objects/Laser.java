package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Laser {
	//TODO encapsulation, getters and setters
	public float movementSpeed;
	public float rotationAngle;
	
	// Graphics and bounding rectangle
	public Rectangle hitBox;
	public TextureRegion textureRegion;
	
	public Laser(float xCenter, float yBottom, float width, float height, float movementSpeed, float angle, TextureRegion laserTextureRegion) {
		this.hitBox = new Rectangle(xCenter - width/2, yBottom, width, height);
		this.movementSpeed = movementSpeed;
		this.rotationAngle = angle;
		this.textureRegion = laserTextureRegion;
	}
	
	public void draw(Batch batch){
		//batch.draw(textureRegion, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
		batch.draw(textureRegion, hitBox.x, hitBox.y, 0, 0, hitBox.width, hitBox.height, 1, 1, -rotationAngle);
	}
	
	public void drawEnemy(Batch batch){
		//batch.draw(textureRegion, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
		batch.draw(textureRegion, hitBox.x, hitBox.y, 0, 0, hitBox.width, hitBox.height, 1, 1, rotationAngle);
	}
}
