package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void appliesMovementVectorAndStoresRotation() {
        assertDirection(Direction.UP, 0, 1, 90f);
        assertDirection(Direction.LEFT, -1, 0, -180f);
        assertDirection(Direction.DOWN, 0, -1, -90f);
        assertDirection(Direction.RIGHT, 1, 0, 0f);
    }

    @Test
    public void calculatesDestinationWithoutChangingOrigin() {
        GridPoint2 origin = new GridPoint2(4, 5);

        assertEquals(new GridPoint2(3, 5), Direction.LEFT.calculateDestinationFrom(origin));
        assertEquals(new GridPoint2(4, 5), origin);
    }

    private static void assertDirection(Direction direction, int x, int y, float rotation) {
        GridPoint2 origin = new GridPoint2(10, 10);
        assertEquals(new GridPoint2(10 + x, 10 + y), direction.calculateDestinationFrom(origin));
        assertEquals(rotation, direction.getRotationDegrees(), 0f);
    }
}
