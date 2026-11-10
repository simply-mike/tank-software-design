package ru.mipt.bit.platformer;

import org.junit.Test;
import ru.mipt.bit.platformer.model.BulletSettings;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GameDesktopLauncherTest {

    @Test
    public void loadsBundledGameConfig() throws IOException {
        Properties config = GameDesktopLauncher.loadGameConfig();

        assertEquals(5, GameDesktopLauncher.readAiTankCount(config));
        assertEquals(0.4f, GameDesktopLauncher.readTankMovementDuration(config), 0f);
        assertEquals(100, GameDesktopLauncher.readTankInitialHealth(config));
        BulletSettings bullets = GameDesktopLauncher.readBulletSettings(config);
        assertEquals(30, bullets.getDamage());
        assertEquals(0.1f, bullets.getMovementDuration(), 0f);
    }

    @Test
    public void readsConfiguredValuesIncludingZeroAiTanks() {
        Properties config = new Properties();
        config.setProperty("ai.tank.count", "0");
        config.setProperty("tank.movement.duration.seconds", "1.25");
        config.setProperty("tank.initial.health", "120");
        config.setProperty("bullet.damage", "40");
        config.setProperty("bullet.movement.duration.seconds", "0.25");

        assertEquals(0, GameDesktopLauncher.readAiTankCount(config));
        assertEquals(1.25f, GameDesktopLauncher.readTankMovementDuration(config), 0f);
        assertEquals(120, GameDesktopLauncher.readTankInitialHealth(config));
        BulletSettings bullets = GameDesktopLauncher.readBulletSettings(config);
        assertEquals(40, bullets.getDamage());
        assertEquals(0.25f, bullets.getMovementDuration(), 0f);
    }

    @Test
    public void rejectsMissingMalformedAndOutOfRangeValues() {
        Properties config = new Properties();
        assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.readAiTankCount(config));
        assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.readTankMovementDuration(config));
        for (String count : new String[]{"", "many", "-1", "2147483648"}) {
            config.setProperty("ai.tank.count", count);
            assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.readAiTankCount(config));
        }
        for (String duration : new String[]{"", "fast", "0", "-0.4", "NaN", "Infinity", "1e100"}) {
            config.setProperty("tank.movement.duration.seconds", duration);
            assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.readTankMovementDuration(config));
        }
    }
}
