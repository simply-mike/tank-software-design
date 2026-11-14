package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Tank;

/** Application command that asks a tank to shoot */
public final class ShootTankCommand implements Command {

    private final Tank tank;

    public ShootTankCommand(Tank tank) {
        this.tank = tank;
    }

    @Override
    public void execute() {
        tank.shoot();
    }
}
