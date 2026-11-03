package ru.mipt.bit.platformer.model;

public interface LevelListener {

    void objectAdded(LevelObject object);

    void objectRemoved(LevelObject object);
}
