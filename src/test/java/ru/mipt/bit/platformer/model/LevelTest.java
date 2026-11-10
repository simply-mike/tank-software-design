package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static ru.mipt.bit.platformer.TankFixtures.spawnTestTank;

public class LevelTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 8;

    @Test
    public void reportsWhetherTileIsFree() {
        Level level = new Level(WIDTH, HEIGHT, java.util.Collections.singletonList(
                new Tree(new GridPoint2(1, 3))
        ), new GridPoint2(0, 0));

        assertFalse(level.isFree(new GridPoint2(1, 3)));
        assertTrue(level.isFree(new GridPoint2(2, 3)));
    }

    @Test
    public void ownsItsObstacleList() {
        List<Tree> obstacles = new ArrayList<>();
        Level level = new Level(WIDTH, HEIGHT, obstacles, new GridPoint2(0, 0));

        obstacles.add(new Tree(new GridPoint2(0, 0)));

        assertTrue(level.isFree(new GridPoint2(0, 0)));
    }

    @Test
    public void reportsTilesOutsideItsBoundsAsOccupied() {
        Level level = new Level(WIDTH, HEIGHT, java.util.Collections.emptyList(), new GridPoint2(0, 0));

        assertFalse(level.isFree(new GridPoint2(-1, 0)));
        assertFalse(level.isFree(new GridPoint2(0, -1)));
        assertFalse(level.isFree(new GridPoint2(WIDTH, 0)));
        assertFalse(level.isFree(new GridPoint2(0, HEIGHT)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyLevel() {
        new Level(0, HEIGHT, java.util.Collections.emptyList(), new GridPoint2(0, 0));
    }

    @Test
    public void spawnedTankBlocksItsTileAndOtherTanks() {
        Level level = new Level(4, 1, List.of(), new GridPoint2(0, 0));
        Tank first = spawnTestTank(level, new GridPoint2(1, 0), 0.4f);
        Tank second = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);

        second.move(Direction.RIGHT);
        first.move(Direction.LEFT);

        assertFalse(level.isFree(new GridPoint2(1, 0)));
        assertEquals(new GridPoint2(0, 0), second.getDestinationCoordinates());
        assertEquals(new GridPoint2(1, 0), first.getDestinationCoordinates());
    }

    @Test
    public void movingTankBlocksBothTilesUntilItArrives() {
        Level level = new Level(4, 2, List.of(), new GridPoint2(0, 0));
        Tank moving = spawnTestTank(level, new GridPoint2(1, 0), 0.4f);
        Tank follower = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank contender = spawnTestTank(level, new GridPoint2(2, 1), 0.4f);

        moving.move(Direction.RIGHT);
        moving.update(0.2f);
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);

        assertFalse(level.isFree(new GridPoint2(1, 0)));
        assertFalse(level.isFree(new GridPoint2(2, 0)));
        assertFalse(follower.isMoving());
        assertFalse(contender.isMoving());

        moving.update(0.2f);

        assertTrue(level.isFree(new GridPoint2(1, 0)));
        assertFalse(level.isFree(new GridPoint2(2, 0)));
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);
        assertTrue(follower.isMoving());
        assertFalse(contender.isMoving());
    }

    @Test
    public void firstMoveReservesSharedDestinationImmediately() {
        Level level = new Level(3, 1, List.of(), new GridPoint2(0, 0));
        Tank first = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank second = spawnTestTank(level, new GridPoint2(2, 0), 0.4f);

        first.move(Direction.RIGHT);
        second.move(Direction.LEFT);

        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    public void rejectsSpawningOutsideLevelOrOnObstacleOrTank() {
        Level level = new Level(3, 2, List.of(new Tree(new GridPoint2(1, 0))), new GridPoint2(0, 0));
        spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        for (GridPoint2 position : List.of(new GridPoint2(0, 0), new GridPoint2(1, 0),
                new GridPoint2(-1, 0), new GridPoint2(3, 0),
                new GridPoint2(0, -1), new GridPoint2(0, 2))) {
            assertThrows(IllegalArgumentException.class, () -> spawnTestTank(level, position, 0.4f));
        }
        assertThrows(IllegalArgumentException.class, () -> spawnTestTank(level, new GridPoint2(2, 0), 0f));
        assertTrue(level.isFree(new GridPoint2(2, 0)));
    }

    @Test
    public void updatesAllRegisteredTanksWithoutExposingItsObjects() {
        Level level = new Level(4, 2, List.of(), new GridPoint2(0, 0));
        Tank first = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank second = spawnTestTank(level, new GridPoint2(2, 1), 0.8f);
        first.move(Direction.RIGHT);
        second.move(Direction.RIGHT);

        level.update(0.2f);

        assertEquals(0.5f, first.getMovementProgress(), 0.0001f);
        assertEquals(0.25f, second.getMovementProgress(), 0.0001f);
        assertEquals(List.of(first, second), level.getObjects());
        assertThrows(UnsupportedOperationException.class, () -> level.getObjects().clear());
    }

    @Test
    public void rejectsInvalidInitialPlacementAndCopiesPlayerCoordinates() {
        Tree tree = new Tree(new GridPoint2(1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Level(2, 1, List.of(tree), new GridPoint2(1, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Level(1, 1, List.of(tree), new GridPoint2(0, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Level(1, 1, List.of(), new GridPoint2(-1, 0)));
        GridPoint2 position = new GridPoint2(0, 0);
        Level level = new Level(2, 1, List.of(), position);
        position.set(1, 0);
        level.getPlayerPosition().set(1, 0);
        assertEquals(new GridPoint2(0, 0), level.getPlayerPosition());
    }
}
