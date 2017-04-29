package net.omega2097.objects;

import net.omega2097.Collider;
import net.omega2097.GameObject;
import net.omega2097.Stat;
import net.omega2097.actors.Actor;
import net.omega2097.actors.Player;

public class Ammo extends Pickable {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean use(Player player) {
        if (player.addAmmo(value) > 0) {
            destroy();
            return true;
        }
        return false;
    }
}