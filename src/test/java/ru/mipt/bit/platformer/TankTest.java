package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TankTest {

    private final Field emptyField = new Field(Collections.emptyList());

    @Test
    public void startsFacingRightAtInitialCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void movesToFreeAdjacentTile() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, emptyField);
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
        Field field = new Field(Collections.singletonList(
                new Tree(new GridPoint2(1, 2))
        ));
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, field);

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void ignoresNewDirectionWhileMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.RIGHT, emptyField);
        tank.move(Direction.UP, emptyField);

        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());
    }

    @Test
    public void doesNotExposeMutableCoordinates() {
        GridPoint2 initialCoordinates = new GridPoint2(1, 1);
        Tank tank = new Tank(initialCoordinates);

        initialCoordinates.set(3, 3);
        tank.getCoordinates().set(4, 4);
        tank.getDestinationCoordinates().set(5, 5);

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }
}
