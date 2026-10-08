package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Tank;

import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.drawUnscaled;

public final class TankGraphics implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public TankGraphics(String texturePath) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    public void draw(Batch batch, Tank tank, TilePlacement tiles) {
        tiles.placeBetweenTileCenters(
                rectangle,
                tank.getCoordinates(),
                tank.getDestinationCoordinates(),
                tank.getMovementProgress()
        );
        drawUnscaled(batch, graphics, rectangle,
                tank.getDirection().getRotationDegrees());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
