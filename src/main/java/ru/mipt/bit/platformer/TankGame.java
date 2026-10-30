package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.LevelGraphics;
import ru.mipt.bit.platformer.graphics.BulletGraphics;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.ButtonPressHandler;
import ru.mipt.bit.platformer.input.RandomTankController;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.TankController;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.level.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.LevelObject;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private final Level level;
    private final int aiTankCount;
    private final float tankMovementDuration;
    private final Map<Tank, TankControlStrategy> controllers = new LinkedHashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private Batch batch;
    private FieldGraphics fieldGraphics;
    private TankGraphics tankGraphics;
    private TreeGraphics treeGraphics;
    private BulletGraphics bulletGraphics;
    private LevelGraphics levelGraphics;

    TankGame(Level level, int aiTankCount, float tankMovementDuration) {
        this.level = level;
        this.aiTankCount = aiTankCount;
        this.tankMovementDuration = tankMovementDuration;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        Tank player = level.spawnTank(level.getPlayerPosition(), tankMovementDuration);
        ButtonPressHandler keyboard = new ButtonPressHandler(Gdx.input::isKeyPressed, commands::add);
        controllers.put(player, new TankController(keyboard, player));

        Random random = new Random();
        for (Tank tank : new RandomTankSpawner(random).spawn(level, aiTankCount, tankMovementDuration)) {
            controllers.put(tank, new RandomTankController(tank, random, commands::add));
        }

        fieldGraphics = new FieldGraphics("level.tmx", batch, level.getWidth(), level.getHeight());
        tankGraphics = new TankGraphics("images/tank_blue.png");
        treeGraphics = new TreeGraphics("images/greenTree.png");
        bulletGraphics = new BulletGraphics();
        levelGraphics = new LevelGraphics(this::createView);
        level.subscribe(levelGraphics);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        commands.clear();
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
        tankGraphics.dispose();
        treeGraphics.dispose();
        bulletGraphics.dispose();
        batch.dispose();
    }

    private Runnable createView(LevelObject object) {
        if (object instanceof Tank) {
            return () -> tankGraphics.draw(batch, (Tank) object, fieldGraphics);
        }
        if (object instanceof Tree) {
            return () -> treeGraphics.draw(batch, (Tree) object, fieldGraphics);
        }
        if (object instanceof Bullet) {
            return () -> bulletGraphics.draw(batch, (Bullet) object, fieldGraphics);
        }
        throw new IllegalArgumentException("No graphics for " + object.getClass().getName());
    }
}
