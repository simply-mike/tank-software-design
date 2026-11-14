package ru.mipt.bit.platformer.command;

/** Input contract for application actions. Also used for view actions */
@FunctionalInterface
public interface Command {

    void execute();
}
