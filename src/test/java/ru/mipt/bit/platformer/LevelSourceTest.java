package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LevelSourceTest {

    @Test
    public void loadsDimensionsAndTilesFromFile() throws IOException {
        Path path = Files.createTempFile("level", ".txt");
        try {
            Files.writeString(path, "_T_\nX__\n___\n");
            Level level = new FileLevelSource(path).load();

            assertEquals(3, level.getWidth());
            assertEquals(3, level.getHeight());
            assertEquals(new GridPoint2(0, 1), level.getPlayerPosition());
            assertEquals(1, level.getTrees().size());
            assertEquals(new GridPoint2(1, 2), level.getTrees().get(0).getCoordinates());
            assertFalse(level.getField().isFree(new GridPoint2(1, 2)));
            assertTrue(level.getField().isFree(new GridPoint2(2, 2)));
            assertFalse(level.getField().isFree(new GridPoint2(3, 0)));
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
                    new FileLevelSource(path).load();
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
        assertTrue(level.getField().isFree(level.getPlayerPosition()));
        for (Tree tree : level.getTrees()) {
            assertFalse(level.getField().isFree(tree.getCoordinates()));
            assertFalse(tree.occupies(level.getPlayerPosition()));
        }
    }
}
