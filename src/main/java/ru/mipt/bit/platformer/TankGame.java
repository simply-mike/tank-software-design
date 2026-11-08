package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.HealthBarVisibility;
import ru.mipt.bit.platformer.graphics.LevelGraphics;
import ru.mipt.bit.platformer.graphics.ObjectViewFactory;
import ru.mipt.bit.platformer.input.ButtonPressHandler;
import ru.mipt.bit.platformer.input.RandomTankController;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.TankController;
import ru.mipt.bit.platformer.model.BulletSettings;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.setup.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.function.Function;

import static com.badlogic.gdx.Input.Keys.L;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private final Level level;
    private final int aiTankCount;
    private final Function<GridPoint2, Tank> spawnTank;
    private final Map<Tank, TankControlStrategy> controllers = new LinkedHashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private Batch batch;
    private ButtonPressHandler viewKeyboard;
    private FieldGraphics fieldGraphics;
    private ObjectViewFactory viewFactory;
    private LevelGraphics levelGraphics;

    TankGame(Level level, int aiTankCount, float tankMovementDuration,
             int tankInitialHealth, BulletSettings bulletSettings) {
        this.level = level;
        this.aiTankCount = aiTankCount;
        spawnTank = tile -> level.spawnTank(tile, tankMovementDuration, tankInitialHealth, bulletSettings);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        Tank player = spawnTank.apply(level.getPlayerPosition());
        ButtonPressHandler keyboard = new ButtonPressHandler(Gdx.input::isKeyPressed, commands::add);
        controllers.put(player, new TankController(keyboard, player));
        HealthBarVisibility healthBars = new HealthBarVisibility();
        viewKeyboard = new ButtonPressHandler(Gdx.input::isKeyJustPressed, commands::add);
        viewKeyboard.bind(healthBars::toggle, L);

        Random random = new Random();
        for (Tank tank : new RandomTankSpawner(random).spawn(level, aiTankCount, spawnTank)) {
            controllers.put(tank, new RandomTankController(tank, random, commands::add));
        }

        fieldGraphics = new FieldGraphics("level.tmx", batch, level.getWidth(), level.getHeight());
        viewFactory = new ObjectViewFactory(batch, fieldGraphics, healthBars::isVisible);
        levelGraphics = new LevelGraphics(viewFactory);
        level.subscribe(levelGraphics);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        commands.clear();
        viewKeyboard.handleInput();
        for (TankControlStrategy controller : controllers.values()) {
            controller.control();
        }
        for (Command command : commands) {
            command.execute();
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        level.update(deltaTime);
        controllers.keySet().removeIf(Tank::isDestroyed);
        fieldGraphics.render();

        batch.begin();
        levelGraphics.render();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        fieldGraphics.resize(width, height);
    }

    @Override
    public void dispose() {
        level.unsubscribe(levelGraphics);
        fieldGraphics.dispose();
        viewFactory.dispose();
        batch.dispose();
    }

}
