package ru.mipt.bit.platformer.setup;

import ru.mipt.bit.platformer.model.Level;

import java.io.IOException;

/** Output port for loading a level */
public interface LevelSource {
    Level load() throws IOException;
}
