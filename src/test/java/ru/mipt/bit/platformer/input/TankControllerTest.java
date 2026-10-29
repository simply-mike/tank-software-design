package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Bullet;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TankControllerTest {

    @Test
    public void spaceQueuesShootingThroughTheSameCommandPipeline() {
        Level level = new Level(4, 4, List.of(), new GridPoint2(0, 0));
        Tank tank = level.spawnTank(new GridPoint2(1, 1), 0.4f);
        List<Command> commands = new ArrayList<>();
        ButtonPressHandler handler = new ButtonPressHandler(key -> key == Input.Keys.SPACE, commands::add);
        new TankController(handler, tank).control();
        assertEquals(1, level.getObjects().size());
        assertEquals(1, commands.size());

        commands.get(0).execute();

        Bullet bullet = (Bullet) level.getObjects().get(1);
        assertEquals(new GridPoint2(2, 1), bullet.getCoordinates());
        assertEquals(Direction.RIGHT, bullet.getDirection());
    }

    @Test
    public void mapsArrowAndLetterKeysToDirections() {
        assertMoves(Input.Keys.UP, Direction.UP);
        assertMoves(Input.Keys.W, Direction.UP);
        assertMoves(Input.Keys.LEFT, Direction.LEFT);
        assertMoves(Input.Keys.A, Direction.LEFT);
        assertMoves(Input.Keys.DOWN, Direction.DOWN);
        assertMoves(Input.Keys.S, Direction.DOWN);
        assertMoves(Input.Keys.RIGHT, Direction.RIGHT);
        assertMoves(Input.Keys.D, Direction.RIGHT);
    }

    private static void assertMoves(int pressedKey, Direction expectedDirection) {
        Level level = new Level(10, 8, Collections.emptyList(), new GridPoint2(0, 0));
        Tank tank = level.spawnTank(new GridPoint2(1, 1), 0.4f);
        ButtonPressHandler handler = new ButtonPressHandler(key -> key == pressedKey, Command::execute);
        TankControlStrategy controller = new TankController(handler, tank);

        controller.control();

        assertEquals(expectedDirection, tank.getDirection());
        assertEquals(expectedDirection.calculateDestinationFrom(new GridPoint2(1, 1)),
                tank.getDestinationCoordinates());
    }
}
