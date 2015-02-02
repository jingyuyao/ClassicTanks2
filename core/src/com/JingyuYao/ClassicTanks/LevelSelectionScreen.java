package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelSelectionScreen implements Screen {

    public static final int PANEL_SIZE = 100;

    private final ClassicTanks game;

    /*
    Renderer objects
     */
    private final Viewport viewPort;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final ShapeRenderer shapeRenderer;

    public LevelSelectionScreen(final ClassicTanks g) {
        game = g;

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewPort = new ScreenViewport();
        viewPort.setCamera(camera);
        shapeRenderer = new ShapeRenderer();

        // Stage setup
        stage = new Stage();
        stage.setViewport(viewPort);

        addLevel(1, 0, 1, Color.GREEN);
        addLevel(2, 1, 1, Color.BLUE);
        addLevel(3, 2, 1, Color.BLACK);

        Gdx.input.setInputProcessor(stage);
    }

    public void startLevel(int levelNumber){
        game.setToGameScreen(levelNumber);
    }

    public OrthographicCamera getCamera(){
        return camera;
    }

    /**
     * Factory method to create and add {@code LevelPanel} to the stage
     * @param levelNumber
     * @param x grid position
     * @param y grid position
     */
    private void addLevel(int levelNumber, int x, int y, Color color){
        stage.addActor(new LevelPanel(this, levelNumber, shapeRenderer, game.font,
                x*PANEL_SIZE, Gdx.graphics.getHeight() - y*PANEL_SIZE, color));
    }

    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

        Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

}