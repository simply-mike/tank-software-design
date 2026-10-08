package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Tree;

import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.graphics.util.TextureRegionUtils.drawUnscaled;

public final class TreeGraphics implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public TreeGraphics(String texturePath) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    public void draw(Batch batch, Tree tree, TilePlacement tiles) {
        tiles.placeAtTileCenter(rectangle, tree.getCoordinates());
        drawUnscaled(batch, graphics, rectangle, 0f);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
