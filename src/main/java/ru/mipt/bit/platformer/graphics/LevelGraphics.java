package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.model.LevelListener;
import ru.mipt.bit.platformer.model.LevelObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/** Output adapter for LevelListener. Adds and removes views as level objects change */
public final class LevelGraphics implements LevelListener {

    private final Function<LevelObject, Runnable> createView;
    private final Map<LevelObject, Runnable> views = new LinkedHashMap<>();

    public LevelGraphics(Function<LevelObject, Runnable> createView) {
        this.createView = Objects.requireNonNull(createView);
    }

    @Override
    public void objectAdded(LevelObject object) {
        views.computeIfAbsent(object, createView);
    }

    @Override
    public void objectRemoved(LevelObject object) {
        views.remove(object);
    }

    public void render() {
        for (Runnable view : views.values()) {
            view.run();
        }
    }
}
