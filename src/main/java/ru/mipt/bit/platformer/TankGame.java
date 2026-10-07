package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.ButtonPressHandler;
import ru.mipt.bit.platformer.input.TankController;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private static final float TANK_MOVEMENT_DURATION = 0.4f;

    private final Level level;
    private Batch batch;
    private FieldGraphics fieldGraphics;
    private Tank tank;
    private TankGraphics tankGraphics;
    private TreeGraphics treeGraphics;
    private ButtonPressHandler buttonPressHandler;

    TankGame(Level level) {
        this.level = level;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        tank = new Tank(level.getPlayerPosition(), level, TANK_MOVEMENT_DURATION);

        fieldGraphics = new FieldGraphics("level.tmx", batch, level.getWidth(), level.getHeight());
        tankGraphics = new TankGraphics("images/tank_blue.png");
        treeGraphics = new TreeGraphics("images/greenTree.png");

        buttonPressHandler = new ButtonPressHandler(Gdx.input::isKeyPressed);
        new TankController(buttonPressHandler, tank);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        buttonPressHandler.handleInput();
        tank.update(Gdx.graphics.getDeltaTime());
        fieldGraphics.render();

        batch.begin();
        tankGraphics.draw(batch, tank, fieldGraphics);
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
