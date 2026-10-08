package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public final class Tank {

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private final TileAvailability tiles;
    private final float movementDuration;

    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    public Tank(GridPoint2 initialCoordinates, TileAvailability tiles, float movementDuration) {
        if (movementDuration <= 0f || !Float.isFinite(movementDuration)) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
        this.tiles = tiles;
        this.movementDuration = movementDuration;
    }

    public void move(Direction newDirection) {
        if (movementProgress < 1f) {
            return;
        }

        direction = newDirection;
        GridPoint2 destination = newDirection.calculateDestinationFrom(coordinates);
        if (tiles.isFree(destination)) {
            destinationCoordinates.set(destination);
            movementProgress = 0f;
        }
    }

    public void update(float deltaTime) {
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
}
