package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Bullet;

import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.drawUnscaled;

public final class BulletGraphics implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public BulletGraphics() {
        Pixmap pixels = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(1f, 0.85f, 0.2f, 1f);
            pixels.fillCircle(6, 6, 5);
            texture = new Texture(pixels);
        } finally {
            pixels.dispose();
        }
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    public void draw(Batch batch, Bullet bullet, TilePlacement tiles) {
        tiles.placeBetweenTileCenters(rectangle, bullet.getCoordinates(),
                bullet.getDestinationCoordinates(), bullet.getMovementProgress());
        drawUnscaled(batch, graphics, rectangle, 0f);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
