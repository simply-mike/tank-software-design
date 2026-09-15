package ru.mipt.bit.platformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

final class ButtonPressHandler {

    private final IntPredicate isPressed;
    private final List<Binding> bindings = new ArrayList<>();

    ButtonPressHandler(IntPredicate isPressed) {
        this.isPressed = isPressed;
    }

    void bind(Runnable action, int... keyCodes) {
        bindings.add(new Binding(action, keyCodes));
    }

    void handleInput() {
        for (Binding binding : bindings) {
            binding.runIfPressed(isPressed);
        }
    }

    private static final class Binding {

        private final Runnable action;
        private final int[] keyCodes;

        private Binding(Runnable action, int[] keyCodes) {
            this.action = action;
            this.keyCodes = keyCodes.clone();
        }

        private void runIfPressed(IntPredicate isPressed) {
            for (int keyCode : keyCodes) {
                if (isPressed.test(keyCode)) {
                    action.run();
                    return;
                }
            }
        }
    }
}
