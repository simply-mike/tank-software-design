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

    TankController(ButtonPressHandler buttonPressHandler, Tank tank, TileAvailability tiles) {
        buttonPressHandler.bind(() -> tank.move(Direction.UP, tiles), UP, W);
        buttonPressHandler.bind(() -> tank.move(Direction.LEFT, tiles), LEFT, A);
        buttonPressHandler.bind(() -> tank.move(Direction.DOWN, tiles), DOWN, S);
        buttonPressHandler.bind(() -> tank.move(Direction.RIGHT, tiles), RIGHT, D);
    }
}
