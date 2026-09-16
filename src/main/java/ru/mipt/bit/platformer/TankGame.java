package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

final class TankGame extends ApplicationAdapter {

    private Batch batch;
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
        Field field = new Field(obstacles);
        tank = new Tank(new GridPoint2(1, 1));

        fieldGraphics = new FieldGraphics("level.tmx", batch);
        tankGraphics = new TankGraphics("images/tank_blue.png");
        treeGraphics = new TreeGraphics("images/greenTree.png");

        ButtonPressHandler buttonPressHandler = new ButtonPressHandler(Gdx.input::isKeyPressed);
        tankController = new TankController(buttonPressHandler, tank, field);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        tankController.handleInput();
        tank.update(Gdx.graphics.getDeltaTime());
        fieldGraphics.render();

        batch.begin();
        tankGraphics.draw(batch, tank, fieldGraphics);
        treeGraphics.draw(batch, tree, fieldGraphics);
        batch.end();
    }

    @Override
    public void dispose() {
        fieldGraphics.dispose();
        tankGraphics.dispose();
        treeGraphics.dispose();
        batch.dispose();
    }
}
