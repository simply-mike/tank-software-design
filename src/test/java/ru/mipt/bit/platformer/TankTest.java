package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TankTest {

    private static final float MOVEMENT_DURATION = 0.4f;
    private final Field emptyField = new Field(10, 8, Collections.emptyList());

    @Test
    public void startsFacingRightAtInitialCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1), emptyField, MOVEMENT_DURATION);

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void movesToFreeAdjacentTile() {
        Tank tank = new Tank(new GridPoint2(1, 1), emptyField, MOVEMENT_DURATION);

        tank.move(Direction.UP);
        tank.update(0.2f);

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);

        tank.update(0.2f);

        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void turnsButDoesNotMoveIntoObstacle() {
        Field field = new Field(10, 8, Collections.singletonList(
                new Tree(new GridPoint2(1, 2))
        ));
        Tank tank = new Tank(new GridPoint2(1, 1), field, MOVEMENT_DURATION);

        tank.move(Direction.UP);

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void ignoresNewDirectionWhileMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1), emptyField, MOVEMENT_DURATION);

        tank.move(Direction.RIGHT);
        tank.move(Direction.UP);

        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());
    }

    @Test
    public void doesNotExposeMutableCoordinates() {
        GridPoint2 initialCoordinates = new GridPoint2(1, 1);
        Tank tank = new Tank(initialCoordinates, emptyField, MOVEMENT_DURATION);

        initialCoordinates.set(3, 3);
        tank.getCoordinates().set(4, 4);
        tank.getDestinationCoordinates().set(5, 5);

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

    @Test
    public void usesConfiguredMovementDuration() {
        Tank tank = new Tank(new GridPoint2(1, 1), emptyField, 0.8f);

        tank.move(Direction.UP);
        tank.update(0.2f);

        assertEquals(0.25f, tank.getMovementProgress(), 0.0001f);
    }

    @Test
    public void acceptsAnyTileAvailabilityImplementation() {
        Tank tank = new Tank(new GridPoint2(1, 1), coordinates -> false, MOVEMENT_DURATION);

        tank.move(Direction.UP);

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }
}
