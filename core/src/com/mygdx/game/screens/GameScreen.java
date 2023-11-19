package com.mygdx.game.screens;

import java.util.LinkedList;
import java.util.ListIterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import com.mygdx.game.ArenaShooterGame;
import com.mygdx.game.DBManager;
import com.mygdx.game.Hud;
import com.mygdx.game.Range;
import com.mygdx.game.animation.Explosion;
import com.mygdx.game.objects.*;

public class GameScreen extends BaseScreen{

	// screen
	private Camera camera;
	private Viewport viewport;
	private Hud hud;
	
	// graphics
	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private TextureRegion[] backgrounds;
	private TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
	  					  enemyShipTextureRegion, enemyShieldTextureRegion,
	  					  playerLaserTextureRegion, enemyLaserTextureRegion;
	private Texture crosshairTexture;
	private Texture explosionTexture;
	
	// world parameters
	private final float WORLD_WIDTH = 300;
	private final float WORLD_HEIGHT = 200;
	
	// timings
	private float timeBetweenEnemySpawns = 3f;
	private float enemySpawnTimer = 0;
	
	private float[] backgroundOffsets = {0, 0, 0, 0};
	private float backgroundMaxScrollingSpeed = (float)WORLD_HEIGHT / 4;

	// game objects
	private PlayerShip playerShip;
	private LinkedList<EnemyShip> enemyShipList;
	private LinkedList<Laser> playerLaserList;
	private LinkedList<Laser> enemyLaserList;
	private LinkedList<Explosion> explosionList;
	
	// music & sounds
	private Music gameMusic;
	private Sound playerShotSound, gameOverSound, shieldHitSound,
				  playerDamagedSound, enemyBlowUp, enemyShieldHitSound;
		
	// game states & variables
	private int score = 0;
	private boolean isGameOver;
	private int waveSize = 2;
	
	// debug
	@SuppressWarnings("unused")
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private boolean isGodMode = false;
	
