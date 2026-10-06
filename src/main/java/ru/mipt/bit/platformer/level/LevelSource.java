package ru.mipt.bit.platformer.level;

import java.io.IOException;

public interface LevelSource {
    Level load() throws IOException;
}
