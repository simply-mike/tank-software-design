package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreeTest {

    @Test
    public void occupiesOnlyItsTile() {
        Tree tree = new Tree(new GridPoint2(1, 3));

        assertTrue(tree.occupies(new GridPoint2(1, 3)));
        assertFalse(tree.occupies(new GridPoint2(1, 2)));
    }

    @Test
    public void ownsItsCoordinates() {
        GridPoint2 coordinates = new GridPoint2(1, 3);
        Tree tree = new Tree(coordinates);

        coordinates.set(5, 5);
        tree.getCoordinates().set(6, 6);

        assertEquals(new GridPoint2(1, 3), tree.getCoordinates());
    }
}
