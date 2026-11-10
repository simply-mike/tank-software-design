package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.MoveTankCommand;
import ru.mipt.bit.platformer.command.ShootTankCommand;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.mipt.bit.platformer.TankFixtures.spawnTestTank;

public class CombatTest {

    @Test
    public void shootingCommandDefersCreationAndPreservesShooterAndDirection() {
        for (Direction direction : Direction.values()) {
            Level level = emptyLevel();
            Tank shooter = spawnTestTank(level, new GridPoint2(3, 3), 0.4f);
            Tank other = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
            shooter.move(direction);
            ShootTankCommand command = new ShootTankCommand(shooter);
            assertTrue(bullets(level).isEmpty());

            command.execute();

            Bullet bullet = bullets(level).get(0);
            assertEquals(direction.calculateDestinationFrom(new GridPoint2(3, 3)), bullet.getCoordinates());
            assertEquals(direction, bullet.getDirection());
            assertTrue(bullet.wasFiredBy(shooter));
            assertFalse(bullet.wasFiredBy(other));
            shooter.update(0.4f);
            shooter.move(direction == Direction.UP ? Direction.DOWN : Direction.UP);
            assertEquals(direction, bullet.getDirection());
        }
    }

    @Test
    public void bulletMovesAcrossEveryCellAndKeepsFractionalProgress() {
        Level level = emptyLevel();
        spawnTestTank(level, new GridPoint2(0, 0), 0.4f).shoot();
        Bullet bullet = bullets(level).get(0);

        level.update(0.25f);

        assertEquals(new GridPoint2(3, 0), bullet.getCoordinates());
        assertEquals(new GridPoint2(4, 0), bullet.getDestinationCoordinates());
        assertEquals(0.5f, bullet.getMovementProgress(), 0.0001f);
        bullet.getCoordinates().set(6, 6);
        bullet.getDestinationCoordinates().set(6, 6);
        assertEquals(new GridPoint2(3, 0), bullet.getCoordinates());
    }

    @Test
    public void hitsAdjacentTankBeforeAnyMovementEvenOnZeroTick() {
        Level level = emptyLevel();
        Tank shooter = level.spawnTank(new GridPoint2(0, 0), 0.4f, 3, new BulletSettings(2, 0.1f));
        Tank target = spawnTestTank(level, new GridPoint2(1, 0), 0.4f);

        shooter.shoot();
        level.update(0f);

        assertEquals(98, target.getHealth());
        assertTrue(bullets(level).isEmpty());
    }

    @Test
    public void adjacentMovingTargetCannotEscapeTheSpawnContactOnALargeTick() {
        Level level = emptyLevel();
        Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank target = spawnTestTank(level, new GridPoint2(1, 0), 0.4f);
        target.move(Direction.UP);
        shooter.shoot();

        level.update(0.4f);

        assertEquals(70, target.getHealth());
        assertTrue(bullets(level).isEmpty());
    }

    @Test
    public void hitsTankOnItsPathDuringLargeTick() {
        Level level = emptyLevel();
        Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank target = spawnTestTank(level, new GridPoint2(4, 0), 0.4f);
        shooter.shoot();

        level.update(1f);

        assertEquals(70, target.getHealth());
        assertTrue(bullets(level).isEmpty());
    }

    @Test
    public void treesStopBulletsAtSpawnAndAlongTheirPathWithoutBeingDestroyed() {
        for (int x : new int[]{1, 4}) {
            Tree tree = new Tree(new GridPoint2(x, 0));
            Level level = new Level(7, 1, List.of(tree), new GridPoint2(0, 0));
            spawnTestTank(level, new GridPoint2(0, 0), 0.4f).shoot();

            level.update(1f);

            assertTrue(bullets(level).isEmpty());
            assertEquals(List.of(tree), level.getTrees());
            assertFalse(level.isFree(tree.getCoordinates()));
        }
    }

    @Test
    public void aNewObstacleTypeBlocksTanksAndBulletsThroughTheExistingContract() {
        Level level = emptyLevel();
        Obstacle rock = coordinates -> coordinates.equals(new GridPoint2(2, 0));
        level.add(rock);
        Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        shooter.shoot();

        level.update(0.2f);

        assertTrue(bullets(level).isEmpty());
        assertFalse(level.isFree(new GridPoint2(2, 0)));
        assertTrue(level.getObjects().contains(rock));
    }

    @Test
    public void keepsZeroHealthTankAndRemovesItOnlyAfterHealthBecomesNegative() {
        Level level = emptyLevel();
        Tank shooter = level.spawnTank(new GridPoint2(0, 0), 0.4f, 3, new BulletSettings(2, 0.1f));
        Tank target = level.spawnTank(new GridPoint2(1, 0), 0.4f, 2, new BulletSettings(1, 0.1f));
        shooter.shoot();
        level.update(0f);
        assertEquals(0, target.getHealth());
        assertFalse(target.isDestroyed());
        assertTrue(level.getObjects().contains(target));
        assertFalse(level.isFree(new GridPoint2(1, 0)));

        shooter.shoot();
        level.update(0f);

        assertEquals(-2, target.getHealth());
        assertTrue(target.isDestroyed());
        assertFalse(level.getObjects().contains(target));
        assertTrue(level.isFree(new GridPoint2(1, 0)));
    }

