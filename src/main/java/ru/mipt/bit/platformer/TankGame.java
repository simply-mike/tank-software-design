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
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.level.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.LevelObject;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private final Level level;
    private final int aiTankCount;
    private final float tankMovementDuration;
    private final List<TankControlStrategy> controllers = new ArrayList<>();
    private Batch batch;
    private FieldGraphics fieldGraphics;
    private TankGraphics tankGraphics;
    private TreeGraphics treeGraphics;

    TankGame(Level level, int aiTankCount, float tankMovementDuration) {
        this.level = level;
        this.aiTankCount = aiTankCount;
        this.tankMovementDuration = tankMovementDuration;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        Tank player = level.spawnTank(level.getPlayerPosition(), tankMovementDuration);
        ButtonPressHandler keyboard = new ButtonPressHandler(Gdx.input::isKeyPressed);
        controllers.add(new TankController(keyboard, player));

        Random random = new Random();
        for (Tank tank : new RandomTankSpawner(random).spawn(level, aiTankCount, tankMovementDuration)) {
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
        level.update(deltaTime);
        fieldGraphics.render();

        batch.begin();
        for (LevelObject object : level.getObjects()) {
            if (object instanceof Tank) {
                tankGraphics.draw(batch, (Tank) object, fieldGraphics);
            }
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
