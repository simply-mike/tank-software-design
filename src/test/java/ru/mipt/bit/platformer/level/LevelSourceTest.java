package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LevelSourceTest {

    @Test
    public void loadsDimensionsAndTilesFromFile() throws IOException {
        Path path = Files.createTempFile("level", ".txt");
        try {
            Files.writeString(path, "___\nXT_\n___\n");
            Level level = new FileLevelSource(path.toUri().toURL()).load();

            assertEquals(3, level.getWidth());
            assertEquals(3, level.getHeight());
            assertEquals(new GridPoint2(0, 1), level.getPlayerPosition());
            assertEquals(1, level.getTrees().size());
            assertEquals(new GridPoint2(1, 1), level.getTrees().get(0).getCoordinates());
            assertFalse(level.isFree(new GridPoint2(1, 1)));
            assertTrue(level.isFree(new GridPoint2(2, 2)));
            assertFalse(level.isFree(new GridPoint2(3, 0)));

            Tank tank = new Tank(level.getPlayerPosition(), level, 0.4f);
            tank.move(Direction.RIGHT);
            assertEquals(new GridPoint2(0, 1), tank.getDestinationCoordinates());
            tank.move(Direction.DOWN);
            tank.update(0.4f);
            assertEquals(new GridPoint2(0, 0), tank.getCoordinates());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void rejectsMalformedFile() throws IOException {
        Path path = Files.createTempFile("level", ".txt");
        try {
            for (String content : new String[]{"___\n", "XX\n", "X_\n_\n", "X?\n"}) {
                Files.writeString(path, content);
                try {
                    new FileLevelSource(path.toUri().toURL()).load();
                    fail("Expected invalid level: " + content);
                } catch (IllegalArgumentException expected) {
                    // Invalid maps are rejected before the game starts.
                }
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void randomlyPlacesDistinctObstaclesAwayFromPlayer() {
        Level level = new RandomLevelSource(10, 8, new Random(7)).load();

        assertEquals(10, level.getWidth());
        assertEquals(8, level.getHeight());
        assertEquals(16, level.getTrees().size());
        assertTrue(level.isFree(level.getPlayerPosition()));
        Set<GridPoint2> positions = new HashSet<>();
        for (Tree tree : level.getTrees()) {
            assertTrue(positions.add(tree.getCoordinates()));
            assertFalse(level.isFree(tree.getCoordinates()));
            assertFalse(tree.occupies(level.getPlayerPosition()));
        }
    }

    @Test
    public void randomPlayerHasAFreeNeighbor() {
        Level level = new RandomLevelSource(10, 8, new Random(6)).load();
        GridPoint2 player = level.getPlayerPosition();
        boolean canMove = false;
        for (Direction direction : Direction.values()) {
            canMove |= level.isFree(direction.calculateDestinationFrom(player));
        }
        assertTrue(canMove);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRandomLevelWithoutRoomForTreeAndMove() {
        new RandomLevelSource(1, 2, new Random());
    }

    @Test
    public void loadsBundledLevelFromClasspath() throws IOException {
        Level level = new FileLevelSource(getClass().getResource("/level.txt")).load();

        assertEquals(10, level.getWidth());
        assertEquals(8, level.getHeight());
        assertEquals(new GridPoint2(1, 1), level.getPlayerPosition());
        assertFalse(level.isFree(new GridPoint2(1, 3)));
    }
}
