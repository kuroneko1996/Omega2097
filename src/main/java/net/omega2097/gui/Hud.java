package net.omega2097.gui;

import net.omega2097.Engine;
import net.omega2097.actors.Player;

public class Hud extends Gui {
    private TextItem playerHealth;
    public Hud(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
    }

    public void setPlayerHealth(TextItem playerHealth) {
        this.playerHealth = playerHealth;
    }

    public void update() {
        Player player = Engine.getInstance().getPlayer();
        String playerHealthString = Integer.toString(player.getHealth().getCurrent().intValue());
        if ( !playerHealthString.equals(playerHealth.getText()) ) {
            playerHealth.setText(String.format("%03d", player.getHealth().getCurrent().intValue()));
        }
    }
}
