package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tank;

import java.util.function.BooleanSupplier;

/** Draws a health bar above a tank view. Infrastructure decorator */
final class HealthBarDecorator implements Runnable {

    private static final float OFFSET_ABOVE_CENTER = 50f;
    private static final float BAR_WIDTH = 64f;
    private static final float BAR_HEIGHT = 6f;

    private final Runnable tankView;
    private final Tank tank;
    private final BooleanSupplier visible;
    private final Batch batch;
    private final TilePlacement tiles;
    private final TextureRegion background;
    private final TextureRegion fill;
    private final Rectangle bar = new Rectangle(0f, 0f, BAR_WIDTH, BAR_HEIGHT);

    HealthBarDecorator(Runnable tankView, Tank tank, BooleanSupplier visible,
                       Batch batch, TilePlacement tiles, TextureRegion background, TextureRegion fill) {
        this.tankView = tankView;
        this.tank = tank;
        this.visible = visible;
        this.batch = batch;
        this.tiles = tiles;
        this.background = background;
        this.fill = fill;
    }

    @Override
    public void run() {
        tankView.run();
        if (visible.getAsBoolean()) {
            drawHealthBar();
        }
    }

    private void drawHealthBar() {
        tiles.placeBetweenTileCenters(bar, tank.getCoordinates(),
                tank.getDestinationCoordinates(), tank.getMovementProgress());
        float y = bar.y + bar.height / 2 + OFFSET_ABOVE_CENTER;
        batch.draw(background, bar.x, y, bar.width, bar.height);
        int health = tank.getHealth();
        if (health > 0) {
            float fillWidth = bar.width * health / tank.getMaxHealth();
            batch.draw(fill, bar.x, y, fillWidth, bar.height);
        }
    }
}
