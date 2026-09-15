package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

final class TreeGraphics implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    TreeGraphics(String texturePath) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    void draw(Batch batch, Tree tree, FieldGraphics fieldGraphics) {
        fieldGraphics.placeAtTileCenter(rectangle, tree.getCoordinates());
        drawTextureRegionUnscaled(batch, graphics, rectangle, 0f);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
