package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;
    private Field field;
    private FieldGraphics fieldGraphics;
    private Tank tank;
    private TankGraphics tankGraphics;
    private Tree tree;
    private TreeGraphics treeGraphics;
    private TankController tankController;

    @Override
    public void create() {
        batch = new SpriteBatch();
        tree = new Tree(new GridPoint2(1, 3));
        List<Obstacle> obstacles = Collections.singletonList(tree);
        field = new Field(obstacles);
        tank = new Tank(new GridPoint2(1, 1));

        fieldGraphics = new FieldGraphics("level.tmx", batch);
        tankGraphics = new TankGraphics("images/tank_blue.png");
        treeGraphics = new TreeGraphics("images/greenTree.png");

        ButtonPressHandler buttonPressHandler = new ButtonPressHandler(Gdx.input::isKeyPressed);
        tankController = new TankController(buttonPressHandler, tank, field);
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        tankController.handleInput();
        tank.update(deltaTime);
        fieldGraphics.render();

        // start recording all drawing commands
        batch.begin();
        tankGraphics.draw(batch, tank, fieldGraphics);
        treeGraphics.draw(batch, tree, fieldGraphics);
        // submit all drawing requests
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        fieldGraphics.dispose();
        tankGraphics.dispose();
        treeGraphics.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
