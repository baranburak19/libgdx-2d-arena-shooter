package com.mygdx.game.screens;

import java.util.LinkedList;
import java.util.ListIterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ArenaShooterGame;
import com.mygdx.game.DBManager;
import com.mygdx.game.objects.*;

public class GameScreen extends BaseScreen{

	// screen
	private Camera camera;
	private Viewport viewport;
	
	// graphics
	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private TextureRegion[] backgrounds;
	private TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
	  					  enemyShipTextureRegion, enemyShieldTextureRegion,
	  					  playerLaserTextureRegion, enemyLaserTextureRegion;
	private Texture crosshairTexture;
	
	// world parameters
	private final float WORLD_WIDTH = 300;
	private final float WORLD_HEIGHT = 200;
	
	// timings
	private float timeBetweenEnemySpawns = 2f;
	private float enemySpawnTimer = 0;
	
	private float[] backgroundOffsets = {0, 0, 0, 0};
	private float backgroundMaxScrollingSpeed;

	// game objects
	private PlayerShip playerShip;
	private LinkedList<EnemyShip> enemyShipList;
	private LinkedList<Laser> playerLaserList;
	private LinkedList<Laser> enemyLaserList;
	
	private Music gameMusic;
	private Sound playerShotSound;
		
	public int score = 0;
	public boolean isGameOver;
	
	public GameScreen(final ArenaShooterGame game) {
		super(game);
		game.gameScreen = this;
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		batch = new SpriteBatch();
		
		backgroundMaxScrollingSpeed = (float)WORLD_HEIGHT / 4;
		
		// set up texture atlas
		textureAtlas = new TextureAtlas("game-main/2d-arena-shooter.atlas");
		
		// initialize texture regions	
		backgrounds = new TextureRegion[4];
		backgrounds[0] = textureAtlas.findRegion("Starscape00");
		backgrounds[1] = textureAtlas.findRegion("Starscape01");
		backgrounds[2] = textureAtlas.findRegion("Starscape02");
		backgrounds[3] = textureAtlas.findRegion("Starscape03");
		
		playerShipTextureRegion = textureAtlas.findRegion("playerShip2_red");
		playerShieldTextureRegion = textureAtlas.findRegion("shield3");
		
		enemyShipTextureRegion = textureAtlas.findRegion("enemyGreen5");
		enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
		enemyShieldTextureRegion.flip(false, true);
		
		playerLaserTextureRegion = textureAtlas.findRegion("laserRed12");
		enemyLaserTextureRegion = textureAtlas.findRegion("laserGreen02");
		
		crosshairTexture = new Texture("game-main/crosshair101.png");
		
		// initialize sounds & music
		playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/9mm-pop-shot.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/hurt-me-plenty.wav"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(game.musicMultiplier);	
	
		// set up initial game objects
		playerShip = new PlayerShip(40, 3, 
									WORLD_WIDTH/2, WORLD_HEIGHT/2, 15, 15,
									2, 10, 150, 0.2f, 
									playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);
		enemyShipList = new LinkedList<EnemyShip>();

		playerLaserList = new LinkedList<Laser>();
		enemyLaserList = new LinkedList<Laser>();
	}

	@Override
	public void render(float deltaTime) {
		if(isGameOver) {
			DBManager dbManager = new DBManager();
			dbManager.saveScore(score);
			dbManager.closeConnection();
			
			game.setScreen(new LeaderboardScreen(game));
			return;
		}
		
		batch.begin();
		
		// render background
		renderBackground(deltaTime);
				
		// process input
		processInput(deltaTime);
		
		
		// update&draw player ship and spawn enemy ships
		playerShip.updateFields(deltaTime);
		playerShip.draw(batch);
		
		// update, move and draw enemies
		spawnEnemyShips(deltaTime);
		
		ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
		while(enemyShipListIterator.hasNext()) {
			EnemyShip enemyShip = enemyShipListIterator.next();
			
		    Vector2 enemyNewDirection = new Vector2(0,0);
		    Vector2 playerLocation, enemyLocation;
		    playerLocation = new Vector2(playerShip.hitBox.x, playerShip.hitBox.y);
		    enemyLocation = new Vector2(enemyShip.hitBox.x, enemyShip.hitBox.y);

		    enemyNewDirection = playerLocation.sub(enemyLocation).nor() ;
		    
		    enemyShip.updateFields(deltaTime, enemyNewDirection.x, enemyNewDirection.y);		
			moveAndRotateEnemy(enemyShip, deltaTime);
			
	        enemyShip.draw(batch);
		}
		
		// create and render lasers
		renderLasers(deltaTime);
		
		// check collisions
		checkCollisions();
		
		batch.end();
	}
	
	private float distanceToPlayerShip(EnemyShip enemyShip) {
		Vector2 distanceVector = new Vector2();
		distanceVector.x = Math.abs(enemyShip.hitBox.x - playerShip.hitBox.x);
		distanceVector.y = Math.abs(enemyShip.hitBox.y - playerShip.hitBox.y);
		return distanceVector.len();
	}

