package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.Hashtable;

/**
 * Data class that describes a level.
 */
public class Level {
    GameScreen gameScreen;

    int levelNumber;
    int startX, startY;

    Array<Enemy> enemies;
    Array<Bullet> bullets;
    Array<Wall> walls;
    Player player;

    TiledMap map;
    TiledMapTileLayer wallLayer;
    TiledMapTile backgroundTile;

    public Level(int levelNumber, int startX, int startY, GameScreen gameScreen){
        this.levelNumber = levelNumber;
        this.startX = startX;
        this.startY = startY;
        this.gameScreen = gameScreen;

        enemies = new Array<Enemy>();
        walls = new Array<Wall>();
        bullets = new Array<Bullet>();
        player = new Player(this,startX, startY, Tank.TankType.NORMAL, Direction.UP);

        String levelString = "level" + levelNumber +".tmx";

        // load tiled map
        gameScreen.game.assetManager.load(levelString, TiledMap.class);
        gameScreen.game.assetManager.finishLoading();

        map = gameScreen.game.assetManager.get(levelString);

        // get a default background grass tile to replace the wallz
        wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        TiledMapTileLayer tmp = (TiledMapTileLayer) (map.getLayers().get("Background"));
        backgroundTile = tmp.getCell(0, 0).getTile();

        makeWallFromTile();

        enemies.add(new Enemy(this,32, 128, Tank.TankType.ARMORED, Direction.RIGHT));
    }

    private void makeWallFromTile() {
        TiledMapTileLayer.Cell cell;
        int prop;
        for (int i = 0; i < wallLayer.getWidth(); i++) {
            for (int j = 0; j < wallLayer.getHeight(); j++) {
                cell = wallLayer.getCell(i, j);
                if (cell != null) {
                    prop = Integer.parseInt(cell.getTile().getProperties()
                            .get("hp", String.class));
                    // multiply by tile size to match pixel coordinates
                    walls.add(new Wall(this, i * gameScreen.TILE_SIZE, j * gameScreen.TILE_SIZE, prop));
                }
            }
        }
    }

    /**
     * Kill the cell at (x,y) position in world space
     * @param x
     * @param y
     */
    public void killCell(float x, float y){
        wallLayer.getCell((int) (x / gameScreen.TILE_SIZE),
                (int) (y / gameScreen.TILE_SIZE)).setTile(backgroundTile);
    }
}