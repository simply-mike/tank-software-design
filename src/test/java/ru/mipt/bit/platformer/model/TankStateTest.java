package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ShootTankCommand;
import ru.mipt.bit.platformer.input.ButtonPressHandler;
import ru.mipt.bit.platformer.input.RandomTankController;
import ru.mipt.bit.platformer.input.TankController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class TankStateTest {

    @Test
    public void healthBoundariesControlMovementAndShootingAgainstOwnInitialHealth() {
        int[][] cases = {{100, 100, 1}, {100, 71, 1}, {100, 70, 2},
                {100, 15, 2}, {100, 14, 3}, {100, 0, 3},
                {80, 80, 1}, {80, 57, 1}, {80, 56, 2}, {80, 12, 2}, {80, 11, 3},
                {3, 3, 1}, {3, 2, 2}, {0, 0, 3},
                {Integer.MAX_VALUE, Integer.MAX_VALUE, 1},
                {Integer.MAX_VALUE, 1503238552, 2}, {Integer.MAX_VALUE, 322122547, 3}};
        for (int[] example : cases) {
            String scenario = "initialHP=" + example[0] + ", currentHP=" + example[1];
            Level level = level();
            Tank tank = spawn(level, 0, example[0]);
            if (example[0] > example[1]) {
                tank.takeDamage(example[0] - example[1]);
            }
            tank.move(Direction.RIGHT);
            assertEquals(scenario, example[2], tank.timeToNextTransition(), 0.000001d);
            level.update(0.5d);
            assertEquals(scenario, 0.5d / example[2], tank.getMovementProgress(), 0.000001d);
            level.update(tank.timeToNextTransition());
            assertFalse(scenario, tank.isMoving());
            assertEquals(scenario, new GridPoint2(1, 0), tank.getCoordinates());
            tank.shoot();
            assertEquals(scenario, example[2] != 3, level.getObjects().stream().anyMatch(Bullet.class::isInstance));
        }
    }

    @Test
    public void damageChangesSpeedImmediatelyWithoutJumpingOrCompoundingSlowdown() {
        Level level = level();
        Tank tank = spawn(level, 0, 100);
        tank.move(Direction.RIGHT);
        level.update(0.25d);
        tank.takeDamage(30);
        assertEquals(0.25f, tank.getMovementProgress(), 0f);
        assertEquals(1.5d, tank.timeToNextTransition(), 0d);
        tank.takeDamage(1);
        assertEquals(1.5d, tank.timeToNextTransition(), 0d);
        level.update(0.5d);
        assertEquals(0.5f, tank.getMovementProgress(), 0f);
        tank.takeDamage(55);
        assertEquals(0.5f, tank.getMovementProgress(), 0f);
        assertEquals(1.5d, tank.timeToNextTransition(), 0d);
        assertFalse(level.isFree(new GridPoint2(0, 0)));
        assertFalse(level.isFree(new GridPoint2(1, 0)));
        level.update(1.5d);
        assertFalse(tank.isMoving());
        assertTrue(level.isFree(new GridPoint2(0, 0)));
    }

    @Test
    public void tanksHaveIndependentStatesAndStillRespectReservedCells() {
        Level level = level();
        Tank light = spawn(level, 0, 100);
        Tank medium = spawn(level, 1, 80);
        Tank heavy = spawn(level, 2, 100);
        medium.takeDamage(24);
        heavy.takeDamage(86);
        for (Tank tank : List.of(light, medium, heavy)) {
            tank.move(Direction.RIGHT);
        }
        level.update(1d);
        assertFalse(light.isMoving());
        assertEquals(0.5f, medium.getMovementProgress(), 0.000001f);
        assertEquals(1f / 3f, heavy.getMovementProgress(), 0.000001f);
        Tank follower = level.spawnTank(new GridPoint2(2, 2), 1f, 100, new BulletSettings(30, 0.25f));
        follower.move(Direction.LEFT);
        assertFalse(follower.isMoving());
    }

    @Test
    public void queuedPlayerAiAndDirectShotsUseStateAtExecutionTime() {
        for (boolean ai : new boolean[]{false, true}) {
            Level level = level();
            Tank tank = spawn(level, 0, 100);
            List<Command> commands = new ArrayList<>();
            if (ai) {
                new RandomTankController(tank, new Random(0) {
                    @Override public int nextInt(int bound) { return bound - 1; }
                }, commands::add).control();
            } else {
                new TankController(new ButtonPressHandler(key -> key == Input.Keys.SPACE,
                        commands::add), tank).control();
            }
            assertEquals(1, commands.size());
            tank.takeDamage(86);
            commands.get(0).execute();
            new ShootTankCommand(tank).execute();
            tank.shoot();
            assertEquals(List.of(tank), level.getObjects());
        }
    }

    @Test
    public void bulletHitDuringTickChangesRemainingTravelConsistentlyAcrossTickSizes() {
        for (double tick : new double[]{1d, 0.125d}) {
            Level level = level();
            Tank shooter = spawn(level, 0, 100);
            Tank target = level.spawnTank(new GridPoint2(2, 0), 1f, 100, new BulletSettings(30, 0.25f));
            target.move(Direction.UP);
            shooter.shoot();
            for (int i = 0; i < (int) (1d / tick); i++) {
                level.update(tick);
            }
            assertEquals(70, target.getHealth());
            assertEquals(0.625f, target.getMovementProgress(), 0f);
            assertEquals(0.75d, target.timeToNextTransition(), 0d);
        }
    }

    private static Level level() {
        return new Level(5, 4, List.of(), new GridPoint2(0, 0));
    }

    private static Tank spawn(Level level, int row, int health) {
        return level.spawnTank(new GridPoint2(0, row), 1f, health, new BulletSettings(30, 0.25f));
    }
}
