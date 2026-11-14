package ru.mipt.bit.platformer.setup;

import ru.mipt.bit.platformer.model.Level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/** Application service that creates tanks on randomly chosen free cells */
public final class RandomTankSpawner {

    private final Random random;

    public RandomTankSpawner(Random random) {
        this.random = random;
    }

    public List<Tank> spawn(Level level, int count, Function<GridPoint2, Tank> spawnTank) {
        if (count < 0) {
            throw new IllegalArgumentException("Tank count must not be negative");
        }
        List<GridPoint2> freeTiles = new ArrayList<>();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (level.isFree(tile)) {
                    freeTiles.add(tile);
                }
            }
        }
        Collections.shuffle(freeTiles, random);
        List<Tank> tanks = new ArrayList<>();
        for (int i = 0; i < Math.min(count, freeTiles.size()); i++) {
            tanks.add(spawnTank.apply(freeTiles.get(i)));
        }
        return tanks;
    }
}
