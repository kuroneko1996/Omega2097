package net.omega2097.objects;

import net.omega2097.Collider;
import net.omega2097.GameObject;
import net.omega2097.Stat;
import net.omega2097.actors.Player;

public class Treasure extends GameObject {
    private int value = 100;

    @Override
    protected void onTriggerEnter(Collider other) {
        super.onTriggerEnter(other);
        if (other.getOwner() instanceof Player) {
            Player player = (Player)other.getOwner();
            Stat<Integer> gold = player.getGold();
            gold.setCurrent(gold.getCurrent() + value);
            System.out.println("Picked up a treasure. Gold: " + gold.getCurrent());
            destroy();
        }
    }
}
