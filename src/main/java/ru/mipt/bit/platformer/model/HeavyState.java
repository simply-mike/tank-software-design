package ru.mipt.bit.platformer.model;

/** Domain state for tanks below 15% of their initial health */
final class HeavyState implements TankState {

    @Override
    public double movementSpeedFactor() {
        return 1d / 3d;
    }

    @Override
    public void shoot(Runnable fire) {
        // A heavily damaged tank cannot fire
    }
}
