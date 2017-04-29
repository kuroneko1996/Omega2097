package net.omega2097.objects;

import net.omega2097.Collider;
import net.omega2097.GameObject;
import net.omega2097.actors.Player;

public abstract class Pickable extends GameObject {
    @Override
    protected void onTriggerEnter(Collider other) {
        super.onTriggerEnter(other);
        if (other.getOwner() instanceof Player) {
            Player player = (Player)other.getOwner();
            use(player);
        }
    }

    public abstract boolean use(Player owner);
}
