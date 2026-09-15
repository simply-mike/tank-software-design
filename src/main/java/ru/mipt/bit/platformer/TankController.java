package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;

final class TankController {

    private final Input input;
    private final Tank tank;
    private final Field field;

    TankController(Input input, Tank tank, Field field) {
        this.input = input;
        this.tank = tank;
        this.field = field;
    }

    void handleInput() {
        moveIfPressed(Direction.UP, Input.Keys.UP, Input.Keys.W);
        moveIfPressed(Direction.LEFT, Input.Keys.LEFT, Input.Keys.A);
        moveIfPressed(Direction.DOWN, Input.Keys.DOWN, Input.Keys.S);
        moveIfPressed(Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D);
    }

    private void moveIfPressed(Direction direction, int arrowKey, int letterKey) {
        if (input.isKeyPressed(arrowKey) || input.isKeyPressed(letterKey)) {
            tank.move(direction, field);
        }
    }
}
