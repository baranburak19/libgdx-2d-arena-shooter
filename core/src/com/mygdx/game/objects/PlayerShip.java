package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PlayerShip extends BaseShip{

	public int lives = 3;
	
	public PlayerShip(float movementSpeed, int shieldAmount, float xCenter, float yCenter, float width, float height,
			float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
			TextureRegion shipTexture, TextureRegion shieldTexture, TextureRegion laserTexture) {
		super(movementSpeed, shieldAmount, xCenter, yCenter, width, height, laserWidth, laserHeight, laserMovementSpeed,
				timeBetweenShots, shipTexture, shieldTexture, laserTexture);
		
	}
	//TODO encapsulation, getters and setters

	@Override
	public Laser[] fireLasers() {
		Laser[] laser = new Laser[2];
		laser[0] = new Laser(hitBox.x + hitBox.width*0.07f, 
							 hitBox.y + hitBox.height*0.45f,
							 laserWidth,
							 laserHeight,
							 laserMovementSpeed,
							 rotationAngle,
							 laserTextureRegion);
		laser[1] = new Laser(hitBox.x + hitBox.width*0.93f, 
				 hitBox.y + hitBox.height*0.45f,
				 laserWidth,
				 laserHeight,
				 laserMovementSpeed,
				 rotationAngle,
				 laserTextureRegion);
		
		timeSinceLastShot = 0;
		
		return laser;
	}
}
