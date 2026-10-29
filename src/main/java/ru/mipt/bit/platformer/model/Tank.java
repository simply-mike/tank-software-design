package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;
import java.util.function.Consumer;

public final class Tank implements Obstacle {

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private final TileAvailability tiles;
    private final float movementDuration;
    private final BulletSettings bulletSettings;
    private final Consumer<Bullet> registerBullet;
    private final Consumer<Bullet> checkCollision;
    private int health;

    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    Tank(GridPoint2 initialCoordinates, TileAvailability tiles, float movementDuration,
         int health, BulletSettings bulletSettings, Consumer<Bullet> registerBullet,
         Consumer<Bullet> checkCollision) {
        if (movementDuration <= 0f || !Float.isFinite(movementDuration)) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        if (health < 0) {
            throw new IllegalArgumentException("Initial tank health must not be negative");
        }
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
        this.tiles = Objects.requireNonNull(tiles);
        this.movementDuration = movementDuration;
        this.health = health;
        this.bulletSettings = Objects.requireNonNull(bulletSettings);
        this.registerBullet = Objects.requireNonNull(registerBullet);
        this.checkCollision = Objects.requireNonNull(checkCollision);
    }

    public void move(Direction newDirection) {
        if (isDestroyed() || isMoving()) {
            return;
        }

        direction = newDirection;
        GridPoint2 destination = newDirection.calculateDestinationFrom(coordinates);
        if (tiles.isFree(destination)) {
            destinationCoordinates.set(destination);
            movementProgress = 0f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (isDestroyed()) {
            return;
        }
        movementProgress = Math.max(0f,
                Math.min(movementProgress + deltaTime / movementDuration, 1f));
        if (movementProgress == 1f) {
            coordinates.set(destinationCoordinates);
        }
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destinationCoordinates);
    }

    public float getMovementProgress() {
        return movementProgress;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    public void shoot() {
        if (!isDestroyed()) {
            registerBullet.accept(new Bullet(direction.calculateDestinationFrom(coordinates),
                    direction, bulletSettings, this, checkCollision));
        }
    }

    public void takeDamage(int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be positive");
        }
        if (!isDestroyed()) {
            health -= damage;
        }
    }

    public int getHealth() {
        return health;
    }

    @Override
    public boolean isDestroyed() {
        return health < 0;
    }

    @Override
    public boolean occupies(GridPoint2 tileCoordinates) {
        return coordinates.equals(tileCoordinates) || destinationCoordinates.equals(tileCoordinates);
    }
}
