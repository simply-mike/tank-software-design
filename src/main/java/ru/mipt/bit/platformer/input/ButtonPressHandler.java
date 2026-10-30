package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class ButtonPressHandler {

    private final IntPredicate isPressed;
    private final Consumer<Command> submit;
    private final List<Binding> bindings = new ArrayList<>();

    public ButtonPressHandler(IntPredicate isPressed, Consumer<Command> submit) {
        this.isPressed = isPressed;
        this.submit = submit;
    }

    void bind(Command action, int... keyCodes) {
        bindings.add(new Binding(action, true, keyCodes));
    }

    void bindOnPress(Command action, int... keyCodes) {
        bindings.add(new Binding(action, false, keyCodes));
    }

    public void handleInput() {
        for (Binding binding : bindings) {
            binding.submitIfPressed(isPressed, submit);
        }
    }

    private static final class Binding {

        private final Command action;
        private final int[] keyCodes;
        private final boolean repeat;
        private boolean wasPressed;

        private Binding(Command action, boolean repeat, int[] keyCodes) {
            this.action = action;
            this.repeat = repeat;
            this.keyCodes = keyCodes.clone();
        }

        private void submitIfPressed(IntPredicate isPressed, Consumer<Command> submit) {
            boolean pressed = false;
            for (int keyCode : keyCodes) {
                if (isPressed.test(keyCode)) {
                    pressed = true;
                    break;
                }
            }
            if (pressed && (repeat || !wasPressed)) {
                submit.accept(action);
            }
            wasPressed = pressed;
        }
    }
}
