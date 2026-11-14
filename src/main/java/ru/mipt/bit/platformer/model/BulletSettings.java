package ru.mipt.bit.platformer.model;

/** Domain value object for bullet damage and movement duration */
public final class BulletSettings {

    private final int damage;
    private final float movementDuration;

    public BulletSettings(int damage, float movementDuration) {
        if (damage <= 0 || movementDuration <= 0f || !Float.isFinite(movementDuration)) {
            throw new IllegalArgumentException("Bullet damage and movement duration must be positive and finite");
        }
        this.damage = damage;
        this.movementDuration = movementDuration;
    }

    public int getDamage() {
        return damage;
    }

    public float getMovementDuration() {
        return movementDuration;
    }
}
