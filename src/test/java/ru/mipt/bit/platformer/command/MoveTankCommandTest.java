package ru.mipt.bit.platformer.command;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveTankCommandTest {

    @Test
    public void defersMoveAndPreservesReceiverAndDirection() {
        Field field = new Field(4, 4, List.of());
        Tank receiver = field.spawnTank(new GridPoint2(1, 1), 0.4f);
        Tank other = field.spawnTank(new GridPoint2(3, 3), 0.4f);
        Command command = new MoveTankCommand(receiver, Direction.UP);

        assertFalse(receiver.isMoving());
        command.execute();

        assertTrue(receiver.isMoving());
        assertEquals(Direction.UP, receiver.getDirection());
        assertEquals(new GridPoint2(1, 2), receiver.getDestinationCoordinates());
        assertFalse(other.isMoving());
    }
}
