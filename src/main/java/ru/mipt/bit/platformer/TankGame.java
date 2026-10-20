package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.ButtonPressHandler;
import ru.mipt.bit.platformer.input.RandomTankController;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.TankController;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private static final float TANK_MOVEMENT_DURATION = 0.4f;
    private static final int AI_TANK_COUNT = 5;

    private final Level level;
    private final List<Tank> tanks = new ArrayList<>();
    private final List<TankControlStrategy> controllers = new ArrayList<>();
    private Batch batch;
    private FieldGraphics fieldGraphics;
    private TankGraphics tankGraphics;
    private TreeGraphics treeGraphics;

    TankGame(Level level) {
        this.level = level;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        Tank player = level.spawnTank(level.getPlayerPosition(), TANK_MOVEMENT_DURATION);
        tanks.add(player);
        ButtonPressHandler keyboard = new ButtonPressHandler(Gdx.input::isKeyPressed);
        controllers.add(new TankController(keyboard, player));

        Random random = new Random();
        for (Tank tank : new RandomTankSpawner(random).spawn(level, AI_TANK_COUNT, TANK_MOVEMENT_DURATION)) {
            tanks.add(tank);
            controllers.add(new RandomTankController(tank, random));
        }

        fieldGraphics = new FieldGraphics("level.tmx", batch, level.getWidth(), level.getHeight());
        tankGraphics = new TankGraphics("images/tank_blue.png");
        treeGraphics = new TreeGraphics("images/greenTree.png");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        for (TankControlStrategy controller : controllers) {
            controller.control();
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        for (Tank tank : tanks) {
            tank.update(deltaTime);
        }
        fieldGraphics.render();

        batch.begin();
        for (Tank tank : tanks) {
            tankGraphics.draw(batch, tank, fieldGraphics);
        }
        for (Tree tree : level.getTrees()) {
            treeGraphics.draw(batch, tree, fieldGraphics);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        fieldGraphics.resize(width, height);
    }

    @Override
    public void dispose() {
        fieldGraphics.dispose();
        tankGraphics.dispose();
        treeGraphics.dispose();
        batch.dispose();
    }
}
