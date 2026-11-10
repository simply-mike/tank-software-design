package ru.mipt.bit.platformer.graphics;

public final class HealthBarVisibility {

    private boolean visible;

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
