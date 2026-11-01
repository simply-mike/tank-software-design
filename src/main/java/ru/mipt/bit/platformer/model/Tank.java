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
    private int health;

    private double movementRemaining;
    private Direction direction = Direction.RIGHT;

    Tank(GridPoint2 initialCoordinates, TileAvailability tiles, float movementDuration,
         int health, BulletSettings bulletSettings, Consumer<Bullet> registerBullet) {
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
    }

    public void move(Direction newDirection) {
        if (isDestroyed() || isMoving()) {
            return;
        }

        direction = newDirection;
        GridPoint2 destination = newDirection.calculateDestinationFrom(coordinates);
        if (tiles.isFree(destination)) {
            destinationCoordinates.set(destination);
            movementRemaining = movementDuration;
        }
    }

    @Override
    public void update(double deltaTime) {
        if (deltaTime < 0d || !Double.isFinite(deltaTime)) {
            throw new IllegalArgumentException("Tick duration must be non-negative and finite");
        }
        if (isDestroyed()) {
            return;
        }
        movementRemaining = Math.max(0d, movementRemaining - deltaTime);
        if (!isMoving()) {
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
        return (float) (1d - movementRemaining / movementDuration);
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return movementRemaining > 0d;
    }

    @Override
    public double timeToNextTransition() {
        return isMoving() ? movementRemaining : Double.POSITIVE_INFINITY;
    }

    public void shoot() {
        if (!isDestroyed()) {
            registerBullet.accept(new Bullet(direction.calculateDestinationFrom(coordinates),
                    direction, bulletSettings, this));
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
