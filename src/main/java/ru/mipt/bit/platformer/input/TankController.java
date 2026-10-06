package ru.mipt.bit.platformer.input;

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

public final class TankController {

    public TankController(ButtonPressHandler buttonPressHandler, Tank tank) {
        buttonPressHandler.bind(() -> tank.move(Direction.UP), UP, W);
        buttonPressHandler.bind(() -> tank.move(Direction.LEFT), LEFT, A);
        buttonPressHandler.bind(() -> tank.move(Direction.DOWN), DOWN, S);
        buttonPressHandler.bind(() -> tank.move(Direction.RIGHT), RIGHT, D);
    }
}
