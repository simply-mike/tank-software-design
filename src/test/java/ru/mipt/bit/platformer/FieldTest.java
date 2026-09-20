package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 8;

    @Test
    public void reportsWhetherTileIsFree() {
        Field field = new Field(WIDTH, HEIGHT, java.util.Collections.singletonList(
                new Tree(new GridPoint2(1, 3))
        ));

        assertFalse(field.isFree(new GridPoint2(1, 3)));
        assertTrue(field.isFree(new GridPoint2(2, 3)));
    }

    @Test
    public void ownsItsObstacleList() {
        List<Obstacle> obstacles = new ArrayList<>();
        Field field = new Field(WIDTH, HEIGHT, obstacles);

        obstacles.add(coordinates -> true);

        assertTrue(field.isFree(new GridPoint2(0, 0)));
    }

    @Test
    public void reportsTilesOutsideItsBoundsAsOccupied() {
        Field field = new Field(WIDTH, HEIGHT, java.util.Collections.emptyList());

        assertFalse(field.isFree(new GridPoint2(-1, 0)));
        assertFalse(field.isFree(new GridPoint2(0, -1)));
        assertFalse(field.isFree(new GridPoint2(WIDTH, 0)));
        assertFalse(field.isFree(new GridPoint2(0, HEIGHT)));
    }
}
