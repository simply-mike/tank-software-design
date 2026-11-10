package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.BulletSettings;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.Tank;

import java.util.function.Function;

public final class TankFixtures {

    private static final int INITIAL_HEALTH = 100;
    private static final BulletSettings BULLETS = new BulletSettings(30, 0.1f);

    private TankFixtures() {
    }

    public static Tank spawnTestTank(Level level, GridPoint2 tile, float movementDuration) {
        return level.spawnTank(tile, movementDuration, INITIAL_HEALTH, BULLETS);
    }

    public static Function<GridPoint2, Tank> testTankFactory(Level level, float movementDuration) {
        return tile -> spawnTestTank(level, tile, movementDuration);
    }
}
