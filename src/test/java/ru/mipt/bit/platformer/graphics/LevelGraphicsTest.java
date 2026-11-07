package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.LevelObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class LevelGraphicsTest {

    @Test
    public void rendersInitialAndDynamicObjectsAndStopsDrawingRemovedObjects() {
        Tree tree = new Tree(new GridPoint2(3, 0));
        Level level = new Level(4, 1, List.of(tree), new GridPoint2(0, 0));
        Tank tank = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        List<LevelObject> drawn = new ArrayList<>();
        AtomicInteger creations = new AtomicInteger();
        LevelGraphics graphics = new LevelGraphics(object -> {
            creations.incrementAndGet();
            return () -> drawn.add(object);
        });
        level.subscribe(graphics);
        level.subscribe(graphics);
        graphics.objectAdded(tank);
        graphics.render();
        assertEquals(List.of(tree, tank), drawn);
        assertEquals(2, creations.get());

        tank.shoot();
        Bullet bullet = (Bullet) level.getObjects().get(2);
        drawn.clear();
        graphics.render();
        assertEquals(List.of(tree, tank, bullet), drawn);
        assertEquals(3, creations.get());

        level.update(1f);
        level.remove(tank);
        drawn.clear();
        graphics.render();
        assertEquals(List.of(tree), drawn);
    }

    @Test
    public void fourthStandardHitRemovesTankViewAndAdjacentHitsLeaveNoBulletViews() {
        Level level = new Level(2, 1, List.of(), new GridPoint2(0, 0));
        Tank shooter = level.spawnTank(new GridPoint2(0, 0), 0.4f);
        Tank target = level.spawnTank(new GridPoint2(1, 0), 0.4f);
        List<LevelObject> drawn = new ArrayList<>();
        LevelGraphics graphics = new LevelGraphics(object -> () -> drawn.add(object));
        level.subscribe(graphics);

        for (int hit = 0; hit < 3; hit++) {
            shooter.shoot();
            level.update(0f);
        }
        graphics.render();
        assertEquals(List.of(shooter, target), drawn);
        drawn.clear();

        shooter.shoot();
        level.update(0f);
        graphics.render();

        assertEquals(List.of(shooter), drawn);
    }
}
