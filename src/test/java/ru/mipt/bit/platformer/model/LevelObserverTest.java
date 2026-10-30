package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LevelObserverTest {

    @Test
    public void replaysInitialObjectsAndReportsLaterAdditionsWithoutDuplicates() {
        Tree tree = new Tree(new GridPoint2(2, 0));
        Level level = new Level(4, 1, List.of(tree), new GridPoint2(0, 0));
        Tank tank = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        RecordingListener listener = new RecordingListener();

        level.subscribe(listener);
        level.subscribe(listener);
        level.add(tree);
        tank.shoot();

        assertEquals(level.getObjects(), listener.added);
        assertEquals(3, listener.added.size());
        assertTrue(listener.removed.isEmpty());
    }

    @Test
    public void removalIsPublishedAfterModelChangesAndOnlyOnce() {
        Level level = new Level(2, 1, List.of(), new GridPoint2(0, 0));
        Tank tank = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        AtomicInteger removals = new AtomicInteger();
        level.subscribe(new LevelListener() {
            @Override public void objectAdded(LevelObject object) {
                assertTrue(level.getObjects().contains(object));
            }
            @Override public void objectRemoved(LevelObject object) {
                assertFalse(level.getObjects().contains(object));
                removals.incrementAndGet();
            }
        });

        level.remove(tank);
        level.remove(tank);

        assertEquals(1, removals.get());
        assertTrue(level.isFree(new GridPoint2(0, 0)));
    }

    @Test
    public void unsubscribedListenersReceiveNoFutureEventsAndCanResynchronize() {
        Level level = new Level(4, 1, List.of(), new GridPoint2(0, 0));
        RecordingListener listener = new RecordingListener();
        level.subscribe(listener);
        level.unsubscribe(listener);
        Tank tank = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        assertTrue(listener.added.isEmpty());

        level.subscribe(listener);
        assertEquals(List.of(tank), listener.added);
        level.unsubscribe(listener);
        level.remove(tank);
        assertTrue(listener.removed.isEmpty());
    }

    @Test
    public void collisionsNotifyRemovalOfBulletsAndDestroyedTanksExactlyOnce() {
        Level level = new Level(3, 1, List.of(), new GridPoint2(0, 0));
        Tank shooter = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank target = level.spawnTank(new GridPoint2(1, 0), 0.4f, 0, new BulletSettings(1, 0.1f));
        RecordingListener listener = new RecordingListener();
        level.subscribe(listener);
        shooter.shoot();
        LevelObject bullet = listener.added.get(2);

        level.update(0f);
        level.update(0f);

        assertEquals(List.of(target, bullet), listener.removed);
        assertEquals(List.of(shooter), level.getObjects());
    }

    @Test
    public void subscriptionsCanChangeDuringNotification() {
        Level level = new Level(3, 1, List.of(), new GridPoint2(0, 0));
        AtomicInteger additions = new AtomicInteger();
        level.subscribe(new LevelListener() {
            @Override public void objectAdded(LevelObject object) {
                additions.incrementAndGet();
                level.unsubscribe(this);
            }
            @Override public void objectRemoved(LevelObject object) { }
        });
        RecordingListener other = new RecordingListener();
        level.subscribe(other);

        Tank tank = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        tank.shoot();

        assertEquals(1, additions.get());
        assertEquals(level.getObjects(), other.added);
    }

    @Test
    public void removedObjectsAreNotUpdatedLaterInTheSameTick() {
        Level level = new Level(3, 1, List.of(), new GridPoint2(0, 0));
        AtomicInteger updates = new AtomicInteger();
        LevelObject child = new LevelObject() {
            @Override public boolean occupies(GridPoint2 coordinates) { return false; }
            @Override public void update(float deltaTime) { updates.incrementAndGet(); }
        };
        level.add(new LevelObject() {
            @Override public boolean occupies(GridPoint2 coordinates) { return false; }
            @Override public void update(float deltaTime) { level.remove(child); }
        });
        level.add(child);

        level.update(0f);

        assertEquals(0, updates.get());
        assertEquals(1, level.getObjects().size());
    }

    private static final class RecordingListener implements LevelListener {
        private final List<LevelObject> added = new ArrayList<>();
        private final List<LevelObject> removed = new ArrayList<>();

        @Override public void objectAdded(LevelObject object) { added.add(object); }
        @Override public void objectRemoved(LevelObject object) { removed.add(object); }
    }
}
