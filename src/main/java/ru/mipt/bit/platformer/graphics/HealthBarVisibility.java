package ru.mipt.bit.platformer.graphics;

/** Stores whether health bars are visible. Presentation state */
public final class HealthBarVisibility {

    private boolean visible;

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
