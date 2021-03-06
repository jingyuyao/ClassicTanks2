package com.JingyuYao.ClassicTanks.objects;

import com.JingyuYao.ClassicTanks.level.Level;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.TimeUtils;

@SuppressWarnings("serial")
public class Tank extends GameObj {

    // Constants
    static final float SIZE = Level.TILE_SIZE;
    static final float HALF_SIZE = SIZE / 2f;
    static final float ONE_DISTANCE = HALF_SIZE; // pixels

    // Tank tankType defaults
    static final float DEFAULT_VELOCITY = 100f;
    static final float SUPER_VELOCITY = 125.0f;
    static final float FAST_VELOCITY = 150.0f;
    static final long DEFAULT_FIRE_RATE = 1000000000l;
    static final long BARRAGE_FIRE_RATE = DEFAULT_FIRE_RATE / 3;
    static final long DUAL_FIRE_RATE = DEFAULT_FIRE_RATE / 5;

    // Tank properties
    private TankType tankType;
    private Bullet.BulletType bulletType;
    private long lastBulletTime;
    private long fireRate; // 1s
    private int numBulletsOut;
    private int maxBullets;
    private boolean shooting;
    private float freezeDuration;

    // Variables for movement
    private Direction moveTowards;
    private boolean moving;
    private float target;
    private float distanceLeft = ONE_DISTANCE;

    //************************************ Constructors *********************************
    public Tank(Level level, float x, float y, TankType tankType) {
        super(level, null, x, y, SIZE, SIZE, DEFAULT_VELOCITY, getRandomDirection());
        setTankType(tankType);
        moving = false;
        lastBulletTime = 0l;
        fireRate = DEFAULT_FIRE_RATE;
        numBulletsOut = 0;
        maxBullets = 1;
        moveTowards = Direction.NONE;
        shooting = false;
        bulletType = Bullet.BulletType.NORMAL;
        gameObjType = GameObjType.TANK;
        freezeDuration = 0l;
    }

    public Tank(Level level, float x, float y, TankType tankType, Direction direction) {
        this(level, x, y, tankType);
        setDirection(direction);
    }

    //************************************ Getters ***************************************
    public TankType getTankType() {
        return tankType;
    }

    /**
     * @return a evenly distributed TankType (except GM)
     */
    public static TankType getRandomTankType() {
        switch (Level.RANDOM.nextInt(6)) {
            case 0:
                return TankType.NORMAL;
            case 1:
                return TankType.BARRAGE;
            case 2:
                return TankType.DUAL;
            case 3:
                return TankType.FAST;
            case 4:
                return TankType.ARMORED;
            case 5:
                return TankType.SUPER;
            default:
                return TankType.NORMAL;
        }
    }

    public boolean getMoving() {
        return moving;
    }

    public Direction getMoveTowards() {
        return moveTowards;
    }

    //************************************ Setters **************************************
    public void setTankType(TankType t) {
        resetType();
        tankType = t;
        sprite = chooseSprite();
        switch (tankType) {
            case NORMAL:
                break;
            case FAST:
                setVelocity(FAST_VELOCITY);
                break;
            case BARRAGE:
                setVelocity(SUPER_VELOCITY);
                fireRate = BARRAGE_FIRE_RATE;
                break;
            case DUAL:
                setVelocity(SUPER_VELOCITY);
                maxBullets = 2;
                break;
            case SUPER:
                fireRate = BARRAGE_FIRE_RATE;
                bulletType = Bullet.BulletType.SUPER;
                setVelocity(FAST_VELOCITY);
                break;
            case ARMORED:
                setHp(3);
                break;
        }
    }

    private void resetType() {
        setHp(1);
        bulletType = Bullet.BulletType.NORMAL;
        fireRate = DEFAULT_FIRE_RATE;
        setVelocity(DEFAULT_VELOCITY);
        maxBullets = 1;
    }