	private void checkCollisions() {
		ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
		while (laserListIterator.hasNext()) {
			Laser laser = laserListIterator.next();
			ListIterator<EnemyShip> enemysShipListIterator = enemyShipList.listIterator();
			while(enemysShipListIterator.hasNext()) {
				EnemyShip enemyShip = enemysShipListIterator.next();
				
				if(enemyShip.isCollideWith(laser.hitBox)) {
					if(enemyShip.registerHit()) {
						enemysShipListIterator.remove();
						score+= 100;
					}
					laserListIterator.remove();
					break; 
					// if laser hits to the ship, remove laser and register hit and skip to next laser. 
					// Without break it would try check if same (removed)laser hit other ships
				}
			}
		}
		
		laserListIterator = enemyLaserList.listIterator();
		while (laserListIterator.hasNext()) {
			Laser laser = laserListIterator.next();
			if(playerShip.isCollideWith(laser.hitBox)) {
				if(playerShip.registerHit()) {
					playerShip.lives--;
					if(playerShip.lives < 0) 
						isGameOver = true;
				}
				laserListIterator.remove();
			}
		}
	}

	private void moveAndRotateEnemy(EnemyShip enemyShip, float delta) {
		float leftLimit, rightLimit, upLimit, downLimit;
		leftLimit = -enemyShip.hitBox.x;
		downLimit = -enemyShip.hitBox.y;
		rightLimit = WORLD_WIDTH - enemyShip.hitBox.x - enemyShip.hitBox.width;
		upLimit = WORLD_HEIGHT - enemyShip.hitBox.y - enemyShip.hitBox.height;

		Vector2 enemyDirectionVector = enemyShip.getDirectionVector();
		
		// rotate the ship to player
		float angle = enemyDirectionVector.angleDeg(new Vector2(0,1));
		enemyShip.rotationAngle = angle;
		
		if(distanceToPlayerShip(enemyShip) > 50) {
			float xMove = enemyDirectionVector.x/enemyDirectionVector.len() *  enemyShip.movementSpeed * delta;
			float yMove = enemyDirectionVector.y/enemyDirectionVector.len() *  enemyShip.movementSpeed * delta;
				
			if(xMove > 0) xMove = Math.min(xMove, rightLimit);
			else xMove = Math.max(xMove, leftLimit);
			
			if(yMove > 0) yMove = Math.min(yMove, upLimit);
			else yMove = Math.max(yMove, downLimit);
			
			enemyShip.move(xMove, yMove);
		}	
	}

	private void spawnEnemyShips(float delta) {
		enemySpawnTimer += delta;
		
		if(enemySpawnTimer > timeBetweenEnemySpawns) {
			enemyShipList.add(new EnemyShip(15, 1, 
					  ArenaShooterGame.random.nextFloat()*(WORLD_WIDTH-20)+5,
					  ArenaShooterGame.random.nextFloat()*(WORLD_HEIGHT-20)+5,
					  12, 12, 
					  2f, 6, 
					  100, 0.8f,
					  enemyShipTextureRegion, 
					  enemyShieldTextureRegion,
					  enemyLaserTextureRegion));
			
			enemySpawnTimer -= timeBetweenEnemySpawns;
		}
	}

