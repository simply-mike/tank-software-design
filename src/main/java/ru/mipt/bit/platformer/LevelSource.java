package ru.mipt.bit.platformer;

import java.io.IOException;

interface LevelSource {
    Level load() throws IOException;
}
