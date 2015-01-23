package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Jingyu on 1/22/2015.
 */
public class GameInputProcessor implements InputProcessor {

    private final Level level;

    public GameInputProcessor(Level level){
        this.level = level;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Keys.LEFT:
                level.player.moveTowards(Direction.LEFT);
                return true;
            case Keys.RIGHT:
                level.player.moveTowards(Direction.RIGHT);
                return true;
            case Keys.UP:
                level.player.moveTowards(Direction.UP);
                return true;
            case Keys.DOWN:
                level.player.moveTowards(Direction.DOWN);
                return true;
            case Keys.SPACE:
                level.player.startShooting();
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Keys.LEFT:
                if(level.player.getMoveTowards() == Direction.LEFT){
                    level.player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.RIGHT:
                if(level.player.getMoveTowards() == Direction.RIGHT){
                    level.player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.UP:
                if(level.player.getMoveTowards() == Direction.UP){
                    level.player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.DOWN:
                if(level.player.getMoveTowards() == Direction.DOWN){
                    level.player.moveTowards(Direction.NONE);
                }
                return true;
            case Keys.SPACE:
                level.player.stopShooting();
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}