package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public final class TextureRegionUtils {

    private TextureRegionUtils() {
    }

    public static Rectangle createBoundingRectangle(TextureRegion region) {
        return new Rectangle(0f, 0f, region.getRegionWidth(), region.getRegionHeight());
    }

    public static void drawUnscaled(Batch batch, TextureRegion region,
                                    Rectangle rectangle, float rotation) {
        float originX = region.getRegionWidth() / 2f;
        float originY = region.getRegionHeight() / 2f;
        batch.draw(region, rectangle.x, rectangle.y, originX, originY,
                region.getRegionWidth(), region.getRegionHeight(), 1f, 1f, rotation);
    }
}
