package com.JingyuYao.ClassicTanks;

@SuppressWarnings("serial")
public class Bullet extends GameObj{

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;

    // Properties
    private Tank origin;

    /**
     *
     * @param x
     * @param y
     * @param direction
     * @param origin the source of the bullet
     */
    public Bullet(Level level, float x, float y, Direction direction, Tank origin) {
        super(level,x,y,HEIGHT,WIDTH,200f);
        this.direction = direction;
        this.origin = origin;
        setProperRecBound();
    }

    /**
     * Set the width and height of the bullet based on its direction.
     */
    private void setProperRecBound(){
        switch(direction){
            case UP:
            case DOWN:
                this.body.width = WIDTH;
                this.body.height = HEIGHT;
                break;
            case LEFT:
            case RIGHT:
                // Fall through
                // Intentional
                this.body.width = HEIGHT;
                this.body.height = WIDTH;
                break;
        }
    }

    // Getter / Setter
    public Tank getOrigin() { return origin; }

    /**
     * Updates the bullet's location. Also handles collision
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        GameObj result = collideAll(0.0f, 0.0f);

        if(result == null){
            return;
        }else{
            // If both objects are bullets, they cancel each other out
            if(result instanceof Bullet){
                level.bullets.removeValue((Bullet)result, true);
                level.bullets.removeValue(this, true);
            }

            // If bullet is fired by the player
            if(this.getOrigin() instanceof Player){
                // and it hits a wall
                if(result instanceof Wall && result.getHp() != -2){
                    result.damage();
                    level.bullets.removeValue(this, true);
                }else{
                    result.damage();
                    level.bullets.removeValue(this, true);
                }
            }else{
                // Enemy tank only damages players
                if(result instanceof Player){
                    result.damage();
                }
                level.bullets.removeValue(this, true);
            }
        }
    }
}
