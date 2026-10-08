package ru.mipt.bit.platformer;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.level.FileLevelSource;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelSource;
import ru.mipt.bit.platformer.level.RandomLevelSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public final class GameDesktopLauncher {

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: [random | path-to-level.txt]");
        }
        LevelSource source = args.length == 1 && args[0].equals("random")
                ? new RandomLevelSource(10, 8, new Random())
                : new FileLevelSource(args.length == 0
                        ? GameDesktopLauncher.class.getResource("/level.txt")
                        : Path.of(args[0]).toUri().toURL());
        Level level = source.load();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        Graphics.DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode();
        config.setWindowedMode(
                (int) Math.min(level.getWidth() * 128L, display.width * 9L / 10),
                (int) Math.min(level.getHeight() * 128L, display.height * 9L / 10));
        new Lwjgl3Application(new TankGame(level), config);
    }
}
