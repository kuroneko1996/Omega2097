package net.omega2097.actors;


import net.omega2097.Engine;
import net.omega2097.GameObject;
import org.lwjgl.util.vector.Vector3f;

public class EnemyAi extends Ai {
    enum State {
        IDLE,
        CHASING,
        ATTACKING,
        DYING,
        DEAD
    }
    private static final long ATTACK_DURATION = 1000; // after chasing
    private static final long DYING_DURATION = 250 * 3;
    private long lastStateChangeTime;

    private static final float CHASING_STOP_DISTANCE = 1.25f;
    private static final float MOVEMENT_SPEED = 0.02f;
    private static final float FIELD_OF_VIEW_HALVED = 60;
    private static final float MAX_DISTANCE_TO_SEE = 6;
    private State state = State.IDLE;
    private GameObject target;

    private Animation[] animations;
    private int currentFrame;

    class Animation {
        int[] frames;
        boolean loop = false;
        long lastFrameTime;
        long frameDuration = 250;

        Animation(int[] frames, boolean loop) {
            this.frames = frames;
            this.loop = loop;
        }
        int getCurrentFrame() {
            if ((System.currentTimeMillis() - lastFrameTime) > frameDuration) {
                lastFrameTime = System.currentTimeMillis();
                currentFrame += 1;
                if ( currentFrame == frames.length) {
                    if (loop) {
                        currentFrame = 0;
                    } else {
                        currentFrame -= 1;
                    }
                }
            }
            return currentFrame;
        }
        int getCurrentTexture() {
            return frames[getCurrentFrame()];
        }
    }

    public EnemyAi(Actor owner) {
        this.owner = owner;
        this.state = State.IDLE;
        this.target = null;

        // TODO explicitly map states to animations
        animations = new Animation[]{
                new Animation(new int[]{0}, true), // idle
                new Animation(new int[]{1,2,3,4}, true), // chasing
                new Animation(new int[]{5,6}, true), // attacking
                new Animation(new int[]{7, 8, 9, 10}, false), // dying
                new Animation(new int[]{11}, false) // dead
        };
    }

    public void setState(State state) {
        this.state = state;
        currentFrame = 0;
        lastStateChangeTime = System.currentTimeMillis();
        System.out.println("[" + owner.getName() + "] state changed to " + state);
    }

    private void updateAnimation() {
        Animation animation = animations[state.ordinal()];
        owner.getModel().setCurrentTexture(animation.getCurrentTexture());
    }

    private void updateIdle() {
        updateAnimation();

        GameObject player = Engine.getInstance().getPlayer();
        if (canSee(player)) {
            setState(State.CHASING);
            this.target = player;
        }
    }

    private void updateChasing() {
        if (target == null) {
            setState(State.IDLE);
            return;
        }

        updateAnimation();

        Vector3f ownerToTarget = Vector3f.sub(target.getPosition(), owner.getPosition(), null);
        float distance = ownerToTarget.length();
        Vector3f ownerToTargetNormalized = ownerToTarget.normalise(null); // TODO divide by distance

        if (distance > CHASING_STOP_DISTANCE) {
            float moveAmount = MOVEMENT_SPEED;
            Vector3f movement = new Vector3f(ownerToTargetNormalized.x * moveAmount,
                    0, ownerToTargetNormalized.z * moveAmount);

            if (true) {
                Vector3f newPosition = Vector3f.add(owner.getPosition(), movement, null);
                owner.setPosition(newPosition.x, newPosition.y, newPosition.z);
            }

        } else {
            setState(State.ATTACKING);
        }
    }

    private void updateAttacking() {
        updateAnimation();

        if ((System.currentTimeMillis() - lastStateChangeTime) > ATTACK_DURATION) {
            setState(State.CHASING);
        }
    }

    private void updateDying() {
        updateAnimation();

        if ((System.currentTimeMillis() - lastStateChangeTime) > DYING_DURATION) {
            setState(State.DEAD);
        }
    }

    private void updateDead() {
        updateAnimation();
        owner.setSolid(false);
    }

    // cone vision
    private boolean canSee(GameObject target) {
        Vector3f targetPos = target.getPosition();
        Vector3f ownerPos = owner.getPosition();
        Vector3f ownerFacing = owner.getDirection();

        Vector3f ownerToTarget = Vector3f.sub(targetPos, ownerPos, null);
        float distance = ownerToTarget.length();
        if (distance == 0) return true;
        if (distance > MAX_DISTANCE_TO_SEE) return false;

        Vector3f ownerToTargetNormalized = ownerToTarget.normalise(null); // TODO just divide by distance
        float angle = (float)Math.acos(Vector3f.dot(ownerFacing, ownerToTargetNormalized));
        return(angle <= FIELD_OF_VIEW_HALVED && distance <= MAX_DISTANCE_TO_SEE); // TODO obstacles checking
    }

    @Override
    protected void update() {
        if (state != State.DYING && state != State.DEAD && owner.getHealth().getCurrent() <= 0) {
            setState(State.DYING);
        }

        switch(state) {
            case IDLE: updateIdle(); break;
            case CHASING: updateChasing(); break;
            case ATTACKING: updateAttacking(); break;
            case DYING: updateDying(); break;
            case DEAD: updateDead(); break;
            default: updateIdle();
        }
    }
}