	public GameScreen(final ArenaShooterGame game) {
		super(game);
		game.gameScreen = this;
		// Adjust possible enemy spawn coordinates
		ArenaShooterGame.randomGenerator.addXRange(new Range(0, (int)WORLD_WIDTH/3));
		ArenaShooterGame.randomGenerator.addXRange(new Range((int)WORLD_WIDTH*2/3, (int)WORLD_WIDTH));
		ArenaShooterGame.randomGenerator.addYRange(new Range(0, (int)WORLD_HEIGHT/3));
		ArenaShooterGame.randomGenerator.addYRange(new Range((int)WORLD_HEIGHT*2/3, (int)WORLD_HEIGHT));
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		batch = new SpriteBatch(); 
		
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
		explosionTexture = new Texture("game-main/explosion.png");
		
		// initialize sounds & music
		shieldHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shield-hit.wav"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/game-over.wav"));
		playerShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/retro-laser-shot-01.wav"));
		playerDamagedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player-hit.wav"));
		enemyBlowUp = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy-destroyed.wav"));
		enemyShieldHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy-shield-hit.ogg"));
		
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/DavidKBD - InterstellarPack - 02 - Plasma Storm.ogg"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(game.musicMultiplier);	
	
		// set up initial game objects
		playerShip = new PlayerShip(60, 6, 
									WORLD_WIDTH/2, WORLD_HEIGHT/2, 15, 15,
									2, 10, 150, 0.2f, 
									playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);
		enemyShipList = new LinkedList<EnemyShip>();

		playerLaserList = new LinkedList<Laser>();
		enemyLaserList = new LinkedList<Laser>();
		
		explosionList = new LinkedList<Explosion>();
		
		// Initialize HUD
		hud = new Hud(batch, this.viewport);
	}

	@Override
	public void render(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			isGameOver = true;
		   }
		
		if(isGameOver) {
			gameOverSound.play(game.soundMultiplier);
			DBManager dbManager = new DBManager();
			dbManager.saveScore(game.difficulty, score);
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

		// spawn enemies
		spawnEnemyShips(deltaTime);
		
		// update, move and draw enemies
		updateMoveDrawEnemies(deltaTime);
		
		// create and render lasers
		renderLasers(deltaTime);
		
		// check collisions
		checkCollisions();
		
		// explosions
		updateAndRenderExplosions(deltaTime);
		
		batch.end();

		hud.update(playerShip.lives, playerShip.shieldAmount, score);
		hud.getStage().act();
		hud.getStage().draw();
		
		// debug hitboxes
//		shapeRenderer.setProjectionMatrix(camera.combined);
//		renderDebug(shapeRenderer);
	}
	
	public void renderDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		
		// player ship
		shapeRenderer.rect(playerShip.hitBox.x, playerShip.hitBox.y, playerShip.hitBox.width, playerShip.hitBox.height);
		
		// enemy ships
		shapeRenderer.setColor(Color.RED);
		ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
		while(enemyShipListIterator.hasNext()) {
			EnemyShip enemyShip = enemyShipListIterator.next();
			// Draw the rectangle's borders using the ShapeRenderer
			shapeRenderer.rect(enemyShip.hitBox.x, enemyShip.hitBox.y, enemyShip.hitBox.width, enemyShip.hitBox.height);
		}
		
		// enemy lasers
		shapeRenderer.setColor(Color.CYAN);
		ListIterator<Laser> enemyLaserIterator= enemyLaserList.listIterator();
		while(enemyLaserIterator.hasNext()) {
			Laser enemyLaser= enemyLaserIterator.next();
			shapeRenderer.rect(enemyLaser.hitBox.x, enemyLaser.hitBox.y, enemyLaser.hitBox.width, enemyLaser.hitBox.height);
		}
		// player lasers
		ListIterator<Laser> playerLaserIterator= playerLaserList.listIterator();
		while(playerLaserIterator.hasNext()) {
			Laser playerLaser= playerLaserIterator.next();
			shapeRenderer.rect(playerLaser.hitBox.x, playerLaser.hitBox.y, playerLaser.hitBox.width, playerLaser.hitBox.height);
		}
		
		shapeRenderer.end();
    }
	
	private void updateMoveDrawEnemies(float deltaTime) {
		ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
		while(enemyShipListIterator.hasNext()) {
			EnemyShip enemyShip = enemyShipListIterator.next();
			
		    Vector2 enemyNewDirection = new Vector2(0,0);
		    Vector2 playerLocation, enemyLocation;
		    playerLocation = new Vector2(playerShip.hitBox.x, playerShip.hitBox.y);
		    enemyLocation = new Vector2(enemyShip.hitBox.x, enemyShip.hitBox.y);

		    enemyNewDirection = playerLocation.sub(enemyLocation).nor() ;
		    // update enemy
		    enemyShip.updateFields(deltaTime, enemyNewDirection.x, enemyNewDirection.y);		
			// move and rotate 
		    moveAndRotateEnemy(enemyShip, deltaTime);
			// draw enemy
	        enemyShip.draw(batch);
		}
	}
	
	private void updateAndRenderExplosions(float deltaTime) {
		ListIterator<Explosion> explosionIterator = explosionList.listIterator();
		while(explosionIterator.hasNext()) {
			Explosion explosion = explosionIterator.next();
			explosion.update(deltaTime);
			if(explosion.isFinished()) {
				explosionIterator.remove();				
			}
			else {
				explosion.draw(batch);
			}
		}
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
					enemyShieldHitSound.play(game.soundMultiplier);
					if(enemyShip.registerHit()) {
						enemysShipListIterator.remove();
						enemyBlowUp.play(game.soundMultiplier);
						explosionList.add(new Explosion(explosionTexture,
														new Rectangle(enemyShip.hitBox),
														1f));
						score+= 100;
					}
					laserListIterator.remove();
					break; 
					// if laser hits to the ship, remove laser and register hit and skip to next laser. 
					// Without break it would try check if same (to be removed)laser hit other ships
				}
			}
		}
		
		laserListIterator = enemyLaserList.listIterator();
		while (laserListIterator.hasNext()) {
			Laser laser = laserListIterator.next();
			if(playerShip.isCollideWith(laser.hitBox)) {
				if(playerShip.registerHit()) {
					explosionList.add(new Explosion(explosionTexture,
							new Rectangle(playerShip.hitBox), 
							2f));
					playerShip.lives--;
					playerDamagedSound.play(game.soundMultiplier);
					if(playerShip.lives < 0 && !isGodMode) isGameOver = true;
				} else {
					shieldHitSound.play(game.soundMultiplier);
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
			for(int i = 0; i < waveSize; ++i) {
				enemyShipList.add(new EnemyShip(60, 2, 
					  ArenaShooterGame.randomGenerator.generateRandomInXRanges(),  
					  ArenaShooterGame.randomGenerator.generateRandomInYRanges(),
					  15, 15, 
					  1.5f, 8, 
					  80 + (10 * game.difficulty), 1f / game.difficulty,
					  enemyShipTextureRegion, 
					  enemyShieldTextureRegion,
					  enemyLaserTextureRegion));			
			}
			
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
		
		if(cursorPoint.x < 0) cursorPoint.x = 0;
		if(cursorPoint.x > WORLD_WIDTH - 16) cursorPoint.x = WORLD_WIDTH - 16;
		if(cursorPoint.y < 0) cursorPoint.y = 0;
		if(cursorPoint.y > WORLD_HEIGHT - 16) cursorPoint.y = WORLD_HEIGHT - 16;
		
		batch.setColor(Color.FIREBRICK);
		batch.draw(crosshairTexture, cursorPoint.x, cursorPoint.y, 16, 16);
		batch.setColor(new Color(1, 1, 1, 1));
		
		// rotate the ship to cursor
		Vector2 cursorRelativeToPlayer = new Vector2(cursorPoint.x - playerShip.hitBox.x, cursorPoint.y - playerShip.hitBox.y);
		float angle = (new Vector2(0,1)).angleDeg(cursorRelativeToPlayer);
		playerShip.rotationAngle = angle;
		playerShip.directionVector.rotateDeg(-playerShip.rotationAngle + playerShip.lastRotationAngle);
		playerShip.lastRotationAngle = playerShip.rotationAngle;
		
		// check for left click
		if(Gdx.input.justTouched() && playerShip.canFire()) {
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
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
			isGodMode = !isGodMode;
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
		Gdx.input.setCursorCatched(false);
	}
	
	@Override
	public void show() {
		if(UIMusic.isPlaying())
			UIMusic.stop();
		gameMusic.play();
		
		Gdx.input.setCursorCatched(true);
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
