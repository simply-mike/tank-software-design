package ru.mipt.bit.platformer.model;

/** Output port for notifications when objects enter or leave the level */
public interface LevelListener {

    void objectAdded(LevelObject object);

    void objectRemoved(LevelObject object);
}
