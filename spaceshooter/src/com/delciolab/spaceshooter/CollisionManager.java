package com.delciolab.spaceshooter;

public class CollisionManager {

	private AnimatedSprite spaceshipAnimated;
	private Enemy enemy;
	private ShotManager shotManager;

	public CollisionManager(AnimatedSprite spaceshipAnimated, Enemy enemy,
			ShotManager shotManager) {
				this.spaceshipAnimated = spaceshipAnimated;
				this.enemy = enemy;
				this.shotManager = shotManager;
	}

	public void handleCollisions() {
		handleEnemyShot();
		handlePlayerShot();
	}

	private void handlePlayerShot() {
		if (shotManager.enemyShotTouches(spaceshipAnimated.getBoundingBox())) {
			spaceshipAnimated.setDead(true);
		}
	}

	private void handleEnemyShot() {
		if (shotManager.playerShotTouches(enemy.getBoundingBox())) {
			enemy.hit();
		}
	}
	
}
