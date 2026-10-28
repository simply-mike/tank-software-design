package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Level;

import java.io.IOException;

public interface LevelSource {
    Level load() throws IOException;
}
