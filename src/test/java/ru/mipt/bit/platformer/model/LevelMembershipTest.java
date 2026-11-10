package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveTankCommand;
import ru.mipt.bit.platformer.command.ShootTankCommand;

import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.mipt.bit.platformer.TankFixtures.spawnTestTank;

public class LevelMembershipTest {

    @Test
    public void publicApiCannotRegisterAnObjectOwnedByAnotherLevel() throws Exception {
        assertFalse(Modifier.isPublic(Level.class.getDeclaredMethod("add", LevelObject.class).getModifiers()));
        for (var constructor : Tank.class.getDeclaredConstructors()) {
            assertFalse(Modifier.isPublic(constructor.getModifiers()));
        }
        for (var constructor : Bullet.class.getDeclaredConstructors()) {
            assertFalse(Modifier.isPublic(constructor.getModifiers()));
        }
    }

    @Test
    public void removalEndsTankActivityWithoutChangingHealthOrExecutingQueuedCommands() {
        Level level = emptyLevel();
        Tank tank = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        Command move = new MoveTankCommand(tank, Direction.UP);
        Command shoot = new ShootTankCommand(tank);

        level.remove(tank);
        move.execute();
        shoot.execute();
        tank.update(1d);
        level.add(tank);

        assertTrue(tank.isDestroyed());
        assertEquals(100, tank.getHealth());
        assertEquals(new GridPoint2(0, 0), tank.getDestinationCoordinates());
        assertTrue(level.getObjects().isEmpty());
    }

    @Test
    public void removedBulletCannotContinueMovingOrBeRegisteredAgain() {
        Level level = emptyLevel();
        Tank tank = spawnTestTank(level, new GridPoint2(0, 0), 0.4f);
        tank.shoot();
        Bullet bullet = (Bullet) level.getObjects().get(1);

        level.remove(bullet);
        bullet.update(0.2d);
        level.add(bullet);

        assertTrue(bullet.isDestroyed());
        assertEquals(new GridPoint2(1, 0), bullet.getCoordinates());
        assertEquals(List.of(tank), level.getObjects());
    }

    @Test
    public void anotherLevelCannotRemoveOrDeactivateTheOwnersTank() {
        Level owner = emptyLevel();
        Level other = emptyLevel();
        Tank tank = spawnTestTank(owner, new GridPoint2(0, 0), 0.4f);

        other.remove(tank);
        tank.shoot();

        assertFalse(tank.isDestroyed());
        assertEquals(2, owner.getObjects().size());
        assertTrue(other.getObjects().isEmpty());
    }

    private static Level emptyLevel() {
        return new Level(4, 2, List.of(), new GridPoint2(0, 0));
    }
}
