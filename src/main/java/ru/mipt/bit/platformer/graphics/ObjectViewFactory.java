package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.LevelObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class ObjectViewFactory implements Function<LevelObject, Runnable>, Disposable {

    private static final int BACKGROUND_COLOR_RGBA = 0x252525ff;
    private static final int FILL_COLOR_RGBA = 0x55e665ff;
    private static final int BACKGROUND_PIXEL_X = 0;
    private static final int FILL_PIXEL_X = 1;

    private final Batch batch;
    private final TilePlacement tiles;
    private final TankGraphics tanks;
    private final TreeGraphics trees;
    private final BulletGraphics bullets;

    private final Texture healthBarColors;
    private final TextureRegion healthBarBackground;
    private final TextureRegion healthBarFill;
    private final BooleanSupplier healthBarsVisible;

    public ObjectViewFactory(Batch batch, TilePlacement tiles, BooleanSupplier healthBarsVisible) {
        this.batch = Objects.requireNonNull(batch);
        this.tiles = Objects.requireNonNull(tiles);
        this.healthBarsVisible = Objects.requireNonNull(healthBarsVisible);
        tanks = new TankGraphics("images/tank_blue.png");
        trees = new TreeGraphics("images/greenTree.png");
        bullets = new BulletGraphics();
        Pixmap colors = new Pixmap(2, 1, Pixmap.Format.RGBA8888);
        try {
            colors.drawPixel(BACKGROUND_PIXEL_X, 0, BACKGROUND_COLOR_RGBA);
            colors.drawPixel(FILL_PIXEL_X, 0, FILL_COLOR_RGBA);
            healthBarColors = new Texture(colors);
        } finally {
            colors.dispose();
        }
        healthBarBackground = new TextureRegion(healthBarColors, BACKGROUND_PIXEL_X, 0, 1, 1);
        healthBarFill = new TextureRegion(healthBarColors, FILL_PIXEL_X, 0, 1, 1);
    }

    @Override
    public Runnable apply(LevelObject object) {
        if (object instanceof Tank) {
            Tank tank = (Tank) object;
            return new HealthBarDecorator(() -> tanks.draw(batch, tank, tiles),
                    tank, healthBarsVisible, batch, tiles, healthBarBackground, healthBarFill);
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
        healthBarColors.dispose();
    }
}
