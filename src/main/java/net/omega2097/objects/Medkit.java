package net.omega2097.objects;

import net.omega2097.Collider;
import net.omega2097.GameObject;
import net.omega2097.Stat;
import net.omega2097.actors.Player;

public class Medkit extends GameObject {
    private float value = 30f;

    @Override
    protected void onTriggerEnter(Collider other) {
        super.onTriggerEnter(other);
        if (other.getOwner() instanceof Player) {
            Player player = (Player)other.getOwner();
            Stat<Float> health = player.getHealth();
            float currentHealth = health.getCurrent();
            if (!health.isMax()) {
                float newValue = currentHealth + value;
                if (newValue > health.getMax()) {
                    newValue = health.getMax();
                }
                health.setCurrent(newValue);

                System.out.println("Picked up a medkit. Health: " + health.getCurrent());
                destroy();
            }
        }
    }
}
