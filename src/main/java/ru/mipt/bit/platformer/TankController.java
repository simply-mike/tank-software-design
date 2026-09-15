package ru.mipt.bit.platformer;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

final class TankController {

    private final ButtonPressHandler buttonPressHandler;

    TankController(ButtonPressHandler buttonPressHandler, Tank tank, Field field) {
        this.buttonPressHandler = buttonPressHandler;
        buttonPressHandler.bind(() -> tank.move(Direction.UP, field), UP, W);
        buttonPressHandler.bind(() -> tank.move(Direction.LEFT, field), LEFT, A);
        buttonPressHandler.bind(() -> tank.move(Direction.DOWN, field), DOWN, S);
        buttonPressHandler.bind(() -> tank.move(Direction.RIGHT, field), RIGHT, D);
    }

    void handleInput() {
        buttonPressHandler.handleInput();
    }
}