    private Sprite chooseSprite() {
        TextureAtlas atlas = getLevel().getTextureAtlas();
        switch (getTankType()) {
            case NORMAL:
                return new Sprite(atlas.findRegion("NORMAL"));
            case BARRAGE:
                return new Sprite(atlas.findRegion("BARRAGE"));
            case DUAL:
                return new Sprite(atlas.findRegion("DUAL"));
            case FAST:
                return new Sprite(atlas.findRegion("FAST"));
            case ARMORED:
                return new Sprite(atlas.findRegion("ARMORED"));
            case SUPER:
                return new Sprite(atlas.findRegion("SUPER"));
        }
        return null;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    //************************************ Con

    public void moveTowards(Direction direction) {
        moveTowards = direction;
    }

    public void startShooting() {
        shooting = true;
    }

    public void stopShooting() {
        shooting = false;
    }

    //******************************* Gameplay ***********************

    public void freeze(float duration) {
        freezeDuration += duration;
    }

    public void addBullet() {
        //How can this break....
        if (numBulletsOut > 0) {
            numBulletsOut--;
        }
    }

    /**
     * Fire a bullet iff {@code curTime - lastBulletTime < fireRate || numBulletsOut >= maxBullets}
     */
    private void shoot() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastBulletTime > fireRate && numBulletsOut < maxBullets) {
            lastBulletTime = curTime;

            float bodyX = getX(), bodyY = getY();
            Direction direction = getDirection();
            Bullet bullet = new Bullet(getLevel(),
                    -1, -1, direction, this, bulletType);
            switch (direction) {
                case DOWN:
                    bullet.setX(bodyX + HALF_SIZE - Bullet.WIDTH / 2f);
                    bullet.setY(bodyY - Bullet.HEIGHT);
                    break;
                case LEFT:
                    bullet.setX(bodyX - Bullet.HEIGHT);
                    bullet.setY(bodyY + HALF_SIZE - Bullet.WIDTH + 1);
                    break;
                case RIGHT:
                    bullet.setX(bodyX + SIZE + Bullet.WIDTH);
                    bullet.setY(bodyY + HALF_SIZE - Bullet.HEIGHT / 2f + 1);
                    break;
                case UP:
                    bullet.setX(bodyX + HALF_SIZE - Bullet.WIDTH / 2f);
                    bullet.setY(bodyY + SIZE);
                    break;
                case NONE:
                default:
                    bullet = null;
                    break;
            }
            if (bullet != null && getStage() != null) {
                getStage().addActor(bullet);
                numBulletsOut++;

                postFiring();

                //special conditions for DUAL tankType
                //flips fire rate every bullet
                if (tankType == TankType.DUAL) {
                    if (fireRate == DEFAULT_FIRE_RATE) {
                        fireRate = DUAL_FIRE_RATE;
                    } else {
                        fireRate = DEFAULT_FIRE_RATE;
                    }
                }
            }
        }
    }

    /**
     * Should be overridden if child want to apply some post firing effects
     */
    protected void postFiring() {
        //default do nothing
    }

    protected void handleBuff(Buff buff) {
        //does nothing by default
        //should be overridden by child class to handle buffs.
    }

    //******************************* Movement ************************

    /**
     * Set a target position for this unit to move towards.
     * Also checks for collision with other objects before moving.
     *
     * @return
     */
    public boolean forward() {
        GameObj result = null;
        float bodyX = getX(), bodyY = getY();
        // set a target value to avoid rounding errors
        switch (getDirection()) {
            case DOWN:
                target = bodyY - ONE_DISTANCE;
                result = collideAll(0.0f, -ONE_DISTANCE);
                break;
            case LEFT:
                target = bodyX - ONE_DISTANCE;
                result = collideAll(-ONE_DISTANCE, 0.0f);
                break;
            case RIGHT:
                target = bodyX + ONE_DISTANCE;
                result = collideAll(ONE_DISTANCE, 0.0f);
                break;
            case UP:
                target = bodyY + ONE_DISTANCE;
                result = collideAll(0.0f, ONE_DISTANCE);
                break;
        }
        // This prevents tanks dodging bullets
        if (result == null || result instanceof Bullet) {
            setMoving(true);
        } else if (result instanceof Buff) {
            setMoving(true);
            handleBuff((Buff) result);
        } else {
            setMoving(false);
        }
        return getMoving();
    }

    //******************************** Actor methods *******************

    /**
     * Updates the tanks position while preventing rounding error of final position.
     * Forward the tank if {@code moveTowards} has been set to anything besides {@code NONE}
     *
     * @param deltaTime
     */
    @Override
    public void act(float deltaTime) {
        if (freezeDuration > 0) {
            freezeDuration -= deltaTime;
            freezeDuration = freezeDuration < 0 ? 0 : freezeDuration;
            return;
        }
        if (moving) {
            float curMove = deltaTime * getVelocity();
            distanceLeft -= curMove;
            if (distanceLeft < 0) {
                distanceLeft = ONE_DISTANCE;// reset
                setMoving(false);// finished moving

                // this prevents final coordinate rounding error
                switch (getDirection()) {
                    case UP:
                    case DOWN:
                        // Fall through
                        setY(target);
                        break;
                    case LEFT:
                    case RIGHT:
                        // Fall through
                        setX(target);
                        break;
                }
                return;
            }
            // change the distance and bump back if it hit something
            switch (getDirection()) {
                case UP:
                    setY(getY() + curMove);
                    break;
                case DOWN:
                    setY(getY() - curMove);
                    break;
                case LEFT:
                    setX(getX() - curMove);
                    break;
                case RIGHT:
                    setX(getX() + curMove);
                    break;
            }
        } else if (moveTowards != Direction.NONE) {
            if (moveTowards != getDirection()) {
                setDirection(moveTowards);
            } else {
                forward();
            }
        }
        if (shooting) {
            shoot();
        }
    }

    //******************************* Enum data types *******************

    /**
     * TODO: Use subclass instead of enum
     * All tankType of tanks.
     */
    public static enum TankType {
        NORMAL,
        BARRAGE, // Fast bullets
        DUAL, // Dual shot
        FAST, // Fast movement
        ARMORED, // Extra health for enemy
        SUPER, // Fast movement and shots, super bullets
    }

    //******************************* Debug ******************************

    @Override
    public String toString() {
        return "Tank{" +
                ", tankType=" + tankType +
                ", lastBulletTime=" + lastBulletTime +
                ", fireRate=" + fireRate +
                ", numBulletsOut=" + numBulletsOut +
                ", maxBullets=" + maxBullets +
                ", shooting=" + shooting +
                ", moveTowards=" + moveTowards +
                ", moving=" + moving +
                "} ";
    }
}
