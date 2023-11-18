package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class BaseShip {
	//TODO encapsulation, getters and setters
	// BaseShip properties
	public float movementSpeed;
	public int shieldAmount;
	public Vector2 directionVector = new Vector2(0,1);
	
	// Laser weapon stats 
	public float laserWidth, laserHeight;
	public float laserMovementSpeed;
	public float timeBetweenShots;
	
	// Timing
	public float timeSinceLastShot = 0;
	
	// Graphics and bounding rectangle
	TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;
	public Rectangle hitBox;
	public float lastRotationAngle = 0;
	public float rotationAngle = 0;
	
	public BaseShip(float movementSpeed, int shieldAmount,
					float xCenter, float yCenter,
					float width, float height,
					float laserWidth, float laserHeight, float laserMovementSpeed,
					float timeBetweenShots,
					TextureRegion shipTexture,
					TextureRegion shieldTexture,
					TextureRegion laserTexture) {
		this.movementSpeed = movementSpeed;
		this.shieldAmount = shieldAmount;
		this.hitBox = new Rectangle(xCenter - width/2, yCenter - height/2, width, height);
		this.laserWidth = laserWidth;
		this.laserHeight = laserHeight;
		this.laserMovementSpeed = laserMovementSpeed;
		this.timeBetweenShots = timeBetweenShots;
		this.shipTextureRegion = shipTexture;
		this.shieldTextureRegion = shieldTexture;
		this.laserTextureRegion = laserTexture;
	}
	
	public void updateFields(float deltaTime) {
		timeSinceLastShot += deltaTime;
	}
	
	public boolean canFire() {
		return timeSinceLastShot > timeBetweenShots;
	}
	
	public abstract Laser[] fireLasers();
	
	public boolean isCollideWith(Rectangle otherBox) {
		return hitBox.overlaps(otherBox);
	}
	
	public boolean registerHit() {
		if(shieldAmount > 0) {
			--shieldAmount;
			return false;
		}
		return true;
	}
	
	public void move(float xChange, float yChange) {
		hitBox.setPosition(hitBox.x + xChange, hitBox.y + yChange);
	}
	
	public void draw(Batch batch) {
		batch.draw(shipTextureRegion, hitBox.x, hitBox.y, hitBox.width/2, hitBox.height/2, hitBox.width, hitBox.height, 1, 1, -rotationAngle);
		if(shieldAmount > 0) {
			batch.draw(shieldTextureRegion, hitBox.x, hitBox.y, hitBox.width/2, hitBox.height/2, hitBox.width, hitBox.height, 1, 1, -rotationAngle);
		}
	}
}
