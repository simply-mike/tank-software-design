package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RandomTankControllerTest {

    @Test
    public void choosesRepeatableDirectionOnlyWhenTankIsIdle() {
        Field field = new Field(5, 5, List.of());
        Tank tank = field.spawnTank(new GridPoint2(2, 2), 0.4f);
        TankControlStrategy controller = new RandomTankController(tank, new Random(7));
        Random expectedRandom = new Random(7);
        Direction first = Direction.values()[expectedRandom.nextInt(4)];

        controller.control();
        assertEquals(first, tank.getDirection());
        assertEquals(first.calculateDestinationFrom(new GridPoint2(2, 2)), tank.getDestinationCoordinates());
        tank.update(0.2f);
        controller.control();
        assertEquals(first, tank.getDirection());
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);

        tank.update(0.2f);
        GridPoint2 nextStart = tank.getCoordinates();
        Direction second = Direction.values()[expectedRandom.nextInt(4)];
        controller.control();
        assertEquals(second, tank.getDirection());
        assertEquals(second.calculateDestinationFrom(nextStart), tank.getDestinationCoordinates());
    }

    @Test
    public void bothStrategiesRespectBoundariesObstaclesAndTanks() {
        for (boolean ai : new boolean[]{false, true}) {
            Field field = new Field(3, 2, List.of(new Tree(new GridPoint2(0, 1))));
            Tank tank = field.spawnTank(new GridPoint2(0, 0), 0.4f);
            Tank blocker = field.spawnTank(new GridPoint2(1, 0), 0.4f);
            for (Direction direction : Direction.values()) {
                strategy(tank, direction, ai).control();
                assertEquals(direction, tank.getDirection());
                assertFalse(tank.isMoving());
            }

            blocker.move(Direction.RIGHT);
            strategy(tank, Direction.RIGHT, ai).control();
            assertFalse(tank.isMoving());
            blocker.update(0.4f);
            strategy(tank, Direction.RIGHT, ai).control();
            assertTrue(tank.isMoving());
        }
    }

    @Test
    public void aiDoesNotTakeOverPlayerAndKeyboardDoesNotControlAi() {
        Field field = new Field(4, 4, List.of());
        Tank player = field.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank ai = field.spawnTank(new GridPoint2(2, 2), 0.4f);
        TankControlStrategy keyboard = strategy(player, Direction.UP, false);
        TankControlStrategy random = strategy(ai, Direction.DOWN, true);

        random.control();
        assertFalse(player.isMoving());
        assertEquals(new GridPoint2(2, 1), ai.getDestinationCoordinates());
        keyboard.control();
        assertEquals(new GridPoint2(0, 1), player.getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 1), ai.getDestinationCoordinates());
    }

    private static TankControlStrategy strategy(Tank tank, Direction direction, boolean ai) {
        if (ai) {
            return new RandomTankController(tank, new Random(7) {
                @Override
                public int nextInt(int bound) {
                    return direction.ordinal();
                }
            });
        }
        int[] keys = {Input.Keys.UP, Input.Keys.LEFT, Input.Keys.DOWN, Input.Keys.RIGHT};
        return new TankController(new ButtonPressHandler(key -> key == keys[direction.ordinal()]), tank);
    }
}
