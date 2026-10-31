package ru.mipt.bit.platformer;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.setup.FileLevelSource;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.setup.LevelSource;
import ru.mipt.bit.platformer.setup.RandomLevelSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
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
        Properties gameConfig = loadGameConfig();
        TankGame game = new TankGame(level, readAiTankCount(gameConfig), readTankMovementDuration(gameConfig));

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        Graphics.DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode();
        config.setWindowedMode(
                (int) Math.min(level.getWidth() * 128L, display.width * 9L / 10),
                (int) Math.min(level.getHeight() * 128L, display.height * 9L / 10));
        new Lwjgl3Application(game, config);
    }

    static Properties loadGameConfig() throws IOException {
        Properties config = new Properties();
        try (InputStream input = GameDesktopLauncher.class.getResourceAsStream("/game.properties")) {
            if (input == null) {
                throw new IOException("game.properties not found");
            }
            config.load(input);
        }
        return config;
    }

    static int readAiTankCount(Properties config) {
        int count = Integer.parseInt(config.getProperty("ai.tank.count", ""));
        if (count < 0) {
            throw new IllegalArgumentException("ai.tank.count must not be negative");
        }
        return count;
    }

    static float readTankMovementDuration(Properties config) {
        float duration = Float.parseFloat(config.getProperty("tank.movement.duration.seconds", ""));
        if (duration <= 0f || !Float.isFinite(duration)) {
            throw new IllegalArgumentException("tank.movement.duration.seconds must be positive and finite");
        }
        return duration;
    }
}
