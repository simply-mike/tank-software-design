package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SimulationTimeTest {

    @Test
    public void parallelBulletsKeepTheirDistanceRegardlessOfTickSizeAndUpdateOrder() {
        for (boolean frontFirst : new boolean[]{false, true}) {
            Level largeTick = parallelShots(frontFirst);
            Level smallTicks = parallelShots(frontFirst);

            largeTick.update(0.6f);
            for (int i = 0; i < 6; i++) {
                smallTicks.update(0.1f);
            }

            assertEquals(List.of(new GridPoint2(7, 0)), bulletPositions(largeTick));
            assertEquals(bulletPositions(smallTicks), bulletPositions(largeTick));
        }
    }

    @Test
    public void headOnBulletsCollideEvenWhenTheirEndpointsSwap() {
        Level level = new Level(5, 1, List.of(), new GridPoint2(0, 0));
        Tank first = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank second = level.spawnTank(new GridPoint2(4, 0), 0.4f);
        second.move(Direction.LEFT);
        second.update(0.4f);
        first.shoot();
        second.shoot();

        level.update(0.1f);

        assertTrue(bulletPositions(level).isEmpty());
        assertEquals(100, first.getHealth());
        assertEquals(100, second.getHealth());
    }

    @Test
    public void fasterBulletCatchesSlowerBulletAtTheSameTimeInEitherInsertionOrder() {
        for (boolean slowFirst : new boolean[]{false, true}) {
            Level level = new Level(20, 2, List.of(), new GridPoint2(0, 0));
            Tank fast = level.spawnTank(new GridPoint2(0, 0), 0.4f, 3, new BulletSettings(1, 0.1f));
            Tank slow = level.spawnTank(new GridPoint2(6, 0), 0.4f, 3, new BulletSettings(1, 0.2f));
            if (slowFirst) {
                slow.shoot();
                fast.shoot();
            } else {
                fast.shoot();
                slow.shoot();
            }
            fast.move(Direction.UP);
            slow.move(Direction.UP);

            level.update(1f);
            assertEquals(2, bulletPositions(level).size());
            level.update(0.3f);
            assertTrue(bulletPositions(level).isEmpty());
            assertEquals(3, slow.getHealth());
        }
    }

    @Test
    public void tankFinishingItsMoveBeforeBulletArrivalReleasesItsSourceCell() {
        Level level = new Level(5, 2, List.of(), new GridPoint2(0, 0));
        Tank shooter = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank target = level.spawnTank(new GridPoint2(2, 0), 0.05f);
        shooter.shoot();
        target.move(Direction.UP);

        level.update(0.1f);

        assertEquals(100, target.getHealth());
        assertEquals(List.of(new GridPoint2(2, 0)), bulletPositions(level));
    }

    @Test
    public void simultaneousBulletContactsDoNotLeaveAnUnprocessedThirdBullet() {
        Level level = new Level(5, 2, List.of(), new GridPoint2(0, 0));
        Tank shooter = level.spawnTank(new GridPoint2(0, 1), 0.4f);
        for (int i = 0; i < 3; i++) {
            level.add(new Bullet(new GridPoint2(2, 0), Direction.RIGHT,
                    new BulletSettings(1, 0.1f), shooter));
        }

        level.update(0d);

        assertTrue(bulletPositions(level).isEmpty());
    }

    @Test
    public void rejectsAnInvalidTransitionTimeInsteadOfLoopingForever() {
        for (double transition : new double[]{0d, -1d, Double.NaN}) {
            Level level = new Level(2, 1, List.of(), new GridPoint2(0, 0));
            level.add(new LevelObject() {
                @Override public boolean occupies(GridPoint2 position) { return false; }
                @Override public double timeToNextTransition() { return transition; }
            });

            assertThrows(IllegalStateException.class, () -> level.update(0.1d));
        }
    }

    private static Level parallelShots(boolean frontFirst) {
        Level level = new Level(10, 2, List.of(), new GridPoint2(0, 0));
        Tank rear = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank front = level.spawnTank(new GridPoint2(6, 0), 0.4f);
        if (frontFirst) {
            front.shoot();
            rear.shoot();
        } else {
            rear.shoot();
            front.shoot();
        }
        rear.move(Direction.UP);
        front.move(Direction.UP);
        return level;
    }

    private static List<GridPoint2> bulletPositions(Level level) {
        return level.getObjects().stream().filter(Bullet.class::isInstance)
                .map(Bullet.class::cast).map(Bullet::getCoordinates).collect(Collectors.toList());
    }
}
