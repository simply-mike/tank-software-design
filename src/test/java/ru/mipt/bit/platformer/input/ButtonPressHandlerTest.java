package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

import org.junit.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ButtonPressHandlerTest {

    @Test
    public void collectsActionsBeforeExecutingThemInBindingOrder() {
        List<Command> commands = new ArrayList<>();
        List<Integer> calls = new ArrayList<>();
        ButtonPressHandler handler = new ButtonPressHandler(key -> true, commands::add);
        handler.bind(() -> calls.add(1), 1);
        handler.bind(() -> calls.add(2), 2);

        handler.handleInput();
        assertEquals(List.of(), calls);
        assertEquals(2, commands.size());
        commands.forEach(Command::execute);
        assertEquals(List.of(1, 2), calls);
    }

    @Test
    public void runsActionWhenAnyBoundKeyIsPressed() {
        Set<Integer> pressedKeys = new HashSet<>(Arrays.asList(2, 3));
        AtomicInteger calls = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(pressedKeys::contains, Command::execute);
        handler.bind(calls::incrementAndGet, 1, 2, 3);

        handler.handleInput();

        assertEquals(1, calls.get());
    }

    @Test
    public void skipsActionWhenNoBoundKeyIsPressed() {
        AtomicInteger calls = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(key -> false, Command::execute);
        handler.bind(calls::incrementAndGet, 1);

        handler.handleInput();

        assertEquals(0, calls.get());
    }

    @Test
    public void pressBindingsRequireReleaseWhileHeldMovementKeepsRepeating() {
        Set<Integer> pressed = new HashSet<>();
        AtomicInteger moves = new AtomicInteger();
        AtomicInteger shots = new AtomicInteger();
        ButtonPressHandler handler = new ButtonPressHandler(pressed::contains, Command::execute);
        handler.bind(moves::incrementAndGet, 1);
        handler.bindOnPress(shots::incrementAndGet, 2);
        pressed.addAll(List.of(1, 2));

        handler.handleInput();
        handler.handleInput();
        assertEquals(2, moves.get());
        assertEquals(1, shots.get());
        pressed.remove(2);
        handler.handleInput();
        pressed.add(2);
        handler.handleInput();
        assertEquals(4, moves.get());
        assertEquals(2, shots.get());
    }
}
