package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveTankCommand;
import ru.mipt.bit.platformer.command.ShootTankCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RandomTankController implements TankControlStrategy {

    private final Tank tank;
    private final Random random;
    private final Consumer<Command> submit;
    private final List<Command> actions = new ArrayList<>();

    public RandomTankController(Tank tank, Random random, Consumer<Command> submit) {
        this.tank = tank;
        this.random = random;
        this.submit = submit;
        for (Direction direction : Direction.values()) {
            actions.add(new MoveTankCommand(tank, direction));
        }
        actions.add(new ShootTankCommand(tank));
    }

    @Override
    public void control() {
        if (!tank.isDestroyed() && !tank.isMoving()) {
            submit.accept(actions.get(random.nextInt(actions.size())));
        }
    }
}