	private void renderLasers(float delta) {
		//  create new lasers
		
        //   enemy lasers
		ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
		while(enemyShipListIterator.hasNext()) {
			EnemyShip enemyShip = enemyShipListIterator.next();
			if(enemyShip.canFire()) {
	        	Laser[] lasers = enemyShip.fireLasers();
	        	for(Laser laser : lasers) {
	        		enemyLaserList.add(laser);
	        	}
	        }	
		}
        
        // draw lasers
        // remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()) {
        	Laser laser = iterator.next();
        	laser.draw(batch);
        	
        	// advance laser wrt direction of laser
        	if(laser.rotationAngle > 270 || laser.rotationAngle < 90) {
        		laser.hitBox.y += laser.movementSpeed * Math.cos(laser.rotationAngle * 6.14/360) * delta;        		
        	}
        	else if(laser.rotationAngle < 270 || laser.rotationAngle > 90) {
        		laser.hitBox.y -= laser.movementSpeed * -Math.cos(laser.rotationAngle * 6.14/360) * delta;
        	}
        	
        	if(laser.rotationAngle > 0 || laser.rotationAngle < 180) {
        		laser.hitBox.x += laser.movementSpeed * Math.sin(laser.rotationAngle * 6.14/360) * delta;
        	}
        	else if(laser.rotationAngle < 360 || laser.rotationAngle > 180) {
        		laser.hitBox.x -= laser.movementSpeed * Math.sin(laser.rotationAngle * 6.14/360) * delta;
        	}
        	
        	// check if laser is outside of borders
        	if(laser.hitBox.y > WORLD_HEIGHT || laser.hitBox.x > WORLD_WIDTH || laser.hitBox.y < 0 ||laser.hitBox.x < 0) {
        		iterator.remove();
        	}
        }
        
        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()) {
        	Laser laser = iterator.next();
        	laser.drawEnemy(batch);
        	
        	// advance laser wrt direction of laser
        	if(laser.rotationAngle > 270 || laser.rotationAngle < 90) {
        		laser.hitBox.y += laser.movementSpeed * Math.cos(laser.rotationAngle * 6.14/360) * delta;        		
        	}
        	else if(laser.rotationAngle < 270 || laser.rotationAngle > 90) {
        		laser.hitBox.y -= laser.movementSpeed * -Math.cos(laser.rotationAngle * 6.14/360) * delta;
        	}
        	
        	if(laser.rotationAngle > 0 || laser.rotationAngle < 180) {
        		laser.hitBox.x += laser.movementSpeed * Math.sin(-laser.rotationAngle * 6.14/360) * delta;
        	}
        	else if(laser.rotationAngle < 360 || laser.rotationAngle > 180) {
        		laser.hitBox.x -= laser.movementSpeed * Math.sin(-laser.rotationAngle * 6.14/360) * delta;
        	}
        	
        	// check if laser is outside of borders
        	if(laser.hitBox.y > WORLD_HEIGHT || laser.hitBox.x > WORLD_WIDTH || laser.hitBox.y < 0 ||laser.hitBox.x < 0) {
        		iterator.remove();
        	}
        }
	}
	
	private void processInput(float deltaTime) {
		// draw crosshair & set rotationAngle ship w.r.t. cursor position
		int xCursorPixels = Gdx.input.getX();
		int yCursorPixels = Gdx.input.getY();
		
		Vector2 cursorPoint = new Vector2(xCursorPixels, yCursorPixels);
		cursorPoint = viewport.unproject(cursorPoint);
		
		batch.draw(crosshairTexture, cursorPoint.x - 15/2, cursorPoint.y - 15/2, 15, 15);
		
		// rotate the ship to cursor
		Vector2 cursorRelativeToPlayer = new Vector2(cursorPoint.x - playerShip.hitBox.x, cursorPoint.y - playerShip.hitBox.y);
		float angle = (new Vector2(0,1)).angleDeg(cursorRelativeToPlayer);
		playerShip.rotationAngle = angle;
		playerShip.directionVector.rotateDeg(-playerShip.rotationAngle + playerShip.lastRotationAngle);
		playerShip.lastRotationAngle = playerShip.rotationAngle;
		
		// check for left click
		if(Gdx.input.justTouched() && playerShip.canFire()) {
			// player lasers
	        Laser[] lasers = playerShip.fireLasers();
	       	for(Laser laser : lasers) {
	       		playerLaserList.add(laser);  	
	        }
			playerShotSound.play(game.soundMultiplier);
		}
		
		// define maximum space on sides
		float leftLimit, rightLimit, upLimit, downLimit;
		leftLimit = -playerShip.hitBox.x;
		downLimit = -playerShip.hitBox.y;
		rightLimit = WORLD_WIDTH - playerShip.hitBox.x - playerShip.hitBox.width;
		upLimit = WORLD_HEIGHT - playerShip.hitBox.y - playerShip.hitBox.height;

		
		// keyboard input to move
		if(Gdx.input.isKeyPressed(Input.Keys.W) && upLimit > 0) {
			playerShip.move(0f, Math.min(playerShip.movementSpeed*deltaTime, upLimit));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D) && rightLimit > 0) {
			playerShip.move(Math.min(playerShip.movementSpeed*deltaTime, rightLimit), 0f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S) && downLimit < 0) {
			playerShip.move(0f, Math.max(-playerShip.movementSpeed*deltaTime, downLimit));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A) && leftLimit < 0) {
			playerShip.move(Math.max(-playerShip.movementSpeed*deltaTime, leftLimit), 0f);
		}
	}

	private void renderBackground(float deltaTime) {
		backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
		backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
		backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
		backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed ;
	
		for(int layer = 0; layer < backgroundOffsets.length; layer++) {
			if(backgroundOffsets[layer] > WORLD_HEIGHT) {
				backgroundOffsets[layer] = 0;
			}
			batch.draw(backgrounds[layer],
					   0, -backgroundOffsets[layer],
					   WORLD_WIDTH, WORLD_HEIGHT);
			batch.draw(backgrounds[layer],
					   0, -backgroundOffsets[layer] + WORLD_HEIGHT,
					   WORLD_WIDTH, WORLD_HEIGHT);
			
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		this.dispose();
	}
	
	@Override
	public void show() {
		if(UIMusic.isPlaying())
			UIMusic.stop();
		gameMusic.play();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		textureAtlas.dispose();
		crosshairTexture.dispose();
		gameMusic.dispose();
		playerShotSound.dispose();
		System.out.println("Disposed GameScreen");
	}

}
