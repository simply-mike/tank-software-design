package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.LevelObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.Objects;
import java.util.function.Function;

public final class ObjectViewFactory implements Function<LevelObject, Runnable>, Disposable {

    private final Batch batch;
    private final TilePlacement tiles;
    private final TankGraphics tanks;
    private final TreeGraphics trees;
    private final BulletGraphics bullets;

    public ObjectViewFactory(Batch batch, TilePlacement tiles) {
        this.batch = Objects.requireNonNull(batch);
        this.tiles = Objects.requireNonNull(tiles);
        tanks = new TankGraphics("images/tank_blue.png");
        trees = new TreeGraphics("images/greenTree.png");
        bullets = new BulletGraphics();
    }

    @Override
    public Runnable apply(LevelObject object) {
        if (object instanceof Tank) {
            return () -> tanks.draw(batch, (Tank) object, tiles);
        }
        if (object instanceof Tree) {
            return () -> trees.draw(batch, (Tree) object, tiles);
        }
        if (object instanceof Bullet) {
            return () -> bullets.draw(batch, (Bullet) object, tiles);
        }
        throw new IllegalArgumentException("No graphics for " + object.getClass().getName());
    }

    @Override
    public void dispose() {
        tanks.dispose();
        trees.dispose();
        bullets.dispose();
    }
}
