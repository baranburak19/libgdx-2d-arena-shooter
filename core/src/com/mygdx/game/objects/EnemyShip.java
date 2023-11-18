package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
public class EnemyShip extends BaseShip{

	public Vector2 directionVector;
	public float timeSinceLastDirectionChange = 0;
	public float directionChangeFrequency = 0.75f;
	
	public EnemyShip(float movementSpeed, int shieldAmount, float xCenter, float yCenter, float width, float height,
			float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
			TextureRegion shipTexture, TextureRegion shieldTexture, TextureRegion laserTexture) {
		super(movementSpeed, shieldAmount, xCenter, yCenter, width, height, laserWidth, laserHeight, laserMovementSpeed,
				timeBetweenShots, shipTexture, shieldTexture, laserTexture);
		
		directionVector = new Vector2(0, -1);
	}
	
	public Vector2 getDirectionVector() {
		return this.directionVector;
	}
	
	private void adjustDirectionVector(float x, float y) {
		this.directionVector.x = x;
		this.directionVector.y = y;
	}
	
	public void updateFields(float delta, float x, float y) {
		timeSinceLastShot += delta;
		timeSinceLastDirectionChange += delta;
		if(timeSinceLastDirectionChange > directionChangeFrequency) {
			adjustDirectionVector(x, y);
			timeSinceLastDirectionChange -= directionChangeFrequency;
		}
	}

	@Override
	public Laser[] fireLasers() {
		Laser[] laser = new Laser[2];
		laser[0] = new Laser(hitBox.x + hitBox.width*0.18f, 
							 hitBox.y - laserHeight,
							 laserWidth,
							 laserHeight,
							 laserMovementSpeed,
							 rotationAngle,
							 laserTextureRegion);
		laser[1] = new Laser(hitBox.x + hitBox.width*0.82f, 
				 hitBox.y - laserHeight,
				 laserWidth,
				 laserHeight,
				 laserMovementSpeed,
				 rotationAngle,
				 laserTextureRegion);
		
		timeSinceLastShot = 0;
		
		return laser;
	}
	
	@Override
	public void draw(Batch batch) {
		batch.draw(shipTextureRegion, hitBox.x, hitBox.y, hitBox.width/2, hitBox.height/2, hitBox.width, hitBox.height, 1, 1, rotationAngle + 180);
		if(shieldAmount > 0) {
			batch.draw(shieldTextureRegion, hitBox.x, hitBox.y - hitBox.height*0.2f, hitBox.width/2, hitBox.height/2, hitBox.width, hitBox.height, 1, 1, rotationAngle  + 180);
		}
	}
}
