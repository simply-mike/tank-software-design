package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

final class TankGraphics implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    TankGraphics(String texturePath) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    void draw(Batch batch, Tank tank, FieldGraphics fieldGraphics) {
        fieldGraphics.placeBetweenTileCenters(
                rectangle,
                tank.getCoordinates(),
                tank.getDestinationCoordinates(),
                tank.getMovementProgress()
        );
        drawTextureRegionUnscaled(batch, graphics, rectangle,
                tank.getDirection().getRotationDegrees());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
