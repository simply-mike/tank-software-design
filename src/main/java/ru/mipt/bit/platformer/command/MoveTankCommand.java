package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

public final class MoveTankCommand implements Command {

    private final Tank tank;
    private final Direction direction;

    public MoveTankCommand(Tank tank, Direction direction) {
        this.tank = tank;
        this.direction = direction;
    }

    @Override
    public void execute() {
        tank.move(direction);
    }
}
