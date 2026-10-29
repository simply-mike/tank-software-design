package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveTankCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Random;
import java.util.function.Consumer;

public final class RandomTankController implements TankControlStrategy {

    private final Tank tank;
    private final Random random;
    private final Consumer<Command> submit;
    private final Direction[] directions = Direction.values();

    public RandomTankController(Tank tank, Random random, Consumer<Command> submit) {
        this.tank = tank;
        this.random = random;
        this.submit = submit;
    }

    @Override
    public void control() {
        if (!tank.isMoving()) {
            Command command = new MoveTankCommand(tank, directions[random.nextInt(directions.length)]);
            submit.accept(command);
        }
    }
}
