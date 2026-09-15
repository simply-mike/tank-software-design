package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

final class Tank implements Disposable {

    private static final float MOVEMENT_DURATION = 0.4f;

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;

    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    Tank(String texturePath, GridPoint2 initialCoordinates) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
    }

    void move(Direction newDirection, Field field) {
        if (!isEqual(movementProgress, 1f)) {
            return;
        }

        direction = newDirection;
        GridPoint2 destination = newDirection.calculateDestinationFrom(coordinates);
        if (field.isFree(destination)) {
            destinationCoordinates.set(destination);
            movementProgress = 0f;
        }
    }

    void update(float deltaTime, Field field) {
        field.placeBetweenTileCenters(rectangle, coordinates, destinationCoordinates, movementProgress);
        movementProgress = continueProgress(movementProgress, deltaTime, MOVEMENT_DURATION);
        if (isEqual(movementProgress, 1f)) {
            coordinates.set(destinationCoordinates);
        }
    }

    void draw(Batch batch) {
        drawTextureRegionUnscaled(batch, graphics, rectangle, direction.getRotationDegrees());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
