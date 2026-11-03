package ru.mipt.bit.platformer.setup;

import ru.mipt.bit.platformer.model.Level;

import java.io.IOException;

public interface LevelSource {
    Level load() throws IOException;
}
