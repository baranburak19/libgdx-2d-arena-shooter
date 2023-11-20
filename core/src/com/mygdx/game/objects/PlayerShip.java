package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerShip extends BaseShip{

	public int lives = 3;
	
	private boolean isDashing = false;
	private float dashTimer = 0;
	private float dashDuration = 0.1f;
	private float timeSinceLastDash = 0;
	private float dashCooldown = 1;
	
	public PlayerShip(float movementSpeed, int shieldAmount, float xCenter, float yCenter, float width, float height,
			float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
			TextureRegion shipTexture, TextureRegion shieldTexture, TextureRegion laserTexture) {
		super(movementSpeed, shieldAmount, xCenter, yCenter, width, height, laserWidth, laserHeight, laserMovementSpeed,
				timeBetweenShots, shipTexture, shieldTexture, laserTexture);
		
	}

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
	
	public void updateFields(float deltaTime) {
		super.updateFields(deltaTime);
		
		if(isDashing) {
			dashTimer += deltaTime;			
		}else {
			timeSinceLastDash += deltaTime;
		}
		
		if(dashTimer > dashDuration) {
			dashTimer = 0;
			isDashing = false;
			this.movementSpeed /= 5f;
		}
	}
	
	public boolean canDash() {
		return timeSinceLastDash > dashCooldown;
	}
	
	public void dash() {
		this.movementSpeed *= 5f;
		this.isDashing = true;				
		timeSinceLastDash = 0;
	}
}
