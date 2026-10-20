package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyField() {
        new Field(0, HEIGHT, java.util.Collections.emptyList());
    }

    @Test
    public void spawnedTankBlocksItsTileAndOtherTanks() {
        Field field = new Field(4, 1, List.of());
        Tank first = field.spawnTank(new GridPoint2(1, 0), 0.4f);
        Tank second = field.spawnTank(new GridPoint2(0, 0), 0.4f);

        second.move(Direction.RIGHT);
        first.move(Direction.LEFT);

        assertFalse(field.isFree(new GridPoint2(1, 0)));
        assertEquals(new GridPoint2(0, 0), second.getDestinationCoordinates());
        assertEquals(new GridPoint2(1, 0), first.getDestinationCoordinates());
    }

    @Test
    public void movingTankBlocksBothTilesUntilItArrives() {
        Field field = new Field(4, 2, List.of());
        Tank moving = field.spawnTank(new GridPoint2(1, 0), 0.4f);
        Tank follower = field.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank contender = field.spawnTank(new GridPoint2(2, 1), 0.4f);

        moving.move(Direction.RIGHT);
        moving.update(0.2f);
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);

        assertFalse(field.isFree(new GridPoint2(1, 0)));
        assertFalse(field.isFree(new GridPoint2(2, 0)));
        assertFalse(follower.isMoving());
        assertFalse(contender.isMoving());

        moving.update(0.2f);

        assertTrue(field.isFree(new GridPoint2(1, 0)));
        assertFalse(field.isFree(new GridPoint2(2, 0)));
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);
        assertTrue(follower.isMoving());
        assertFalse(contender.isMoving());
    }

    @Test
    public void firstMoveReservesSharedDestinationImmediately() {
        Field field = new Field(3, 1, List.of());
        Tank first = field.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank second = field.spawnTank(new GridPoint2(2, 0), 0.4f);

        first.move(Direction.RIGHT);
        second.move(Direction.LEFT);

        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    public void rejectsSpawningOutsideFieldOrOnObstacleOrTank() {
        Field field = new Field(3, 2, List.of(new Tree(new GridPoint2(1, 0))));
        field.spawnTank(new GridPoint2(0, 0), 0.4f);
        for (GridPoint2 position : List.of(new GridPoint2(0, 0), new GridPoint2(1, 0),
                new GridPoint2(-1, 0), new GridPoint2(3, 0),
                new GridPoint2(0, -1), new GridPoint2(0, 2))) {
            assertThrows(IllegalArgumentException.class, () -> field.spawnTank(position, 0.4f));
        }
        assertThrows(IllegalArgumentException.class, () -> field.spawnTank(new GridPoint2(2, 0), 0f));
        assertTrue(field.isFree(new GridPoint2(2, 0)));
    }
}
