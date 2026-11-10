package ru.mipt.bit.platformer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.model.BulletSettings;
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

    private static final int RANDOM_LEVEL_WIDTH = 10;
    private static final int RANDOM_LEVEL_HEIGHT = 8;

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: [random | path-to-level.txt]");
        }
        LevelSource source = args.length == 1 && args[0].equals("random")
                ? new RandomLevelSource(RANDOM_LEVEL_WIDTH, RANDOM_LEVEL_HEIGHT, new Random())
                : new FileLevelSource(args.length == 0
                        ? GameDesktopLauncher.class.getResource("/level.txt")
                        : Path.of(args[0]).toUri().toURL());
        Level level = source.load();
        Properties gameConfig = loadGameConfig();
        TankGame game = new TankGame(level, readAiTankCount(gameConfig), readTankMovementDuration(gameConfig),
                readTankInitialHealth(gameConfig), readBulletSettings(gameConfig));

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
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
        return readMovementDuration(config, "tank.movement.duration.seconds");
    }

    static int readTankInitialHealth(Properties config) {
        return readPositiveInt(config, "tank.initial.health");
    }

    static BulletSettings readBulletSettings(Properties config) {
        return new BulletSettings(readPositiveInt(config, "bullet.damage"),
                readMovementDuration(config, "bullet.movement.duration.seconds"));
    }

    private static int readPositiveInt(Properties config, String key) {
        int value = Integer.parseInt(config.getProperty(key, ""));
        if (value <= 0) {
            throw new IllegalArgumentException(key + " must be positive");
        }
        return value;
    }

    private static float readMovementDuration(Properties config, String key) {
        float duration = Float.parseFloat(config.getProperty(key, ""));
        if (duration <= 0f || !Float.isFinite(duration)) {
            throw new IllegalArgumentException(key + " must be positive and finite");
        }
        return duration;
    }
}