    @Test
    public void destroysBothBulletsWhenTheyAppearInTheSameCell() {
        Level level = emptyLevel();
        Tank first = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank second = spawnTestTank(level, new GridPoint2(3, 0), 0.4f);
        second.move(Direction.LEFT);
        second.update(0.4f);
        first.shoot();
        second.shoot();

        level.update(0f);

        assertTrue(bullets(level).isEmpty());
        assertEquals(100, first.getHealth());
        assertEquals(100, second.getHealth());
    }

    @Test
    public void destroysBothBulletsMeetingHeadOnDuringMovement() {
        Level level = emptyLevel();
        Tank first = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Tank second = spawnTestTank(level, new GridPoint2(6, 0), 0.4f);
        second.move(Direction.LEFT);
        second.update(0.4f);
        first.shoot();
        second.shoot();

        level.update(0.2f);

        assertTrue(bullets(level).isEmpty());
        assertEquals(100, first.getHealth());
        assertEquals(100, second.getHealth());
    }

    @Test
    public void hitsBothCellsOccupiedByAMovingTank() {
        for (int y : new int[]{0, 1}) {
            Level level = emptyLevel();
            Tank shooter = spawnTestTank(level, new GridPoint2(0, y), 0.4f);
            Tank target = spawnTestTank(level, new GridPoint2(2, 0), 0.4f);
            target.move(Direction.UP);
            shooter.shoot();

            level.update(0.1f);

            assertEquals(70, target.getHealth());
            assertTrue(bullets(level).isEmpty());
        }
    }

    @Test
    public void movingShooterDoesNotHitItselfAndBulletsDoNotBlockTankMovement() {
        Level level = emptyLevel();
        Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        shooter.move(Direction.RIGHT);
        shooter.shoot();
        level.update(0f);
        assertEquals(100, shooter.getHealth());
        assertEquals(1, bullets(level).size());

        Level otherLevel = emptyLevel();
        spawnTestTank(otherLevel, new GridPoint2(0, 0), 0.4f).shoot();
        Tank other = spawnTestTank(otherLevel, new GridPoint2(2, 0), 0.4f);
        other.move(Direction.LEFT);
        assertTrue(other.isMoving());
    }

    @Test
    public void removesBulletsAtEveryBoundaryIncludingOutOfBoundsSpawn() {
        for (Direction direction : Direction.values()) {
            Level level = new Level(1, 1, List.of(), new GridPoint2(0, 0));
            Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
            shooter.move(direction);
            shooter.shoot();
            level.update(0f);
            assertTrue(bullets(level).isEmpty());
            assertEquals(100, shooter.getHealth());
        }
        Level level = emptyLevel();
        spawnTestTank(level, new GridPoint2(0, 0), 0.4f).shoot();
        level.update(Float.MAX_VALUE);
        assertTrue(bullets(level).isEmpty());
    }

    @Test
    public void destroyedTanksIgnoreQueuedCommandsAndFurtherDamage() {
        Level level = emptyLevel();
        Tank target = spawnTestTank(level, new GridPoint2(1, 0), 0.4f);
        MoveTankCommand move = new MoveTankCommand(target, Direction.UP);
        ShootTankCommand shoot = new ShootTankCommand(target);
        target.takeDamage(Integer.MAX_VALUE);
        int health = target.getHealth();

        move.execute();
        shoot.execute();
        target.takeDamage(Integer.MAX_VALUE);
        level.update(0f);

        assertEquals(health, target.getHealth());
        assertFalse(target.isMoving());
        assertTrue(level.getObjects().isEmpty());
    }

    @Test
    public void objectsAddedDuringUpdateStartUpdatingOnTheNextTick() {
        Level level = emptyLevel();
        AtomicInteger updates = new AtomicInteger();
        LevelObject child = new LevelObject() {
            @Override public boolean occupies(GridPoint2 position) { return false; }
            @Override public void update(double deltaTime) { updates.incrementAndGet(); }
        };
        level.add(new LevelObject() {
            @Override public boolean occupies(GridPoint2 position) { return false; }
            @Override public void update(double deltaTime) { level.add(child); }
        });

        level.update(0f);
        assertEquals(0, updates.get());
        level.update(0f);
        assertEquals(1, updates.get());
        assertEquals(2, level.getObjects().size());
    }

    @Test
    public void rejectsInvalidCombatSettingsAndTicksWithoutMutatingLevel() {
        for (float duration : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new BulletSettings(1, duration));
        }
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(0, 0.1f));
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(-1, 0.1f));
        Level level = emptyLevel();
        assertThrows(IllegalArgumentException.class,
                () -> level.spawnTank(new GridPoint2(0, 0), 0.4f, -1, new BulletSettings(1, 0.1f)));
        assertTrue(level.getObjects().isEmpty());
        Tank shooter = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        assertThrows(IllegalArgumentException.class, () -> shooter.takeDamage(0));
        shooter.shoot();
        Bullet bullet = bullets(level).get(0);
        for (float delta : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> level.update(delta));
            assertThrows(IllegalArgumentException.class, () -> bullet.update(delta));
        }
        assertEquals(new GridPoint2(1, 0), bullet.getCoordinates());
        assertEquals(0f, bullet.getMovementProgress(), 0f);
    }

    private static Level emptyLevel() {
        return new Level(7, 7, List.of(), new GridPoint2(0, 0));
    }

    private static List<Bullet> bullets(Level level) {
        return level.getObjects().stream().filter(Bullet.class::isInstance).map(Bullet.class::cast)
                .collect(Collectors.toList());
    }
}
