package net.omega2097.actors;


import net.omega2097.Engine;
import net.omega2097.Game;
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
    class Target {
        float distance;
        Actor actor;
    }

    private static final float CHASING_STOP_DISTANCE = 1.25f;
    private static final float MOVEMENT_SPEED = 1.0f;
    private static final float FIELD_OF_VIEW_HALVED = 60;
    private static final float MAX_DISTANCE_TO_SEE = 10;
    private State state = State.IDLE;
    private GameObject target;

    public EnemyAi(Actor owner) {
        this.owner = owner;
        this.state = State.IDLE;
        this.target = null;
    }

    public void setState(State state) {
        this.state = state;
        System.out.println("[" + owner.getName() + "] state changed to " + state);
    }

    private void updateIdle() {
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

        Vector3f ownerToTarget = Vector3f.sub(target.getPosition(), owner.getPosition(), null);
        float distance = ownerToTarget.length();
        Vector3f ownerToTargetNormalized = ownerToTarget.normalise(null); // TODO divide by distance

        if (distance > CHASING_STOP_DISTANCE) {
            float moveAmount = MOVEMENT_SPEED;
            Vector3f movement = new Vector3f();

            if (true) {
                Vector3f newPosition = Vector3f.add(owner.getPosition(), movement, null);
                owner.setPosition(newPosition.x, newPosition.y, newPosition.z);
            }

        } else {
            setState(State.ATTACKING);
        }
    }

    private void updateAttacking() {

    }

    private void updateDying() {

    }

    private void updateDead() {

    }

    // cone vision
    private boolean canSee(GameObject target) {
        Vector3f targetPos = target.getPosition();
        Vector3f ownerPos = owner.getPosition();
        Vector3f ownerFacing = owner.getDirection();

        Vector3f ownerToTarget = Vector3f.sub(targetPos, ownerPos, null);
        float distance = ownerToTarget.length();
        if (distance == 0) return true;

        Vector3f ownerToTargetNormalized = ownerToTarget.normalise(null); // TODO just divide by distance
        float angle = (float)Math.acos(Vector3f.dot(ownerFacing, ownerToTargetNormalized));
        return(angle <= FIELD_OF_VIEW_HALVED && distance < MAX_DISTANCE_TO_SEE); // TODO obstacles checking
    }

    @Override
    protected void update() {
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
