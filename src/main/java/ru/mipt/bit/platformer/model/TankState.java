package ru.mipt.bit.platformer.model;

/** Domain state that controls movement speed and shooting */
interface TankState {

    // shortcut: no shot timing or effect priority, revisit shoot and forHealth when adding boosters
    double movementSpeedFactor();

    default void shoot(Runnable fire) {
        fire.run();
    }

    static TankState forHealth(int health, int initialHealth) {
        if (initialHealth == 0 || 100L * health < 15L * initialHealth) {
            return new HeavyState();
        }
        if (100L * health <= 70L * initialHealth) {
            return new MediumState();
        }
        return new LightState();
    }
}
