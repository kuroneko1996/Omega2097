package net.omega2097.objects;

import net.omega2097.Collider;
import net.omega2097.GameObject;
import net.omega2097.Stat;
import net.omega2097.actors.Player;

public class Medkit extends Pickable {
    private float value = 30f;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public boolean use(Player player) {
        if (player.heal(value) > 0) {
            destroy();
            return true;
        }
        return false;
    }
}
