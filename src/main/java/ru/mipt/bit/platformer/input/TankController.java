package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.MoveTankCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

public final class TankController implements TankControlStrategy {

    private final ButtonPressHandler buttonPressHandler;

    public TankController(ButtonPressHandler buttonPressHandler, Tank tank) {
        this.buttonPressHandler = buttonPressHandler;
        buttonPressHandler.bind(new MoveTankCommand(tank, Direction.UP), UP, W);
        buttonPressHandler.bind(new MoveTankCommand(tank, Direction.LEFT), LEFT, A);
        buttonPressHandler.bind(new MoveTankCommand(tank, Direction.DOWN), DOWN, S);
        buttonPressHandler.bind(new MoveTankCommand(tank, Direction.RIGHT), RIGHT, D);
    }

    @Override
    public void control() {
        buttonPressHandler.handleInput();
    }
}
