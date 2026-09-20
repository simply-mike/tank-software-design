package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TankControllerTest {

    @Test
    public void mapsArrowAndLetterKeysToDirections() {
        assertMoves(Input.Keys.UP, Direction.UP);
        assertMoves(Input.Keys.W, Direction.UP);
        assertMoves(Input.Keys.LEFT, Direction.LEFT);
        assertMoves(Input.Keys.A, Direction.LEFT);
        assertMoves(Input.Keys.DOWN, Direction.DOWN);
        assertMoves(Input.Keys.S, Direction.DOWN);
        assertMoves(Input.Keys.RIGHT, Direction.RIGHT);
        assertMoves(Input.Keys.D, Direction.RIGHT);
    }

    private static void assertMoves(int pressedKey, Direction expectedDirection) {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(10, 8, Collections.emptyList());
        ButtonPressHandler handler = new ButtonPressHandler(key -> key == pressedKey);
        new TankController(handler, tank, field);

        handler.handleInput();

        assertEquals(expectedDirection, tank.getDirection());
        assertEquals(expectedDirection.calculateDestinationFrom(new GridPoint2(1, 1)),
                tank.getDestinationCoordinates());
    }
}
