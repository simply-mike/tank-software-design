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
        bindings.add(new Binding(action, keyCodes));
    }

    public void handleInput() {
        for (Binding binding : bindings) {
            binding.submitIfPressed(isPressed, submit);
        }
    }

    private static final class Binding {

        private final Command action;
        private final int[] keyCodes;

        private Binding(Command action, int[] keyCodes) {
            this.action = action;
            this.keyCodes = keyCodes.clone();
        }

        private void submitIfPressed(IntPredicate isPressed, Consumer<Command> submit) {
            for (int keyCode : keyCodes) {
                if (isPressed.test(keyCode)) {
                    submit.accept(action);
                    return;
                }
            }
        }
    }
}
