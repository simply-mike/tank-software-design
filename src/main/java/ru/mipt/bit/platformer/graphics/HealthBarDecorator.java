package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tank;

import java.util.function.BooleanSupplier;

final class HealthBarDecorator implements Runnable {

    private static final float OFFSET_ABOVE_CENTER = 50f;

    private final Runnable tankView;
    private final Tank tank;
    private final BooleanSupplier visible;
    private final Batch batch;
    private final TilePlacement tiles;
    private final Texture colors;
    private final Rectangle bar = new Rectangle(0f, 0f, 64f, 6f);

    HealthBarDecorator(Runnable tankView, Tank tank, BooleanSupplier visible,
                       Batch batch, TilePlacement tiles, Texture colors) {
        this.tankView = tankView;
        this.tank = tank;
        this.visible = visible;
        this.batch = batch;
        this.tiles = tiles;
        this.colors = colors;
    }

    @Override
    public void run() {
        tankView.run();
        if (!visible.getAsBoolean()) {
            return;
        }

        tiles.placeBetweenTileCenters(bar, tank.getCoordinates(),
                tank.getDestinationCoordinates(), tank.getMovementProgress());
        float y = bar.y + bar.height / 2 + OFFSET_ABOVE_CENTER;
        batch.draw(colors, bar.x, y, bar.width, bar.height, 0, 0, 1, 1, false, false);
        int health = tank.getHealth();
        if (health > 0) {
            float fillWidth = bar.width * health / tank.getMaxHealth();
            batch.draw(colors, bar.x, y, fillWidth, bar.height, 1, 0, 1, 1, false, false);
        }
    }
}
