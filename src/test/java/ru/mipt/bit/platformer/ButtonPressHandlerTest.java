package ru.mipt.bit.platformer;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ButtonPressHandlerTest {

    @Test
    public void runsActionWhenAnyBoundKeyIsPressed() {
        Set<Integer> pressedKeys = new HashSet<>(Arrays.asList(2, 3));
        AtomicInteger calls = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(pressedKeys::contains);
        handler.bind(calls::incrementAndGet, 1, 2, 3);

        handler.handleInput();

        assertEquals(1, calls.get());
    }

    @Test
    public void skipsActionWhenNoBoundKeyIsPressed() {
        AtomicInteger calls = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(key -> false);
        handler.bind(calls::incrementAndGet, 1);

        handler.handleInput();

        assertEquals(0, calls.get());
    }

    @Test
    public void supportsIndependentActions() {
        AtomicInteger movementCalls = new AtomicInteger();
        AtomicInteger firingCalls = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(key -> key == 1 || key == 2);
        handler.bind(movementCalls::incrementAndGet, 1);
        handler.bind(firingCalls::incrementAndGet, 2);

        handler.handleInput();

        assertEquals(1, movementCalls.get());
        assertEquals(1, firingCalls.get());
    }
}
