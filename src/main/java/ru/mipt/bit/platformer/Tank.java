package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

final class Tank {

    private static final float MOVEMENT_DURATION = 0.4f;

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;

    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    Tank(GridPoint2 initialCoordinates) {
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
    }

    void move(Direction newDirection, Field field) {
        if (movementProgress < 1f) {
            return;
        }

        direction = newDirection;
        GridPoint2 destination = newDirection.calculateDestinationFrom(coordinates);
        if (field.isFree(destination)) {
            destinationCoordinates.set(destination);
            movementProgress = 0f;
        }
    }

    void update(float deltaTime) {
        movementProgress = Math.max(0f,
                Math.min(movementProgress + deltaTime / MOVEMENT_DURATION, 1f));
        if (movementProgress == 1f) {
            coordinates.set(destinationCoordinates);
        }
    }

    GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destinationCoordinates);
    }

    float getMovementProgress() {
        return movementProgress;
    }

    Direction getDirection() {
        return direction;
    }
}
