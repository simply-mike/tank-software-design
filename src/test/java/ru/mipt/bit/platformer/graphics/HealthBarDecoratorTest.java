package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.Tank;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.mipt.bit.platformer.TankFixtures.spawnTestTank;

public class HealthBarDecoratorTest {

    @Test
    public void drawsBaseViewAndUsesCurrentHealthOnlyWhenEnabled() {
        GridPoint2 start = new GridPoint2(0, 0);
        Tank tank = spawnTestTank(new Level(1, 1, List.of(), start), start, 0.4f);
        HealthBarVisibility visibility = new HealthBarVisibility();
        Command toggle = visibility::toggle;
        List<Float> widths = new ArrayList<>();
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    assertEquals("draw", method.getName());
                    widths.add((Float) args[3]);
                    return null;
                });
        TilePlacement tiles = new TilePlacement() {
            @Override
            public void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates) {
                rectangle.setCenter(64f, 64f);
            }

            @Override
            public void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                                GridPoint2 to, float progress) {
                placeAtTileCenter(rectangle, from);
            }
        };
        List<String> baseDraws = new ArrayList<>();
        Runnable view = new HealthBarDecorator(() -> baseDraws.add("tank"), tank,
                visibility::isVisible, batch, tiles, new TextureRegion(), new TextureRegion());

        view.run();
        assertEquals(List.of(), widths);

        toggle.execute();
        tank.takeDamage(50);
        view.run();
        assertEquals(List.of(64f, 32f), widths);
        assertEquals(50, tank.getHealth());

        widths.clear();
        tank.takeDamage(50);
        view.run();
        assertEquals(List.of(64f), widths);

        widths.clear();
        toggle.execute();
        view.run();
        assertEquals(List.of(), widths);
        assertEquals(List.of("tank", "tank", "tank", "tank"), baseDraws);
    }
}
