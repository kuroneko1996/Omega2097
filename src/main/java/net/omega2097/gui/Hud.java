package net.omega2097.gui;

import net.omega2097.Engine;
import net.omega2097.actors.Player;

public class Hud extends Gui {
    private TextItem playerHealth;
    private TextItem playerAmmo;
    private boolean updated;

    public Hud(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
    }

    public void setPlayerHealth(TextItem playerHealth) {
        this.playerHealth = playerHealth;
    }

    public void setPlayerAmmo(TextItem playerAmmo) {
        this.playerAmmo = playerAmmo;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void update() {
        if (updated) {
            Player player = Engine.getInstance().getPlayer();
            playerHealth.setText(String.format("%03d", player.getHealth().getCurrent().intValue()));
            playerAmmo.setText(String.format("%03d", player.getAmmo().getCurrent()));
        }
    }
}
