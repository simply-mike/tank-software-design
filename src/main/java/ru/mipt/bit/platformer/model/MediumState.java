package ru.mipt.bit.platformer.model;

/** Domain state for tanks at 15% to 70% of their initial health, including both limits */
final class MediumState implements TankState {

    @Override
    public double movementSpeedFactor() {
        return 0.5d;
    }
}
