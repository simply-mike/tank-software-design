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

    private static final int SPRITE_SIZE = 12;
    private static final int BULLET_RADIUS = 5;
    private static final int BULLET_COLOR_RGBA = 0xffd833ff;

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public BulletGraphics() {
        Pixmap pixels = new Pixmap(SPRITE_SIZE, SPRITE_SIZE, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(BULLET_COLOR_RGBA);
            int center = SPRITE_SIZE / 2;
            pixels.fillCircle(center, center, BULLET_RADIUS);
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
