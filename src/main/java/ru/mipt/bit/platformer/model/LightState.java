package ru.mipt.bit.platformer.model;

/** Domain state for tanks above 70% of their initial health */
final class LightState implements TankState {

    @Override
    public double movementSpeedFactor() {
        return 1d;
    }
}
