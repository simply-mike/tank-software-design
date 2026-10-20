package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RandomTankSpawnerTest {

    @Test
    public void spawnsDistinctTanksInsideLevelAwayFromTreesAndPlayer() {
        Level level = new RandomLevelSource(10, 8, new Random(7)).load();
        Tank player = level.spawnTank(level.getPlayerPosition(), 0.4f);

        List<Tank> tanks = new RandomTankSpawner(new Random(7)).spawn(level, 5, 0.8f);

        assertEquals(5, tanks.size());
        assertEquals(level.getPlayerPosition(), player.getCoordinates());
        Set<GridPoint2> positions = new HashSet<>();
        positions.add(player.getCoordinates());
        for (Tank tank : tanks) {
            GridPoint2 position = tank.getCoordinates();
            assertTrue(positions.add(position));
            assertTrue(position.x >= 0 && position.x < level.getWidth());
            assertTrue(position.y >= 0 && position.y < level.getHeight());
            assertFalse(level.isFree(position));
            for (Tree tree : level.getTrees()) {
                assertFalse(tree.occupies(position));
            }
        }
    }

    @Test
    public void fillsOnlyAvailableCellsWithoutRetryingForever() {
        Level level = new Level(4, 1, List.of(new Tree(new GridPoint2(3, 0))), new GridPoint2(0, 0));
        Tank player = level.spawnTank(level.getPlayerPosition(), 0.4f);
        player.move(Direction.RIGHT);
        RandomTankSpawner spawner = new RandomTankSpawner(new Random(7));

        List<Tank> tanks = spawner.spawn(level, 100, 0.8f);

        assertEquals(1, tanks.size());
        Tank tank = tanks.get(0);
        assertEquals(new GridPoint2(2, 0), tank.getCoordinates());
        assertTrue(spawner.spawn(level, 5, 0.8f).isEmpty());
        player.update(0.4f);
        tank.move(Direction.LEFT);
        assertFalse(tank.isMoving());
        tank.move(Direction.RIGHT);
        assertFalse(tank.isMoving());
    }

    @Test
    public void usesConfiguredMovementDuration() {
        Level empty = new Level(3, 1, List.of(), new GridPoint2(0, 0));
        Tank slower = new RandomTankSpawner(new Random(7)).spawn(empty, 1, 0.8f).get(0);
        slower.move(slower.getCoordinates().x == 0 ? Direction.RIGHT : Direction.LEFT);
        slower.update(0.2f);
        assertEquals(0.25f, slower.getMovementProgress(), 0.0001f);
    }

    @Test
    public void handlesLevelWithRoomOnlyForPlayer() {
        Level level = new Level(1, 1, List.of(), new GridPoint2(0, 0));
        level.spawnTank(level.getPlayerPosition(), 0.4f);

        assertTrue(new RandomTankSpawner(new Random(7)).spawn(level, 5, 0.4f).isEmpty());
    }

    @Test
    public void usesInjectedRandomnessForRepeatablePlacement() {
        assertEquals(positionsForSeed(7), positionsForSeed(7));
        assertNotEquals(positionsForSeed(7), positionsForSeed(8));
    }

    @Test
    public void acceptsZeroCountAndRejectsNegativeCount() {
        Level level = new Level(2, 2, List.of(), new GridPoint2(0, 0));
        RandomTankSpawner spawner = new RandomTankSpawner(new Random(7));

        assertTrue(spawner.spawn(level, 0, 0.4f).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(level, -1, 0.4f));
        assertTrue(level.isFree(new GridPoint2(1, 1)));
    }

    private static List<GridPoint2> positionsForSeed(long seed) {
        Level level = new Level(4, 4, List.of(), new GridPoint2(0, 0));
        level.spawnTank(level.getPlayerPosition(), 0.4f);
        return new RandomTankSpawner(new Random(seed)).spawn(level, 5, 0.4f).stream()
                .map(Tank::getCoordinates).collect(Collectors.toList());
    }
}
